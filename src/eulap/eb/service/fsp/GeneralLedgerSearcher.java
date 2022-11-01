package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service that will handle the searching for General Entries.

 */
@Service
public class GeneralLedgerSearcher implements SearchableFormPlugin{
	@Autowired
	private GeneralLedgerDao ledgerDao;

	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		Page<GeneralLedger> generalLedgers =
				ledgerDao.searchGeneralLedger(searchCriteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for(GeneralLedger gl : generalLedgers.getData()) {
			String title = ("JV No. " + String.valueOf(gl.getSequenceNo()));
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", getCompany(gl)));
			properties.add(ResultProperty.getInstance("Source", gl.getGlEntrySource().getDescription()));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(gl.getGlDate())));
			properties.add(ResultProperty.getInstance("Description", gl.getComment()));
			properties.add(ResultProperty.getInstance("Status", gl.getFormWorkflow().
					getCurrentFormStatus().getDescription()));
			String amount = NumberFormatUtil.format(getTotalAmount(gl));
			properties.add(ResultProperty.getInstance("Amount", amount));
			result.add(FormSearchResult.getInstanceOf(gl.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Compute for the total amount of the General ledger.
	 * @param gl The General ledger object.
	 * @return The total amount.
	 */
	private double getTotalAmount(GeneralLedger gl) {
		Collection<GlEntry> entries = gl.getGlEntries();
		double totalAmount = 0;
		for(GlEntry entry : entries) {
			if(entry.isDebit()) {
				totalAmount += entry.getAmount();
			}
		}
		return totalAmount;
	}

	/**
	 * Get the first company in the account combination.
	 * @param gl The general ledger object.
	 * @return The company name.
	 */
	private String getCompany (GeneralLedger gl) {
		Collection<GlEntry> glEntries = gl.getGlEntries();
		boolean firstElement = true;
		String companyName = "";
		for(GlEntry glEnrty : glEntries) {
			if(firstElement) {
				companyName += glEnrty.getAccountCombination().getCompany().getName();
				return companyName;
			} else {
				return "";
			}
		}
		return companyName;
	}
}
