package eulap.eb.service.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.web.dto.SDEByCustomerAndMonthDto;
import eulap.eb.web.dto.SDEMainDto;
import eulap.eb.web.dto.SalesDeliveryEfficiencyDto;

/**
 * Sales Delivery Efficiency report service.

 *
 */
@Service
public class SalesDeliveryEfficiencyService {
	@Autowired
	private SalesOrderDao salesOrderDao;

	/**
	 * Get the {@link SDEMainDto}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param customerId The ar customer id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @return {@link SDEMainDto}
	 * @throws ParseException 
	 */
	public List<SDEMainDto> getSalesDeliveryEfficiency(Integer companyId, Integer divisionId, Integer customerId,
			int monthFrom, int yearFrom, int monthTo, int yearTo) throws ParseException{
		List<SDEMainDto> sdesMain = new ArrayList<SDEMainDto>();
		SDEMainDto sdeMain = new SDEMainDto();
		Date dateFrom = parseMonthAndYear(Integer.toString(monthFrom), Integer.toString(yearFrom), true);
		Date dateTo = parseMonthAndYear(Integer.toString(monthTo), Integer.toString(yearTo), false);
		List<SalesDeliveryEfficiencyDto> sdesDtos = salesOrderDao.getSalesDeliveryEfficiency(companyId, divisionId,
				customerId != null ? customerId : -1, dateFrom, dateTo);
		List<SDEByCustomerAndMonthDto> sdebcDtos = summarizeSDEByCustomer(sdesDtos);
		List<SDEByCustomerAndMonthDto> sdebInstances = summarizeSDEByMonth(sdesDtos, dateFrom, dateTo);
		sdeMain.setSdes(sdesDtos);
		sdeMain.setSdebcDto(sdebcDtos);
		sdeMain.setSdebcbmDto(sdebInstances);
		sdesMain.add(sdeMain);
		return sdesMain;
	}

	private Date parseMonthAndYear(String month, String year, boolean isFrom) throws ParseException {
		Date date = new Date();
		String dateString = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if(month != "" && year != "") {
			dateString = year+"-"+month+"-"+"1"; 
			if(isFrom) {
				date = formatter.parse(dateString);
			} else {
				date = formatter.parse(dateString);
				date = DateUtil.getEndDayOfMonth(date);
			}
		}
		return date;
	}

	private List<SDEByCustomerAndMonthDto> summarizeSDEByCustomer(List<SalesDeliveryEfficiencyDto> sdesDto){
		Map<Integer, SDEByCustomerAndMonthDto> sdeHM = new HashMap<Integer, SDEByCustomerAndMonthDto>();
		Map<Integer, SDEByCustomerAndMonthDto> sdeDRHM = new HashMap<Integer, SDEByCustomerAndMonthDto>();
		SDEByCustomerAndMonthDto sdebcDto = null;
		SDEByCustomerAndMonthDto sdebcrDto = null;
		Integer key = null;
		Integer rrKey = null;
		for(SalesDeliveryEfficiencyDto sdeDto : sdesDto) {
			key = sdeDto.getCustomerId();
			sdebcDto = new SDEByCustomerAndMonthDto();
			if(sdeHM.containsKey(key)) {
				sdebcDto = sdeHM.get(key);
				sdeHM.put(key, sdebcDto);
			} else {
				sdebcDto.setCustomerName(sdeDto.getCustomerName());
				sdeHM.put(key, sdebcDto);
			}

			rrKey = sdeDto.getDeliveryReceiptId();
			sdebcrDto = new SDEByCustomerAndMonthDto();
			if(sdeDRHM.containsKey(rrKey)) {
				sdebcrDto = sdeDRHM.get(rrKey);
				sdeDRHM.put(rrKey, sdebcrDto);
			} else {
				sdebcrDto.setCustomerName(sdeDto.getCustomerName());
				sdebcrDto.setTotalDelivery(1);
				sdeDRHM.put(rrKey, sdebcrDto);
			}
		}

		for(SDEByCustomerAndMonthDto d : sdeHM.values()) {
			Double total = 0.0;
			for(SDEByCustomerAndMonthDto dto : sdeDRHM.values()) {
				if(dto.getCustomerName().equals(d.getCustomerName())) {
					total += dto.getTotalDelivery();
				}
			}
			d.setTotalDelivery(total);
		}
		return new ArrayList<SDEByCustomerAndMonthDto>(sdeHM.values());
	}

	private List<SDEByCustomerAndMonthDto> summarizeSDEByMonth(List<SalesDeliveryEfficiencyDto> sdesDto, Date dateFrom, Date dateTo){
		Map<String, SDEByCustomerAndMonthDto> sdeHM = new HashMap<String, SDEByCustomerAndMonthDto>();
		Map<Integer, SDEByCustomerAndMonthDto> sdeDRHM = new HashMap<Integer, SDEByCustomerAndMonthDto>();
		SDEByCustomerAndMonthDto sdebcbmDto = null;
		SDEByCustomerAndMonthDto sdebcrDto = null;
		String key = null;
		Integer rrKey = null;
		for(SalesDeliveryEfficiencyDto sdeDto : sdesDto) {
			key = Integer.toString(sdeDto.getMonth())+ Integer.toString(sdeDto.getYear());
			sdebcbmDto = new SDEByCustomerAndMonthDto();
			if(sdeHM.containsKey(key)) {
				sdebcbmDto = sdeHM.get(key);
				sdeHM.put(key, sdebcbmDto);
			} else {
				sdebcbmDto.setMonth(sdeDto.getMonth());
				sdebcbmDto.setYear(sdeDto.getYear());
				sdeHM.put(key, sdebcbmDto);
			}

			rrKey = sdeDto.getDeliveryReceiptId();
			sdebcrDto = new SDEByCustomerAndMonthDto();
			if(sdeDRHM.containsKey(rrKey)) {
				sdebcrDto = sdeDRHM.get(rrKey);
				sdeDRHM.put(rrKey, sdebcrDto);
			} else {
				sdebcrDto.setMonth(sdeDto.getMonth());
				sdebcrDto.setYear(sdeDto.getYear());
				sdebcrDto.setTotalDelivery(1);
				sdeDRHM.put(rrKey, sdebcrDto);
			}
		}

		for(SDEByCustomerAndMonthDto d : sdeHM.values()) {
			Double total = 0.0;
			for(SDEByCustomerAndMonthDto dto : sdeDRHM.values()) {
				if(dto.getMonth() == d.getMonth() && dto.getYear() == d.getYear()) {
					total += dto.getTotalDelivery();
				}
			}
			d.setTotalDelivery(total);
		}
		return createDatesInstances(new ArrayList<SDEByCustomerAndMonthDto>(sdeHM.values()), dateFrom, dateTo);
	}

	private List<SDEByCustomerAndMonthDto> createDatesInstances(ArrayList<SDEByCustomerAndMonthDto> sdesDto, Date dateFrom, Date dateTo){
		List<SDEByCustomerAndMonthDto> sdes = new ArrayList<SDEByCustomerAndMonthDto>();
		SDEByCustomerAndMonthDto sde = new SDEByCustomerAndMonthDto();
		Period period = Period.between(convertToLocalDateViaInstant(dateFrom), convertToLocalDateViaInstant(dateTo));
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFrom);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(dateTo);
		boolean yearEnd = false;
		boolean newYear = false;
		boolean midYear = false;
		//creation of instances by date difference
		int counter = 0;
		int deductEndMonth = 11 - cal.get(Calendar.MONTH);
		for(int i = 0; i <= period.getYears(); i++) {
			int monthsDiff = 0;
			if(i == period.getYears()) {
				yearEnd = true;
			}//note: months are arrays; starts with 0
			if(period.getYears() == 0) {
				monthsDiff = period.getMonths();
			}
			if(i > 0) {
				newYear = true;
				monthsDiff = endCal.get(Calendar.MONTH);
			}
			if(i > 0 && i != period.getYears()) {
				midYear = true;
			}
			for(int j = 0; j <= (yearEnd ? monthsDiff : (midYear ? 11 : deductEndMonth)); j++) {
				sde = new SDEByCustomerAndMonthDto();
				sde.setYear(cal.get(Calendar.YEAR)+i);
				if(newYear) {
					sde.setMonth(j);
					sde.setMonthName(DateUtil.getMonthName(j));
				} else {
					sde.setMonth(cal.get(Calendar.MONTH)+j);
					sde.setMonthName(DateUtil.getMonthName(cal.get(Calendar.MONTH)+j));
				}
				sdes.add(sde);
				counter++;
			}
			if(yearEnd) {
				sdes.add(getEndInstance(counter, cal.get(Calendar.YEAR)+i));
			}
		}

		//add date unto the created instances by year and month
		double totalDelivery = 0;
		for(SDEByCustomerAndMonthDto sdeDto : sdes) {
			for(SDEByCustomerAndMonthDto sded : sdesDto) {
				if(sded.getMonth() == sdeDto.getMonth() && sded.getYear() == sdeDto.getYear()) {
					sdeDto.setTotalDelivery(sded.getTotalDelivery());
				} else {
					sdeDto.setTotalDelivery(sdeDto.getTotalDelivery() != 0.0 ? sdeDto.getTotalDelivery() : 0.0);
				}
			}
			totalDelivery += sdeDto.getTotalDelivery();
			if(sdeDto.getMonth() == 12) {
				double totalMonths = sdeDto.getTotalMonths();
				sdeDto.setTotalDelivery(NumberFormatUtil.roundOffTo2DecPlaces(totalDelivery/totalMonths));
			}
		}
		return sdes;
	}

	private SDEByCustomerAndMonthDto getEndInstance(int counter, int year) {
		SDEByCustomerAndMonthDto endInstance = new SDEByCustomerAndMonthDto();
		endInstance.setMonth(12);
		endInstance.setYear(year);
		endInstance.setMonthName("Average for "+counter+" months");
		endInstance.setTotalMonths(counter);
		return endInstance;
	}

	private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Get the date range string
	 * @param monthFrom The month from
	 * @param yearFrom The year from
	 * @param monthTo The month to
	 * @param yearTo The year to
	 * @return
	 */
	public String getDateRangeByMonthAndYear(int monthFrom, int yearFrom, int monthTo, int yearTo) {
		String monthF = DateUtil.getMonthName(monthFrom-1);
		String monthT = DateUtil.getMonthName(monthTo-1);
		return monthF+" "+Integer.toString(yearFrom)+" - "+monthT+" "+Integer.toString(yearTo);
	}
}
