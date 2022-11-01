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
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Business logic that will handle the searching of Stock Adjustment forms.

 *
 */
@Service
public class StockAdjustmentSearcher {
	private static Logger logger = Logger.getLogger(StockAdjustmentSearcher.class);
	@Autowired
	private StockAdjustmentDao saDao;
	@Autowired
	private FormStatusDao formStatusDao;

	public List<FormSearchResult> search(User user, String searchCriteria, int typeId) {
		logger.info("Searching Stock Adjustment forms");
		logger.debug("Searching for: "+searchCriteria);
		Page<StockAdjustment> searchResults = saDao.searchStockAdjustments(searchCriteria, typeId, new PageSetting(1));
		if(searchResults.getData().isEmpty()) {
			logger.info("No Stock Adjustment found.");
			return Collections.emptyList();
		}

		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		List<ResultProperty> properties = null;
		String title = null;
		String status = null;
		for (StockAdjustment sa : searchResults.getData()) {
			title = sa.getSaNumber().toString();
			properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", sa.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(sa.getSaDate())));
			properties.add(ResultProperty.getInstance("Warehouse", sa.getWarehouse().getName()));
			properties.add(ResultProperty.getInstance("Adjustment Type", sa.getAdjustmentType().getName()));
			if(sa.getRemarks() != null)
				properties.add(ResultProperty.getInstance("Remarks", sa.getRemarks()));
			status = formStatusDao.get(sa.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(sa.getId(), title, properties));
			logger.trace("Added SA Number "+sa.getSaNumber()+" to the search result.");
		}
		logger.info("Successfully retrieved "+result.size()+" Stock Adjustment forms.");

		logger.info("======>>>> Freeing up memory allocation.");
		properties = null;
		title = null;
		status = null;
		return result;
	}

}
