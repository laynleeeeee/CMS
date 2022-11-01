package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.dao.GlEntryDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.GlStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.GeneralLedgerDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Service for General Ledger Entry.

 *
 */
@Service
public class GlEntryService {
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private GeneralLedgerDao generalLedgerDao;
	@Autowired
	private GlEntryDao glEntryDao;
	@Autowired
	private FormWorkflowDao formWorkflowDao;
	@Autowired
	private FormWorkflowLogDao formWorkflowLogDao;

	/**
	 * Get all the companies found in the account combination.
	 * @param isActiveOnly True if only the active companies are to be retrieved, 
	 * otherwise false.
	 * @param user The logged user.
	 * @return The companies.
	 */
	public Collection<Company> getCompanies (boolean isActiveOnly, User user) {
		return accountCombinationDao.getCompanyByAcctCombination(isActiveOnly, user);
	}

	/**
	 * Get all the divisions found in the account combination.
	 * @param companyId The company id.
	 * @return The divisions.
	 */
	public Collection<Division> getDivisions (int companyId) {
		return divisionDao.getDivisionByAcctCombination(companyId);
	}

	/**
	 * Get all the accounts found in the account combination.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The accounts.
	 */
	public Collection<Account> getAccounts (int companyId, int divisionId) {
		return accountService.getAccounts(companyId, divisionId);
	}

	/**
	 * Convert the json array to collection of gl entries.
	 * @param jsonGlEntry
	 * @return The collection of converted gl entries.
	 */
	public Collection<GlEntry> convertToGlEntry (JSONArray jsonGlEntry) {
		Collection<GlEntry> glEntries = new ArrayList<GlEntry>();
		for (int i=0; i<jsonGlEntry.size(); i++) {
			JSONArray glArrEntry = jsonGlEntry.getJSONArray(i);
			GlEntry glEntry = new GlEntry();
			int companyId = 0, divisionId = 0, accountId = 0;
			for (int j=0; j<glArrEntry.size(); j++) {
				JSONObject jsonObj = glArrEntry.getJSONObject(j);
				if (jsonObj.containsKey("glEntryId")) {
					if (!jsonObj.get("glEntryId").toString().trim().isEmpty())
						glEntry.setId(Integer.valueOf(jsonObj.get("glEntryId").toString()));
					else
						glEntry.setId(0);
				} else if (jsonObj.containsKey("companyId")) {
					String strCId = jsonObj.get("companyId").toString();
					companyId = !strCId.trim().equals("") ? Integer.valueOf(strCId) : 0;
					glEntry.setCompanyId(companyId);
				} else if (jsonObj.containsKey("divisionId")) {
					String strDId = jsonObj.get("divisionId").toString();
					divisionId = !strDId.trim().equals("") ? Integer.valueOf(strDId) : 0;
				} else if (jsonObj.containsKey("accountId")) {
					String strAId = jsonObj.get("accountId").toString();
					accountId = !strAId.trim().equals("") ? Integer.valueOf(strAId) : 0;
				} else if (jsonObj.containsKey("debit")) {
					double amtDebit = Double.valueOf(jsonObj.get("debit").toString());
					if (amtDebit != 0.0) {
						glEntry.setDebitAmount(NumberFormatUtil.roundOffTo2DecPlaces(amtDebit));
						glEntry.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amtDebit));
						glEntry.setDebit(true);
					}
				} else if (jsonObj.containsKey("credit")) {
					double amtCredit = Double.valueOf(jsonObj.get("credit").toString());
					if (amtCredit != 0.0) {
						glEntry.setCreditAmount(NumberFormatUtil.roundOffTo2DecPlaces(amtCredit));
						glEntry.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amtCredit));
						glEntry.setDebit(false);
					}
				} else if (jsonObj.containsKey("description"))
					glEntry.setDescription(jsonObj.get("description").toString());
				if (companyId !=0 && divisionId !=0 && accountId !=0)
					glEntry.setAccountCombinationId(accountCombinationDao.getAccountCombination(companyId, 
							divisionId, accountId).getId());
				else
					glEntry.setAccountCombinationId(0);
			}
			glEntries.add(glEntry);
		}
		return glEntries;
	}

	/**
	 * Convert the gl entries into json string to be reloaded in the jsp.
	 * @param glEntries The gl entries.
	 * @return The json string.
	 */
	public String convertToJsonString (Collection<GlEntry> glEntries) {
		String jsonString = "[";
		int ctr = 0;
		for (GlEntry gl : glEntries) {
			if (ctr > 0)
				jsonString += ", ";

			Company company = gl.getAccountCombination().getCompany();
			Division division = gl.getAccountCombination().getDivision();
			Account account = gl.getAccountCombination().getAccount();

			jsonString += "[{\"glEntryId\":\"" + gl.getId() + "\"}, ";
			jsonString += "{\"companyNumber\":\"" + company.getCompanyNumber() + "\"}, ";
			jsonString += "{\"companyId\":\"" + company.getId() + "\"}, ";
			jsonString += "{\"companyName\":\"" + company.getName() +" - "+"\"}, ";
			jsonString += "{\"divisionNumber\":\"" + division.getNumber() + "\"}, ";
			jsonString += "{\"divisionId\":\"" + division.getId() + "\"}, ";
			jsonString += "{\"divisionName\":\"" + division.getName() +" - "+"\"}, ";
			jsonString += "{\"accountNumber\":\"" + account.getNumber() + "\"}, ";
			jsonString += "{\"accountId\":\"" + account.getId() + "\"}, ";
			jsonString += "{\"accountName\":\"" + account.getAccountName() + "\"}, ";
			jsonString += "{\"debit\":\"" + (gl.isDebit() ? gl.getAmount() : 0.0) + "\"}, ";
			jsonString += "{\"credit\":\"" + (!gl.isDebit() ? gl.getAmount() : 0.0) + "\"}, ";
			String description = gl.getDescription() == null ? "" : gl.getDescription();
			jsonString += "{\"description\":\"" + description + "\"}]";

			if (ctr == 0)
				ctr++;
		}
		jsonString += "]";
		return jsonString;
	}

	/**
	 * Get all gl entries
	 * @param pageSetting The page setting.
	 * @return The paged gl entries.
	 */
	public Page<GlEntry> getGlEntries (PageSetting pageSetting) {
		return glEntryDao.getGlEntries(pageSetting);
	}

	/**
	 * Filter general entries for account analysis report.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param divisionIdFrom The first division id of the range.
	 * @param divisionIdTo The second division id of the range.
	 * @param dateFrom The first date of the range.
	 * @param dateTo The second date of the range.
	 * @param pageSetting The page setting.
	 * @return The paged gl entries.
	 */
	public Page<GlEntry> getGlEntries (Integer companyId, Integer accountId, Integer divisionIdFrom,
			Integer divisionIdTo, String dateFrom, String dateTo, PageSetting pageSetting) {
		return glEntryDao.getGlEntries(pageSetting);
	}

	/**
	 * Process the General Ledgers that have null form workflow.
	 * This will be invoked only when there is at least one 
	 * General Ledger that has a null form workflow id.
	 */
	public void processPrevSavedGLEntries () {
		boolean hasNullFW = generalLedgerDao.hasNullFW();
		while (hasNullFW) {
			List<GeneralLedger> generalLedgers = generalLedgerDao.getGLWithNullWF();
			
			if (!generalLedgers.isEmpty()) {
				List<Domain> toBeSaved = new ArrayList<Domain>();
				for (GeneralLedger gl : generalLedgers) {
					if (gl.getFormWorkflowId() == null) {
						Integer glStatusId = gl.getGlStatusId();
						FormWorkflow fw = new FormWorkflow();
						if (glStatusId == GlStatus.STATUS_NEW) {
							fw.setCurrentStatusId(FormStatus.CREATED_ID);
							formWorkflowDao.save(fw);
							FormWorkflowLog fwl = new FormWorkflowLog();
							fwl.setFormStatusId(FormStatus.CREATED_ID);
							fwl.setFormWorkflowId(fw.getId());
							fwl.setCreatedBy(gl.getCreatedBy());
							fwl.setCreatedDate(gl.getCreatedDate());
							formWorkflowLogDao.save(fwl);
						} else if (glStatusId == GlStatus.STATUS_POSTED) {
							fw.setCurrentStatusId(FormStatus.POSTED_ID);
							fw.setComplete(true);
							formWorkflowDao.save(fw);
							for (int i=GlStatus.STATUS_NEW; i <= GlStatus.STATUS_POSTED; i++) {
								FormWorkflowLog fwl = new FormWorkflowLog();
								if (i == GlStatus.STATUS_NEW) {
									fwl.setFormStatusId(FormStatus.CREATED_ID);
									fwl.setCreatedBy(gl.getCreatedBy());
									fwl.setCreatedDate(gl.getCreatedDate());
								} else if (i == GlStatus.STATUS_POSTED) {
									fwl.setFormStatusId(FormStatus.POSTED_ID);
									fwl.setCreatedBy(gl.getUpdatedBy());
									fwl.setCreatedDate(gl.getUpdatedDate());
								}
								fwl.setFormWorkflowId(fw.getId());
								formWorkflowLogDao.save(fwl);
							}
						} else if (glStatusId == GlStatus.STATUS_CANCELLED) {
							fw.setCurrentStatusId(FormStatus.CANCELLED_ID);
							fw.setComplete(false);
							formWorkflowDao.save(fw);
							for (int i=GlStatus.STATUS_NEW; i <= GlStatus.STATUS_CANCELLED; i++) {
								FormWorkflowLog fwl = new FormWorkflowLog();
								if (i == GlStatus.STATUS_NEW) {
									fwl.setFormStatusId(FormStatus.CREATED_ID);
									fwl.setCreatedBy(gl.getCreatedBy());
									fwl.setCreatedDate(gl.getCreatedDate());
								} else if (i == GlStatus.STATUS_POSTED) {
									fwl.setFormStatusId(FormStatus.POSTED_ID);
									fwl.setCreatedBy(gl.getUpdatedBy());
									fwl.setCreatedDate(gl.getUpdatedDate());
								} else if (i == GlStatus.STATUS_CANCELLED) {
									fwl.setFormStatusId(FormStatus.CANCELLED_ID);
									fwl.setCreatedBy(gl.getUpdatedBy());
									fwl.setCreatedDate(gl.getUpdatedDate());
								}
								fwl.setFormWorkflowId(fw.getId());
								formWorkflowLogDao.save(fwl);
							}
						}
						gl.setFormWorkflowId(fw.getId());
						toBeSaved.add(gl);
					}
				}
				if (!toBeSaved.isEmpty())
					generalLedgerDao.batchSaveOrUpdate(toBeSaved);
			} else {
				hasNullFW = false;
			}
		}
	}

	public List<Company> getCompanies(String companyName, boolean isActive, Integer limit) {
		return glEntryDao.getCompanies(companyName, isActive, limit);
	}

	/**
	 * GEt the total amount from the list of GL Entries
	 * {@link GlEntry#getDebitAmount()} or {@link GlEntry#getCreditAmount()}
	 * @param glEntries
	 * @param isDebit True if amount to be totaled is debit, otherwise False
	 * @return Total amount
	 */
	public double getTotalAmt(Collection<GlEntry> glEntries, boolean isDebit) {
		double totalAmt = 0;
		for (Iterator<GlEntry> iterator = glEntries.iterator(); iterator.hasNext();) {
			GlEntry glEntry = (GlEntry) iterator.next();
			if (isDebit) {
				totalAmt += glEntry.getDebitAmount();
			} else {
				totalAmt += glEntry.getCreditAmount();
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmt);
	}

	/**
	 * Iterate over the GL Entries and get the total amount
	 * {@link GlEntry#getAmount()}
	 * @param glEntries
	 * @return
	 */
	public double getTotalAmt(Collection<GlEntry> glEntries) {
		double totalAmt = 0;

		// Iterate over the collection
		Iterator<GlEntry> iterator = glEntries.iterator();
		GlEntry glEntry = null;
		while (iterator.hasNext()) {
			glEntry = (GlEntry) iterator.next();
			// Get only amount if it is debit amount
			if (glEntry.isDebit()) {
				totalAmt += glEntry.getAmount();
			}
		}
		return totalAmt;
	}

	/**
	 * Process the GL Lines for editing the General Journal Form
	 * @param dto
	 */
	public void loadGlLines(GeneralLedgerDto dto) {
		Collection<GlEntry> glEntries = dto.getGeneralLedger().getGlEntries();
		Collection<GlEntry> processedList = new ArrayList<GlEntry>();
		for (Iterator<GlEntry> iterator = glEntries.iterator(); iterator.hasNext();) {
			GlEntry entry = (GlEntry) iterator.next();
			// Set the account combination details
			AccountCombination ac = entry.getAccountCombination();
			String combination = ac.getCompany().getName() + " - "+ ac.getDivision().getName()
					+ " - " +ac.getAccount().getAccountName();
			entry.setCombination(combination);
			// Set the division details
			entry.setDivisionName(ac.getDivision().getNumber());
			entry.setDivisionId(ac.getDivisionId());
			// Set the account details
			entry.setAccountName(ac.getAccount().getNumber());
			entry.setAccountId(ac.getAccountId());
			Double rate = dto.getGeneralLedger().getCurrencyRateValue();
			setAmount(entry, rate != null ? rate : 1.0);
			processedList.add(entry);
		}
		dto.setGlEntries(processedList);
	}

	/**
	 * Set the amount to Debit if value of {@link GlEntry#isDebit()} is TRUE,
	 * otherwise set the amount to Credit
	 * 
	 * @param entry
	 */
	public void setAmount(GlEntry entry, double rate) {
		// Set the amount
		if (entry.isDebit()) {
			entry.setDebitAmount(CurrencyUtil.convertMonetaryValues(entry.getAmount(), rate));
		} else {
			entry.setCreditAmount(CurrencyUtil.convertMonetaryValues(entry.getAmount(), rate));
		}
	}
}
