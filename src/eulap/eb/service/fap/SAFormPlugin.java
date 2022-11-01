package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.common.util.TitleAndLabel;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentClassification;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the list of forms for approval.

 *
 */
@Service
public class SAFormPlugin extends MultiFormPlugin{
	private static Logger logger = Logger.getLogger(SAFormPlugin.class);
	@Autowired
	private StockAdjustmentDao saDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<StockAdjustment> searchResults = saDao.getAllSAsByStatus(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		logger.info("Retrieved "+searchResults.getTotalRecords()+" Stock adjustment forms.");
		boolean highlight = false;
		FormWorkflow workflow = null;
		FormWorkflowLog workflowLog = null;
		StringBuffer shortDesc = null;
		String label = null;
		for (StockAdjustment sa : searchResults.getData()) {
			if(typeId <5 ) {
				switch (typeId) {
				case StockAdjustment.STOCK_ADJUSTMENT_IN:
					label = "I";
					break;
				case StockAdjustment.STOCK_ADJUSTMENT_OUT:
					label = "O";
					break;
				case StockAdjustment.STOCK_ADJUSTMENT_IS_IN:
					label = "I - IS";
					break;
				default:
					label = "O - IS";
					break;
				}
			} else {
				switch (typeId) {
				case StockAdjustmentClassification.STOCK_IN_CENTRAL:
					label = "I - Central";
					break;
				case StockAdjustmentClassification.STOCK_IN_NSB3:
					label = "I - NSB 3";
					break;
				case StockAdjustmentClassification.STOCK_IN_NSB4:
					label = "I - NSB 4";
					break;
				case StockAdjustmentClassification.STOCK_IN_NSB5:
					label = "I - NSB 5";
					break;
				case StockAdjustmentClassification.STOCK_IN_NSB8:
					label = "I - NSB 8";
					break;
				case StockAdjustmentClassification.STOCK_IN_NSB8A:
					label = "I - NSB 8A";
					break;
				case StockAdjustmentClassification.STOCK_OUT_CENTRAL:
					label = "O - Central";
					break;
				case StockAdjustmentClassification.STOCK_OUT_NSB3:
					label = "O - NSB 3";
					break;
				case StockAdjustmentClassification.STOCK_OUT_NSB4:
					label = "O - NSB 4";
					break;
				case StockAdjustmentClassification.STOCK_OUT_NSB5:
					label = "O - NSB 5";
					break;
				case StockAdjustmentClassification.STOCK_OUT_NSB8:
					label = "O - NSB 8";
					break;
				case StockAdjustmentClassification.STOCK_OUT_NSB8A:
					label = "O - NSB 8A";
					break;
				}
			}

			String preTitle = TitleAndLabel.getString("SAFormPlugin.0");

			shortDesc = new StringBuffer(String.format(preTitle, label, sa.getSaNumber()))
					.append(" DATE "+DateUtil.formatDate(sa.getSaDate()))
					.append(" "+sa.getWarehouse().getName())
					.append(" "+sa.getAdjustmentType().getName());
			workflow = sa.getFormWorkflow();
			workflowLog = workflow.getCurrentLogStatus();
			highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(sa.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
			logger.debug("Added "+sa.getSaNumber()+" to the list.");
		}

		logger.info("=======>> Freeing up memory allocation.");
		workflow = null;
		workflowLog = null;

		logger.info("Showing the list of Stock Adjustments for approval.");
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResults.getTotalRecords());
	}
}
