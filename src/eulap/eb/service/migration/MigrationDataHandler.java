package eulap.eb.service.migration;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;

/**
 * Interface for the Migration Data Handler.

 *
 */
public interface MigrationDataHandler {

	/**
	 * Initialize this data handler.
	 */
	void initDataHandler();

	/**
	 * Begin the parsing of item. 
	 */
	void beginItem();

	/**
	 * Handle the parsed item from the reader. 
	 * @param item the parsed item. 
	 */
	void handleParsedItem (Item item);

	/**
	 * Called when the parsing of item is done.
	 */
	void endItem();

	/**
	 * Begin the parsing of supplier. 
	 */
	void beginSupplier();

	/**
	 * Handle the parsed supplier from the reader. 
	 * @param supplier the parsed supplier. 
	 */
	void handleParsedSupplier (Supplier supplier);

	/**
	 * Called when the parsing of supplier is done.
	 */
	void endSupplier();

	/**
	 * Supplier account was identified and will begin parsing the fields.
	 */
	void beginSupplierAccount();
	
	/**
	 * Handle the parsed supplier account. 
	 */
	void handleParsedSupplierAccount(SupplierAccount supplierAccount);

	/**
	 * End of the supplier account. 
	 */
	void endSupplierAccount();

	/**
	 * Start the parsing for the beginning balance of Supplier.
	 */
	void beginSupplierBeginingBalance();

	/**
	 * Handle the parsed Begining Balance of Supplier.
	 */
	void handleParsedSupplierBeginningBalance(APInvoice apInvoice);

	/**
	 * End the parsing of the Beginning Balance of Supplier.
	 */
	void endSupplierBeginningBalance();

	/**
	 * Begin the parsing of company. 
	 */
	void beginCompany();

	/**
	 * Handle the parsed company from the reader. 
	 * @param company the parsed company. 
	 */
	void handleParsedCompany (Company company);

	/**
	 * Called when the parsing of company is done.
	 */
	void endCompany();

	/**
	 * Begin parsing division from the migration sheet.
	 */
	void beginDivision();

	/**
	 * Handle the parsed Division from the reader.
	 * @param division The parsed division object.
	 */
	void handleParsedDivision(Division division);

	/**
	 * End the parsing of division.
	 */
	void endDivision();

	/**
	 * Begin parsing Account Types from the Migration Sheet.
	 */
	void beginAccountType();

	/**
	 * Handle the parsed Account Types from the reader.
	 * @param accountType The parsed Account Type.
	 */
	void handleParsedAccountType(AccountType accountType);

	/**
	 * End the parsing of Account Type
	 */
	void endAccountType();

	/**
	 * Begin the parsing of Accounts from the Migration Sheet.
	 */
	void beginAccount();

	/**
	 * Handle the parsed Account from the reader.
	 * @param account The Account parsed.
	 */
	void handleParsedAccount(Account account);

	/**
	 * End the parsing of Account.
	 */
	void endAccount();

	/**
	 * Begin parsing of the User Position from the Migration Sheet.
	 */
	void beginUserPosition();

	/**
	 * Handle the parsing of Position from the reader.
	 * @param position The parsed position.
	 */
	void handleParsedUserPosition(Position position);

	/**
	 * End the parsing of Position.
	 */
	void endUserPosition();

	/**
	 * Begin the parsing of User Group.
	 */
	void beginUserGroup();

	/**
	 * Handle the parsed User Group from the reader.
	 * @param userGroup The parsed User Group.
	 */
	void handleParsedUserGroup(UserGroup userGroup);

	/**
	 * End the parsing of User Group.
	 */
	void endUserGroup();

	/**
	 * Begin parsing of User in the Migration Sheet.
	 */
	void beginUser();

	/**
	 * Handle the parsing of User from the reader.
	 * @param user The parsed User.
	 */
	void handleParsedUser(User user);

	/**
	 * End the parsing of User.
	 */
	void endUser();

	/**
	 * Begin parsing of Terms in the Migration Sheet.
	 */
	void beginTerm();

	/**
	 * Handle the parsing of Term from the reader.
	 * @param term The parsed Term.
	 */
	void handleParsedTerm(Term term);

	/**
	 * End the parsing of Term
	 */
	void endTerm();

	/**
	 * Begin parsing of Time Period in Migration Sheet.
	 */
	void beginTimePeriod();

	/**
	 * Handle the parsing of Time Period from the reader.
	 * @param timePeriod The parsed Time Period.
	 */
	void handleParsedTimePeriod(TimePeriod timePeriod);

	/**
	 * End the parsing of Time Period.
	 */
	void endTimePeriod();

	/**
	 * Begin the parsing of Customer and Customer Account.
	 */
	void beginCustomer();

	/**
	 * Handle the parsing of Customer with the Customer Account.
	 * @param arCustomerAccount The AR Customer Account parsed.
	 */
	void handleParsedCustomer(ArCustomer customer);

	/**
	 * End the parsing of the AR Customer with Customer Account.
	 */
	void endCustomer();
	
	/**
	 * Calls everything there is a new customer account is identified.
	 */
	void beginCustomerAccount ();
	/**
	 * Handle the customer account parsed data.
	 * @param customerAccount The parsed data.
	 */
	void handleParsedCustomerAccount (ArCustomerAccount customerAccount);
	
	/**
	 * End of the customer account.
	 */
	void endCustomerAccount ();
	
	/**
	 * A new customer balances is identified.
	 */
	void beginCustomerBalance();

	/**
	 * Handle the parsed customer balance data.
	 */
	void handleParsedCustomerBalance (ArTransaction arTransaction);

	/**
	 * end of customer balances
	 */
	void endCustomerBalance();
	
	/**
	 * Calls when parsing is done. 
	 */
	void end();
}
