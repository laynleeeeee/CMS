package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.ItemBuyingAddOnDao;
import eulap.eb.dao.ItemBuyingDiscountDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.RReceivingReportRmItemDao;
import eulap.eb.dao.RriBagDiscountDao;
import eulap.eb.dao.RriBagQuantityDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReceivingReportRmItem;
import eulap.eb.domain.hibernate.RriBagDiscount;
import eulap.eb.domain.hibernate.RriBagQuantity;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.web.dto.RrRawMatItemDto;

/**
 * Business logic for Receiving Report - Raw Materials.


 *
 */
@Service
public class RrRawMaterialService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RrRawMaterialService.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private TermService termService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private RReceivingReportService receivingReportService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private RReceivingReportItemDao rrItemDao;
	@Autowired
	private RReceivingReportRmItemDao rmItemDao;
	@Autowired
	private ItemBuyingAddOnDao buyingAddOnDao;
	@Autowired
	private ItemBuyingDiscountDao buyingDiscountDao;
	@Autowired
	private RriBagQuantityDao rriBagQuantityDao;
	@Autowired
	private RriBagDiscountDao rriBagDiscountDao;
	@Autowired
	private ApPaymentInvoiceDao paymentInvoiceDao;
	@Autowired
	private ApPaymentDao paymentDao;

	/**
	 * Get the RR - Raw Materials object which is {@link APInvoice}
	 * @param id The unique id of the AP Invoice.
	 * @return The {@link APInvoice} object.
	 */
	public APInvoice getRrRawMaterial(int id) {
		logger.info("Retrieving the RR - Raw Materials object using the id: "+id);
		return apInvoiceService.getInvoice(id);
	}

	/**
	 * Get the RR - Raw Materials object with the list of items.
	 * @param id The unique id of the AP Invoice.
	 * @param typeId The AP invoice type id
	 * @return The {@link APInvoice} object.
	 */
	public APInvoice getRawMaterialWithItems(int id, Integer typeId) {
		APInvoice rrRawMaterial = getRrRawMaterial(id);
		rrRawMaterial.setReceivingReport(receivingReportService.getRrByInvoiceId(id));
		List<RReceivingReportItem> rrItems = receivingReportService.getRrItems(id);
		for (RReceivingReportItem rri : rrItems) {
			rri.setStockCode(rri.getItem().getStockCode());
			rri.setRmItem(getRmItem(rri.getId()));
			if(typeId.equals(InvoiceType.RR_RAW_MAT_TYPE_ID)) {
				rri.setBuyingDiscountId(rri.getRmItem().getItemBDiscountId());
				rri.setBuyingAddOnId(rri.getRmItem().getItemBAddOnId());
			}
			rri.setEbObjectId(rri.getRmItem().getEbObjectId());
		}
		rrRawMaterial.setRrItems(rrItems);
		List<ApInvoiceLine> apInvLines = getApInvoiceLines(id);
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			rrRawMaterial.setRriBagQuantities(getRriBagQuantitiesByRefObjectId(rrRawMaterial.getEbObjectId()));
			rrRawMaterial.setRriBagDiscounts(getRriBagDiscountsByRefObjectId(rrRawMaterial.getEbObjectId()));
			rrRawMaterial.setRrRawMatItemDto(convertRrItemToRrRawMatDto(rrItems, rrRawMaterial.getRriBagQuantities(), rrRawMaterial.getRriBagDiscounts(), apInvLines,
					rrRawMaterial.getCompanyId(), rrRawMaterial.getReceivingReport().getWarehouseId()));
		}

		if(apInvLines != null && !apInvLines.isEmpty()) {
			for (ApInvoiceLine apl : apInvLines) {
				//Set the name for editing.
				apl.setApLineSetupName(apl.getApLineSetup().getName());
			}
		}
		rrRawMaterial.setApInvoiceLines(apInvLines);
		return rrRawMaterial;
	}

	public APInvoice getRRawMaterialsForViewing(Integer rrId, Integer typeId) {
		APInvoice rrRawMaterial = getRrRawMaterial(rrId);
		rrRawMaterial.setReceivingReport(receivingReportService.getRrByInvoiceId(rrId));
		List<RReceivingReportItem> rrItems = receivingReportService.getRrItems(rrId);
		for (RReceivingReportItem rri : rrItems) {
			double existingStocks = itemService.getTotalAvailStocks(rri.getItem().getStockCode(),
					rrRawMaterial.getReceivingReport().getWarehouseId());
			rri.setExistingStocks(existingStocks);
			rri.setRmItem(getRmItem(rri.getId()));
		}
		rrRawMaterial.setRrItems(rrItems);
		rrRawMaterial.setApInvoiceLines(getApInvoiceLines(rrId));
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			rrRawMaterial.setRriBagQuantities(getRriBagQuantitiesByRefObjectId(rrRawMaterial.getEbObjectId()));
			rrRawMaterial.setRriBagDiscounts(getRriBagDiscountsByRefObjectId(rrRawMaterial.getEbObjectId()));
			rrRawMaterial.setRrRawMatItemDto(convertRrItemToRrRawMatDto(rrItems, rrRawMaterial.getRriBagQuantities(), rrRawMaterial.getRriBagDiscounts(),
					rrRawMaterial.getApInvoiceLines(), rrRawMaterial.getCompanyId(), rrRawMaterial.getReceivingReport().getWarehouseId()));
			rrRawMaterial.setRrItems(rrItems);
		}
		return rrRawMaterial;
	}

	public void reloadForm(APInvoice rrRawMaterial) {
		//Reload form
		RReceivingReport rr = rrRawMaterial.getReceivingReport();
		if(rr.getCompanyId() != null) {
			rr.setApInvoice(rrRawMaterial);
			rr.setCompany(companyService.getCompany(rr.getCompanyId()));
			rrRawMaterial.setReceivingReport(rr);
		}
		if(rrRawMaterial.getFormWorkflowId() != null) {
			rrRawMaterial.setFormWorkflow(getFormWorkflow(rrRawMaterial.getId()));
		}
	}

	/**
	 * Get the list of AP Invoice lines by AP Invoice id.
	 * @param apInvoiceId The unique id of the ap invoice.
	 * @return The list of {@link ApInvoiceLine}
	 */
	public List<ApInvoiceLine> getApInvoiceLines(int apInvoiceId) {
		return apInvoiceLineDao.getAllByRefId("apInvoiceId", apInvoiceId);
	}

	public void loadSelections(User user, Model model) {
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("terms", termService.getTerms(user));
	}

	/**
	 * Get the {@link RReceivingReportRmItem} object using the RR Item id.
	 * @param rrItemId The unique id of the RR Item.
	 * @return {@link RReceivingReportRmItem}
	 */
	public RReceivingReportRmItem getRmItem(int rrItemId) {
		List<RReceivingReportRmItem> rmItems = rmItemDao.getAllByRefId("rReceivingReportItemId", rrItemId);
		if(rmItems.isEmpty()) {
			return null;
		}
		logger.debug("Successfully retrieved the associated RM "
				+ "Item object using the RR Item id: "+rrItemId);
		return rmItems.get(0);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice rrRawMaterial = (APInvoice) form;
		Integer typeId = rrRawMaterial.getInvoiceTypeId();

		List<RReceivingReportItem> toBeSaveRrItems = rrRawMaterial.getRrItems();
		List<RReceivingReportItem> processedItems = null;
		double totalAmount = 0.0;
		if(typeId.equals(InvoiceType.RR_RAW_MAT_TYPE_ID)) {
			processedItems = processAddOnsAndDiscounts(toBeSaveRrItems);
			double computedValue = 0.0;
			double computedUnitCost = 0.0;
			for (RReceivingReportItem rri : processedItems) {
				// Set unit cost + add on
				if(rri.getBuyingAddOnId() != null) {
					ItemBuyingAddOn addOn = buyingAddOnDao.get(rri.getBuyingAddOnId());
					computedUnitCost = rri.getUnitCost() + addOn.getValue();
					logger.info("Computed unit cost: "+computedUnitCost);
					rri.setUnitCost(computedUnitCost);
				}
				// Set unit cost - discount
				if(rri.getBuyingDiscountId() != null) {
					double computedDisc = computeDiscount(rri);
					computedValue = NumberFormatUtil.divideWFP(computedDisc, rri.getQuantity());
					computedUnitCost = rri.getUnitCost() - computedValue;
					logger.info("Computed unit cost: "+computedUnitCost);
					rri.setUnitCost(computedUnitCost);
				}
			}
			totalAmount = computeTotalAmt(rrRawMaterial);
		} else {
			processedItems = toBeSaveRrItems;
			double weight = 0;
			for (RriBagQuantity rriBagQuantity : rrRawMaterial.getRriBagQuantities()) {
				weight += rriBagQuantity.getQuantity() - (NumberFormatUtil.divideWFP(rriBagQuantity.getBagQuantity(), 2));
			}

			double discount = 0;
			for (RriBagDiscount rriBagDiscount : rrRawMaterial.getRriBagDiscounts()) {
				discount += NumberFormatUtil.multiplyWFP(rriBagDiscount.getBagQuantity(), rriBagDiscount.getDiscountQuantity());
			}

			RReceivingReportRmItem rmItem = null;
			for (RReceivingReportItem rri : processedItems) {
				rmItem = new RReceivingReportRmItem();
				if(rri.getUnitCost() == null) {
					logger.debug("Unit cost is null. Set to zero.");
					rri.setUnitCost(0.0);
				}
				rmItem.setAmount(NumberFormatUtil.multiplyWFP((weight - discount), rri.getUnitCost()));
				rri.setRmItem(rmItem);
			}
			totalAmount = computeTotalNetAmount(rrRawMaterial.getRrItems(), rrRawMaterial.getApInvoiceLines(),
					rrRawMaterial.getRriBagQuantities(), rrRawMaterial.getRriBagDiscounts(),
					rrRawMaterial.getRrRawMatItemDto());
		}

		rrRawMaterial.setRrItems(processedItems);
		rrRawMaterial.setAmount(totalAmount);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the RR - Raw Material.");
		APInvoice rrRawMaterial = (APInvoice) form;
		boolean isNew = rrRawMaterial.getId() == 0;
		int userId = user.getId();
		Date date = new Date ();
		AuditUtil.addAudit(rrRawMaterial, new Audit(userId, isNew, date));
		rrRawMaterial.setServiceLeaseKeyId(user.getServiceLeaseKeyId());

		Integer typeId = rrRawMaterial.getInvoiceTypeId();
		if(isNew) {
			int companyId = rrRawMaterial.getReceivingReport().getCompanyId();
			int sequenceNo = apInvoiceDao.generateSequenceNumber(typeId, companyId);
			rrRawMaterial.setSequenceNumber(sequenceNo);
			logger.debug("Generated sequence number: "+sequenceNo);
		} else {
			APInvoice savedRR = getRrRawMaterial(rrRawMaterial.getId());
			DateUtil.setCreatedDate(rrRawMaterial, savedRR.getCreatedDate());

			//Delete the list of Raw Material items
			List<RReceivingReportItem> savedRrItems = rrItemDao.getRrItems(rrRawMaterial.getId());
			RReceivingReportRmItem rmItem = null;
			for (RReceivingReportItem rri : savedRrItems) {
				//For RR - Raw Materials, there will always be an RM Item table attached.
				 rmItem = rmItemDao.getAllByRefId("rReceivingReportItemId", rri.getId()).get(0);
				 rmItemDao.delete(rmItem);
				 rrItemDao.delete(rri);
			}

			if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
				int refObjectId = rrRawMaterial.getEbObjectId();
				inactiveRriBagQuantities(getRriBagQuantitiesByRefObjectId(refObjectId));
				inactiveRriBagDiscounts(getRriBagDiscountsByRefObjectId(refObjectId));
			}
		}

		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			rrRawMaterial.getReceivingReport().setDeliveryReceiptNo(rrRawMaterial.getInvoiceNumber());
		}

		// Save the RR - Raw Material header
		apInvoiceDao.saveOrUpdate(rrRawMaterial);
		logger.info("Sucessfully saved RR - Raw Materials with Seq. No: "+rrRawMaterial.getSequenceNumber());

		// Save the RR details
		RReceivingReport rr = rrRawMaterial.getReceivingReport();
		AuditUtil.addAudit(rr, new Audit (user.getId(), isNew, date));
		rrRawMaterial.setReceivingReport(rr);

		rr.setApInvoiceId(rrRawMaterial.getId());
		apInvoiceDao.saveOrUpdate(rr);
		logger.debug("saved rr: "+rr);

		RReceivingReportRmItem rmItem = null;
		for (RReceivingReportItem rri : rrRawMaterial.getRrItems()) {
			rri.setApInvoiceId(rrRawMaterial.getId());
			rrItemDao.save(rri); // Save the RR Item.
			rmItem = rri.getRmItem();
			rmItem.setrReceivingReportItemId(rri.getId());
			rmItemDao.save(rmItem); // Save the RM Item.
		}
		logger.info("Successfully saved the items of the Raw Materials.");

		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			// Save RR item bag quantities
			saveRriBagQuantities(rrRawMaterial.getRriBagQuantities(), userId, date);
			// Save RR item bag discounts
			saveRriBagDiscounts(rrRawMaterial.getRriBagDiscounts(), userId, date);
		}

		// Save Other Charges
		saveApInvoiceLines(rrRawMaterial);
	}

	/**
	 * Process the add ons, discounts and amount of the item.
	 * @param rItems The list of items.
	 * @return The processed add ons and discounts of the item.
	 */
	public List<RReceivingReportItem> processAddOnsAndDiscounts(List<RReceivingReportItem> rItems) {
		List<RReceivingReportItem> processedItems = new ArrayList<RReceivingReportItem>();
		RReceivingReportRmItem rmItem = null;
		for (RReceivingReportItem rri : rItems) {
			double amount = 0;
			double buyingPrice = 0;
			rmItem = new RReceivingReportRmItem();
			//Set amount to zero if unit cost is null
			if(rri.getUnitCost() == null) {
				logger.debug("Unit cost is null. Set to zero.");
				rri.setUnitCost(0.0);
			}

			buyingPrice = rri.getUnitCost();
			if(rri.getBuyingAddOnId() != null) {
				//Add the selected add on to the buying price (unit cost) of the item
				ItemBuyingAddOn addOn = buyingAddOnDao.get(rri.getBuyingAddOnId());
				rmItem.setItemBAddOnId(addOn.getId());
				rmItem.setAddOn(addOn.getValue());
				buyingPrice += addOn.getValue();
			}

			amount = NumberFormatUtil.multiplyWFP(rri.getQuantity(), buyingPrice);
			if(rri.getBuyingDiscountId() != null) {
				double computedDisc = computeDiscount(rri);
				rmItem.setItemBDiscountId(rri.getBuyingDiscountId());
				rmItem.setDiscount(computedDisc);
				//Deduct the selected discount to the amount.
				amount -= computedDisc;

			}
			rmItem.setAmount(amount);
			logger.debug("To be saved RM Item: "+rmItem);
			logger.debug("To be saved RR Item: "+rri);
			rri.setRmItem(rmItem);
			processedItems.add(rri);
		}
		return processedItems;
	}

	/**
	 * Compute the discount selected.
	 */
	private double computeDiscount(RReceivingReportItem rrItem) {
		ItemBuyingDiscount buyingDiscount = buyingDiscountDao.get(rrItem.getBuyingDiscountId());
		double computedDiscount = SaleItemUtil.computeDiscount(buyingDiscount, buyingDiscount.getItemDiscountTypeId(),
				rrItem.getQuantity(), rrItem.getUnitCost(), null);
		logger.debug("Computed discount: "+computedDiscount);
		return computedDiscount;
	}

	/**
	 * Compute the total amount of the RR - Raw Material Form.
	 * <br>Total amount = sum of AMOUNT from RM Item + sum of AMOUNT from AP Invoice Line
	 * @param rrRawMaterial The form.
	 * @return The computed total amount.
	 */
	public double computeTotalAmt(APInvoice rrRawMaterial) {
		return computeTotalAmt(rrRawMaterial.getRrItems(), rrRawMaterial.getApInvoiceLines());
	}

	public double computeTotalAmt(List<RReceivingReportItem> rrItems, List<ApInvoiceLine> apLines) {
		double totalAmt = 0;
		for (RReceivingReportItem rri : rrItems) {
			totalAmt += rri.getRmItem().getAmount() == null ? 0 : rri.getRmItem().getAmount();
		}
		logger.debug("computed total amount from RR Items: "+totalAmt);
		for (ApInvoiceLine apl : apLines) {
			totalAmt += apl.getAmount() == null ? 0 : apl.getAmount();
		}
		logger.debug("Total amount: "+totalAmt);
		return totalAmt;
	}

	/**
	 * Save the AP Invoice Lines.
	 */
	public void saveApInvoiceLines(APInvoice rrRawMaterial) {
		int invoiceId = rrRawMaterial.getId();
		if (invoiceId > 0) {
			List<ApInvoiceLine> savedOtherCharges = getApInvoiceLines(invoiceId);
			for (ApInvoiceLine oc : savedOtherCharges) {
				apInvoiceLineDao.delete(oc);
			}
		}
		List<ApInvoiceLine> invoiceLines = rrRawMaterial.getApInvoiceLines();
		for (ApInvoiceLine apl : invoiceLines) {
			apl.setApInvoiceId(rrRawMaterial.getId());
			apInvoiceLineDao.save(apl);
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		APInvoice rrRawMaterial = getRrRawMaterial(id);
		if(rrRawMaterial != null) {
			logger.debug("Retrieved the workflow for RR - Raw Material: "
					+rrRawMaterial.getFormWorkflowId());
			return rrRawMaterial.getFormWorkflow();
		}
		logger.warn("No RR - Raw Material form for the id: "+id);
		return null;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return apInvoiceDao.getAllByRefId("ebObjectId", ebObjectId).get(0);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		APInvoice invoice = apInvoiceDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& invoice.getFormWorkflow().isComplete()) {
			String errorMessage = null;
			List<ApPaymentInvoice> paymentInvoices = paymentInvoiceDao.getPaidInvoices(invoice.getId(), null);
			if(paymentInvoices != null && !paymentInvoices.isEmpty()) {
				int count = 0;
				if(paymentInvoices != null && !paymentInvoices.isEmpty()) {
					for (ApPaymentInvoice api : paymentInvoices) {
						if(count == 0) {
							errorMessage = "Unable to cancel form, corresponding document "
									+ "was paid on following AP Payment form/s :";
							count++;
						}
						errorMessage += "<br> APP Voucher No. " + paymentDao.get(api.getApPaymentId()).getVoucherNumber();
					}
					paymentInvoices = null;
				}
			} else {
				invoice = getRawMaterialWithItems(invoice.getId(), invoice.getInvoiceTypeId());
				List<RReceivingReportItem> rrItems = invoice.getRrItems();
				invoice = null; // Free up memory.
				List<RReceivingReportRmItem> rmItems = new ArrayList<RReceivingReportRmItem>();
				for (RReceivingReportItem rri : rrItems) {
					rmItems.add(rri.getRmItem());
					rri = null; // Free up memory.
				}
				rrItems = null; // Free up memory.
				errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService, rmItems);
				rmItems = null; // Free up memory.
			}

			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceDao.getByWorkflowId(workflowId);
	}

	/**
	 * Appends RReceivingReportItem to the list.
	 * @param rrRawMatItem The RrRawMatItemDto which holds the item data..
	 * @return The List of RReceivingReportItem.
	 */
	public List<RReceivingReportItem> converDtoToRrItem(RrRawMatItemDto rrRawMatItem) {
		List<RReceivingReportItem> rrItems = new ArrayList<>();
		if(rrRawMatItem.getItemId() != null) {
			Item item = itemService.getItem(rrRawMatItem.getItemId());
			RReceivingReportItem rrItem = new RReceivingReportItem();
			rrItem.setItemId(item.getId());
			rrItem.setItem(item);
			rrItem.setQuantity(rrRawMatItem.getQuantity());
			rrItem.setUnitCost(rrRawMatItem.getBuyingPrice());
			rrItems.add(rrItem);
		}
		return rrItems;
	}

	/**
	 * Get the list of {@link RriBagQuantity}.
	 * @param refObjectId The EB_OBJECT_ID of parent.
	 * @param orTypeId The orTypeId.
	 * @return The list of {@link RriBagQuantity}.
	 */
	public List<RriBagQuantity> getRriBagQuantitiesByRefObjectId(Integer refObjectId) {
		return rriBagQuantityDao.getRriBagQuantitiesByRefObjectId(refObjectId);
	}

	/**
	 * Get the list of {@link RriBagDiscount}
	 * @param refObjectId The EbObjectId of the parent.
	 * @param orTypeId The orTypeId of {@link RriBagDiscount}.
	 * @return The list of {@link RriBagDiscount}.
	 */
	public List<RriBagDiscount> getRriBagDiscountsByRefObjectId(Integer refObjectId) {
		return rriBagDiscountDao.getRriDiscountsByRefObjectId(refObjectId);
	}

	private RrRawMatItemDto convertRrItemToRrRawMatDto(List<RReceivingReportItem> rrItems, List<RriBagQuantity> rriBagQuantities,
			List<RriBagDiscount> rriBagDiscounts, List<ApInvoiceLine> apLines, Integer companyId, Integer wareHouseId) {
		RReceivingReportItem rrItem = rrItems.get(0); // Expecting only one row
		rrItem.setRmItem(getRmItem(rrItem.getId()));

		Double amount = 0.0;
		Double totalNetWeight = 0.0;
		Double totalDiscounts = 0.0;
		Double totalOtherCharges = 0.0;
		Double buyingPrice = rrItem.getUnitCost();

		if(rriBagQuantities != null && !rriBagQuantities.isEmpty()) {
			for(RriBagQuantity rriBagQuantity : rriBagQuantities) {
				totalNetWeight += rriBagQuantity.getNetWeight();
			}
		}

		if(rriBagDiscounts != null && !rriBagDiscounts.isEmpty()) {
			double totalBags = 0.0;
			double totalDiscQty = 0.0;
			for(RriBagDiscount rriBagDiscount : rriBagDiscounts) {
				totalBags = rriBagDiscount.getBagQuantity();
				totalDiscQty = rriBagDiscount.getDiscountQuantity();
				totalDiscounts += NumberFormatUtil.multiplyWFP(totalBags, totalDiscQty);
			}
		}

		amount = NumberFormatUtil.multiplyWFP((totalNetWeight - totalDiscounts), buyingPrice);
	
		if(apLines != null && !apLines.isEmpty()) {
			for(ApInvoiceLine apLine : apLines) {
				totalOtherCharges += apLine.getAmount();
			}
		}

		return RrRawMatItemDto.getInstanceOf(rrItem.getItemId(), rrItem.getItem().getStockCode(),  rrItem.getItem().getDescription(),
				rrItem.getItem().getUnitMeasurement().getName(), buyingPrice, rrItem.getQuantity(), amount, totalNetWeight, totalDiscounts, totalOtherCharges);
	}

	/**
	 * Compute total net amount for RR -RM items
	 * @param rrItems The receiving report items
	 * @param apLines The other charges lines
	 * @param rriBagQtys The receiving report item bag quantities
	 * @param rriBagDiscounts receiving report item bag discounts
	 * @param rrRawMatItemDto The receiving report - rm item DTO
	 * @return
	 */
	public double computeTotalNetAmount(List<RReceivingReportItem> rrItems, List<ApInvoiceLine> apLines,
			List<RriBagQuantity> rriBagQtys, List<RriBagDiscount> rriBagDiscounts,
			RrRawMatItemDto rrRawMatItemDto) {
		double buyingPrice = 0;
		for (RReceivingReportItem rri : rrItems) {
			buyingPrice += rri.getUnitCost();
		}

		double weight = 0;
		for (RriBagQuantity rriBagQuantity : rriBagQtys) {
			weight += rriBagQuantity.getQuantity() - (NumberFormatUtil.divideWFP(rriBagQuantity.getBagQuantity(), 2));
		}

		double discount = 0;
		for (RriBagDiscount rriBagDiscount : rriBagDiscounts) {
			discount += NumberFormatUtil.multiplyWFP(rriBagDiscount.getBagQuantity(), rriBagDiscount.getDiscountQuantity());
		}
		double totalAmt = NumberFormatUtil.multiplyWFP((weight - discount), buyingPrice);

		logger.debug("computed total amount from RR Items: " + totalAmt);
		for (ApInvoiceLine apl : apLines) {
			totalAmt += apl.getAmount() == null ? 0 : apl.getAmount();
		}

		logger.debug("Total amount: " + totalAmt);
		return totalAmt;
	}

	private void inactiveRriBagQuantities(List<RriBagQuantity> rriBagQuantities) {
		List<Domain> tobeUpdatedDomains = new ArrayList<>();
		for(RriBagQuantity rriBagQuantity : rriBagQuantities) {
			rriBagQuantity.setActive(false);
			tobeUpdatedDomains.add(rriBagQuantity);
		}
		rriBagQuantityDao.batchSaveOrUpdate(tobeUpdatedDomains);
	}

	private void inactiveRriBagDiscounts(List<RriBagDiscount> rriBagDiscounts) {
		List<Domain> tobeUpdatedDomains = new ArrayList<>();
		for(RriBagDiscount rriBagDiscount : rriBagDiscounts) {
			rriBagDiscount.setActive(false);
			tobeUpdatedDomains.add(rriBagDiscount);
		}
		rriBagDiscountDao.batchSaveOrUpdate(tobeUpdatedDomains);
	}

	private void saveRriBagQuantities(List<RriBagQuantity> rriBagQuantities, int userId, Date currDate) {
		if(rriBagQuantities != null && !rriBagQuantities.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for(RriBagQuantity rriBagQuantity : rriBagQuantities) {
				rriBagQuantity.setActive(true);
				AuditUtil.addAudit(rriBagQuantity, new Audit(userId, true, currDate));
				toBeSaved.add(rriBagQuantity);
			}
			rriBagQuantityDao.batchSave(toBeSaved);
		}
	}

	private void saveRriBagDiscounts(List<RriBagDiscount> rriBagDiscounts, int userId, Date currDate) {
		if(rriBagDiscounts != null && !rriBagDiscounts.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for(RriBagDiscount rriBagDiscount : rriBagDiscounts) {
				rriBagDiscount.setActive(true);
				AuditUtil.addAudit(rriBagDiscount, new Audit(userId, true, currDate));
				toBeSaved.add(rriBagDiscount);
			}
			rriBagDiscountDao.batchSave(toBeSaved);
		}
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo apInvoiceObjectInfo = apInvoiceService.getObjectInfo(ebObjectId, user);
		String title = "";
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		if(apInvoice.getInvoiceTypeId() == InvoiceType.RR_RAW_MAT_TYPE_ID) {
			title = "Receiving Report - Raw Materials IS";
		} else {
			title = "Receiving Report - Raw Materials Palay";
		}
		return ObjectInfo.getInstance(ebObjectId, title + " - " + apInvoice.getSequenceNumber(),
				apInvoiceObjectInfo.getLatestStatus(),apInvoiceObjectInfo.getShortDescription(),
				apInvoiceObjectInfo.getFullDescription(),apInvoiceObjectInfo.getPopupLink(),
				apInvoiceObjectInfo.getPrintOutLink());
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.RR_RAW_MATERIAL_OBJECT_TYPE_ID:
			case APInvoice.RR_NET_WEIGHT_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case RriBagQuantity.RRI_BAG_QUANTITY_OBJECT_TYPE_ID:
				return rriBagQuantityDao.getByEbObjectId(ebObjectId);
			case RReceivingReportRmItem.OBJECT_TYPE_ID:
				return rmItemDao.getByEbObjectId(ebObjectId);
			case RriBagDiscount.RRI_BAG_DISCOUNT_OBJECT_TYPE_ID:
				return rriBagDiscountDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
