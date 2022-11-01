package eulap.eb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.bir.AlphalistDetail;
import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;
import eulap.eb.service.bir.AlphalistWriter;
import eulap.eb.web.dto.SLPuchasesHeaderDto;
import eulap.eb.web.dto.SLSalesHeaderDto;
import eulap.eb.web.dto.bir.SLImportationsHeaderDto;
import eulap.eb.web.dto.bir.SummaryLSPImportationDto;
import eulap.eb.web.dto.bir.SummaryLSPPurchasesDto;
import eulap.eb.web.dto.bir.SummaryLSPSalesDto;

/**
 * A business logic class that process the necessary data for Summary list
 * of Sales and Purchases report


 *
 */
@Service
public class SummaryLSPService {
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private CompanyDao companyDao;

	public static final int SALES_TAX_TYPE = 0;
	public static final int PURCHASES_TAX_TYPE = 1;
	public static final int IMPORTATION_TAX_TYPE = 2;
	public static final int MAX_DECIMALS = 2;

	/**
	 * Generate the data source of Summary List of Sales.
	 */
	public List<SummaryLSPSalesDto> slSalesSvc (int companyId, int divisionId, int year, int month) {
		return arInvoiceDao.getSLSales(companyId, divisionId, year, month, true);
	}

	/**
	 * Generate the data source of Summary List of Purchases.
	 */
	public List<SummaryLSPPurchasesDto> slPurchasesSvc (int companyId, int divisionId, int year, int month) {
		return arInvoiceDao.getSLPurchases(companyId, divisionId, year, month, true);
	}

	/**
	 * Generate the data source of Summary List of Imports.
	 */
	public List<SummaryLSPImportationDto> slImportsSvc (int companyId, int divisionId, int year, int month) {
		return arInvoiceDao.getSLImportations(companyId, divisionId, year, month, true);
	}

	public Path generateBIRDatFile(Integer divisionId, Integer taxType, Integer month, Integer year,
			boolean processTin, Integer companyId) {
		Company company = companyDao.get(companyId);
		String tin = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());

		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		AlphalistHeader header = null;
		if(taxType == SALES_TAX_TYPE) {
			header = createSalesHeader(divisionId, month, year, company, tin, branchCode);
		} else if(taxType == PURCHASES_TAX_TYPE) {
			header = createPurchasesHeader(divisionId, month, year, company, tin, branchCode);
		} else {
			header = createImportationHeader(divisionId, month, year, company, tin, branchCode);
		}
		headers.add(header);
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		return path;
	}

	private AlphalistHeader createPurchasesHeader(Integer divisionId, Integer month, Integer year, Company company,
			String formattedTin, String branchCode) {
		List<SummaryLSPPurchasesDto> slPurchases = arInvoiceDao.getSLPurchases(company.getId(), divisionId, year, month, false);
		Date taxableMonth = DateUtil.getEndDayOfMonth(DateUtil.getDateByYearAndMonth(year, month - 1));
		String fileName = company.getTin()+"_"+month+year+"2550";
		Double exemptPurchases = 0.0;
		Double zeroRatedPurchases = 0.0;
		Double taxablePurchases = 0.0;
		Double servicePurchases = 0.0;
		Double capitalGoodsPurchases = 0.0;
		Double goodsPurchases = 0.0;
		Double inputTax = 0.0;
		Double nonCreditable = 0.0;
		for(SummaryLSPPurchasesDto slPurchase : slPurchases) {
			exemptPurchases += slPurchase.getExemptPurchases();
			zeroRatedPurchases += slPurchase.getZeroRatedPurchases();
			taxablePurchases += slPurchase.getTaxablePurchases();
			servicePurchases += slPurchase.getPurchasesServicesAmt();
			capitalGoodsPurchases += slPurchase.getPurchasesCapitalGoodsAmt();
			goodsPurchases += slPurchase.getPurchasesGoodsOtherThanCapitalGoodsAmt();
			inputTax += slPurchase.getInputTaxAmt();
			slPurchase.setOwnersTin(formattedTin);
		}
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		controls.add(AlphalistSchedule.getInstance(new ArrayList<AlphalistDetail>(slPurchases), null));
		return SLPuchasesHeaderDto.getInstance(formattedTin, branchCode, company.getName(), "", "", "", 
				"", "", company.getAddress(), convertToBigDecimals(exemptPurchases), convertToBigDecimals(zeroRatedPurchases), convertToBigDecimals(taxablePurchases), 
				convertToBigDecimals(servicePurchases),  convertToBigDecimals(capitalGoodsPurchases), convertToBigDecimals(goodsPurchases), convertToBigDecimals(inputTax), 
				convertToBigDecimals(nonCreditable), DateUtil.formatDate(taxableMonth), month, fileName, controls);
	}

	private AlphalistHeader createSalesHeader(Integer divisionId, Integer month, Integer year, Company company,
			String formattedTin, String branchCode) {
		Date taxableMonth = DateUtil.getEndDayOfMonth(DateUtil.getDateByYearAndMonth(year, month - 1));
		List<SummaryLSPSalesDto> slSales = arInvoiceDao.getSLSales(company.getId(), divisionId, year, month, false);
		Double exemptSales = 0.0;
		Double zeroRatedSales = 0.0;
		Double taxableSales = 0.0;
		Double outputTax = 0.0;
		for(SummaryLSPSalesDto slSale : slSales) {
			exemptSales += slSale.getExemptSalesAmt();
			zeroRatedSales += slSale.getZeroRatedSales();
			taxableSales += slSale.getTaxableSales();
			outputTax += slSale.getOutputTax();
			slSale.setOwnersTin(formattedTin);
		}
		String fileName = company.getTin()+"_"+month+year+"2550";
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		controls.add(AlphalistSchedule.getInstance(new ArrayList<AlphalistDetail>(slSales), null));
		return SLSalesHeaderDto.getInstance(formattedTin, branchCode, company.getName(), "", "", "", "", "", company.getAddress(), 
				convertToBigDecimals(exemptSales), convertToBigDecimals(zeroRatedSales), convertToBigDecimals(taxableSales), 
				convertToBigDecimals(outputTax), DateUtil.formatDate(taxableMonth), month, fileName, controls);
	}

	private AlphalistHeader createImportationHeader(Integer divisionId, Integer month, Integer year, Company company,
			String formattedTin, String branchCode) {
		Date taxableMonth = DateUtil.getEndDayOfMonth(DateUtil.getDateByYearAndMonth(year, month - 1));
		List<SummaryLSPImportationDto> slImportations = arInvoiceDao.getSLImportations(company.getId(), divisionId, year, month, false);
		Double dutiableValue = 0.0;
		Double chargesFromCustom = 0.0;
		Double taxableImports = 0.0;
		Double exemptImports = 0.0;
		Double vat = 0.0;
		for(SummaryLSPImportationDto slImportation : slImportations) {
			dutiableValue += slImportation.getDutiableValue();
			chargesFromCustom += slImportation.getChargesFromCustom();
			taxableImports += slImportation.getTaxableImports();
			exemptImports += slImportation.getExemptImports();
			vat += slImportation.getVat();
			slImportation.setOwnersTin(formattedTin);
		}
		String fileName = company.getTin()+"_"+month+year+"2550";
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		controls.add(AlphalistSchedule.getInstance(new ArrayList<AlphalistDetail>(slImportations), null));
		return SLImportationsHeaderDto.getInstance(formattedTin, company.getName(), "", "", "", "", 
				"", company.getAddress(), convertToBigDecimals(dutiableValue), convertToBigDecimals(chargesFromCustom), 
				convertToBigDecimals(exemptImports), convertToBigDecimals(taxableImports), 
				convertToBigDecimals(vat), DateUtil.formatDate(taxableMonth), month, fileName, controls);
	}

	private BigDecimal convertToBigDecimals (Double number) {
		return new BigDecimal(number.toString()).setScale(MAX_DECIMALS, RoundingMode.HALF_UP);
	}
}
