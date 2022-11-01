package eulap.eb.service.workflow;

import java.util.List;

import org.springframework.context.ApplicationContext;

import eulap.common.domain.Domain;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.inventory.InventoryItem;
import eulap.eb.service.inventory.ItemCOGSReallocator;
import eulap.eb.service.oo.OOLinkHelper;

/**
 * All of the form-service that are related to inventory should extend this class

 *
 */
public abstract class InventoryWorkflowService extends BaseWorkflowService{
	
	@Override
	public void processAfterSaving(ApplicationContext ac, OOLinkHelper ooLinkHelper, WorkflowService serviceHandler, FormWorkflowLog currentWorkflowLog) {
		try {
			reAllocateItem(ac, ooLinkHelper, serviceHandler, currentWorkflowLog);
		} catch (ClassNotFoundException | CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		doAfterSaving(currentWorkflowLog);
		
	}

	private void reAllocateItem (ApplicationContext ac, OOLinkHelper ooLinkHelper, WorkflowService serviceHandler, FormWorkflowLog currentWorkflowLog) throws ClassNotFoundException, CloneNotSupportedException {
		ItemCOGSReallocator reAllocator = new ItemCOGSReallocator(ac, ooLinkHelper);
		reAllocator.reAllocateItem(this, currentWorkflowLog.getFormWorkflowId());
	}
	
	/**
	 * Get the items for this form. 
	 * @param currentWorkflowLog the current workflow log. 
	 * @return the list of items of this form. 
	 */
	public abstract List<? extends Domain> getItems (BaseFormWorkflow form);
	
	/**
	 * Get the inventory item transaction
	 * @param objectId the object id. 
	 * @return the inventory item transaction
	 */
	public abstract InventoryItem getItemTransaction (Integer objectId);

	/**
	 * Get the warehouse where the items will be withdrawn.
	 * @param form The actual form.
	 * @param itemLine the item line.
	 * @return The list of warehouse that are associated to this transaction.
	 */
	public abstract List<Integer> getWarehouses (BaseFormWorkflow form, Domain itemLine);

	/**
	 * Get the items that are associated to this item line transaction.
	 * @param form the actual form.
	 * @param itemLine the item line.
	 * @return The list of items that are associated to this transaction.
	 */
	public abstract List<Integer> getItems (BaseFormWorkflow form, Domain itemLine);
}
