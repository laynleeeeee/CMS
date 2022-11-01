package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.RepackingDao;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Retrieves and generates the list of Repacking forms for searching.

 *
 */
@Service
public class RepackingSearcher {
	private static Logger logger = Logger.getLogger(RepackingSearcher.class);
	@Autowired
	private RepackingDao rpDao;
	@Autowired
	private FormStatusDao formStatusDao;

	/**
	 * Search repacking form based on the search criteria
	 * @param typeId The repacking type id
	 * @param divisionId The repacking division id
	 * @param user The current user logged
	 * @param searchCriteria The search criteria
	 * @return The list of repacking forms based on the criteria
	 */
	public List<FormSearchResult> search(int typeId, User user, String searchCriteria) {
		return search(typeId, null, user, searchCriteria);
	}

	/**
	 * Search repacking form based on the search criteria
	 * @param typeId The repacking type id
	 * @param user The current user logged
	 * @param searchCriteria The search criteria
	 * @return The list of repacking forms based on the criteria
	 */
	public List<FormSearchResult> search(int typeId, Integer divisionId, User user, String searchCriteria) {
		logger.info("Searching Repacking forms.");
		searchCriteria = StringFormatUtil.removeExtraWhiteSpaces(searchCriteria);
		Page<Repacking> searchResults = rpDao.searchRepackingForms(typeId, divisionId, searchCriteria, new PageSetting(PageSetting.START_PAGE));
		if (searchResults.getData().isEmpty()) {
			logger.info("No Repacking forms found.");
			return Collections.emptyList();
		}

		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		List<ResultProperty> properties = null;
		String title = null;
		String status = null;
		for (Repacking rp : searchResults.getData()) {
			title = rp.getFormattedRNumber();
			properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", rp.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Warehouse", rp.getWarehouse().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(rp.getrDate())));
			if (rp.getRemarks() != null) {
				properties.add(ResultProperty.getInstance("Remarks", rp.getRemarks()));
			}
			status = formStatusDao.get(rp.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(rp.getId(), title, properties));
			logger.trace("Added Repacking No. "+rp.getFormattedRNumber()+" to the search result.");
		}

		logger.info("Successfully retrieved "+result.size()+" Repacking forms.");
		logger.info("======>>>> Freeing up memory allocation.");
		properties = null;
		title = null;
		status = null;
		return result;
	}

}
