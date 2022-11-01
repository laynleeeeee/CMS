package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.Division;

/**
 * Data Access Object of {@link ApPaymentInvoice}

 */
public interface ApPaymentInvoiceDao extends Dao<ApPaymentInvoice>{

	/**
	 * Get the collection of AP Payment Invoices.
	 * @param apInvoiceId The unique AP Invoice id.
	 * @return The collection of AP Payment Invoices.
	 */
	Collection<ApPaymentInvoice> getPaidInvoices (Integer apInvoiceId);

	/**
	 * Get the collection of AP payment invoices.
	 * @param apPaymentId The unique id of the payment.
	 * @return The collection of AP payment invoices.
	 */
	Collection<ApPaymentInvoice> getPaidInvoicesByPaymentId (Integer apPaymentId);

	/**
	 * Compute the total debit of the AP Payment Invoice per account.
	 * <br>Summated amount are greater than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total debit of AP Payment.
	 */
	public double getTotalDebit(int companyId, Date asOfDate, int accountId);

	/**
	 * Compute the total credit of the AP Payment Invoice per account.
	 * <br>Summated amount are lesser than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total credit of AP Payment.
	 */
	public double getTotalCredit(int companyId, Date asOfDate, int accountId);

	/**
	 * Compute the bank clearing total debit of the AP Payment Invoice per account.
	 * <br>Summated amount are greater than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total debit of AP Payment.
	 */
	public double getBCTotalDebit(int companyId, Date asOfDate, int accountId);

	/**
	 * Compute the bank clearing total credit of the AP Payment Invoice per account.
	 * <br>Summated amount are lesser than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total credit of AP Payment.
	 */
	public double getBCTotalCredit(int companyId, Date asOfDate, int accountId);

	/**
	 * Get the list of Bank Clearing AP payment invoices.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @return The list of paid invoices.
	 */
	public List<ApPaymentInvoice> getBCApPaymentInvoices (int companyId, int accountId, 
			Collection<Division> divisions, Date dateFrom, Date dateTo);

	/**
	 * Compute the total amount from AP Payment Invoice per account.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total amount.
	 */
	double totalPerAccount(int companyId, Date asOfDate, int accountId);

	/**
	 * Get the AP Invoice using the invoice number and supplier account.
	 * @param invoiceId The Ap invoice id.
	 * @return The AP Invoice.
	 */
	ApPaymentInvoice getApPaymentCheckBookNumber (int invoiceId);

	/**
	 * Get the list of paid invoices.
	 * @param invoiceId The invoice id.
	 * @param asOfDate The check date of payment.
	 * @return The list of paid invoices.
	 */
	List<ApPaymentInvoice> getPaidInvoices(Integer invoiceId, Date asOfDate);

	/**
	 * Get the collection of AP Payment Invoices excluding in payment id.
	 * @param apInvoiceId The unique AP Invoice id.
	 * @param paymentId The ap payment id.
	 * @return The collection of AP Payment Invoices.
	 */
	Collection<ApPaymentInvoice> getPaidInvoicesExcludeInPaymentId (Integer apInvoiceId, Integer paymentId);
}
