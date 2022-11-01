package eulap.eb.service.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemVatTypeDao;
import eulap.eb.dao.PositionDao;
import eulap.eb.dao.TermDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.dao.UserGroupDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemAddOnType;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ItemVatType;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.UserService;

/**
 * A class that handle the data that will be parsed from the reader. 
 * This will the objects from the database.   
 * 

 *
 */
@Service
public class MigrationService {
	private static final String ACCOUNT_RECEIVABLE_NUMBER = "3001030000";
	private static final String ACCOUNT_PAYABLE_NUMBER = "4001010003";
	private static final String SALES_ACCOUNT_NUMBER = "1000000006";
	private static final String SALES_DISCOUNT_ACCOUNT_NUMBER = "1000000005";
	private static final String SALES_RETURN_ACCOUNT_NUMBER = "1000000007";
	private static final String COST_OF_SALE_ACCOUNT_NUMBER = "5010000000";
	private static final String INVENTORY_ACCOUNT_NUBMER = "3001080005";
	private static final String CAPITAL_ACCOUNT_NUBMER = "5002020002";
	private static final String VAT = "VAT";
	private static final String QUANTITY_TYPE = "QUANTITY";
	private static final String PERCENTAGE_TYPE = "PERCENTAGE";
	private static final String TIME_PERIOD_STATUS_OPEN = "Open";
	private static final int PERCENTAGE_TYPE_ID = 1;
	private static final int QUANTITY_TYPE_ID = 3;
	private static final int SERVICE_LEASE_KEY_ID = 1;
	private static final int NON_VAT_ID = 1;
	private static final int VAT_ID = 2;
	private static final int COD_ID = 1;
	private static final int OPEN_STATUS_ID = 2;
	private static final int NEVER_OPENED_STATUS_ID = 1;
	private static final String BEGINNING_BALANCE = "Beginning Balance";
	private static final int DEFAULT_COMPANY_ID = 1;
	private static final int DEFAULT_DIVISION_ID = 1;
	private static final String BUS_CLASS_INDIVIDUAL = "INDIVIDUAL";
	private static final int BUS_CLASS_INDIVIDUAL_ID = 1;
	private static final int BUS_CLASS_CORPORATE_ID = 2;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private TermDao termDao;
	@Autowired
	private ItemCategoryDao itemCategoryDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private PositionDao positionDao;
	@Autowired
	private UserGroupDao userGroupDao;
	@Autowired
	private ItemVatTypeDao itemVatTypeDao;

	/**
	 * Migrate excel data to database.
	 * @param strFile The location of the file. 
	 * @param writer the output stream writer that handles the progress of the reading and saving.
	 */
	public void migrateData (String strFile, OutputStreamWriter writer) throws IOException, InvalidFormatException {
		ExcelMigrationReader reader = new ExcelMigrationReader(new CBSMigrationDataHandler(this, writer));
		File file = new File(strFile);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			reader.readFile(fis);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	private static class CBSMigrationDataHandler implements MigrationDataHandler{
		private HashMap<String, ItemCategory> name2ItemCategory;
		private HashMap<String, UnitMeasurement> name2UnitOfMeasurement;
		private Company company;
		private Division division;
		private List<AccountType> accountTypes;
		private List<AccountCombination> accounts;
		private List<Position> positions;
		private List<UserGroup> userGroups;
		private int rowCount;
		private Date date;
		private int createdId;
		private ArCustomer customer;
		private ArCustomerAccount customerAccount;
		private ArLineSetup customerBeginningBalance;
		private User adminUser;
		private MigrationService migrationService;
		private Supplier supplier;
		private SupplierAccount supplierAccount;
		private ItemDiscountType percentageDiscount;
		private ItemDiscountType quantityDiscount;
		private ItemAddOnType percentageAddon;
		private ItemAddOnType quantityAddon;
		private OutputStreamWriter writer;
		private List<Term> terms;
		private int savedata;

		private CBSMigrationDataHandler (MigrationService migrationService, OutputStreamWriter writer) {
			this.migrationService = migrationService;
			this.writer = writer;
		}

		@Override
		public void initDataHandler() {
			try {
				// Admin
				adminUser = new User();
				savedata = 0;
				writer.write("Initializing all the needed objects" + System.lineSeparator());
				date = new Date();
				createdId = 1;
				adminUser.setId(createdId);
				adminUser.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);

				percentageDiscount = new ItemDiscountType();
				percentageDiscount.setName(PERCENTAGE_TYPE);
				percentageDiscount.setId(PERCENTAGE_TYPE_ID);

				quantityDiscount = new ItemDiscountType();
				quantityDiscount.setName(QUANTITY_TYPE);
				quantityDiscount.setId(QUANTITY_TYPE_ID);

				percentageAddon = new ItemAddOnType();
				percentageAddon.setName(PERCENTAGE_TYPE);
				percentageAddon.setId(PERCENTAGE_TYPE_ID);

				quantityAddon = new ItemAddOnType();
				quantityAddon.setName(QUANTITY_TYPE);
				quantityAddon.setId(QUANTITY_TYPE_ID);

				writer.write("Successfully saved the needed objects" + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void beginCompany() {
			// Do nothing.
		}

		@Override
		public void handleParsedCompany(Company company) {
			// Do nothing for now. 
			// The creation of the company will be in the create script.
			this.company = migrationService.companyDao.get(1);
		}

		@Override
		public void endCompany() {
			// Do nothing
		}

		@Override
		public void beginDivision() {
			// Do nothing
		}

		@Override
		public void handleParsedDivision(Division division) {
			division.setActive(true);
			division.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			saveOrUpdate(division);
			this.division = division;
			try {
				writer.write("Successfully saved division : " + division + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endDivision() {
			// Do nothing
		}

		@Override
		public void beginAccountType() {
			accountTypes = new ArrayList<AccountType>();
		}

		@Override
		public void handleParsedAccountType(AccountType accountType) {
			accountType.setName(accountType.getName().trim());
			accountType.setActive(true);
			accountType.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			saveOrUpdate(accountType);
			accountTypes.add(accountType);
			try {
				writer.write("Successfully saved account type : " + accountType + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endAccountType() {
			// Do nothing
		}

		@Override
		public void beginAccount() {
			accounts = new ArrayList<AccountCombination>();
		}
		
		private AccountType getAccountType (String name) {
			for (AccountType at : accountTypes) {
				if (at.getName().equalsIgnoreCase(name))
					return at;
			}
			throw new RuntimeException("Account type cannot be found : " + name);
		}

		@Override
		public void handleParsedAccount(Account account) {
			account.setAccountName(account.getAccountName().trim());
			account.setNumber(account.getNumber().trim());
			account.setDescription(account.getDescription().trim());
			account.setActive(true);
			AccountType at = getAccountType(account.getAccountTypeName().trim());
			account.setAccountTypeId(at.getId());
			account.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			saveOrUpdate(account);

			// Save account combination.
			AccountCombination ac = new AccountCombination();
			ac.setCompanyId(company.getId());
			ac.setDivisionId(division.getId());
			ac.setAccountId(account.getId());
			ac.setAccount(account);
			ac.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			ac.setActive(true);
			saveOrUpdate(ac);
			saveIfReferenceIsNeeded(ac);

			try {
				writer.write("Successfully saved account : " + account + System.lineSeparator());
				writer.write("Successfully saved account combination: " + ac + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void saveIfReferenceIsNeeded (AccountCombination ac) {
			String accountNumber = ac.getAccount().getNumber();
			switch (accountNumber) {
			case MigrationService.ACCOUNT_PAYABLE_NUMBER:
			case MigrationService.ACCOUNT_RECEIVABLE_NUMBER:
			case MigrationService.COST_OF_SALE_ACCOUNT_NUMBER:
			case MigrationService.INVENTORY_ACCOUNT_NUBMER:
			case MigrationService.SALES_ACCOUNT_NUMBER:
			case MigrationService.SALES_DISCOUNT_ACCOUNT_NUMBER:
			case MigrationService.SALES_RETURN_ACCOUNT_NUMBER:
				accounts.add(ac);
				break;
			case CAPITAL_ACCOUNT_NUBMER:
				customerBeginningBalance = new ArLineSetup();
				customerBeginningBalance.setAccountCombinationId(ac.getId());
				customerBeginningBalance.setName(BEGINNING_BALANCE);
				customerBeginningBalance.setActive(true);
				customerBeginningBalance.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
				saveOrUpdate(customerBeginningBalance);
				break;
			}
		}

		@Override
		public void endAccount() {
			accountTypes = null;
		}

		@Override
		public void beginUserPosition() {
			positions = new ArrayList<Position>();
		}

		@Override
		public void handleParsedUserPosition(Position position) {
			String positionName = position.getName();
			if (positionName == null) {
				return;
			}
			Position savedPosition = migrationService.positionDao.getPositionByName(positionName);
			if (savedPosition == null) {
				position.setName(positionName.trim());
				position.setActive(true);
				saveOrUpdate(position);
			} else {
				position = savedPosition;
				savedPosition = null;
			}
			positions.add(position);
			try {
				writer.write("Successfully saved position : " + position + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endUserPosition() {
			// Do nothing
		}

		@Override
		public void beginUserGroup() {
			userGroups = new ArrayList<UserGroup>();
		}

		@Override
		public void handleParsedUserGroup(UserGroup userGroup) {
			String userGroupName = userGroup.getName();
			if (userGroupName == null) {
				return;
			}
			UserGroup savedUserGroup = migrationService.userGroupDao.getUserGroupByName(userGroupName);
			if (savedUserGroup == null) {
				userGroup.setName(userGroupName.trim());
				userGroup.setDescription(userGroup.getDescription().trim());
				userGroup.setActive(true);
				saveOrUpdate(userGroup);
			} else {
				userGroup = savedUserGroup;
				savedUserGroup = null;
			}
			userGroups.add(userGroup);
			try {
				writer.write("Successfully saved user group : " + userGroup + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endUserGroup() {
			// Do nothing
		}

		@Override
		public void beginUser() {
			// Do nothing
		}

		private Position getPosition (String name) {
			for (Position p : positions) {
				if (p.getName().equalsIgnoreCase(name))
					return p;
			}
			throw new RuntimeException("Unknown position name :" + name);
		}

		private UserGroup getUserGroup (String name) {
			for (UserGroup g : userGroups) {
				if (g.getName().equalsIgnoreCase(name)) {
					return g;
				}
			}
			throw new RuntimeException("Unknown user group : " + name);
		}

		@Override
		public void handleParsedUser(User user) {
			if(user.getUsername() == null) {
				return;
			}
			user.setUsername(user.getUsername().trim());
			user.setFirstName(user.getFirstName().trim());
			if (user.getMiddleName() != null) {
				user.setMiddleName(user.getMiddleName().trim());
			}
			user.setLastName(user.getLastName().trim());
			user.setAddress(user.getAddress().trim());
			user.setContactNumber(user.getContactNumber().trim());
			String emailAddress = user.getEmailAddress();
			user.setEmailAddress(emailAddress != null ? emailAddress.trim() : null);
			Position p = getPosition(user.getPosition().getName().trim());
			user.setPositionId(p.getId());
			UserGroup ug = getUserGroup(user.getUserGroup().getName().trim());
			user.setUserGroupId(ug.getId());
			user.setActive(true);
			user.setCompanyId(DEFAULT_COMPANY_ID);
			user.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			try {
				writer.write("Successfully saved user : " + user + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			try {
				migrationService.userService.saveUser(user, adminUser);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void beginCustomer() {
			// Do nothing
		}

		@Override
		public void handleParsedCustomer(ArCustomer customer) {
			if (customer.getName() == null && customer.getFirstName() == null
					&& customer.getLastName() == null) {
				return;
			}
			BusinessClassification bc = customer.getBusinessClassification();
			int typeId = bc.getName().trim().equalsIgnoreCase(BUS_CLASS_INDIVIDUAL)
					? BUS_CLASS_INDIVIDUAL_ID : BUS_CLASS_CORPORATE_ID;
			customer.setBussinessClassificationId(typeId);
			String name = customer.getName();
			if (typeId == BUS_CLASS_INDIVIDUAL_ID) {
				String firstName = customer.getFirstName().trim();
				customer.setFirstName(firstName);
				String middleName = customer.getMiddleName();
				boolean hasMiddleName = middleName != null && !middleName.trim().isEmpty();
				if (hasMiddleName) {
					customer.setMiddleName(middleName.trim());
				}
				String lastName = customer.getLastName().trim();
				customer.setLastName(lastName);
				name = lastName + ", " + firstName + " " + (hasMiddleName ? middleName.trim().charAt(0) : "");
			}
			customer.setName(name.trim());
			customer.setStreetBrgy(customer.getStreetBrgy().trim());
			customer.setCityProvince(customer.getCityProvince().trim());
			String tin = customer.getTin();
			customer.setTin(tin != null && !tin.isEmpty() ? tin.trim() : null);
			String contactPerson = customer.getContactPerson();
			customer.setContactPerson(contactPerson != null && !contactPerson.isEmpty() ? contactPerson.trim() : null);
			String contactNo = customer.getContactNumber();
			customer.setContactNumber(contactNo != null && !contactNo.isEmpty() ? contactNo.trim() : null);
			String emailAdd = customer.getEmailAddress();
			customer.setEmailAddress(emailAdd != null && !emailAdd.isEmpty() ? emailAdd.trim() : null);
			customer.setActive(true);
			customer.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			saveOrUpdate(customer);
			try {
				writer.write("Successfully saved customer : " + customer + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.customer = customer;
		}

		@Override
		public void beginCustomerAccount() {
			// Do nothing
		}

		@Override
		public void handleParsedCustomerAccount(ArCustomerAccount customerAccount) {
			if (customerAccount.getName() == null || customerAccount.getName().trim().isEmpty()) {
				return;
			}
			customerAccount.setArCustomerId(customer.getId());
			customerAccount.setActive(true);
			customerAccount.setCompanyId(DEFAULT_COMPANY_ID);
			Term term = getTerm(customerAccount.getTerm().getName());
			customerAccount.setTermId(term.getId());
			customerAccount.setName(customer.getId()+" "+customerAccount.getName());
			AccountCombination ac = getCombination(ACCOUNT_RECEIVABLE_NUMBER);
			customerAccount.setDefaultDebitACId(ac.getId());
			saveOrUpdate(customerAccount);
			try {
				writer.write("Successfully saved customer account : " + customerAccount + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.customerAccount = customerAccount;
		}

		private AccountCombination getCombination(String accountNumber) {
			if (accounts == null || accounts.isEmpty()) {
				accounts = migrationService.accountCombinationDao.getAccountCombinations(
						DEFAULT_COMPANY_ID, DEFAULT_DIVISION_ID, null, null);
			}
			for (AccountCombination ac : accounts) {
				if (ac.getAccount().getNumber().equals(accountNumber)){
					return ac;
				}
			}
			throw new RuntimeException("Unknown account number : " + accountNumber);
		}

		@Override
		public void endCustomerAccount() {
			customerAccount = null;
		}

		@Override
		public void beginCustomerBalance() {
			// do nothing
			
		}

		@Override
		public void handleParsedCustomerBalance(ArTransaction arTransaction) {
			if (arTransaction.getAmount() == null || arTransaction.getAmount() == 0)
				return;

			arTransaction.setCustomerAcctId(customerAccount.getId());
			arTransaction.setCustomerId(customer.getId());
			arTransaction.setCompanyId(DEFAULT_COMPANY_ID);
			arTransaction.setDescription(BEGINNING_BALANCE);
			arTransaction.setTermId(COD_ID); //COD
			arTransaction.setDueDate(date);
			arTransaction.setGlDate(date);
			arTransaction.setTransactionDate(date);
			arTransaction.setTransactionNumber("Auto-Generated");
			arTransaction.setTransactionTypeId(ArTransactionType.TYPE_REGULAR_TRANSACTION);
			arTransaction.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);

			ArLine arLine = new ArLine();
			arLine.setAmount(arTransaction.getAmount());
			arLine.setArLineSetupId(customerBeginningBalance.getId());

			List<ArLine> arLines = new ArrayList<ArLine>();
			arLines.add(arLine);
			arTransaction.setArLines(arLines);
			try {
				migrationService.arTransactionService.saveArTransaction(adminUser, arTransaction);
				savedata++;
				writer.write("Successfully saved customer beginning balance : " + arTransaction + System.lineSeparator());
				writer.flush();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private Term getTerm (String name) {
			for (Term t : terms) {
				if (t.getName().equalsIgnoreCase(name)) {
					return t;
				}
			}
			throw new RuntimeException("Unknown term : " + name);
		}

		@Override
		public void endCustomerBalance() {
			// Do nothing
		}

		@Override
		public void endCustomer() {
			customer = null;
		}

		@Override
		public void endUser() {
			positions = null;
			userGroups = null;
		}

		@Override
		public void beginTerm() {
			terms = new ArrayList<Term>();
		}

		@Override
		public void handleParsedTerm(Term term) {
			String termName = term.getName();
			if (termName == null) {
				return;
			}
			Term savedTerm = migrationService.termDao.getTermByName(termName);
			if (savedTerm == null) {
				term.setName(term.getName().trim());
				term.setActive(true);
				term.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
				saveOrUpdate(term);
			} else {
				term = savedTerm;
				savedTerm = null;
			}
			terms.add(term);
			try {
				writer.write("Successfully saved term : " + term + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endTerm() {
			// do nothing
		}

		@Override
		public void beginItem() {
			rowCount = 0;
			name2ItemCategory = new HashMap<String, ItemCategory>();
			name2UnitOfMeasurement = new HashMap<String, UnitMeasurement>();
		}

		@Override
		public void beginTimePeriod() {
			// do nothing
		}

		@Override
		public void handleParsedTimePeriod(TimePeriod timePeriod) {
			if(timePeriod.getName() == null) {
				return;
			}
			String status = timePeriod.getPeriodStatus().getName().trim();
			int timePeriodStatusId = status.equalsIgnoreCase(TIME_PERIOD_STATUS_OPEN)
					? OPEN_STATUS_ID : NEVER_OPENED_STATUS_ID;
			timePeriod.setPeriodStatusId(timePeriodStatusId);
			timePeriod.setName(timePeriod.getName().trim());
			saveOrUpdate(timePeriod);
			try {
				writer.write("Successfully saved time period : " + timePeriod + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endTimePeriod() {
			// Do nothing
		}

		@Override
		public void handleParsedItem(Item item) {
			if(item.getStockCode() == null) {
				return;
			}
			rowCount++;
			ItemCategory ic = item.getItemCategory();
			StringBuilder strBuilder = new StringBuilder();
			String icName = ic.getName();
			if (!name2ItemCategory.containsKey(icName)) {
				ItemCategory savedCategory = migrationService.itemCategoryDao.getItemCategoryByName(icName, DEFAULT_COMPANY_ID);
				if (savedCategory == null) {
					ic.setActive(true);
					saveOrUpdate(ic);

					// Save item category account
					ItemCategoryAccountSetup accountSetup = new ItemCategoryAccountSetup();
					accountSetup.setCompanyId(DEFAULT_COMPANY_ID);
					AccountCombination costAccount = getCombination(COST_OF_SALE_ACCOUNT_NUMBER);
					AccountCombination  mAccount = getCombination(INVENTORY_ACCOUNT_NUBMER);
					AccountCombination salesAccount = getCombination(SALES_ACCOUNT_NUMBER);
					AccountCombination salesDiscount = getCombination(SALES_DISCOUNT_ACCOUNT_NUMBER);
					AccountCombination salesReturn = getCombination(SALES_RETURN_ACCOUNT_NUMBER);

					accountSetup.setCostAccount(costAccount.getId());
					accountSetup.setInventoryAccount(mAccount.getId());
					accountSetup.setSalesAccount(salesAccount.getId());
					accountSetup.setSalesDiscountAccount(salesDiscount.getId());
					accountSetup.setSalesReturnAccount(salesReturn.getId());
					accountSetup.setItemCategoryId(ic.getId());
					accountSetup.setActive(true);
					saveOrUpdate(accountSetup);
					strBuilder.append("created new item category :" + ic + System.lineSeparator());
					strBuilder.append("created inventory account setup :" + accountSetup + System.lineSeparator());
				} else {
					ic = savedCategory;
					savedCategory = null;
				}
				name2ItemCategory.put(ic.getName(), ic);
			} else {
				ic = name2ItemCategory.get(ic.getName());
			}
			UnitMeasurement um = item.getUnitMeasurement();
			String uomName = um.getName();
			if (!name2UnitOfMeasurement.containsKey(uomName)) {
				UnitMeasurement savedUom = migrationService.uomDao.getUMByName(uomName);
				if (savedUom == null) {
					um.setName(uomName.trim());
					um.setActive(true);
					migrationService.itemDao.saveOrUpdate(um);
				} else {
					um = savedUom;
					savedUom = null;
				}
				name2UnitOfMeasurement.put(um.getName(), um);
				strBuilder.append("created new unit of measurement :" + ic + System.lineSeparator());
			} else {
				um = name2UnitOfMeasurement.get(um.getName());
			}

			item.setStockCode(checkAndProcessString(item.getStockCode(), 150));
			item.setDescription(checkAndProcessString(item.getDescription(), 300));
			String partNumber = item.getManufacturerPartNo();
			item.setManufacturerPartNo(partNumber != null ? partNumber.trim() : null);
			item.setItemCategoryId(ic.getId());
			item.setUnitMeasurementId(um.getId());
			item.setActive(true);
			ItemVatType itemVatType = item.getItemVatType();
			int itemVatTypeId = migrationService.itemVatTypeDao.getItemVatTypeByName(itemVatType.getName().trim()).getId();
			itemVatType = null;
			item.setItemVatTypeId(itemVatTypeId);
			saveOrUpdate(item);
			strBuilder.append("created new item :" + item + System.lineSeparator());

			// Saving selling price
			List<ItemSrp> srps = item.getItemSrps();
			for (ItemSrp srp : srps) {
				srp.setActive(true);
				srp.setItemId(item.getId());
				srp.setCompanyId(DEFAULT_COMPANY_ID);
				srp.setDivisionId(DEFAULT_DIVISION_ID);
				saveOrUpdate(srp);
				strBuilder.append("created new selling price :" + srp + System.lineSeparator());
			}

			// saving Discounts
			List<ItemDiscount> itemDiscounts = item.getItemDiscounts();
			for (ItemDiscount itemDiscount : itemDiscounts) {
				itemDiscount.setName(checkAndProcessString(itemDiscount.getName(), 50));
				itemDiscount.setItemId(item.getId());
				itemDiscount.setActive(true);
				String discountType = itemDiscount.getItemDiscountType().getName();
				int discountTypeId = discountType.equalsIgnoreCase(percentageDiscount.getName())
						? PERCENTAGE_TYPE_ID : QUANTITY_TYPE_ID;
				itemDiscount.setItemDiscountTypeId(discountTypeId);
				itemDiscount.setItemId(item.getId());
				itemDiscount.setCompanyId(DEFAULT_COMPANY_ID);
				itemDiscount.setValue(Math.abs(itemDiscount.getValue())); // Absolute value
				saveOrUpdate(itemDiscount);
				strBuilder.append("created new item discount :" + itemDiscount + System.lineSeparator());
			}

			for (ItemAddOn addOn : item.getItemAddOns()) {
				addOn.setName(checkAndProcessString(addOn.getName(), 50));
				addOn.setItemId(item.getId());
				addOn.setCompanyId(DEFAULT_COMPANY_ID);
				addOn.setActive(true);
				String addOnType = addOn.getItemAddOnType().getName();
				int addOnTypeId = addOnType.equalsIgnoreCase(percentageAddon.getName())
						? PERCENTAGE_TYPE_ID : QUANTITY_TYPE_ID;
				addOn.setItemAddOnTypeId(addOnTypeId);
				saveOrUpdate(addOn);
				strBuilder.append("created new add on :" + addOn + System.lineSeparator());
			}
			try {
				writer.write(strBuilder.toString());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endItem() {
			System.out.println("total row count =========" + rowCount);
			name2ItemCategory = null;
			name2UnitOfMeasurement = null;
		}

		/**
		 * Checks the length of the string is more than its limit.
		 * @param string The String to be checked.
		 * @param limit The limit of the string to be saved.
		 * @return The processed string.
		 */
		private String checkAndProcessString(String string, int limit) {
			String processedString = string.trim();
			if(processedString.length() > limit) {
				return processedString.substring(0, (limit-1));
			}
			return processedString;
		}

		@Override
		public void beginSupplier() {
			// Do nothing
		}

		@Override
		public void handleParsedSupplier(Supplier supplier) {
			if (supplier.getName() == null && supplier.getFirstName() == null
					&& supplier.getLastName() == null) {
				return;
			}
			String busReg = supplier.getBusRegType().getName().trim();
			supplier.setBusRegTypeId(busReg.equalsIgnoreCase(VAT) ? VAT_ID : NON_VAT_ID);
			supplier.setActive(true);
			BusinessClassification bc = supplier.getBusinessClassification();
			int typeId = bc.getName().trim().equalsIgnoreCase(BUS_CLASS_INDIVIDUAL)
					? BUS_CLASS_INDIVIDUAL_ID : BUS_CLASS_CORPORATE_ID;
			supplier.setBussinessClassificationId(typeId);
			String name = supplier.getName();
			if (typeId == BUS_CLASS_INDIVIDUAL_ID) {
				String firstName = supplier.getFirstName().trim();
				supplier.setFirstName(firstName);
				String middleName = supplier.getMiddleName();
				boolean hasMiddleName = middleName != null && !middleName.trim().isEmpty();
				if (hasMiddleName) {
					supplier.setMiddleName(middleName.trim());
				}
				String lastName = supplier.getLastName().trim();
				supplier.setLastName(lastName);
				name = lastName + ", " + firstName + " " + (hasMiddleName ? middleName.trim().charAt(0) : "");
			}
			supplier.setName(name.trim());
			supplier.setStreetBrgy(supplier.getStreetBrgy().trim());
			supplier.setCityProvince(supplier.getCityProvince().trim());
			supplier.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			String tin = supplier.getTin();
			supplier.setTin(tin != null ? supplier.getTin().trim() : null);
			String contactPerson = supplier.getContactPerson();
			supplier.setContactPerson(contactPerson != null ? contactPerson.trim() : null);
			String contactNo = supplier.getContactNumber();
			supplier.setContactNumber(contactNo != null ? contactNo.trim() : null);
			saveOrUpdate(supplier);
			this.supplier = supplier;
			try {
				writer.write("Successfully saved supplier : " + supplier + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void beginSupplierAccount() {
			// Do nothing
		}

		@Override
		public void handleParsedSupplierAccount(SupplierAccount supplierAccount) {
			if (supplierAccount.getName() == null || supplierAccount.getName().trim().isEmpty()) {
				return;
			}
			AccountCombination ap = getCombination(ACCOUNT_PAYABLE_NUMBER);
			supplierAccount.setDefaultCreditACId(ap.getId());
			supplierAccount.setCompanyId(DEFAULT_COMPANY_ID);
			supplierAccount.setSupplierId(supplier.getId());
			supplierAccount.setName(supplier.getId()+" "+supplierAccount.getName().trim());
			Term term = getTerm(supplierAccount.getTerm().getName());
			supplierAccount.setTermId(term.getId());
			supplierAccount.setActive(true);
			saveOrUpdate(supplierAccount);
			this.supplierAccount = supplierAccount;
			try {
				writer.write("Successfully saved supplier account : " + supplier + System.lineSeparator());
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void endSupplierAccount() {
			// Do nothing
		}

		@Override
		public void endSupplier() {
			this.supplier = null;
		}

		@Override
		public void beginSupplierBeginingBalance() {
			// Do nothing
		}

		@Override
		public void endSupplierBeginningBalance() {
			// Do nothing
		}

		@Override
		public void handleParsedSupplierBeginningBalance(APInvoice apInvoice) {
			if(apInvoice.getAmount() == 0) {
				return;
			}
			apInvoice.setSupplierId(supplier.getId());
			apInvoice.setSupplierAccountId(supplierAccount.getId());
			apInvoice.setServiceLeaseKeyId(SERVICE_LEASE_KEY_ID);
			apInvoice.setInvoiceTypeId(InvoiceType.REGULAR_TYPE_ID);
			apInvoice.setDescription(BEGINNING_BALANCE);
			apInvoice.setInvoiceNumber("Auto-Generated");
			apInvoice.setTermId(COD_ID); //COD
			apInvoice.setInvoiceDate(date);
			apInvoice.setGlDate(date);
			apInvoice.setDueDate(date);

			APLine apLine = new APLine();
			apLine.setAccountCombinationId(getCombination(ACCOUNT_PAYABLE_NUMBER).getId());
			apLine.setDescription(BEGINNING_BALANCE);
			apLine.setAmount(apInvoice.getAmount());
			List<APLine> apLines = new ArrayList<APLine>();
			apLines.add(apLine);
			apInvoice.setaPlines(apLines);
			try {
				String workflowName = APInvoice.class.getSimpleName() + InvoiceType.REGULAR_TYPE_ID;
				migrationService.apInvoiceService.saveInvoice(adminUser, apInvoice, workflowName);
				savedata++;
				writer.write("Successfully saved supplier beginning balance "+apInvoice);
				writer.flush();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void saveOrUpdate (Domain d) {
			d.setCreatedBy(createdId);
			d.setCreatedDate(date);
			d.setUpdatedBy(createdId);
			d.setUpdatedDate(date);
			migrationService.companyDao.saveOrUpdate(d);
			savedata++;
		}

		@Override
		public void end() {
			try {
				writer.append("Successully completed reading and saving data to database. " + System.lineSeparator());
				writer.append("total created data : " + savedata);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
