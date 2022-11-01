package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.WorkOrder;

/**
 * Data transfer object for {@link WorkOrder}

 */

public class WorkOrderDto {
	private List<WorkOrder> mainWorkOrder;
	private List<WorkOrder> subWorkOrders;

	public List<WorkOrder> getMainWorkOrder() {
		return mainWorkOrder;
	}

	public void setMainWorkOrder(List<WorkOrder> mainWorkOrder) {
		this.mainWorkOrder = mainWorkOrder;
	}

	public List<WorkOrder> getSubWorkOrders() {
		return subWorkOrders;
	}

	public void setSubWorkOrders(List<WorkOrder> subWorkOrders) {
		this.subWorkOrders = subWorkOrders;
	}
}
