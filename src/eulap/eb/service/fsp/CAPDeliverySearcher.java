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
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Business logic to get and process the CAP Delivery forms in general search.

 *
 */
@Service
public class CAPDeliverySearcher {
	private static Logger logger = Logger.getLogger(CAPDeliverySearcher.class);
	@Autowired
	private CAPDeliveryDao deliveryDao;
	@Autowired
	private FormStatusDao formStatusDao;

	public List<FormSearchResult> search(User user, Integer typeId, String searchCriteria) {
		Page<CAPDelivery> deliveries = deliveryDao.searchDeliveries(searchCriteria, typeId, new PageSetting(PageSetting.START_PAGE));
		if(deliveries.getData().isEmpty()) {
			logger.info("No CAP Deliveries found.");
			return Collections.emptyList();
		}

		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		List<ResultProperty> properties = null;
		String status = null;
		for (CAPDelivery delivery : deliveries.getData()) {
			String title = delivery.getFormattedCAPDNumber();
			properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", delivery.getCompany().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Customer", delivery.getArCustomer().getName()));
			properties.add(ResultProperty.getInstance("Customer Account", delivery.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Delivery Date", DateUtil.formatDate(delivery.getDeliveryDate())));
			status = formStatusDao.get(delivery.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(delivery.getId(), title, properties));
			logger.debug("Added "+title+" to the search result.");
		}
		logger.info("Successfully retrieved "+result.size()+" Delivery forms.");
		return result;
	}

}
