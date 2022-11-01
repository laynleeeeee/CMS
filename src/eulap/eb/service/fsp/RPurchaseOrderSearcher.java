package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Business logic to handle the searching of Retail - POs.

 *
 */
@Service
public class RPurchaseOrderSearcher implements SearchableFormPlugin{
	private Logger logger = Logger.getLogger(RPurchaseOrderSearcher.class);
	@Autowired
	private RPurchaseOrderDao poDao;
	@Autowired
	private FormStatusDao formStatusDao;

	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		return searchPurchaseOrderDiv(null, user, searchCriteria);
	}

	/**
	 * Retrieve the list of purchase orders for general search
	 * @param divisionId The division id
	 * @param user The user id
	 * @param searchCriteria The search criteria
	 * @return The list of purchase orders for general search
	 */
	public List<FormSearchResult> searchPurchaseOrderDiv(Integer divisionId, User user, String searchCriteria) {
		logger.info("Searching Retail - POs.");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<RPurchaseOrder> searchResult = poDao.searchPOs(divisionId, searchCriteria.trim(),
				new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		List<ResultProperty> properties = null;
		String status = null;
		for (RPurchaseOrder po : searchResult.getData()) {
			logger.trace("Retrieved PO No. "+po.getFormattedPONumber());
			title = String.valueOf(po.getFormattedPONumber());
			properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", po.getCompany().getName()));
			if (divisionId != null) {
				properties.add(ResultProperty.getInstance("Division", po.getDivision().getName()));
			}
			properties.add(ResultProperty.getInstance("PO Date", DateUtil.formatDate(po.getPoDate())));
			Date estDeliveryDate = po.getEstDeliveryDate();
			if (estDeliveryDate != null) {
				properties.add(ResultProperty.getInstance("Estimated Delivery Date",
						DateUtil.formatDate(estDeliveryDate)));
			}
			properties.add(ResultProperty.getInstance("Supplier", po.getSupplier().getName()));
			properties.add(ResultProperty.getInstance("Supplier Account", po.getSupplierAccount().getName()));
			if (po.getTermId() != null) {
				properties.add(ResultProperty.getInstance("Term", po.getTerm().getName()));
			}
			if (po.getRemarks() != null) {
				properties.add(ResultProperty.getInstance("Remarks", po.getRemarks()));
			}
			status = formStatusDao.get(po.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(po.getId(), title, properties));
		}
		if (searchResult.getData().isEmpty()) {
			logger.info("No matching PO Numbers found.");
		} else {
			logger.info("Successfully retrieved "+searchResult.getData().size()+" Retail - POs");
		}
		return result;
	}
}
