package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.web.dto.InvoicePerRepresentativeDto;
import eulap.eb.web.dto.SalesOutputDto;
import eulap.eb.web.dto.SalesOutputMainDto;
import eulap.eb.web.dto.SalesPoMonitoringDto;

/**
 * Service class that will handle business logic for sales output report generation

 */

@Service
public class SalesOutputService {
	@Autowired
	private SalesOrderDao salesOrderDao;

	private static final int JANUARY_INT_VAL = 0;
	private static final int DECEMBER_INT_VAL = 11;

	private class SalesOutputRanking {
		private int customerId;
		private double totalInvoice;
		private int rank;

		public int getCustomerId() {
			return customerId;
		}

		public void setCustomerId(int customerId) {
			this.customerId = customerId;
		}

		public double getTotalInvoice() {
			return totalInvoice;
		}

		public void setTotalInvoice(double totalInvoice) {
			this.totalInvoice = totalInvoice;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}
	}

	private List<SalesPoMonitoringDto> getSalesOutputReportData(Integer companyId, Integer divisionId, Integer customerId, Integer customerAcctId,
			Integer salesPersonnelId, String poNumber, Date soDateFrom, Date soDateTo, Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo,
			Integer monthFromId, Integer yearFrom, Integer monthToId, Integer yearTo) {
		//Set the SO start date and end date filter based on the start and end month and year data.
		if(monthFromId != null && yearFrom != null && monthToId != null && yearTo != null) {
			Date startDate = DateUtil.getDateByYearAndMonth(yearFrom, monthFromId - 1);
			Date endDate = DateUtil.getDateByYearAndMonth(yearTo, monthToId - 1);
			soDateFrom = DateUtil.getFirstDayOfMonth(startDate);
			soDateTo = DateUtil.getEndDayOfMonth(endDate);
		}
		return salesOrderDao.getSalesOutputReportData(companyId, divisionId, customerId, customerAcctId, salesPersonnelId, poNumber, soDateFrom, soDateTo, 
				drDateFrom, drDateTo, ariDateFrom, ariDateTo);
	}

	/**
	 * Generate and process the report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param customerId The customer id.
	 * @param customerAcctId The customer account id.
	 * @param salesPersonnelId The sales personnel id.
	 * @param poNumber The po number.
	 * @param soDateFrom The so start date filter.
	 * @param soDateTo The so end date filter.
	 * @param drDateFrom The dr start date filter.
	 * @param drDateTo The dr end date filter.
	 * @param ariDateFrom The ar invoice start date filter.
	 * @param ariDateTo The ar invoice end date filter.
	 * @param monthFromId The start month int value.
	 * @param yearFrom The start year.
	 * @param monthToId The end month int value.
	 * @param yearTo The end year.
	 * @return The processed report data.
	 */
	public SalesOutputMainDto processSalesOutputRprtData(Integer companyId, Integer divisionId, Integer customerId, Integer customerAcctId,
			Integer salesPersonnelId, String poNumber, Date soDateFrom, Date soDateTo, Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo,
			Integer monthFromId, Integer yearFrom, Integer monthToId, Integer yearTo) {
		SalesOutputMainDto salesOutputMainDto = new SalesOutputMainDto();
		List<SalesPoMonitoringDto> reportData = getSalesOutputReportData(companyId, divisionId, customerId, customerAcctId, salesPersonnelId, poNumber, 
				soDateFrom, soDateTo, drDateFrom, drDateTo, ariDateFrom, ariDateTo, monthFromId, yearFrom, monthToId, yearTo);

		LinkedHashMap<String, SalesPoMonitoringDto> salesPoMonitoringHM = new LinkedHashMap<String, SalesPoMonitoringDto>();
		LinkedHashMap<String, InvoicePerRepresentativeDto> invoicePerRepHM = new LinkedHashMap<String, InvoicePerRepresentativeDto>();
		LinkedHashMap<String, SalesOutputDto> salesOutputHM = new LinkedHashMap<String, SalesOutputDto>();
		//Initialize sales output crosstab data.
		if(reportData != null && !reportData.isEmpty()) {
			salesOutputHM = initSalesOutputDtos(reportData.get(0), soDateFrom, soDateTo, drDateFrom, drDateTo, 
					ariDateFrom, ariDateTo, monthFromId, yearFrom, monthToId, yearTo);
		}
		Integer noOfMonths = salesOutputHM.size();
		LinkedHashMap<Integer, SalesOutputRanking> totalPerCustomerHm = new LinkedHashMap<Integer, SalesOutputRanking>();
		SalesOutputRanking sar = null;
		SalesPoMonitoringDto salesMonitoringDto = null;
		InvoicePerRepresentativeDto invoicePerRepDto = null;
		SalesOutputDto salesOutputDto = null;
		for(SalesPoMonitoringDto dto : reportData) {
			//Sales PO Monitoring
			String spomKey = dto.getSoId() + "-" + dto.getLineId() + "-" + dto.getDeliveryReceiptId();
			if(salesPoMonitoringHM.containsKey(spomKey)) {
				salesMonitoringDto = salesPoMonitoringHM.get(spomKey);
				salesMonitoringDto.setAriQty(salesMonitoringDto.getAriQty() + dto.getAriQty());
				salesMonitoringDto.setAriAmount(salesMonitoringDto.getAriAmount() + dto.getAriAmount());
				if(dto.getAriDate() != null) {
					salesMonitoringDto.setStrAriDate(formatDataToString(salesMonitoringDto.getStrAriDate(), 
							DateUtil.formatDate(dto.getAriDate())));
				}
				if(dto.getAriNumber() != null) {
					salesMonitoringDto.setStrAriNumber(formatDataToString(salesMonitoringDto.getStrAriNumber(), 
							dto.getAriNumber().toString()));
				}
				salesMonitoringDto.setBalance(getBalance(salesMonitoringDto));
				salesMonitoringDto.setSoStatus(getSoStatus(salesMonitoringDto));
				salesPoMonitoringHM.put(spomKey, salesMonitoringDto);
			} else {
				if(dto.getAriDate() != null) {
					dto.setStrAriDate(DateUtil.formatDate(dto.getAriDate()));
				}
				if(dto.getAriNumber() != null) {
					dto.setStrAriNumber(dto.getAriNumber().toString());
				}
				dto.setBalance(getBalance(dto));
				dto.setSoStatus(getSoStatus(dto));
				salesPoMonitoringHM.put(spomKey, dto);
			}

			if(dto.getAriAmount() > 0) {
				//Total Invoice Per Marketing Representative
				String iprKey = dto.getCustomerId()+ "-" + dto.getSalesPersonnelId();
				if(invoicePerRepHM.containsKey(iprKey)) {
					invoicePerRepDto = invoicePerRepHM.get(iprKey);
					invoicePerRepDto.setInvoiceAmt(invoicePerRepDto.getInvoiceAmt() + dto.getAriAmount());
					invoicePerRepHM.put(iprKey, invoicePerRepDto);
				} else {
					invoicePerRepHM.put(iprKey, processInvoicePerRepDto(dto));
				}
				//Sales output
				int monthId = DateUtil.getMonth(dto.getAriDate());
				int year = DateUtil.getYear(dto.getAriDate());
				String soKey = dto.getCustomerId() + "-" + year + "-" + monthId;
				if(salesOutputHM.containsKey(soKey)) {
					salesOutputDto = salesOutputHM.get(soKey);
					salesOutputDto.setAmount(salesOutputDto.getAmount() + dto.getAriAmount());
					salesOutputHM.put(soKey, salesOutputDto);
				} else {
					salesOutputHM.put(soKey, processSalesOutputDto(dto, monthId, year));
				}

				Integer totalPerCustomerKey = dto.getCustomerId();
				if(totalPerCustomerHm.containsKey(totalPerCustomerKey)) {
					sar = totalPerCustomerHm.get(totalPerCustomerKey);
					sar.setTotalInvoice(sar.getTotalInvoice() + dto.getAriAmount());
					totalPerCustomerHm.put(totalPerCustomerKey, sar);
				} else {
					sar = new SalesOutputRanking();
					sar.setCustomerId(totalPerCustomerKey);
					sar.setTotalInvoice(dto.getAriAmount());
					totalPerCustomerHm.put(totalPerCustomerKey, sar);
				}
			}
		}
		assignTotalAndRanking(salesOutputHM, totalPerCustomerHm, noOfMonths);
		List<SalesOutputDto> salesOutputDtos = sortData(new ArrayList<SalesOutputDto>(salesOutputHM.values()));
		List<InvoicePerRepresentativeDto> ipmrDtos = sortIpmrRprtData(new ArrayList<InvoicePerRepresentativeDto>(invoicePerRepHM.values()));

		salesOutputMainDto.setSalesPoMonitoringDtos(new ArrayList<SalesPoMonitoringDto>(salesPoMonitoringHM.values()));
		salesOutputMainDto.setInvoicePerRepresentativeDtos(ipmrDtos);
		salesOutputMainDto.setSalesOutputDtos(salesOutputDtos);
		salesOutputMainDto.setNoOfMonths(noOfMonths);
		return salesOutputMainDto;
	}

	/**
	 * Assign and compute average per customer for sales output report.
	 * @param salesOutputHM The sales output report data.
	 * @param totalPerCustomerHm The computed total invoices per customer.
	 * @param noOfMonths The number of months.
	 */
	private void assignTotalAndRanking(LinkedHashMap<String, SalesOutputDto> salesOutputHM, LinkedHashMap<Integer, SalesOutputRanking> totalPerCustomerHm,
			Integer noOfMonths) {
		LinkedHashMap<Integer, SalesOutputRanking> sortedSars = new LinkedHashMap<>();
		int rank = 0;
		//get amount ranking
		for(SalesOutputRanking sar : sortRankingData(new ArrayList<>(totalPerCustomerHm.values()))) {
			sar.setRank(++rank);
			sortedSars.put(sar.getCustomerId(), sar);
		}
		//assign total invoices and ranking by customer
		SalesOutputRanking salesOutputRanking = null;
		for(SalesOutputDto dto : salesOutputHM.values()) {
			if(totalPerCustomerHm.containsKey(dto.getCustomerId())) {
				salesOutputRanking = sortedSars.get(dto.getCustomerId());
				dto.setTotalInvoice(salesOutputRanking.getTotalInvoice());
				dto.setAverage(salesOutputRanking.getTotalInvoice() / noOfMonths);
				dto.setRank(salesOutputRanking.getRank());
			}
		}
	}

	private List<SalesOutputRanking> sortRankingData(List<SalesOutputRanking> sors) {
		Collections.sort(sors, new Comparator<SalesOutputRanking>() {
			@Override
			public int compare(SalesOutputRanking o1, SalesOutputRanking o2) {
				return Double.compare(o2.getTotalInvoice(), o1.getTotalInvoice());
			}
		});
		return sors;
	}

	private Double getBalance(SalesPoMonitoringDto salesMonitoringDto) {
		return salesMonitoringDto.getQty() - salesMonitoringDto.getDrQuantity();
	}

	private String getSoStatus(SalesPoMonitoringDto salesMonitoringDto) {
		String soStatus = "";
		if(salesMonitoringDto.getDrQuantity() == 0) {
			soStatus = "Unserved";
		}
		if(salesMonitoringDto.getQty() - salesMonitoringDto.getDrQuantity() > 0 && salesMonitoringDto.getDrQuantity() != 0) {
			soStatus = "Partially Served";
		}
		if(salesMonitoringDto.getQty() - salesMonitoringDto.getDrQuantity() == 0 && salesMonitoringDto.getDrQuantity() != 0) {
			soStatus = "Fully Served";
		}
		return soStatus;
	}

	private String formatDataToString(String baseString, String toBeAddedString) {
		String formattedString = toBeAddedString;
		if(baseString != null) {
			formattedString = toBeAddedString + ", " + formattedString;
		}
		return formattedString;
	}

	private InvoicePerRepresentativeDto processInvoicePerRepDto(SalesPoMonitoringDto spmDto) {
		InvoicePerRepresentativeDto iprDto = new InvoicePerRepresentativeDto();
		iprDto.setCustomer(spmDto.getCustomerName());
		iprDto.setInvoiceAmt(spmDto.getAriAmount());
		iprDto.setRepresentative(spmDto.getRequestor());
		return iprDto;
	}

	private SalesOutputDto processSalesOutputDto(SalesPoMonitoringDto spmDto, int monthId, int year) {
		SalesOutputDto soDto = new SalesOutputDto();
		soDto.setSort(setSortValue(year, monthId));
		soDto.setCustomerId(spmDto.getCustomerId());
		soDto.setMonthId(monthId);
		soDto.setYear(year);
		soDto.setCustomer(spmDto.getCustomerName());
		soDto.setMonth(DateUtil.getMonthName(monthId) + "\n" + year);
		soDto.setAmount(spmDto.getAriAmount());
		return soDto;
	}

	private List<SalesOutputDto> sortData(List<SalesOutputDto> soDtos) {
		Collections.sort(soDtos, new Comparator<SalesOutputDto>() {
			@Override
			public int compare(SalesOutputDto o1, SalesOutputDto o2) {
				int dto = o1.getCustomer().compareTo(o2.getCustomer());
				return dto;
			}
		});
		return soDtos;
	}

	private List<InvoicePerRepresentativeDto> sortIpmrRprtData(List<InvoicePerRepresentativeDto> ipmrDtos) {
		Collections.sort(ipmrDtos, new Comparator<InvoicePerRepresentativeDto>() {
			@Override
			public int compare(InvoicePerRepresentativeDto o1, InvoicePerRepresentativeDto o2) {
				int dto = o1.getCustomer().compareTo(o2.getCustomer());
				return dto;
			}
		});
		return ipmrDtos;
	}

	/**
	 * Initialize sales output report data based on the provided date range.
	 * @param spmDto The SalesPoMonitoringDto.
	 * @param soDateFrom The so start date filter.
	 * @param soDateTo The so end date filter.
	 * @param drDateFrom The dr start date filter.
	 * @param drDateTo The dr end date filter.
	 * @param ariDateFrom The ar invoice start date filter.
	 * @param ariDateTo The ar invoice end date filter.
	 * @return The initial data for the sales output report.
	 */
	private LinkedHashMap<String, SalesOutputDto> initSalesOutputDtos(SalesPoMonitoringDto spmDto, Date soDateFrom, 
			Date soDateTo, Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo,
			Integer ddMonthFromId, Integer ddYearFrom, Integer ddMonthToId, Integer ddYearTo) {
		if(ddMonthFromId != null && ddYearFrom != null && ddMonthToId != null && ddYearTo != null) {
			Date startDate = DateUtil.getDateByYearAndMonth(ddYearFrom, ddMonthFromId - 1);
			Date endDate = DateUtil.getDateByYearAndMonth(ddYearTo, ddMonthToId - 1);
			soDateFrom = DateUtil.getFirstDayOfMonth(startDate);
			soDateTo = DateUtil.getEndDayOfMonth(endDate);
		}
		List<Date> dates = consolidateDates(soDateFrom, soDateTo, drDateFrom, drDateTo, ariDateFrom, ariDateTo);
		Collections.sort(dates);
		Date lowestDate = dates.get(0);
		Date highestDate = dates.get(dates.size() - 1);
		LinkedHashMap<String, SalesOutputDto> salesOutputHM = new LinkedHashMap<String, SalesOutputDto>();
		int monthFrom = DateUtil.getMonth(lowestDate);
		int yearFrom = DateUtil.getYear(lowestDate);
		int monthTo = DateUtil.getMonth(highestDate);
		int yearTo = DateUtil.getYear(highestDate);
		boolean isContinue = true;
		while(isContinue) {
			String key = spmDto.getCustomerId() + "-" + yearFrom + "-" + monthFrom;
			SalesOutputDto soDto = new SalesOutputDto();
			soDto.setSort(setSortValue(yearFrom, monthFrom));
			soDto.setCustomerId(spmDto.getCustomerId());
			soDto.setMonthId(monthFrom);
			soDto.setYear(yearFrom);
			soDto.setCustomer(spmDto.getCustomerName());
			soDto.setMonth(DateUtil.getMonthName(monthFrom) + "\n" + yearFrom);
			soDto.setAmount(0.00);
			salesOutputHM.put(key, soDto);
			if(monthFrom == monthTo && yearFrom == yearTo) {
				isContinue = false;
			}
			//Reset month to january if it reaches december
			monthFrom++;
			if(monthFrom == DECEMBER_INT_VAL + 1) {
				monthFrom = JANUARY_INT_VAL;
				yearFrom++;
			}
		}
		return salesOutputHM;
	}

	/**
	 * Create a sorting key value for data sorting in crosstab.
	 */
	private Integer setSortValue(int yearId, int monthId) {
		String year = (String.valueOf(yearId)); 
		String month = String.valueOf(monthId);
		if(monthId < 10) {
			month = "0" + month;
		}
		return Integer.valueOf(year.concat(month));//Combine 2 strings and convert it back to Integer.
	}

	/**
	 * Consolidate all dates to an array list of dates.
	 * @param dates The list of date parameters.
	 * @return The date array list.
	 */
	private List<Date> consolidateDates(Date...dates) {
		HashSet<Date> dateSet = new HashSet<Date>();
		for(Date date : dates) {
			if(date != null) {
				dateSet.add(date);
			}
		}
		return new ArrayList<Date>(dateSet);
	}

	/**
	 * Process the report date range based on the date parameters provided.
	 * @param soDateFrom The so start date filter.
	 * @param soDateTo The so end date filter.
	 * @param drDateFrom The dr start date filter.
	 * @param drDateTo The dr end date filter.
	 * @param ariDateFrom The ar invoice start date filter.
	 * @param ariDateTo The ar invoice end date filter.
	 * @param monthFromId The start month int value.
	 * @param yearFrom The start year.
	 * @param monthToId The end month int value.
	 * @param yearTo The end year.
	 * @return The process date range string.
	 */
	public String formatReportRangeParam(Integer monthFrom, Integer yearFrom, Integer monthTo, Integer yearTo, 
			Date soDateFrom, Date soDateTo, Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo) {
		Date ddDateFrom = null;
		Date ddDateTo = null;
		if(monthFrom != null && yearFrom != null && monthTo != null && yearTo != null) {
			Date startDate = DateUtil.getDateByYearAndMonth(yearFrom, monthFrom - 1);
			Date endDate = DateUtil.getDateByYearAndMonth(yearTo, monthTo - 1);
			ddDateFrom = DateUtil.getFirstDayOfMonth(startDate);
			ddDateTo = DateUtil.getEndDayOfMonth(endDate);
		}
		List<Date> cDates = consolidateDates(soDateFrom, soDateTo, drDateFrom, drDateTo, ariDateFrom, ariDateTo,
				ddDateFrom, ddDateTo);
		Collections.sort(cDates);
		Date lowestDate = cDates.get(0);
		Date highestDate = cDates.get(cDates.size() - 1);
		String start = DateUtil.getMonthName(lowestDate) + " " + DateUtil.getYear(lowestDate);
		String end = DateUtil.getMonthName(highestDate) + " " + DateUtil.getYear(highestDate);
		return start + " To " + end;
	}
}
