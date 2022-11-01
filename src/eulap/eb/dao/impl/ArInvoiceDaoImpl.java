package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.CertFinalTaxWithheldMonthlyDto;
import eulap.eb.web.dto.SAWTDto;
import eulap.eb.web.dto.bir.SummaryLSPImportationDto;
import eulap.eb.web.dto.bir.SummaryLSPPurchasesDto;
import eulap.eb.web.dto.bir.SummaryLSPSalesDto;

/**
 * Implementing class of {@link ArInvoiceDao}

 */

public class ArInvoiceDaoImpl extends BaseDao<ArInvoice> implements ArInvoiceDao {

	@Override
	protected Class<ArInvoice> getDomainClass() {
		return ArInvoice.class;
	}

	@Override
	public Page<ArInvoice> getArInvoices(Integer typeId, FormPluginParam param) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, param.getUser());
		if(!param.getSearchCriteria().trim().isEmpty()){
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", param.getSearchCriteria(), Hibernate.STRING));
		}
		dc.add(Restrictions.eq(ArInvoice.FIELD.arInvoiceTypeId.name(), typeId));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (param.getStatuses().size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), param.getStatuses());
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(ArInvoice.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(ArInvoice.FIELD.date.name()));
		return getAll(dc, param.getPageSetting());
	}

	@Override
	public Page<ArInvoice> searchArInvoices(String criteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		dc.addOrder(Order.desc(ArInvoice.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(ArInvoice.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public int generateSeqNo(int companyId, int typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ArInvoice.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(ArInvoice.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(ArInvoice.FIELD.arInvoiceTypeId.name(), typeId));
		return generateSeqNo(dc);
	}

	@Override
	public ArInvoice getByDeliveryReceipt(int ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		dc.add(Subqueries.propertyIn(ArInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ArInvoice.FIELD.ebObjectId.name(), ooDc));
		return get(dc);
	}

	@Override
	public List<ArInvoice> getArInvoices(int arCustomerAcctId, String criteria,
			String tNumbers, boolean isShow) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArInvoice.FIELD.arCustomerAccountId.name(), arCustomerAcctId));
		if (criteria.startsWith("ARI-")) {
			criteria = criteria.replace("ARI-", "").split(" ")[0];
		}
		dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
			StringFormatUtil.appendWildCard(criteria), Hibernate.STRING));
		if (isShow && (tNumbers != null && !tNumbers.isEmpty())) {
			String tmpNumbers[] = tNumbers.split(";");
			if (tmpNumbers.length > 0) {
				for (String str : tmpNumbers) {
					if (str.startsWith("ARI-")) {
						String arInvoice = str.replace("ARI-", "").split(" ")[0];
						dc.add(Restrictions.sqlRestriction("SEQUENCE_NO != ?",
								StringFormatUtil.removeExtraWhiteSpaces(arInvoice),
								Hibernate.STRING));
					}
				}
			}
		}
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(ArInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public ArInvoice getArInvoiceBySequenceNo(int sequenceNo, int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArInvoice.FIELD.sequenceNo.name(), sequenceNo));
		dc.add(Restrictions.eq(ArInvoice.FIELD.companyId.name(), companyId));
		return get(dc);
	}

	@Override
	public Page<ArInvoice> searchARInvoice(String criteria, int typeId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		if(typeId != 0) {
			dc.add(Restrictions.eq(ArInvoice.FIELD.arInvoiceTypeId.name(), typeId));
		}
		dc.addOrder(Order.desc(ArInvoice.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(ArInvoice.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ArInvoice> getARIsByDRId(Integer drId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(ArInvoice.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(ArInvoice.FIELD.deliveryRecieptId.name(), drId));
		return getAll(dc);
	}

	@Override
	public List<SAWTDto> getSAWT(Integer companyId, Integer divisionId, Integer birAtcId, Integer monthFrom, Integer monthTo, Integer yearTo, boolean processTin) {
		List<SAWTDto> sawts = new ArrayList<SAWTDto>();
		List<Object> objects = executeSP("GET_SAWT", companyId, divisionId, birAtcId, monthFrom, monthTo, yearTo);
		if (objects != null && !objects.isEmpty()) {
			SAWTDto dto = null;
			Object[] row = null;
			for (Object obj: objects) {
				row = (Object[]) obj;
				dto = new SAWTDto();
				// dto.setSequenceNumber((Integer) row[0]);
				String tin = (String) row[1];
				dto.setTin(processTin ? StringFormatUtil.processBirTinTo13Digits(tin) : tin);
				dto.setCorporateName((String) row[2]);
				dto.setIndividualName((String) row[3]);
				dto.setBirAtcCode((String) row[4]);
				dto.setNatureOfPayment((String) row[5]);
				dto.setStrAmount(NumberFormatUtil.convertDoubleToString((Double) row[6]));
				dto.setAmount((Double) row[6]);
				dto.setStrTaxRate(NumberFormatUtil.convertDoubleToString((Double) row[7]));
				dto.setTaxRate((Double) row[7]);
				dto.setTaxWitheld((Double) row[8]);
				dto.setStrTaxWitheld(NumberFormatUtil.convertDoubleToString((Double) row[8]));
				dto.setLastName((String) row[9]);
				dto.setFirstName((String) row[10]);
				dto.setMiddleName((String) row[11]);
				sawts.add(dto);
				dto = null;
			}
		}
		return sawts;
	}

	@Override
	public List<SummaryLSPSalesDto> getSLSales(int companyId, int divisionId, int year, int month, boolean isTinFormatted) {
		List<SummaryLSPSalesDto> slSales = new ArrayList<SummaryLSPSalesDto>();
		List<Object> objects = executeSP("GET_SUMMARY_LIST_OF_SALES", companyId, divisionId, year, month);
		if(objects != null && !objects.isEmpty()) {
			int seqNum = 1;
			for(Object obj: objects) {
				SummaryLSPSalesDto slSale = new SummaryLSPSalesDto();
				Object[] row = (Object[]) obj;
				slSale.setSeqNum(seqNum++);
				slSale.setTaxableMonth((Date) row[0]);
				String tin = (String) row[1];
				if (tin != null && !tin.isEmpty()) {
					slSale.setTinCust(isTinFormatted ? StringFormatUtil.processBirTinTo13Digits(tin)
							: StringFormatUtil.parseBIRTIN(tin));
				}
				int busClasId = (Integer) row[2];
				if (busClasId == BusinessClassification.CORPORATE_TYPE) {
					//TRADE_NAME
					slSale.setRegisteredNameCust((String) row[3]);
				} else {
					slSale.setFirstNameCust((String) row[4]);
					slSale.setMiddleNameCust((String) row[5]);
					slSale.setLastNameCust((String) row[6]);
				}
				slSale.setGrossSalesAmt((Double) row[7]);
				slSale.setExemptSalesAmt((Double) row[8]);
				slSale.setZeroRatedSales((Double) row[9]);
				slSale.setTaxableSales((Double) row[10]);
				slSale.setOutputTax((Double) row[11]);
				slSale.setGrossTaxableSales((Double) row[12]);
				String streetBrgy = (String) row[13];
				String cityProvince = (String) row[14];
				slSale.setStreetBrgy(streetBrgy);
				slSale.setCityProvince(cityProvince);
				slSale.setRegisteredAddressCust(streetBrgy+", " + cityProvince);
				slSales.add(slSale);
			}
			return slSales;
		}
		return null;
	}

	@Override
	public List<SummaryLSPPurchasesDto> getSLPurchases(int companyId, int divisionId, int year, int month, boolean isTinFormatted) {
		List<SummaryLSPPurchasesDto> slPurchases = new ArrayList<>();
		List<Object> objects = executeSP("GET_SUMMARY_LIST_OF_PURCHASES", companyId, divisionId, year, month);
		if(objects != null && !objects.isEmpty()) {
			SummaryLSPPurchasesDto slPurchase = null;
			int seqNum = 1;
			for(Object obj: objects) {
				Object[] row = (Object[]) obj;
				slPurchase = new SummaryLSPPurchasesDto();
				slPurchase.setSeqNum(seqNum++);
				slPurchase.setTaxableMonth(DateUtil.formatDate((Date) row[0]));
				String tin = (String) row[1];
				if (tin != null && !tin.isEmpty()) {
					slPurchase.setTinSup(isTinFormatted ? StringFormatUtil.processBirTinTo13Digits(tin)
							: StringFormatUtil.parseBIRTIN(tin));
				}
				int busClasId = (Integer) row[2];
				if (busClasId == BusinessClassification.CORPORATE_TYPE) {
					slPurchase.setRegisteredNameSup((String) row[3]);
				} else {
					slPurchase.setFirstNameSup((String) row[4]);
					slPurchase.setMiddleNameSup((String) row[5]);
					slPurchase.setLastNameSup((String) row[6]);
				}
				slPurchase.setGrossPurchasesAmt((Double) row[7]);
				slPurchase.setExemptPurchases((Double) row[8]);
				Double taxablePurchases = (Double) row[9];
				slPurchase.setTaxablePurchases(taxablePurchases);
				slPurchase.setZeroRatedPurchases((Double) row[10]);
				slPurchase.setPurchasesServicesAmt((Double) row[11]);
				slPurchase.setPurchasesCapitalGoodsAmt((Double) row[12]);
				slPurchase.setPurchasesGoodsOtherThanCapitalGoodsAmt((Double) row[13]);
				slPurchase.setInputTaxAmt((Double) row[14]);
				slPurchase.setSupplierId((Integer) row[15]);
				String streetBrgy = (String) row[16];
				String cityProvince = (String) row[17];
				slPurchase.setStreetBrgy(streetBrgy);
				slPurchase.setCityProvince(cityProvince);
				slPurchase.setRegisteredAddressSup(streetBrgy+", " + cityProvince);
				slPurchase.setGrossTaxablePurchasesAmt(taxablePurchases);
				slPurchases.add(slPurchase);
			}
		}
		return slPurchases;
	}

	public List<CertFinalTaxWithheldMonthlyDto> getFinalTaxWithheldMonthly(int companyId, int divisionId, int year,
			int fromMonth, int toMonth, int birAtcId, PageSetting pageSetting) {
		String finalTaxSP = "";
		if(birAtcId == 1) {
			finalTaxSP = "GET_FINAL_TAX_WITHHELD_GOODS";
		} else if (birAtcId == 2) {
			finalTaxSP = "GET_FINAL_TAX_WITHHELD_SERVICES";
		} else {
			finalTaxSP = "GET_FINAL_TAX_WITHHELD";
		}
		List<CertFinalTaxWithheldMonthlyDto> ret = new ArrayList<CertFinalTaxWithheldMonthlyDto>();
		List<Object> getMonthlyFinalTax = executeSP(finalTaxSP, companyId, divisionId, year, fromMonth,
				toMonth, birAtcId);
		if(getMonthlyFinalTax !=  null  && !getMonthlyFinalTax.isEmpty()) {
			for (Object obj : getMonthlyFinalTax) {
				Object[] row = (Object[]) obj;
				CertFinalTaxWithheldMonthlyDto dto = new CertFinalTaxWithheldMonthlyDto();
				dto.setMonth((String) row[0]);
				dto.setDate((Date) row[1]);
				dto.setCustomer((String) row[2]);
				dto.setOfficialReceipt((String) row[3]);
				dto.setNetCash(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[4]));
				dto.setCreditableTaxWIthheld(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[5]));
				dto.setAddFinalTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[6]));
				dto.setGross((Double) row[7]);
				ret.add(dto);
			}
		}
		return ret;
	}

	@Override
	public List<SummaryLSPImportationDto> getSLImportations(int companyId, int divisionId, int year, int month, boolean isTinFormatted) {
		List<SummaryLSPImportationDto> slImportations = new ArrayList<>();
		List<Object> objects = executeSP("GET_SUMMARY_LIST_OF_IMPORTATIONS", companyId, divisionId, year, month);
		if(objects != null && !objects.isEmpty()) {
			SummaryLSPImportationDto slImportation = null;
			for(Object obj: objects) {
				Object[] row = (Object[]) obj;
				slImportation = new SummaryLSPImportationDto();
				slImportation.setTaxableMonth(DateUtil.formatDate((Date) row[0]));
				slImportation.setImportNumber((String) row[1]);
				slImportation.setReleaseDate(DateUtil.formatDate((Date) row[2]));
				slImportation.setRegisteredName((String) row[3]);
				slImportation.setImportDate(DateUtil.formatDate((Date) row[4]));
				slImportation.setCountryOfOrigin((String) row[5]);
				slImportation.setLandedCost((Double) row[6]);
				slImportation.setDutiableValue((Double) row[7]);
				slImportation.setChargesFromCustom((Double) row[8]);
				slImportation.setTaxableImports((Double) row[9]);
				slImportation.setExemptImports((Double) row[10]);
				slImportation.setVat((Double) row[11]);
				slImportation.setOrNumber((String) row[12]);
				Date date = (Date) row[13];
				slImportation.setDateOfPayment(date != null ? DateUtil.formatDate(date) : "");
				slImportations.add(slImportation);
			}
		}
		return slImportations;
	}

	@Override
	public List<COCTaxDto> getCOCTaxData(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo) {
		List<COCTaxDto> cocTaxDtos = new ArrayList<COCTaxDto>();
		List<Object> objects = executeSP("GET_COC_TAXWITHELD_AT_SOURCE", companyId, divisionId, dateFrom, dateTo);
		if(objects != null && !objects.isEmpty()) {
			COCTaxDto  cocTaxDto = null;
			for (Object obj : objects) {
				int index = 0;
				Object[] rowResult = (Object[]) obj;
				cocTaxDto = new COCTaxDto();
				cocTaxDto.setCutOffDate((Date) rowResult[index]);
				cocTaxDto.setCustomer((String) rowResult[++index]);
				cocTaxDto.setAtc((String) rowResult[++index]);
				cocTaxDto.setNatureOP((String) rowResult[++index]);
				cocTaxDto.setTaxBase((Double) rowResult[++index]);
				cocTaxDto.setTaxRate((Double) rowResult[++index]);
				cocTaxDto.setTaxAmt((Double) rowResult[++index]);
				cocTaxDtos.add( cocTaxDto);
			}
		}
		return cocTaxDtos;
	}
}
