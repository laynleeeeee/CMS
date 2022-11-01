package eulap.eb.service;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.dao.TermDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;

/**
 * A service class that will handle the random population in the database
 * for testing purposes.  

 *
 */
@Service
public class PopulateDateService {
	private static final int CATOGORY_ID = 1;
	private static final int UOM_ID = 1;
	private Logger logger = Logger.getLogger(PopulateDateService.class);
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	
	@Autowired
	private GeneralLedgerService generalLedgerService;
	
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private TermDao termDao;

	/**
	 * Populate items in the database. 
	 * 	This assumes assumes the following data:
	 * 	1. There is an item category id 1 in the database. 
	 *  2. There is a unit of measurement id 1 in the database. 
	 * @param numberOfItem number of item to be populated. 
	 * @return the message. 
	 */
	public String populuteItem (int numberOfItem, User user) {
		Item item = null;
		Date currentDate = new Date();
		ItemCategory ic = new ItemCategory ();
		UUID itemCategoryID = UUID.randomUUID();
		ic.setName(Long.toHexString(itemCategoryID.getMostSignificantBits()));
		setCreator(ic, user, currentDate);
		itemDao.saveOrUpdate(ic);
		
		ItemCategoryAccountSetup accountSetup = new ItemCategoryAccountSetup();
		accountSetup.setItemCategoryId(ic.getId());
		accountSetup.setCompanyId(user.getCompanyId());
		accountSetup.setCostAccount(1);
		accountSetup.setInventoryAccount(1);
		accountSetup.setSalesAccount(1);
		accountSetup.setSalesDiscountAccount(1);
		accountSetup.setSalesReturnAccount(1);
		setCreator(accountSetup, user, currentDate);
		itemDao.saveOrUpdate(accountSetup);

		ic.setActive(true);
		List<Domain> toBeSaved = new ArrayList<>(); 
		for (int counter=0; counter < numberOfItem; counter++) {
			item = new Item();
			item.setItemCategoryId(ic.getId());
			item.setUnitMeasurementId(UOM_ID);
			UUID uuid = UUID.randomUUID();
			String stockCode = Long.toHexString(uuid.getMostSignificantBits());
			item.setStockCode(stockCode);
			item.setDescription(uuid.toString());
			item.setActive(true);
			setCreator(item, user, currentDate);
			toBeSaved.add(item);
			if (toBeSaved.size() > 500) {
				itemDao.batchSaveOrUpdate(new ArrayList<>(toBeSaved));
				createItemSrp(toBeSaved);
				toBeSaved.clear();
			}
		}
		itemDao.batchSaveOrUpdate(new ArrayList<>(toBeSaved));
		createItemSrp(toBeSaved);
		return null;
	}

	private void createItemSrp (List<Domain> items) {
		List<Domain> srps = new ArrayList<>();
		for (Domain item : items) {
			ItemSrp srp = new ItemSrp();
			srp.setCompanyId(1);
			srp.setItemId(item.getId());
			srp.setSrp(10.00);
			srps.add(srp);
		}
		itemDao.batchSave(srps);
	}
	/**
	 * Populate the General Journal entries. This will not follow the proper
	 * accounting for debit and credit
	 */
	public String populateGeneralLedgers (int targetPopulation, User user) {
		logger.info("Populating General Ledger");
		Date timeStart = new Date();
		for (int counter=0; counter < targetPopulation; counter++) {
			GeneralLedger gl = new GeneralLedger();
			Date randomDate = getRandomDate();
			gl.setGlDate(randomDate);
			UUID uuid = UUID.randomUUID();
			String randomString = uuid.toString();
			gl.setComment(randomString);
			setCreator(gl, user, randomDate);
			gl.setGlEntrySourceId(1);
			List<GlEntry> entries = new ArrayList<>();
			GlEntry debitEntry = new GlEntry();
			debitEntry.setAccountCombinationId(1);
			debitEntry.setAmount(1000);
			debitEntry.setDescription("Dummy data : " + randomString);
			debitEntry.setGeneralLedgerId(gl.getId());
			debitEntry.setDebit(true);
			entries.add(debitEntry);
			GlEntry creditEntry = new GlEntry();
			creditEntry.setAccountCombinationId(2);
			creditEntry.setAmount(1000);
			creditEntry.setDescription("Dummy data : " + randomString);
			creditEntry.setGeneralLedgerId(gl.getId());
			entries.add(creditEntry);

			logger.debug("creating gl : " + gl);
			logger.debug("creating gl entries, debit " + debitEntry);
			logger.debug("creating gl entries, credit " + creditEntry);
			//generalLedgerService.saveGeneralLedger(user, gl, entries);
			
			logger.debug("Time Start : "  + timeStart.getTime());
			Date endDate = new Date ();
			logger.debug("Time end : "  +endDate.getTime());
		}
		return null;
	}

	/**
	 * Populate the ap invoie with dummy date. 
	 * @param targetPopulation the target number of population
	 * @param user the current user. 
	 */
	public String populateAPInvoice (int targetPopulation, User user) throws InvalidClassException, ClassNotFoundException {
		logger.info("Populating AP Invoice");
		Supplier supplier = new Supplier ();
		UUID uuid = UUID.randomUUID();
		String supplierName = Long.toHexString(uuid.getMostSignificantBits());
		supplier.setName(supplierName);
		supplier.setAddress(uuid.toString());
		supplier.setBusRegTypeId(1);
		supplier.setServiceLeaseKeyId(1);
		Date randomDate = getRandomDate();
		supplier.setActive(true);
		setCreator(supplier, user, randomDate);
		supplierDao.saveOrUpdate(supplier);
		
		Term term = new Term();
		UUID termUUID = UUID.randomUUID();
		String termName = Long.toHexString(termUUID.getMostSignificantBits());
		term.setName(termName);
		term.setDays(0);
		term.setServiceLeaseKeyId(1);
		term.setActive(true);
		setCreator(term, user, randomDate);
		termDao.saveOrUpdate(term);
		
		SupplierAccount sc = new SupplierAccount();
		sc.setCompanyId(user.getCompanyId());
		sc.setTermId(term.getId());
		sc.setSupplierId(supplier.getId());
		sc.setDefaultCreditACId(2);
		sc.setName(supplierName);
		sc.setActive(true);
		setCreator(sc, user, randomDate);
		supplierAccountDao.saveOrUpdate(sc);

		for (int counter=0; counter < targetPopulation; counter++) {
			UUID invoiceUUID = UUID.randomUUID();
			
			String invoiceRefNumber = Long.toHexString(uuid.getMostSignificantBits());
			APInvoice apInvoice = new APInvoice();
			apInvoice.setInvoiceTypeId(1);
			apInvoice.setInvoiceNumber(invoiceRefNumber);
			apInvoice.setSupplierId(supplier.getId());
			apInvoice.setSupplierAccountId(sc.getId());
			apInvoice.setTermId(term.getId());
			Date date = getRandomDate();
			apInvoice.setGlDate(date);
			apInvoice.setInvoiceDate(date);
			apInvoice.setDueDate(date);
			String description = invoiceUUID.toString();
			apInvoice.setDescription(description);
			Double amount = 1000.00;
			apInvoice.setAmount(amount);
			setCreator(apInvoice, user, date);

			List<APLine> apLines = new ArrayList<>();
			APLine apLine = new APLine();
			apLine.setAccountCombinationId(19);
			apLine.setAmount(amount);
			apLine.setDescription(description);
			apLines.add(apLine);
			apInvoice.setaPlines(apLines);
			List<ReferenceDocument> documents = new ArrayList<>();
			apInvoice.setReferenceDocuments(documents);
			ebFormServiceHandler.saveForm(apInvoice, user);
		}
		return null;
	}
	
	private Date getRandomDate () {
		Random gen = new Random();
		int range = 2*365; //5 years       
		Date date = new Date ();
		return DateUtil.deductDaysToDate(date, gen.nextInt(range));
	}
	private void setCreator (Domain domain, User user, Date date) {
		domain.setCreatedBy(user.getId());
		domain.setCreatedDate(date);
		domain.setUpdatedBy(user.getId());
		domain.setUpdatedDate(date);
	}
}
