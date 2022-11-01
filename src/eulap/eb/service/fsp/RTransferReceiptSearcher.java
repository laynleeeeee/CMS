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
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Business logic to handle the searching of Retail - TR forms.

 *
 */
@Service
public class RTransferReceiptSearcher{
	private static Logger logger = Logger.getLogger(RTransferReceiptSearcher.class);
	@Autowired
	private RTransferReceiptDao trDao;
	@Autowired
	private FormStatusDao formStatusDao;

	public List<FormSearchResult> search(User user, String searchCriteria, int typeId) {
		logger.info("Searching Retail - TR forms");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<RTransferReceipt> searchResult = trDao.searchTRs(searchCriteria.trim(), typeId, new PageSetting(1));
		if(searchResult.getData().isEmpty()) {
			logger.info("Returned an empty list.");
			return Collections.emptyList();
		}
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		List<ResultProperty> properties = null;
		String title = null;
		String status = null;
		for (RTransferReceipt tr : searchResult.getData()) {
			title = tr.getFormattedTRNumber();
			properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", tr.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(tr.getTrDate())));
			properties.add(ResultProperty.getInstance("Warehouse From", tr.getWarehouseFrom().getName()));
			properties.add(ResultProperty.getInstance("Warehouse To", tr.getWarehouseTo().getName()));
			if(tr.getDrNumber() != null)
				properties.add(ResultProperty.getInstance("DR No.", tr.getDrNumber()));
			status = formStatusDao.get(tr.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(tr.getId(), title, properties));
			logger.trace("Added TR No. "+title+" to the search result.");
		}
		logger.info("Successfully retrieved "+result.size()+" Retail - TR form/s.");

		logger.warn("======>>>> Freeing up memory allocation.");
		properties = null;
		title = null;
		status = null;
		return result;
	}
}
