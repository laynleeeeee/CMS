package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ProductLineDao;
import eulap.eb.dao.ProductLineItemDao;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.ProductLineItem;
import eulap.eb.domain.hibernate.User;

/**
 * Class that handles the business logic {@link ProductLine}

 *
 */
@Service
public class ProductLineService {
	@Autowired
	private ProductLineDao productLineDao;
	@Autowired
	private ProductLineItemDao productLineItemDao;

	/**
	 * Save the product line.
	 * @param productLine The product line.
	 * @param user The user current logged.
	 */
	public void saveProductLine(ProductLine productLine, User user) {
		int productLineId = productLine.getId();
		boolean isNew = productLineId == 0;
		AuditUtil.addAudit(productLine, new Audit (user.getId(), isNew, new Date ()));
		productLineDao.saveOrUpdate(productLine);

		productLineId = productLine.getId();
		List<ProductLineItem> productLineItems = productLineItemDao.getAllByRefId("productLineId", productLineId);
		if(productLineItems != null && !productLineItems.isEmpty()){
			List<Integer> toBeDeleteLineItemIds = new ArrayList<Integer>();
			for (ProductLineItem productLineItem : productLineItems) {
				toBeDeleteLineItemIds.add(productLineItem.getId());
			}
			if(!toBeDeleteLineItemIds.isEmpty()){
				productLineItemDao.delete(toBeDeleteLineItemIds);
			}
		}

		for (ProductLineItem productLineItem : productLine.getProductLineItems()) {
			productLineItem.setProductLineId(productLineId);
			productLineItemDao.save(productLineItem);
		}
	}

	/**
	 * Check if product line has duplicate entry.
	 * @param productLine The product line.
	 * @return True if the product line has duplicate entry, otherwise false.
	 */
	public boolean isDuplicateProductline(ProductLine productLine) {
		return productLineDao.isDuplicateProductline(productLine);
	}

	/**
	 * Get the list of product list.
	 * @param productLine The main item stock code.
	 * @param rawMaterial The raw material stock code.
	 * @param status The search status.
	 * @param pageNumber The page number.
	 * @return The list of product line.
	 */
	public Page<ProductLine> getProductList(String productLine, String rawMaterial, String status, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<ProductLine> productLines = productLineDao.getProductList(productLine, rawMaterial, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (ProductLine line : productLines.getData()) {
			line.setProductLineItems(productLineItemDao.getAllByRefId("productLineId", line.getId()));
		}
		return productLines;
	}

	/**
	 * Get the product line by id.
	 * @param productLineId The product line id.
	 * @return The product line.
	 */
	public ProductLine getProductLine(Integer productLineId) {
		ProductLine productLine = productLineDao.get(productLineId);
		productLine.setMainItemName(productLine.getMainItem().getStockCode());
		List<ProductLineItem> productLineItems = productLineItemDao.getAllByRefId("productLineId", productLineId);
		for (ProductLineItem productLineItem : productLineItems) {
			productLineItem.setItemStockCode(productLineItem.getItem().getStockCode());
			productLineItem.setDescription(productLineItem.getItem().getDescription());
			productLineItem.setUom(productLineItem.getItem().getUnitMeasurement().getName());
		}
		productLine.setProductLineItems(productLineItems);
		return productLine;
	}

	/**
	 * Get the list of raw materials of the main item.
	 * @param mainItemId The id of the main item.
	 * @return The list of raw materials of the main product.
	 */
	public List<ProductLineItem> getRawMaterials(int mainItemId) {
		return productLineItemDao.getRawMaterials(mainItemId);
	}
}