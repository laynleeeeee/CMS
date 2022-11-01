package eulap.eb.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.impl.sql.SQLProperyReader;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBuyingPrice;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrOtherMaterialsItem;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.RReceivingReportRmItem;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.service.inventory.ReceivedStock;
import eulap.eb.web.dto.DailyItemSale;
import eulap.eb.web.dto.DailyItemSaleDetail;
import eulap.eb.web.dto.GrossProfitAnalysis;
import eulap.eb.web.dto.ItemSalesCustomer;
import eulap.eb.web.dto.ItemTransaction;
import eulap.eb.web.dto.ItemTransactionHistory;
import eulap.eb.web.dto.PhysicalInventory;
import eulap.eb.web.dto.RItemDetail;
import eulap.eb.web.dto.RItemDetailValue;
import eulap.eb.web.dto.ReorderPointDto;
import eulap.eb.web.dto.StockcardDto;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * The implementation class of {@link ItemDao}

 *
 */
public class ItemDaoImpl extends BaseDao<Item> implements ItemDao {
	@Autowired
	private Integer itemTransactionTbaleCount;
	@Autowired
	private Integer itemReceivedStocksTblCount;

	@Override
	protected Class<Item> getDomainClass() {
		return Item.class;
	}

	@Override
	public boolean isUniqueStockCode(String stockCode, int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode));
		dc.add(Restrictions.ne(Item.FIELD.id.name(), itemId));
		return getAll(dc).isEmpty();
	}
	private static abstract class ItemListHibernateCallBack implements HibernateCallback<Page<Item>> {
		private ItemDaoImpl itemDao;
		private PageSetting pageSetting;
		private ItemListHibernateCallBack(ItemDaoImpl itemDao, PageSetting pageSetting) {
			this.itemDao = itemDao;
			this.pageSetting = pageSetting;
		}
		
		@Override
		public Page<Item> doInHibernate(Session session) throws HibernateException, SQLException {
			Criteria criteria = getCriteria(session);
			Page<Item> items = itemDao.getAll(criteria, pageSetting);
			HibernateTemplate ht = itemDao.getHibernateTemplate();
			for (Item item : items.getData()) {
				itemDao.initializedCommonReference(item, ht);
				initializeOtherReference(item, ht);
			}
			return items;
		}
		
		protected abstract Criteria getCriteria (Session session);
		
		protected void initializeOtherReference (Item item, HibernateTemplate ht) {
			// Do nothing. For sub class to initialize other reference object
		}
	}
	
	private void initializedCommonReference (Item item, HibernateTemplate ht) {
		ht.initialize(item.getItemCategory());
		ht.initialize(item.getUnitMeasurement());
	}
	
	private static abstract class ItemHibernateCallBack implements HibernateCallback<Item> {
		private ItemDaoImpl itemDao;
		private ItemHibernateCallBack(ItemDaoImpl itemDao) {
			this.itemDao = itemDao;
		}
		
		@Override
		public Item doInHibernate(Session session) throws HibernateException, SQLException {
			Criteria criteria = getCriteria(session);
			Item item = itemDao.get(criteria);
			HibernateTemplate ht = itemDao.getHibernateTemplate();
			if (item != null) {
				itemDao.initializedCommonReference(item, ht);
				initializeOtherReference(item, ht);
			}
			return item;
		}

		protected abstract Criteria getCriteria (Session session);
	
		protected void initializeOtherReference (Item item, HibernateTemplate ht) {
			// Do nothing. For sub class to initialize other reference object
		}
	}

	@Override
	public Item getItemByStockCode(final String stockCode, final Integer itemCategoryId) {
		return getHibernateTemplate().execute(new ItemHibernateCallBack (this) {

			@Override
			protected Criteria getCriteria(Session session) {
				Criteria criteria = session.createCriteria(Item.class);
				if(itemCategoryId != null)
					criteria.add(Restrictions.eq(Item.FIELD.itemCategoryId.name(), itemCategoryId));
				criteria.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode.trim()));
				criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				return criteria;
			}
		});
	}

	@Override
	public Page<Item> getAllItems(final boolean isActive, PageSetting pageSetting) {
		return getHibernateTemplate().execute(new ItemListHibernateCallBack(this, pageSetting) {
			
			@Override
			protected Criteria getCriteria(Session session) {
				Criteria criteria = session.createCriteria(Item.class);
				if(isActive) {
					criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				}
				return criteria;
			}
		});
	}

	private static class ReceivedStocksRetriever implements QueryResultHandler<ReceivedStock> {
		private final int itemId;
		private final Date startDate;
		private final Date endDate;
		private ReceivedStocksRetriever (int itemId, Date startDate, Date endDate) {
			this.itemId = itemId;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		@Override
		public List<ReceivedStock> convert(List<Object[]> queryResult) {
			List<ReceivedStock> receivedStocks = new ArrayList<ReceivedStock>();
			for (Object[] row : queryResult){
				Date date = (Date)row[0];
				// Disregard created date
				int itemId = (Integer) row[2];
				double quantity = (Double) row[3];
				double unitCost = 0;
				Object obj = row[4];
				if (obj != null)
					unitCost = (Double) row[4];
				obj = row[5];
				double invCost = 0;
				if (obj != null)
					invCost = (Double) row[5];
				String formName = (String) row[6];
				int formId = (Integer) row[7];
				receivedStocks.add(new ReceivedStock(date, itemId, quantity, 
						unitCost, invCost, formName, formId));
			}
			return receivedStocks;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, itemId);
			query.setParameter(index++, itemId);
			query.setParameter(index++, itemId);
			query.setParameter(index, itemId);
			if (startDate != null && endDate == null) {
				index++;
				query.setParameter(index, startDate);
			} else if (startDate != null && endDate != null) {
				index++;
				query.setParameter(index++, startDate);
				query.setParameter(index, endDate);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("CREATED_DATE", Hibernate.TIMESTAMP);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("INV_UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("FORM", Hibernate.STRING);
			query.addScalar("FORM_ID", Hibernate.INTEGER);
		}
	}

	@Override
	public Item getRetailItem(final String stockCode, final Integer warehouseId, final Integer itemCategoryId,
			final Integer companyId, final Integer divisionId, final Boolean isExcludeFG) {
		return getHibernateTemplate().execute(new ItemHibernateCallBack(this) {
			@Override
			protected Criteria getCriteria(Session session) {
				Criteria criteria = session.createCriteria(Item.class);
				criteria.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode.trim()));
				criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				if (itemCategoryId != null) {
					if (itemCategoryId != -1) {
						criteria.add(Restrictions.eq(Item.FIELD.itemCategoryId.name(), itemCategoryId));
					}
				}
				if(isExcludeFG != null && isExcludeFG) {
					DetachedCriteria dc = DetachedCriteria.forClass(ItemCategory.class);
					dc.setProjection(Projections.property(ItemCategory.FIELD.id.name()));
					dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%Finished Goods%"));
					criteria.add(Subqueries.propertyNotIn(Item.FIELD.itemCategoryId.name(), dc));
				}
				if (companyId != null) {
					DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
					srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
					srpDc.add(Restrictions.gt(ItemSrp.FIELD.srp.name(), 0.0));
					if (divisionId != null) {
						srpDc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
					}
					criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
				}
				return criteria;
			}

			@Override
			protected void initializeOtherReference(Item item, HibernateTemplate ht) {
				if(warehouseId != null)
					item.setExistingStocks(getItemExistingStocks(item.getId(), warehouseId, new Date()));
				ht.initialize(item.getItemSrps());
				ht.initialize(item.getItemDiscounts());
				ht.initialize(item.getItemAddOns());
			}
		});
	}

	@Override
	public Page<Item> getRetailItems(final String stockCode, final String description,
			final Integer unitMeasurementId, final Integer itemCategoryId, 
			final int status, final boolean isOrderByCategory, final PageSetting pageSetting) {
		return getRetailItems(null, stockCode, description, unitMeasurementId, itemCategoryId, status, isOrderByCategory, pageSetting);
	}

	@Override
	public Page<Item> getRetailItemWithDivision(Integer divisionId, final String stockCode, final String description,
			final Integer unitMeasurementId, final Integer itemCategoryId, 
			final int status, final boolean isOrderByCategory, final PageSetting pageSetting) {
		return getRetailItems(divisionId, stockCode, description, unitMeasurementId, itemCategoryId, status, isOrderByCategory, pageSetting);
	}

	private Page<Item> getRetailItems(Integer divisionId, final String stockCode, final String description,
			final Integer unitMeasurementId, final Integer itemCategoryId, 
			final int status, final boolean isOrderByCategory, final PageSetting pageSetting) {
		return getHibernateTemplate().execute(new ItemListHibernateCallBack(this,pageSetting) {
				@Override
				protected Criteria getCriteria(Session session) {
					Criteria criteria = session.createCriteria(Item.class);
					if(divisionId != null && divisionId != -1) {
						DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
						srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
						srpDc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
						criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
					}
					boolean result = status == 1;
					if (stockCode != null && !stockCode.isEmpty())
						criteria.add(Restrictions.like(Item.FIELD.stockCode.name(), StringFormatUtil.appendWildCard(stockCode)));
					if (description != null && !description.isEmpty())
						criteria.add(Restrictions.like(Item.FIELD.description.name(), StringFormatUtil.appendWildCard(description)));
					if (unitMeasurementId != null)
						criteria.add(Restrictions.eq(Item.FIELD.unitMeasurementId.name(), unitMeasurementId));
					if (itemCategoryId != null)
						criteria.add(Restrictions.eq(Item.FIELD.itemCategoryId.name(), itemCategoryId));
					if(status != -1)
						criteria.add(Restrictions.eq(Item.FIELD.active.name(), result));
					if(isOrderByCategory) {
						criteria.addOrder(Order.asc(Item.FIELD.itemCategoryId.name()));
						criteria.addOrder(Order.asc(Item.FIELD.stockCode.name()));
						criteria.addOrder(Order.asc(Item.FIELD.description.name()));
						criteria.addOrder(Order.asc(Item.FIELD.unitMeasurementId.name()));
					} else {
						criteria.addOrder(Order.asc(Item.FIELD.stockCode.name()));
						criteria.addOrder(Order.asc(Item.FIELD.description.name()));
						criteria.addOrder(Order.asc(Item.FIELD.unitMeasurementId.name()));
						criteria.addOrder(Order.asc(Item.FIELD.itemCategoryId.name()));
					}
					return criteria;
				}
			});
	}

	@Override
	public Item getRetailItem(final Integer itemId, final Integer companyId) {
		return getHibernateTemplate().execute(new ItemHibernateCallBack(this) {
			
			@Override
			protected Criteria getCriteria(Session session) {
				Criteria criteria = session.createCriteria(Item.class);
				if(itemId != null)
					criteria.add(Restrictions.eq(Item.FIELD.id.name(), itemId));
				criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				if(companyId != null) {
					DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
					srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
					srpDc.add(Restrictions.gt(ItemSrp.FIELD.srp.name(), 0.0));
					criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
				}
				return criteria;
			}
			
			@Override
			protected void initializeOtherReference(Item item, HibernateTemplate ht) {
				ht.initialize(item.getItemSrps());
				ht.initialize(item.getItemDiscounts());
				ht.initialize(item.getItemAddOns());
			}
		});
	}
	
	public List<Item> getRetailItems(final Integer companyId, final Integer divisionId, final Integer warehouseId,
			final Integer itemCategoryId, final String stockCode, final List<String> stockCodes, final boolean isShowAll,
			final Boolean isMixing, final Boolean isExcludeFG) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Item>>() {
			@Override
			public List<Item> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Item.class);
				criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				if (companyId != null) {
					DetachedCriteria dc = DetachedCriteria.forClass(ItemSrp.class);
					dc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
					dc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
					dc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
					if (divisionId != null) {
						dc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
					}
					criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), dc));
				}
				if (itemCategoryId != null && itemCategoryId > 0) {
					criteria.add(Restrictions.eq(Item.FIELD.itemCategoryId.name(), itemCategoryId));
				}
				if(isExcludeFG != null && isExcludeFG) {
					DetachedCriteria dc = DetachedCriteria.forClass(ItemCategory.class);
					dc.setProjection(Projections.property(ItemCategory.FIELD.id.name()));
					dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%Finished Goods%"));
					criteria.add(Subqueries.propertyNotIn(Item.FIELD.itemCategoryId.name(), dc));
				}
				if (stockCode != null && !stockCode.trim().isEmpty()) {
					criteria.add(Restrictions.or(Restrictions.like(Item.FIELD.description.name(), StringFormatUtil.appendWildCard(stockCode)),
							Restrictions.like(Item.FIELD.stockCode.name(), StringFormatUtil.appendWildCard(stockCode))));
				}
				if (stockCodes != null && !stockCodes.isEmpty()) {
					for (String sc : stockCodes) {
						criteria.add(Restrictions.ne(Item.FIELD.stockCode.name(), sc.trim()));
					}
				}
				if (!isShowAll) {
					criteria.setMaxResults(10);
				}
				// Subquery to Product Line to check if the item is the finished product
				if (isMixing != null && isMixing) {
					DetachedCriteria dc = DetachedCriteria.forClass(ProductLine.class);
					dc.setProjection(Projections.property(ProductLine.FIELD.mainItemId.name()));
					dc.add(Restrictions.eq(ProductLine.FIELD.active.name(), true));
					criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), dc));
				}
				List<Item> items = getAllByCriteria(criteria);
				for (Item item : items) {
					if (warehouseId != null) {
						item.setExistingStocks(getItemExistingStocks(item.getId(), warehouseId, new Date()));
					}
					getHibernateTemplate().initialize(item.getItemSrps());
					getHibernateTemplate().initialize(item.getItemDiscounts());
					getHibernateTemplate().initialize(item.getItemAddOns());
					getHibernateTemplate().initialize(item.getItemCategory());
					getHibernateTemplate().initialize(item.getUnitMeasurement());
				}
				return items;
			}
			
		});
	}

	@Override
	public List<Item> getRetailItems(String stockCode, Integer companyId) {
		DetachedCriteria itemDc = getDetachedCriteria();
		itemDc.add(Restrictions.eq(Item.FIELD.active.name(), true));
		if (companyId != null) {
			DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
			srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
			srpDc.add(Restrictions.gt(ItemSrp.FIELD.srp.name(), 0.0));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
			itemDc.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
		}
		if (stockCode != null && !stockCode.trim().isEmpty()) {
			itemDc.add(Restrictions.or(Restrictions.like(Item.FIELD.description.name(), "%" + stockCode.trim() + "%"),
					Restrictions.like(Item.FIELD.stockCode.name(), "%" + stockCode.trim() + "%")));
		}
		return getAll(itemDc);
	}

	@Override
	public Page<Item> getRepackedItems(PageSetting pageSetting) {
		
		return getHibernateTemplate().execute(new ItemListHibernateCallBack(this, pageSetting) {
			
			@Override
			protected Criteria getCriteria(Session session) {
				DetachedCriteria itemDc = getDetachedCriteria();
				itemDc.add(Restrictions.eq(Item.FIELD.active.name(), true));
				
				DetachedCriteria repackedItemDc = DetachedCriteria.forClass(RepackingItem.class);
				repackedItemDc.setProjection(Projections.property(RepackingItem.FIELD.fromItemId.name()));
				
				itemDc.add(Subqueries.propertyIn(Item.FIELD.id.name(), repackedItemDc));
				return itemDc.getExecutableCriteria(session);

			}
		});
	}
	
	@Override
	public boolean hasDuplicateDescription(Item item) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Item.FIELD.description.name(), item.getDescription().trim()));
		dc.add(Restrictions.ne(Item.FIELD.id.name(), item.getId()));
		return getAll(dc).size() >= 1;
	}

	@Override
	public Page<ReceivedStock> getItemReceivedStocks(int itemId, int warehouseId, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("get_item_received_stocks_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		sql += " WHERE DATE <= ? ORDER BY DATE DESC, CREATED_DATE DESC, FORM_ID DESC";
		return getAllAsPage(sql, pageSetting, new ItemReceivedStockHandler(itemId, warehouseId, new Date(), null, itemReceivedStocksTblCount));
	}

	@Override
	public Page<ReceivedStock> getItemReceivedStocksAsOf(int itemId, int warehouseId,
			Date asOfDate, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("get_item_received_stocks_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		sql += " WHERE DATE <= ? AND QUANTITY > 0 ORDER BY DATE DESC, CREATED_DATE DESC, FORM_ID DESC ";
		return getAllAsPage(sql, pageSetting, new ItemReceivedStockHandler(itemId, warehouseId, asOfDate, null, itemReceivedStocksTblCount));
	}

	@Override
	public Page<ReceivedStock> getItemReceivedStocksAfterDate(int itemId,
			int warehouseId, Date startDate, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("get_item_received_stocks_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		sql += " WHERE DATE > ? AND QUANTITY > 0 ORDER BY DATE DESC, CREATED_DATE DESC, FORM_ID DESC ";
		return getAllAsPage(sql, pageSetting, new ItemReceivedStockHandler(itemId, warehouseId, startDate, null, itemReceivedStocksTblCount));
	}

	@Override
	public Page<ReceivedStock> getItemReceivedStocks(int itemId,
			int warehouseId, Date startDate, Date endDate, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("get_item_received_stocks_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		sql += " WHERE DATE BETWEEN ? AND ? ORDER BY DATE DESC, CREATED_DATE DESC, FORM_ID DESC ";
		return getAllAsPage(sql, pageSetting, new ItemReceivedStockHandler(itemId, warehouseId, startDate, endDate, itemReceivedStocksTblCount));
	}

	private static class ItemReceivedStockHandler extends ReceivedStocksRetriever {
		private final Integer warehouseId;
		private final Integer tblCount;

		private ItemReceivedStockHandler (Integer itemId, Integer warehouseId, Date dateFrom, Date dateTo, Integer tblCount) {
			super (itemId, dateFrom, dateTo);
			this.warehouseId = warehouseId;
			this.tblCount = tblCount;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int totalNoOfTables = tblCount;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index++, super.itemId);
				query.setParameter(index, warehouseId);
				if(i < (totalNoOfTables-1))
					index++;
			}

			if(super.startDate != null) {
				query.setParameter(++index, super.startDate);
			}

			if(super.endDate != null) {
				query.setParameter(++index, super.endDate);
			}
			return index;
		}
	}

	@Override
	public double getItemExistingStocks(int itemId, int warehouseId, Date asOfDate) {
		return getItemExistingStocks(itemId, warehouseId, asOfDate, -1);
	}

	@Override
	public Page<PhysicalInventory> generateInvListingFromUnusedStocks(Integer itemCategoryId, int companyId,
			int warehouseId, int stockOptionId, Date asOfDate, int statusId, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("inventory_listing_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		if(stockOptionId != -1)
			sql += " WHERE ROUND(SUM(BALANCE),3) > 0 ";
		sql += " ORDER BY STOCK_CODE ASC, CREATED_DATE ASC, FORM ASC ";
		return getAllAsPage(sql, pageSetting, new InvListingHandlerForUnusedStocks(itemCategoryId,
				companyId, warehouseId, asOfDate, statusId, 1));
	}

	private static class InvListingHandlerForUnusedStocks implements QueryResultHandler<PhysicalInventory> {
		private final Integer itemCategoryId;
		private final Integer companyId;
		private final Integer warehouseId;
		private final Date asOfDate;
		private final int statusId;
		private final int workflowStatusId;

		private InvListingHandlerForUnusedStocks (Integer itemCategoryId, Integer companyId,
				Integer warehouseId, Date asOfDate, int statusId, int workflowStatusId) {
			this.itemCategoryId = itemCategoryId;
			this.companyId = companyId;
			this.warehouseId = warehouseId;
			this.asOfDate = asOfDate;
			this.statusId = statusId;
			this.workflowStatusId = workflowStatusId;
		}

		@Override
		public List<PhysicalInventory> convert(List<Object[]> queryResult) {
			List<PhysicalInventory> physicalInventoryItems = new ArrayList<PhysicalInventory>();
			PhysicalInventory piDto = null;
			for (Object[] rowResult : queryResult) {
				piDto = new PhysicalInventory();
				piDto.setItemId((Integer)rowResult[2]);
				piDto.setStockCode((String)rowResult[3]);
				piDto.setDescription((String)rowResult[4]);
				piDto.setItemCategoryId((Integer)rowResult[5]);
				piDto.setQuantity((Double)rowResult[6]);
				piDto.setUnitCost((Double)rowResult[7]);
				piDto.setAmount((Double) rowResult[8]);
				piDto.setMeasurement((String)rowResult[9]);
				piDto.setSrp((Double) rowResult[10]);
				physicalInventoryItems.add(piDto);
			}
			return physicalInventoryItems;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numberOfTables = 28;
			query.setParameter(index++, companyId);
			query.setParameter(index++, itemCategoryId);
			query.setParameter(index++, itemCategoryId);
			query.setParameter(index++, itemCategoryId);
			query.setParameter(index++, statusId);
			query.setParameter(index++, statusId);
			query.setParameter(index++, statusId);
			for (int i = 0; i < numberOfTables; i++) {
				//loop through 11 tables.
				query.setParameter(index++, warehouseId);
				query.setParameter(index++, asOfDate);
				query.setParameter(index++, itemCategoryId);
				query.setParameter(index++, itemCategoryId);
				query.setParameter(index++, itemCategoryId);
				query.setParameter(index++, statusId);
				query.setParameter(index++, statusId);
				query.setParameter(index++, statusId);
				if(i < (numberOfTables-1))
					index++;
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("FORM", Hibernate.STRING);
			query.addScalar("WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("CATEGORY_ID", Hibernate.INTEGER);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("SRP", Hibernate.DOUBLE);
		}
	}

	private static class InventoryListingHandler implements QueryResultHandler<PhysicalInventory>{
		@Override
		public List<PhysicalInventory> convert(List<Object[]> queryResult) {
			List<PhysicalInventory> physicalInventoryItems = new ArrayList<PhysicalInventory>();
			PhysicalInventory piDto = null;
			for (Object[] rowResult : queryResult) {
				piDto = new PhysicalInventory();
				piDto.setItemId((Integer)rowResult[0]);
				piDto.setStockCode((String)rowResult[1]);
				piDto.setDescription((String)rowResult[2]);
				piDto.setItemCategoryId((Integer)rowResult[3]);
				piDto.setUnitMeasurementId((Integer)rowResult[4]);
				piDto.setQuantity((Double)rowResult[5]);
				piDto.setUnitCost((Double)rowResult[6]);
				piDto.setDivisionName((String)rowResult[7]);
				piDto.setAmount(piDto.getQuantity() * piDto.getUnitCost());
				piDto.setSrp((Double)rowResult[10]);
				piDto.setMeasurement((String)rowResult[11]);
				physicalInventoryItems.add(piDto);
			}
			return physicalInventoryItems;
		}

		@Override
		public int setParamater(SQLQuery query) {
			// Do nothing
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			// Do nothing
		}
	}

	@Override
	public Page<PhysicalInventory> generateInventoryListingData(Integer divisionId,Integer itemCategoryId, int companyId,
			int warehouseId, int stockOptionId, Date asOfDate, int statusId, int workflowStatusId, int orderBy, PageSetting pageSetting) {
		InventoryListingHandler handler = new InventoryListingHandler();
		return executePagedSP("GET_INVENTORY_LISTING", pageSetting, handler, companyId, asOfDate, itemCategoryId, divisionId,
				warehouseId, statusId, workflowStatusId, orderBy, stockOptionId);
	}

	@Override
	public Page<StockcardDto> getStockcardPerItem(int itemId, int companyId, Integer divisionId,
			int warehouseId, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		
		return executePagedSP("GET_STOCKARD_PER_ITEM", pageSetting,
				new StockcardPerItemHandler(),companyId, itemId, warehouseId, divisionId, dateFrom, dateTo);
	}

	private static class StockcardPerItemHandler implements QueryResultHandler<StockcardDto> {
		@Override
		public List<StockcardDto> convert(List<Object[]> queryResult) {
			List<StockcardDto> itemTransactions = new ArrayList<StockcardDto>();
			StockcardDto scd = null;
			for (Object[] rowResult : queryResult){
				scd = new StockcardDto();
				scd.setDate((Date)rowResult[1]);
				scd.setFormNumber((String)rowResult[2]);
				scd.setInvoiceNumber((String)rowResult[3]);
				scd.setDescription((String)rowResult[4]);
				scd.setQuantity((Double) rowResult[5]);
				scd.setUnitCost((Double) rowResult[6]);
				Double srp = ((BigDecimal)rowResult[7]).doubleValue();
				scd.setSrp(srp);
				scd.setDivisionName((String)rowResult[8]);
				scd.setBmsNumber((String)rowResult[9]);
				itemTransactions.add(scd);
			}
			return itemTransactions;
		}

		@Override
		public int setParamater(SQLQuery query) {
			// do nothing
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
		}
	}

	@Override
	public List<StockcardDto> getStockcardBeginningBal(int companyId, int itemId, int warehouseId, int typeId, Date asOfDate) {
		List<Object> begBalance = 
				executeSP("GET_BEGINNING_BALANCE", companyId, itemId, warehouseId, asOfDate);
		if(begBalance ==  null  || begBalance.isEmpty()) { 
			return null;
		}
		
		List<StockcardDto> ret = new ArrayList<StockcardDto>();
		for (Object obj : begBalance) {
			Object[] row = (Object[]) obj;
			String source = String.valueOf(row[0]);
			Object objQuantity = row[3];
			Double quantity = objQuantity == null ? 0 : (Double) objQuantity;
			Object objUnitCost = row[4];
			Double unitCost = objUnitCost == null ? 0 : (Double) objUnitCost;

			StockcardDto dto = new StockcardDto();
			dto.setFormName(source);
			dto.setDescription("Beginning Balance");
			dto.setQuantity(quantity);
			dto.setUnitCost(unitCost);
			ret.add(dto);

		}
		return ret;
	}	

	@Override
	public List<GrossProfitAnalysis> getGrossProfitAnalysis(Integer companyId, Integer divisionId,
			Integer itemCategoryId, Date dateFrom, Date dateTo) {
		List<GrossProfitAnalysis> gpaDto = new ArrayList<GrossProfitAnalysis>();
		List<Object> gpaList = executeSP("GET_GROSS_PROFIT_ANALYSIS", companyId, divisionId, itemCategoryId,
				dateFrom, dateTo);
		if (gpaList != null && !gpaList.isEmpty()) {
			for (Object obj : gpaList) {
				// Type casting
				Object[] row = (Object[]) obj;
				int colNum = 0;
				String division = (String)row[colNum]; //1
				String stockCode = (String)row[++colNum]; //2
				String description = (String)row[++colNum]; //3
				Double qtySold = (Double)row[++colNum]; //4
				String uom = (String)row[++colNum]; //5
				Double netSales = (Double)row[++colNum]; //6
				Double costOfSales = (Double)row[++colNum]; //7
				Double grossProfit = netSales - costOfSales;
				Double grossProfitPercent = grossProfit / netSales;
				GrossProfitAnalysis gpa = GrossProfitAnalysis.getInstanceOf(stockCode, division, description, 
						qtySold, uom, netSales, costOfSales, grossProfit, grossProfitPercent);
				gpaDto.add(gpa);
			}
			return gpaDto;
		}
		return null;
	}

	@Override
	public Page<ItemTransactionHistory> getFutureWithdrawalTransactions (int companyId, int warehouseId, 
			int itemId, Date date, PageSetting pageSetting) {
		return executePagedSP("GET_ITEM_FUTURE_WITHDRAWAL_TRANSACTIONS",
				pageSetting, new ItemTransHistoryHandler(), companyId, warehouseId, itemId, date);
	}
	
	private static class ItemTransHistoryHandler implements QueryResultHandler<ItemTransactionHistory> {

		@Override
		public List<ItemTransactionHistory> convert(List<Object[]> result) {
			List<ItemTransactionHistory> ret = new ArrayList<>();
			if (result != null && !result.isEmpty()) {
				for (Object obj : result) {
					Object[] row = (Object[]) obj;
					Integer itemId = (Integer) row[0];
					Date date = (Date) row[1];
					Date createdDate = (Date) row[2];
					Integer warehouseId = (Integer) row[3];
					Double quantity = (Double) row[4];
					Double unitCost =  (Double) row[5];
					Integer parentObjectId = (Integer) row[6];
					Integer ebObjectId = (Integer) row[7]; 

					ItemTransactionHistory ith = new ItemTransactionHistory();
					ith.setItemId(itemId);
					ith.setDate(date);
					ith.setCreatedtDate(createdDate);
					ith.setWarehouseId(warehouseId);
					ith.setQuantity(quantity);
					ith.setUnitCost(unitCost);
					ith.setParentObjectId(parentObjectId);
					ith.setEbObjectId(ebObjectId);
					ret.add(ith);
				}
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			// Do nothing
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			// DO nothing
			
		}
		
	}
	
	@Override
	public Page<ItemTransaction> getItemTransaction(int itemId,
			int warehouseId, Date date, PageSetting pageSetting) {
		String sql = SQLProperyReader.getValue("item_transaction_sql", SQLProperyReader.RETAIL_ITEM_SQL);
		sql += " ORDER BY DATE ASC, CREATED_DATE ASC ";
		return getAllAsPage(sql, pageSetting, new ItemTransactionHandler(itemId, warehouseId, date, itemTransactionTbaleCount));
	}

	private static class ItemTransactionHandler implements QueryResultHandler<ItemTransaction> {
		private final int itemId;
		private final int warehouseId;
		private final int totalNoOfTables;
		private Date date;

		private ItemTransactionHandler(int itemId, int warehouseId, Date date, int totalNoOfTbles) {
			this.itemId = itemId;
			this.warehouseId = warehouseId;
			this.totalNoOfTables = totalNoOfTbles;
			this.date = date;
		}

		@Override
		public List<ItemTransaction> convert(List<Object[]> queryResult) {
			List<ItemTransaction> transactions = new ArrayList<ItemTransaction>();
			for (Object[] rowResult : queryResult) {
				transactions.add(ItemTransaction.getRItemTransaction((Integer)rowResult[0],
						(Integer)rowResult[8], (Integer)rowResult[3], (Integer)rowResult[4],
						(Integer)rowResult[5], (Double) rowResult[9], (Double) rowResult[10],
						(String)rowResult[6], (Integer)rowResult[7]));
			}
			return transactions;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0 ; i < totalNoOfTables; i ++) {
				query.setParameter(index++, itemId);
				query.setParameter(index++, date);
				query.setParameter(index, warehouseId);
				if(i < (totalNoOfTables-1))
					index++;
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("CREATED_DATE", Hibernate.DATE);
			query.addScalar("WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("DISCOUNT", Hibernate.INTEGER);
			query.addScalar("SRP", Hibernate.INTEGER);
			query.addScalar("FORM", Hibernate.STRING);
			query.addScalar("NUMBER", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
		}
	}

	@Override
	public List<DailyItemSale> getDailyItemSales(Integer companyId,
			String stockCode, Date dateFrom, Date dateTo, Integer itemCategoryId, Integer warehouseId) {
		List<DailyItemSale> dailyItemSales = new ArrayList<DailyItemSale>();
		List<Object> objects = executeSP("GET_DAILY_ITEM_SALES", companyId, stockCode, dateFrom,
				dateTo, itemCategoryId, warehouseId);
		if (objects != null && !objects.isEmpty()) {
			for (Object obj : objects) {
				// Type casting
				Object[] row = (Object[]) obj;
				int columnNumber = 0;
				String disStockCode = (String)row[columnNumber++]; //0
				String description = (String)row[columnNumber++]; //1
				String uom = (String)row[columnNumber++]; //2
				DailyItemSale dis = DailyItemSale.getInstanceOf(disStockCode, description, uom);
				dailyItemSales.add(dis);
			}
		}
		return dailyItemSales;
	}

	@Override
	public List<DailyItemSaleDetail> getDailyItemSaleDetails(Integer companyId, String stockCode,
			String invoiceNo, Date dateFrom, Date dateTo, Integer itemCategoryId, Integer warehouseId) {
		List<DailyItemSaleDetail> details = new ArrayList<DailyItemSaleDetail>();
		List<Object> objects = executeSP("GET_DAILY_ITEM_SALE_DETAIL", companyId, 
				stockCode, invoiceNo, dateFrom, dateTo, itemCategoryId, warehouseId);
		if (objects != null && !objects.isEmpty()) {
			for (Object obj : objects) {
				// Type casting
				Object[] row = (Object[]) obj;
				int columnNumber = 0;
				String sequenceNo =  (String)row[++columnNumber]; //1
				String invoiceNumber = (String)row[++columnNumber]; //2
				double quantity = (Double)row[++columnNumber]; //3
				double amount = (Double)row[++columnNumber]; //4
				DailyItemSaleDetail disd = 
						DailyItemSaleDetail.getInstanceOf(sequenceNo, invoiceNumber, quantity, amount);
				details.add(disd);
			}
		}
		return details;
	}

	@Override
	public Page<ItemSalesCustomer> getItemSoldByCustomer (int companyId, int divisionId, int customerId, int customerAccountId,
			int itemCategoryId, int itemId, Date dateFrom, Date dateTo, boolean isExcludeReturns,
			Integer warehouseId, PageSetting pageSetting) {
		
		ItemSoldByCustomerHandler handler = new ItemSoldByCustomerHandler();
		return executePagedSP("GET_ITEM_SOLD_TO_COSTUMER", pageSetting, handler, companyId, divisionId, customerId, customerAccountId,
				itemCategoryId, itemId, dateFrom, dateTo);
	}

	private static class ItemSoldByCustomerHandler implements QueryResultHandler<ItemSalesCustomer> {
		
		@Override
		public List<ItemSalesCustomer> convert(List<Object[]> queryResult) {
			List<ItemSalesCustomer> itemSalesCustomers = new ArrayList<ItemSalesCustomer>();
			for (Object[] rowResult : queryResult) {
			int colNum = 0;
				String stockCode = (String) rowResult[colNum++];
				String description = (String) rowResult[colNum++];
				Double qty = (Double) rowResult[colNum++];
				String uom = (String) rowResult[colNum++];
				Integer itemId = (Integer) rowResult[colNum++];
				String divisionName = (String) rowResult[colNum++];
				String refNo = (String) rowResult[colNum++];
				ItemSalesCustomer isc = new ItemSalesCustomer();
				
					isc.setStockCode(stockCode);
					isc.setDescription(description);
					isc.setQty(qty);
					isc.setUom(uom);
					isc.setItemId(itemId);
					isc.setRefNo(refNo);
					isc.setDivisionName(divisionName);
					itemSalesCustomers.add(isc);
				
			}
			return itemSalesCustomers;
		}
		
		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0; //-1
		}
	}

	@Override
	public Page<ItemSalesCustomer> getItemSalesCustomer(int companyId, int divisionId,
			int customerId, int customerAccountId, int itemCategoryId, int itemId,
			Date dateFrom, Date dateTo, PageSetting pageSetting) {
		ItemSalesCustomerHandler itemSalesCustomerHandler = new ItemSalesCustomerHandler();
		return executePagedSP("GET_ITEM_SOLD_TO_COSTUMER", pageSetting, itemSalesCustomerHandler,
				companyId, divisionId, customerId, customerAccountId, itemCategoryId, itemId, dateFrom, dateTo);
	}

	private static class ItemSalesCustomerHandler implements QueryResultHandler<ItemSalesCustomer> {

		private ItemSalesCustomerHandler() {}

		@Override
		public List<ItemSalesCustomer> convert(List<Object[]> queryResult) {
			List<ItemSalesCustomer> itemSoldByCustomer = new ArrayList<ItemSalesCustomer>();
			ItemSalesCustomer isBc = null;
			for (Object[] rowResult : queryResult) {
				isBc = new ItemSalesCustomer();
				isBc.setDivisionName((String)rowResult[0]);
				isBc.setDate((Date)rowResult[1]);
				isBc.setStockCode((String)rowResult[2]);
				isBc.setDescription((String)rowResult[3]);
				isBc.setBmsNumber((String)rowResult[4]);
				isBc.setRefNo((String)rowResult[5]);
				isBc.setQty((Double)rowResult[6]);
				isBc.setUom((String)rowResult[7]);
				isBc.setSrp((Double)rowResult[8]);
				isBc.setAmount((Double)rowResult[9]);
				isBc.setDiscount((Double)rowResult[10]);
				isBc.setNetAmount((Double)rowResult[11]);
				itemSoldByCustomer.add(isBc);
			}
			return itemSoldByCustomer;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
//			query.addScalar("DIVISION_NAME", Hibernate.STRING);
//			query.addScalar("DATE", Hibernate.DATE);
//			query.addScalar("STOCK_CODE", Hibernate.STRING);
//			query.addScalar("DESCRIPTION", Hibernate.STRING);
//			query.addScalar("BMS", Hibernate.STRING);
//			query.addScalar("REF_NO", Hibernate.STRING);
//			query.addScalar("QTY", Hibernate.DOUBLE);
//			query.addScalar("UOM", Hibernate.STRING);
//			query.addScalar("SRP", Hibernate.DOUBLE);
//			query.addScalar("AMOUNT", Hibernate.DOUBLE);
//			query.addScalar("DISCOUNT", Hibernate.DOUBLE);
//			query.addScalar("NET_AMOUNT", Hibernate.DOUBLE);
		}
	}

	@Override
	public Item getRetailItem(final String stockCode, final Integer companyId, final Integer warehouseId,
			final boolean isActiveOnly, final Integer divisionId, Boolean isExcludeFG) {
		return getHibernateTemplate().execute(new ItemHibernateCallBack(this) {
			
			@Override
			protected Criteria getCriteria(Session session) {
				Criteria criteria = session.createCriteria(Item.class);
				if(stockCode != null) {
					criteria.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode));
				}
				if (isActiveOnly) {
					criteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				}

				if(isExcludeFG != null && isExcludeFG) {
					DetachedCriteria dc = DetachedCriteria.forClass(ItemCategory.class);
					dc.setProjection(Projections.property(ItemCategory.FIELD.id.name()));
					dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%Finished Goods%"));
					criteria.add(Subqueries.propertyNotIn(Item.FIELD.itemCategoryId.name(), dc));
				}

				if (companyId != null) {
					DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
					srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
					if (divisionId != null) {
						srpDc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
					}
					criteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
				}
				return criteria;
			}
			@Override
			protected void initializeOtherReference(Item item, HibernateTemplate ht) {
				if(warehouseId != null) {
					item.setExistingStocks(getItemExistingStocks(item.getId(), warehouseId, new Date()));
				}
				ht.initialize(item.getItemSrps());
				ht.initialize(item.getItemDiscounts());
				ht.initialize(item.getItemAddOns());
			}
		});
	}


	@Override
	public List<RItemDetail> getRItemDetails(int itemId, boolean isActiveOnly) {
		StringBuffer sql = new StringBuffer("SELECT ITEM_ID, COMPANY_ID, COMPANY_NAME, SP, "
				+ "BUYING_PRICE AS BP, DIVISION_NAME FROM ( "
				+ "SELECT ISP.ITEM_ID, ISP.COMPANY_ID, SC.NAME AS COMPANY_NAME, ISP.SRP AS SP, "
				+ "0 AS BUYING_PRICE, SD.NAME AS DIVISION_NAME "
				+ "FROM ITEM_SRP ISP "
				+ "INNER JOIN COMPANY SC ON SC.COMPANY_ID = ISP.COMPANY_ID "
				+ "LEFT JOIN DIVISION SD ON SD.DIVISION_ID = ISP.DIVISION_ID "
				+ "WHERE ISP.ITEM_ID = ? ");
				if (isActiveOnly) {
					sql.append("AND ISP.ACTIVE = 1 ");
				}
//				Disabling buying price
//				sql.append("UNION ALL ");
//				sql.append("SELECT IBP.ITEM_ID,  IBP.COMPANY_ID, BC.NAME AS COMPANY_NAME, "
//						+ "0, IBP.BUYING_PRICE, '' AS DIVISION_NAME "
//						+ "FROM ITEM_BUYING_PRICE IBP "
//						+ "INNER JOIN COMPANY BC ON BC.COMPANY_ID = IBP.COMPANY_ID "
//						+ "WHERE IBP.ITEM_ID = ? ");
//				if (isActiveOnly) {
//					sql.append("AND IBP.ACTIVE = 1 ");
//				}
				sql.append(") AS ITEM_PRICE_TBL");
		return (List<RItemDetail>) get(sql.toString(), new RItemDetailHandler(itemId, this));
	}

	private static class RItemDetailHandler implements QueryResultHandler<RItemDetail> {
		private int itemId;
		private ItemDaoImpl daoImpl;

		private RItemDetailHandler (int itemId, ItemDaoImpl daoImpl) {
			this.itemId = itemId;
			this.daoImpl = daoImpl;
		}

		@Override
		public List<RItemDetail> convert(List<Object[]> queryResult) {
			List<RItemDetail> itemDetails = new ArrayList<RItemDetail>();
			RItemDetail detail = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				detail = new RItemDetail();
				detail.setItemId((Integer) rowResult[colNum++]);
				detail.setCompanyId((Integer) rowResult[colNum++]);
				detail.setCompanyName((String) rowResult[colNum++]);
				detail.setSellingPrice((Double) rowResult[colNum++]);
				detail.setBuyingPrice((Double) rowResult[colNum++]);
				detail.setDivisionName((String) rowResult[colNum++]);
				detail.setValues(getDetailValues(itemId, detail.getCompanyId()));
				itemDetails.add(detail);
			}
			return itemDetails;
		}

		@Override
		public int setParamater(SQLQuery query) {
			query.setParameter(0, itemId);
//			query.setParameter(1, itemId);
			return 1;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_NAME", Hibernate.STRING);
			query.addScalar("SP", Hibernate.DOUBLE);
			query.addScalar("BP", Hibernate.DOUBLE);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
		}

		private List<RItemDetailValue> getDetailValues(final int itemId, final int companyId) {
			StringBuffer sql = new StringBuffer("SELECT ISD.ITEM_DISCOUNT_ID AS ID, ISD.NAME, ISD.VALUE, 1 AS TYPE_ID "
					+ "FROM ITEM_DISCOUNT ISD WHERE ISD.ITEM_ID = ? AND ISD.COMPANY_ID = ? AND ISD.ACTIVE = 1 "
					+ "UNION ALL "
					+ "SELECT ISA.ITEM_ADD_ON_ID AS ID, ISA.NAME, ISA.VALUE, 2 AS TYPE_ID "
					+ "FROM ITEM_ADD_ON ISA WHERE ISA.ITEM_ID = ? AND ISA.COMPANY_ID = ? AND ISA.ACTIVE = 1  "
					+ "UNION ALL "
					+ "SELECT IBD.ITEM_BUYING_DISCOUNT_ID AS ID, IBD.NAME, IBD.VALUE, 3 AS TYPE_ID "
					+ "FROM ITEM_BUYING_DISCOUNT IBD WHERE IBD.ITEM_ID = ? AND IBD.COMPANY_ID = ? AND IBD.ACTIVE = 1 "
					+ "UNION ALL "
					+ "SELECT IBA.ITEM_BUYING_ADD_ON_ID AS ID, IBA.NAME, IBA.VALUE, 4 AS TYPE_ID  "
					+ "FROM ITEM_BUYING_ADD_ON IBA WHERE IBA.ITEM_ID = ? AND IBA.COMPANY_ID = ? AND IBA.ACTIVE = 1  ");
			Collection<RItemDetailValue> colItemValues = daoImpl.get(sql.toString(), new QueryResultHandler<RItemDetailValue>() {

				@Override
				public List<RItemDetailValue> convert(List<Object[]> queryResult) {
					List<RItemDetailValue> rItemValues = new ArrayList<RItemDetailValue>();
					for (Object[] rowResult : queryResult) {
						rItemValues.add(RItemDetailValue.getInstanceOf(
								(Integer)rowResult[3],
								(String)rowResult[1],
								(Double)rowResult[2]));
					}
					return rItemValues;
				}

				@Override
				public int setParamater(SQLQuery query) {
					int index = 0;
					int numOfTables = 4;
					for (int i = 0; i < numOfTables; i++) {
						query.setParameter(index++, itemId);
						query.setParameter(index++, companyId);
					}
					return --index;
				}

				@Override
				public void setScalars(SQLQuery query) {
					query.addScalar("ID", Hibernate.INTEGER);
					query.addScalar("NAME", Hibernate.STRING);
					query.addScalar("VALUE", Hibernate.DOUBLE);
					query.addScalar("TYPE_ID", Hibernate.INTEGER);
				}
			});

			return (List<RItemDetailValue>) colItemValues;
		}
	}
	@Override
	public Page<ReorderPointDto> getReorderingPointData(Integer companyId, Integer divisionId, Integer warehousId,
			Integer statusId, Integer categoryId, Date asOfDate, String orderBy, Integer stockOptionId,
			boolean isExcludePO, String description, PageSetting pageSetting) {
		ReorderingPointHandler handler = new ReorderingPointHandler(companyId, categoryId, warehousId, 
				asOfDate, divisionId, statusId);
		return executePagedSP("GET_REORDERING_POINT", pageSetting, handler, companyId, categoryId, warehousId, asOfDate, divisionId, statusId);
	}

	private static class ReorderingPointHandler implements QueryResultHandler<ReorderPointDto> {
		private Integer companyId;
		private Integer categoryId;
		private Integer warehousId;
		private Date asOfDate;
		private Integer divisionId;
		private Integer statusId;

		private ReorderingPointHandler(Integer companyId, Integer categoryId, Integer warehousId,
				Date asOfDate, Integer divisionId, Integer statusId) {
			this.companyId = companyId;
			this.categoryId = categoryId;
			this.warehousId = warehousId;
			this.asOfDate = asOfDate;
			this.divisionId = divisionId;
			this.statusId = statusId;
		}

		@Override
		public List<ReorderPointDto> convert(List<Object[]> queryResult) {
			List<ReorderPointDto> rpDtos = new ArrayList<ReorderPointDto>();
			ReorderPointDto rpDto = null;
			for (Object[] rowResult : queryResult) {
				int index = 0;
				rpDto = new ReorderPointDto();
				rpDto.setItemId((Integer) rowResult[index]);
				rpDto.setWarehouseId((Integer) rowResult[++index]);
				rpDto.setStockCode((String) rowResult[++index]);
				rpDto.setDescription((String) rowResult[++index]);
				rpDto.setDivisionName((String) rowResult[++index]);
				rpDto.setWarehouseName((String) rowResult[++index]);
				rpDto.setUom((String) rowResult[++index]);
				rpDto.setReorderingPoint((Integer) rowResult[++index]);
				rpDto.setOnHand((Double) rowResult[++index]);
				rpDtos.add(rpDto);
			}
			return rpDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			// Item SRP
			query.setParameter(index, companyId);
			if(categoryId != null) {
				query.setParameter(++index, categoryId);
			}
			if(warehousId != null) {
				query.setParameter(++index, warehousId);
			}
			if(asOfDate != null) {
				query.setParameter(++index, asOfDate);
			}
			if(divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			if(statusId != -1) {
				query.setParameter(++index, statusId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {}

	}

	private double getTotalUnservePO (int itemId) {
		String sql = "SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY FROM ("
				+ "SELECT POI.ITEM_ID, POI.QUANTITY "
				+ "FROM R_PURCHASE_ORDER PO "
				+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID AND FW.IS_COMPLETE = 1 "
				+ "WHERE POI.ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT RRI.ITEM_ID, -RRI.QUANTITY FROM AP_INVOICE API "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RRI ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON OTO.FROM_OBJECT_ID = POI.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID AND FW.CURRENT_STATUS_ID != 4 "
				+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID AND FW1.IS_COMPLETE = 1 "
				+ "WHERE RRI.ITEM_ID = ?) "
				+ "TBL GROUP BY ITEM_ID";
		Collection<Double> unservePo = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {
					Double qty = (Double) row[1];
					ret.add(qty);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, itemId);
				query.setParameter(++index, itemId);
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ITEM_ID", Hibernate.DOUBLE);
				query.addScalar("QUANTITY", Hibernate.DOUBLE);
			}
		});
		return !unservePo.isEmpty() ? unservePo.iterator().next() : 0;
	}

	@Override
	public List<Item> getBuyingItems(final Integer companyId, final String stockCode) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Item>>() {

			@Override
			public List<Item> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria itemCrit = session.createCriteria(Item.class);

				if (companyId != null) {
					DetachedCriteria bpDc = DetachedCriteria.forClass(ItemBuyingPrice.class);
					bpDc.setProjection(Projections.property(ItemBuyingPrice.FIELD.itemId.name()));
					bpDc.add(Restrictions.gt(ItemBuyingPrice.FIELD.buyingPrice.name(), 0.0));

					bpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.active.name(), true));
					bpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.companyId.name(), companyId));
					itemCrit.add(Subqueries.propertyIn(Item.FIELD.id.name(), bpDc));
				}

				if (stockCode != null && !stockCode.trim().isEmpty()) {
					itemCrit.add(Restrictions.or(Restrictions.like(Item.FIELD.description.name(), "%" + stockCode.trim() + "%"),
							Restrictions.like(Item.FIELD.stockCode.name(), "%" + stockCode.trim() + "%")));
				}

				itemCrit.add(Restrictions.eq(Item.FIELD.active.name(), true));
				itemCrit.setMaxResults(10);
				return getAllByCriteria(itemCrit);
			}
		});
	}

	@Override
	public Item getBuyingItem(Integer companyId, Integer warehouseId, String stockCode) {
		DetachedCriteria itemDc = getDetachedCriteria();
		itemDc.add(Restrictions.eq(Item.FIELD.active.name(), true));
		itemDc.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode.trim()));
		DetachedCriteria ibpDc = DetachedCriteria.forClass(ItemBuyingPrice.class);
		ibpDc.setProjection(Projections.property(ItemBuyingPrice.FIELD.itemId.name()));
		ibpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.companyId.name(), companyId));
		itemDc.add(Subqueries.propertyIn(Item.FIELD.id.name(), ibpDc));
		Item retailItem = get(itemDc);
		if(retailItem == null) {
			return null;
		}
		//Set the existing stocks of the item.
		retailItem.setExistingStocks(getTotalAvailStocks(stockCode, warehouseId));
		return retailItem;
	}

	@Override
	public List<AvailableStock> getAvailableStocks(String stockCode, Integer warehouseId, Integer companyId) {
		StringBuilder sql = new StringBuilder(availableStocksSql(warehouseId, stockCode, companyId));
		sql.append(" ORDER BY DATE, CREATED_DATE ASC ");
		return (List<AvailableStock>) get(sql.toString(),
				new AvailableStockHandler(stockCode, warehouseId, companyId));
	}

	private String availableStocksSql(Integer warehouseId, String stockCode, Integer companyId) {
		return availableStocksSql(warehouseId, null, stockCode, null, companyId);
	}

	private String availableStocksSql(Integer warehouseId, Integer itemCategoryId, String stockCode, Date asOfDate, Integer companyId) {
		boolean hasStockCode = stockCode != null && !stockCode.trim().isEmpty();
		String sql = "SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY, UNIT_COST, EB_OBJECT_ID, "
				+ "SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM, DATE, CREATED_DATE FROM ("
				+ "SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY, UNIT_COST, EB_OBJECT_ID, "
				+ "SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM, DATE, CREATED_DATE FROM ("
				+ "SELECT RRI.ITEM_ID,RRI.QUANTITY, RRI.UNIT_COST, EBO.EB_OBJECT_ID, EBO.EB_OBJECT_ID AS SOURCE_OBJECT_ID, "
				+ "CONCAT('RR-', INV.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, INV.GL_DATE AS DATE, INV.CREATED_DATE AS CREATED_DATE "
				+ "FROM R_RECEIVING_REPORT_RM_ITEM RRRMI "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = RRRMI.EB_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RRI ON RRI.R_RECEIVING_REPORT_ITEM_ID = RRRMI.R_RECEIVING_REPORT_ITEM_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = RRI.ITEM_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN AP_INVOICE INV ON INV.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = INV.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND INV.GL_DATE <= ? " : "")
				+ (warehouseId != null ? "AND RR.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND RR.COMPANY_ID = ? " : "")
				+ " UNION ALL "
				+ "SELECT I.ITEM_ID, 0 AS QUANTITY, 0 AS UNIT_COST, 0 AS EB_OBJECT_ID, 0 AS SOURCE_OBJECT_ID, "
				+ "'' AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, '1900-01-01' AS DATE, '1900-01-01' AS CREATED_DATE "
				+ "FROM ITEM I "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "INNER JOIN ITEM_BUYING_PRICE IBP ON IBP.ITEM_ID = I.ITEM_ID "
				+ "WHERE I.ITEM_ID NOT IN ( "
				+ "SELECT ITEM_ID FROM R_RECEIVING_REPORT_ITEM  "
				+ "WHERE AP_INVOICE_ID IN (SELECT AP_INVOICE_ID FROM AP_INVOICE "
				+ "WHERE FORM_WORKFLOW_ID IN (SELECT FORM_WORKFLOW_ID FROM FORM_WORKFLOW "
				+ "WHERE IS_COMPLETE = 1))) "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (companyId != null ? "AND IBP.COMPANY_ID = ? " : "")
				+ " UNION ALL "
				+ "SELECT OMI.ITEM_ID,-OMI.QUANTITY, OMI.UNIT_COST, EBO.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, "
				+ "CONCAT('PR-', PR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE AS CREATED_DATE "
				+ "FROM PR_OTHER_MATERIALS_ITEM OMI "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = OMI.EB_OBJECT_ID "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = OMI.PROCESSING_REPORT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = OMI.EB_OBJECT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = OMI.ITEM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND OO.OR_TYPE_ID = 2 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND PR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND OMI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND PR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT RMI.ITEM_ID,-RMI.QUANTITY, RMI.UNIT_COST, EBO.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('PR-', PR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE AS CREATED_DATE  "
				+ "FROM PR_RAW_MATERIALS_ITEM RMI "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = RMI.EB_OBJECT_ID "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = RMI.PROCESSING_REPORT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = RMI.ITEM_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = RMI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND OO.OR_TYPE_ID = 2 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND PR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND RMI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND PR.COMPANY_ID = ? " : "")
				+ " UNION ALL "
				+ "SELECT MP.ITEM_ID, MP.QUANTITY, MP.UNIT_COST, EBO.EB_OBJECT_ID, EBO.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('PR-', PR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE AS CREATED_DATE "
				+ "FROM PR_MAIN_PRODUCT MP "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = MP.EB_OBJECT_ID "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = MP.PROCESSING_REPORT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = MP.ITEM_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = MP.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND PR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND MP.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND PR.COMPANY_ID = ? " : "")
				+ " UNION ALL "
				+ "SELECT BP.ITEM_ID, BP.QUANTITY, BP.UNIT_COST, EBO.EB_OBJECT_ID, EBO.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('PR-', PR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE AS CREATED_DATE "
				+ "FROM PR_BY_PRODUCT BP "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = BP.EB_OBJECT_ID "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = BP.PROCESSING_REPORT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = BP.ITEM_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = BP.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND PR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND BP.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND PR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT SAO.ITEM_ID, SAO.QUANTITY, SAO.UNIT_COST, SAO.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('SAO-', SA.SA_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, SA.SA_DATE AS DATE, SA.CREATED_DATE AS CREATED_DATE "
				+ "FROM STOCK_ADJUSTMENT_ITEM SAO "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = SAO.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = SAO.EB_OBJECT_ID "
				+ "INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAO.STOCK_ADJUSTMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = SAO.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 3 "
				+ "AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 4 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND SA.SA_DATE <= ? " : "")
				+ (warehouseId != null ? "AND SA.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND SA.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT SAI.ITEM_ID, SAI.QUANTITY, SAI.UNIT_COST, SAI.EB_OBJECT_ID, SAI.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('SAI-', SA.SA_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, SA.SA_DATE AS DATE, SA.CREATED_DATE AS CREATED_DATE "
				+ "FROM STOCK_ADJUSTMENT_ITEM SAI "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = SAI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = SAI.EB_OBJECT_ID "
				+ "INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = SAI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 3 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND SA.SA_DATE <= ? " : "")
				+ (warehouseId != null ? "AND SA.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND SA.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT CSI.ITEM_ID, -CSI.QUANTITY, CSI.UNIT_COST, CSI.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('CS-', CS.CS_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, CS.RECEIPT_DATE AS DATE, CS.CREATED_DATE AS CREATED_DATE "
				+ "FROM CASH_SALE_ITEM CSI "
				+ "INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = CSI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = CSI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 4 "
				+ "AND CS.CASH_SALE_TYPE_ID = 3 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND CS.RECEIPT_DATE <= ? " : "")
				+ (warehouseId != null ? "AND CSI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND CS.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT ASI.ITEM_ID, -ASI.QUANTITY, ASI.UNIT_COST, ASI.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('AS-', AT.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, AT.TRANSACTION_DATE AS DATE, AT.CREATED_DATE AS CREATED_DATE "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = ASI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ASI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 5 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 10 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND AT.TRANSACTION_DATE <= ? " : "")
				+ (warehouseId != null ? "AND ASI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND AT.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT CSRI.ITEM_ID, -CSRI.QUANTITY, CSRI.UNIT_COST, CSRI.EB_OBJECT_ID, CSRI.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('CSR-', CSR.CSR_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, CSR.DATE AS DATE, CSR.CREATED_DATE AS CREATED_DATE "
				+ "FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = CSRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = CSRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND CSR.CASH_SALE_TYPE_ID = 3 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND CSR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND CSRI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND CSR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT CSRI.ITEM_ID, -CSRI.QUANTITY, CSRI.UNIT_COST, CSRI.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('CSR-', CSR.CSR_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, CSR.DATE AS DATE, CSR.CREATED_DATE AS CREATED_DATE "
				+ "FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = CSRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = CSRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 6 "
				+ "AND CSR.CASH_SALE_TYPE_ID = 3 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND CSR.DATE <= ? " : "")
				+ (warehouseId != null ? "AND CSRI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND CSR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT ASRI.ITEM_ID, -ASRI.QUANTITY, ASRI.UNIT_COST, ASRI.EB_OBJECT_ID, ASRI.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('ASR-', ASR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, ASR.TRANSACTION_DATE AS DATE, ASR.CREATED_DATE AS CREATED_DATE "
				+ "FROM ACCOUNT_SALE_ITEM ASRI "
				+ "INNER JOIN AR_TRANSACTION ASR ON ASR.AR_TRANSACTION_ID = ASRI.AR_TRANSACTION_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = ASRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ASRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ASR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND ASR.AR_TRANSACTION_TYPE_ID = 11 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND ASR.TRANSACTION_DATE <= ? " : "")
				+ (warehouseId != null ? "AND ASRI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND ASR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT ASRI.ITEM_ID, -ASRI.QUANTITY, ASRI.UNIT_COST, ASRI.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('ASR-', ASR.SEQUENCE_NO) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, ASR.TRANSACTION_DATE AS DATE, ASR.CREATED_DATE AS CREATED_DATE "
				+ "FROM ACCOUNT_SALE_ITEM ASRI "
				+ "INNER JOIN AR_TRANSACTION ASR ON ASR.AR_TRANSACTION_ID = ASRI.AR_TRANSACTION_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = ASRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ASRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ASR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 7 "
				+ "AND ASR.AR_TRANSACTION_TYPE_ID = 11 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND ASR.TRANSACTION_DATE <= ? " : "")
				+ (warehouseId != null ? "AND ASRI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND ASR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT TRI.ITEM_ID, TRI.QUANTITY, TRI.UNIT_COST, TRI.EB_OBJECT_ID, TRI.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('TRI-', TR.TR_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, TR.TR_DATE AS DATE, TR.CREATED_DATE AS CREATED_DATE "
				+ "FROM R_TRANSFER_RECEIPT_ITEM TRI "
				+ "INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRI.R_TRANSFER_RECEIPT_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = TRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = TRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = TRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 1 "
				+ "AND TR.TRANSFER_RECEIPT_TYPE_ID = 2 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND TR.TR_DATE <= ? " : "")
				+ (warehouseId != null ? "AND TR.WAREHOUSE_TO_ID = ? " : "")
				+ (companyId != null ? "AND TR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT TRO.ITEM_ID, -TRO.QUANTITY, TRO.UNIT_COST, TRO.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('TRO-', TR.TR_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, TR.TR_DATE AS DATE, TR.CREATED_DATE AS CREATED_DATE "
				+ "FROM R_TRANSFER_RECEIPT_ITEM TRO "
				+ "INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRO.R_TRANSFER_RECEIPT_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = TRO.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = TRO.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = TRO.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 8 "
				+ "AND TR.TRANSFER_RECEIPT_TYPE_ID = 2 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND TR.TR_DATE <= ? " : "")
				+ (warehouseId != null ? "AND TR.WAREHOUSE_FROM_ID = ? " : "")
				+ (companyId != null ? "AND TR.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT CAPDI.ITEM_ID, -CAPDI.QUANTITY, CAPDI.UNIT_COST, CAPDI.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('PAID-', CAPD.CAPD_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, CAPD.DELIVERY_DATE AS DATE, CAPD.CREATED_DATE AS CREATED_DATE "
				+ "FROM CAP_DELIVERY_ITEM CAPDI "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = CAPDI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = CAPDI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CAPDI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 12 "
				+ "AND CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND CAPD.DELIVERY_DATE <= ? " : "")
				+ (warehouseId != null ? "AND CAPDI.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND CAPD.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT ICL.FROM_ITEM_ID, -ICL.QUANTITY, ICL.UNIT_COST, ICL.EB_OBJECT_ID, OO.FROM_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('IC-', IC.ITEM_CONVERSION_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, IC.DATE AS DATE, IC.CREATED_DATE AS CREATED_DATE "
				+ "FROM ITEM_CONVERSION_LINE ICL "
				+ "INNER JOIN ITEM_CONVERSION IC ON ICL.ITEM_CONVERSION_ID = IC.ITEM_CONVERSION_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = ICL.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ICL.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = IC.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ICL.FROM_ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = 15 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND IC.DATE <= ? " : "")
				+ (warehouseId != null ? "AND IC.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND IC.COMPANY_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT ICL.TO_ITEM_ID, ICL.CONVERTED_QUANTITY, ICL.CONVERTED_UNIT_COST, ICL.EB_OBJECT_ID, ICL.EB_OBJECT_ID AS SOURCE_OBJECT, "
				+ "CONCAT('IC-', IC.ITEM_CONVERSION_NUMBER) AS SOURCE, I.STOCK_CODE, I.DESCRIPTION, UM.NAME AS UOM, IC.DATE AS DATE, IC.CREATED_DATE AS CREATED_DATE "
				+ "FROM ITEM_CONVERSION_LINE ICL "
				+ "INNER JOIN ITEM_CONVERSION IC ON ICL.ITEM_CONVERSION_ID = IC.ITEM_CONVERSION_ID "
				+ "INNER JOIN EB_OBJECT EBO ON EBO.EB_OBJECT_ID = ICL.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ICL.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = IC.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ICL.TO_ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE EBO.OBJECT_TYPE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 15 "
				+ (hasStockCode ? "AND (I.STOCK_CODE = ?) " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "")
				+ (asOfDate != null ? "AND IC.DATE <= ? " : "")
				+ (warehouseId != null ? "AND IC.WAREHOUSE_ID = ? " : "")
				+ (companyId != null ? "AND IC.COMPANY_ID = ? " : "")
				+ ") AS TBL GROUP BY " + (warehouseId != null ? "EB_OBJECT_ID, ITEM_ID ORDER BY EB_OBJECT_ID" : "ITEM_ID")
				+ ") AS AVAILABLE_STOCK GROUP BY " + (warehouseId != null ? "SOURCE_OBJECT_ID" : "ITEM_ID");
		return sql;
	}

	private static class AvailableStockHandler implements QueryResultHandler<AvailableStock> {
		private final String stockCode;
		private final Integer itemCategoryId;
		private final Integer warehouseId;
		private final Integer companyId;
		private final Date asOfDate;
		private final boolean addZeroQty;
		private int paramIndex;

		private AvailableStockHandler(String stockCode, Integer warehouseId, Integer companyId) {
			this.stockCode = stockCode;
			this.warehouseId = warehouseId;
			this.companyId = companyId;
			this.itemCategoryId = null;
			this.asOfDate = null;
			this.addZeroQty = false;
		}

		private AvailableStockHandler(String stockCode, Integer itemCategoryId,
					Integer warehouseId, Date asOfDate, boolean addZeroQty) {
			this.stockCode = stockCode;
			this.itemCategoryId = itemCategoryId;
			this.warehouseId = warehouseId;
			this.asOfDate = asOfDate;
			this.addZeroQty = addZeroQty;
			this.companyId = null;
		}

		@Override
		public List<AvailableStock> convert(List<Object[]> queryResult) {
			List<AvailableStock> availableStocks = new ArrayList<>();
			for (Object[] row : queryResult){
				AvailableStock availableStock = new AvailableStock();
				availableStock.setItemId((Integer) row[0]);
				// Add only if available quantity is not equal to zero.
				Object obj = row[1]; // empty return
				if (obj == null)
					break;
				availableStock.setQuantity(warehouseId != null ? (Double) row[1] : 0);
				double qty = availableStock.getQuantity().doubleValue();
				if (warehouseId == null || (warehouseId != null &&  (qty > 0 || addZeroQty ))) {
					availableStock.setUnitCost(warehouseId != null ? (Double) row[2] : null);
					availableStock.setEbObjectId(warehouseId != null ? (Integer) row[3] : null);
					availableStock.setSource(warehouseId != null ? (String) row[4] : null);
					availableStock.setStockCode((String) row[5]);
					availableStock.setDescription((String) row[6]);
					availableStock.setUnitOfMeasurement((String) row[7]);
					availableStocks.add(availableStock);
				}
			}
			return availableStocks;
		}

		@Override
		public int setParamater(SQLQuery query) {
			paramIndex = 0;
			//Receiving Report RM Item parameters
			query.setParameter(paramIndex, RReceivingReportRmItem.OBJECT_TYPE_ID);
			setCommonParam(query);

			//Receiving Report RM Item (NOT IN) parameters
			if(stockCode != null && !stockCode.trim().isEmpty()) {
				query.setParameter(++paramIndex, stockCode);
			}
			if(companyId != null) {
				query.setParameter(++paramIndex, companyId);
			}

			//Processing Report Other Materials parameters
			query.setParameter(++paramIndex, PrOtherMaterialsItem.OBJECT_TYPE_ID);
			setCommonParam(query);

			//Processing Report Raw Materials parameters
			query.setParameter(++paramIndex, PrRawMaterialsItem.OBJECT_TYPE_ID);
			setCommonParam(query);

			//Processing Report Main Product parameters
			query.setParameter(++paramIndex, PrMainProduct.OBJECT_TYPE_ID);
			setCommonParam(query);

			//Processing Report By Product parameters
			query.setParameter(++paramIndex, PrByProduct.OBJECT_TYPE_ID);
			setCommonParam(query);

			//Stock Adjustment OUT
			query.setParameter(++paramIndex, 11);
			setCommonParam(query);

			//Stock Adjustment IN
			query.setParameter(++paramIndex, 16);
			setCommonParam(query);

			//Cash Sales
			query.setParameter(++paramIndex, 12);
			setCommonParam(query);

			//Account Sales
			query.setParameter(++paramIndex, 15);
			setCommonParam(query);

			//Cash Sales Return
			query.setParameter(++paramIndex, 20);
			setCommonParam(query);

			//Cash Sales Exchange
			query.setParameter(++paramIndex, 21);
			setCommonParam(query);

			//Account Sales Return
			query.setParameter(++paramIndex, 23);
			setCommonParam(query);

			//Account Sales Exchange
			query.setParameter(++paramIndex, 24);
			setCommonParam(query);

			//Transfer Receipt - TO
			query.setParameter(++paramIndex, 26);
			setCommonParam(query);

			//Transfer Receipt - FROM
			query.setParameter(++paramIndex, 26);
			setCommonParam(query);

			//Paid In Advance Delivery - IS
			query.setParameter(++paramIndex, 54);
			setCommonParam(query);

			//Item Conversion - FROM
			query.setParameter(++paramIndex, 42);
			setCommonParam(query);

			//Item Conversion - TO
			query.setParameter(++paramIndex, 42);
			setCommonParam(query);

			return paramIndex;
		}

		private void setCommonParam(SQLQuery query) {
			if(stockCode != null && !stockCode.trim().isEmpty()) {
				query.setParameter(++paramIndex, stockCode);
//				query.setParameter(++paramIndex, "%" + stockCode + "%");
			}
			if(itemCategoryId != null) {
				query.setParameter(++paramIndex, itemCategoryId);
			}
			if(asOfDate != null) {
				query.setParameter(++paramIndex, asOfDate);
			}
			if(warehouseId != null) {
				query.setParameter(++paramIndex, warehouseId);
			}
			if(companyId != null) {
				query.setParameter(++paramIndex, companyId);
			}
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("UOM", Hibernate.STRING);
		}
	}

	@Override
	public AvailableStock getAvailStocksByReference(String stockCode,
			Integer warehouseId, Integer refObjectId) {
		StringBuilder availableStocksSql = new StringBuilder("SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY, UNIT_COST, "
				+ "EB_OBJECT_ID, SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM FROM (");
		availableStocksSql.append(availableStocksSql(warehouseId, stockCode, null));
		availableStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS WHERE SOURCE_OBJECT_ID = "+refObjectId);
		Collection<AvailableStock> availStocks = get(availableStocksSql.toString(),
				new AvailableStockHandler(stockCode, null, warehouseId, null, true));
		if(!availStocks.isEmpty()) {
			AvailableStock availableStock = availStocks.iterator().next();
			//Return null if the quantity is null.
			if(availableStock.getQuantity() == null) {
				return null;
			}
			return availableStock;
		}
		return null;
	}

	@Override
	public double getTotalAvailStocks(String stockCode, Integer warehouseId) {
		StringBuilder availableStocksSql = new StringBuilder("SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY, UNIT_COST, "
				+ "EB_OBJECT_ID, SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM FROM (");
		availableStocksSql.append(availableStocksSql(warehouseId, stockCode, null));
		availableStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS ");
		Collection<AvailableStock> availStocks = get(availableStocksSql.toString(), new AvailableStockHandler(stockCode, warehouseId, null));
		if(!availStocks.isEmpty()) {
			AvailableStock availableStock = availStocks.iterator().next();
			if(availableStock.getQuantity() != null) {
				return availableStock.getQuantity();
			}
		}
		return 0;
	}

	@Override
	public List<AvailableStock> getItemsWithAvailStocks(String stockCode, Integer warehouseId) {
		StringBuilder availableStocksSql = new StringBuilder("SELECT ITEM_ID, SUM(QUANTITY) AS QUANTITY, UNIT_COST, "
				+ "EB_OBJECT_ID, SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM FROM (");
		availableStocksSql.append(availableStocksSql(warehouseId, stockCode, null));
		availableStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS GROUP BY ITEM_ID ");
		return (List<AvailableStock>) get(availableStocksSql.toString(), new AvailableStockHandler(stockCode, warehouseId, null));
	}

	@Override
	public Page<AvailableStock> getAvailableStocksRptData(Integer warehouseId, Integer itemCategoryId,
			String stockCode, String orderBy, Date asOfDate, PageSetting pageSetting) {
		StringBuilder availableStocksSql = new StringBuilder("SELECT ITEM_ID, QUANTITY, UNIT_COST, "
				+ "EB_OBJECT_ID, SOURCE_OBJECT_ID, SOURCE, STOCK_CODE, DESCRIPTION, UOM FROM (");
		availableStocksSql.append(availableStocksSql(warehouseId, itemCategoryId, stockCode, asOfDate, null));
		availableStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS WHERE QUANTITY != 0 ");
		String ordering = orderBy.equals("sc") ? " STOCK_CODE " : "DESCRIPTION ";
		availableStocksSql.append("ORDER BY "+ordering);
		return getAllAsPage(availableStocksSql.toString(), pageSetting,
				new AvailableStockHandler(stockCode, itemCategoryId, warehouseId, asOfDate, false));
	}

	@Override
	public List<Item> getMainProducts(final String stockCode, final int companyId, final Boolean isExact, final Boolean isActiveOnly) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Item>>() {
			@Override
			public List<Item> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria itemCriteria = session.createCriteria(Item.class);
				if (isExact) {
					itemCriteria.add(Restrictions.eq(Item.FIELD.stockCode.name(), StringFormatUtil.removeExtraWhiteSpaces(stockCode)));
				} else {
					itemCriteria.add(Restrictions.or(Restrictions.like(Item.FIELD.description.name(), StringFormatUtil.appendWildCard(stockCode)),
							Restrictions.like(Item.FIELD.stockCode.name(), StringFormatUtil.appendWildCard(stockCode))));
				}
				if (isActiveOnly) {
					itemCriteria.add(Restrictions.eq(Item.FIELD.active.name(), true));
				}

				if (companyId > 0) {
					// SRP
					DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
					srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
					srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
					srpDc.add(Restrictions.gt(ItemSrp.FIELD.srp.name(), 0.0));
					itemCriteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), srpDc));
				}

				// Product Line
				DetachedCriteria prodLineDc = DetachedCriteria.forClass(ProductLine.class);
				prodLineDc.setProjection(Projections.property(ProductLine.FIELD.mainItemId.name()));
				prodLineDc.add(Restrictions.eq(ProductLine.FIELD.menu.name(), true));
				prodLineDc.add(Restrictions.eq(ProductLine.FIELD.active.name(), true));
				itemCriteria.add(Subqueries.propertyIn(Item.FIELD.id.name(), prodLineDc));

				itemCriteria.setMaxResults(20);
				itemCriteria.addOrder(Order.asc(Item.FIELD.stockCode.name()));
				List<Item> items = getAllByCriteria(itemCriteria);
				for (Item item : items) {
					getHibernateTemplate().initialize(item.getItemSrps());
					getHibernateTemplate().initialize(item.getItemDiscounts());
					getHibernateTemplate().initialize(item.getItemAddOns());
					getHibernateTemplate().initialize(item.getItemCategory());
					getHibernateTemplate().initialize(item.getUnitMeasurement());
				}
				return items;
			}
		});
	}

	@Override
	public Item getByDescription(String description) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Item.FIELD.description.name(), description.trim()));
		return get(dc);
	}

	@Override
	public List<AvailableStock> getFormsByRefObjectId(Integer refObjectId) {
		List<Object> objects = executeSP("GET_FORMS_BY_REF_OBJECT_ID", refObjectId);
		List<AvailableStock> asDtos = new ArrayList<AvailableStock>();
		AvailableStock asDto = null;
		for (Object object : objects) {
			int colNo = 0;
			Object[] rowResult = (Object[]) object;
			asDto = new AvailableStock();
			asDto.setSource((String) rowResult[colNo++]);
			asDto.setEbObjectId((Integer) rowResult[colNo++]);
			asDtos.add(asDto);
		}
		return asDtos;
	}

	@Override
	public ReceivedStock getAvailableStocks(int itemId, int companyId, int warehouseId, Date date) {
		return getReceivedStocks("GET_AVAILABLE_STOCKS", itemId, companyId, warehouseId, date);
	}
	
	@Override
	public ReceivedStock getReceivedStocksFuture (int itemId, int companyId, int warehouseId, Date date) {
		return getReceivedStocks("GET_RECEIEVED_STOCKS_FUTURE", itemId, companyId, warehouseId, date);
	}

	private ReceivedStock getReceivedStocks (String storedProcedure, int itemId, int companyId, int warehouseId, Date date) {
		List<Object> weightedAve = executeSP(storedProcedure, companyId, itemId, warehouseId, date);
		ReceivedStock availableStock = null;
		for (Object rowObj: weightedAve) {
			Object[] row = (Object[]) rowObj;
			Object ojbQuantity = (Double) row[2];
			Double quantity = ojbQuantity != null ? (Double) ojbQuantity : 0;
			Object ojbUnitCost = (Double) row[3];
			Double unitCost = ojbUnitCost != null ? (Double) ojbUnitCost : 0;
			availableStock = new ReceivedStock(null, itemId, quantity, unitCost, unitCost, null, -1);
		}
		return availableStock;
	}

	@Override
	public double getItemExistingStocks(int itemId, int warehouseId, Date asOfDate, int companyId) {
		List<Object> ret = executeSP("GET_ITEM_EXISTING_STOCKS", itemId, warehouseId, asOfDate, companyId);
		Double existingStocks = (Double) ret.iterator().next();
		return existingStocks == null ? 0 : existingStocks;
	}

	@Override
	public double getLatestItemUnitCost(int companyId, int warehouseId, int itemId) {
		List<Object> ret = executeSP("GET_LATEST_ITEM_UNIT_COST", companyId, warehouseId, itemId);
		for (Object rowObj: ret) {
			Object[] row = (Object[]) rowObj;
			Double unitCost = (Double) row[1];
			return unitCost != null ? unitCost : 0; // expecting 1 row
		}
		return 0;
	}

	@Override
	public Item getByBarcode(String barcode) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Item.FIELD.barcode.name(), barcode));
		return get(dc);
	}


}