package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.StockcardDto;

/**
 * Business logic for generating the Stockcard per item report.

 */
@Service
public class StockcardPerItemService {
	@Autowired
	private ItemDao itemDao;

	/**
	 * Generate the data source of the stockcard per item report.
	 */
	public JRDataSource generateStockcardDatasource(int itemId, int companyId, Integer divisionId, int warehouseId,
			String strDateFrom, String strDateTo, String companyName, int typeId) {
		EBJRServiceHandler<StockcardDto> stockcardHandler = new StockcardPerItemJRHandler(itemDao,
				itemId, companyId, divisionId, warehouseId, strDateFrom, strDateTo, companyName, typeId);
		return new EBDataSource<StockcardDto>(stockcardHandler);
	}

	private static class StockcardPerItemJRHandler implements EBJRServiceHandler<StockcardDto> {
		private static final Logger logger = Logger.getLogger(StockcardPerItemJRHandler.class);
		private final ItemDao itemDao;
		private final int itemId;
		private final int companyId;
		private final Integer divisionId;
		private final int warehouseId;
		private final Date dateFrom;
		private final Date dateTo;
		private Double qtyBalance = 0.0;
		private Double balanceAmount = 0.0;
		private String companyName;
		private final int typeId;
		private Page<StockcardDto> currentPage;
		private List<StockcardDto> beginningBalances = new ArrayList<StockcardDto>();
		private boolean isFirstPage = true;

		private StockcardPerItemJRHandler(ItemDao itemDao, int itemId, int companyId, Integer divisionId, int warehouseId,
				String strDateFrom, String strDateTo, String companyName, int typeId) {
			this.itemDao = itemDao;
			this.itemId = itemId;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.dateFrom = DateUtil.parseDate(strDateFrom);
			this.dateTo = DateUtil.parseDate(strDateTo);
			this.companyName = companyName;
			this.typeId = typeId;
			computeBeginningBalance();
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
			beginningBalances = null;
			balanceAmount = null;
			qtyBalance = null;
		}

		public void computeBeginningBalance () {
			logger.info("Generating the Beginning balance as of "+DateUtil.formatDate(dateFrom));
			Date asOfDate = DateUtil.deductDaysToDate(dateFrom, 1);
			List<StockcardDto> unusedStocks = itemDao.getStockcardBeginningBal(companyId, itemId, warehouseId, typeId, asOfDate);
			if (unusedStocks == null)
				return;
			StockcardDto currentUnusedStock = null;
			for (StockcardDto unusedStock : unusedStocks) {
				currentUnusedStock = unusedStock;
				qtyBalance += unusedStock.getQuantity();
				currentUnusedStock.setBalance(qtyBalance);

				balanceAmount += NumberFormatUtil.multiplyWFP(unusedStock.getUnitCost(), unusedStock.getQuantity());
				currentUnusedStock.setBalanceAmount(balanceAmount);
				beginningBalances.add(currentUnusedStock);
			}
		}


		@Override
		public Page<StockcardDto> nextPage(PageSetting pageSetting) {
			List<StockcardDto> stockcardRet = new ArrayList<StockcardDto>();

			if(isFirstPage) {
				stockcardRet.addAll(beginningBalances);
				logger.info("Successfully added Beginning balance to the report.");
				isFirstPage = false;
			}

			Page<StockcardDto> stockcardResult = itemDao.getStockcardPerItem(itemId, companyId, divisionId,
					warehouseId, dateFrom, dateTo, pageSetting);
			if(!stockcardResult.getData().isEmpty()) {
				logger.debug("Retrieved "+stockcardResult.getTotalRecords()+" to be added to the report.");
				String formNumber = null;
				for (StockcardDto scd : stockcardResult.getData()) {
					formNumber = companyName+" "+scd.getFormNumber();
					if(scd.getUnitCost() == null) {
						logger.warn("Unit cost for form number "+formNumber+" is null.");
						logger.debug("Setting the unit cost to zero.");
						scd.setUnitCost(0.0);
					}

					scd.setFormNumber(formNumber);
					logger.trace("Added form number "+formNumber+" to the report.");

					logger.debug("Formatting the quantity and unit cost to 2 decimal places.");
					scd.setQuantity(NumberFormatUtil.roundOffTo2DecPlaces(scd.getQuantity()));
					scd.setUnitCost(NumberFormatUtil.roundOffNumber(scd.getUnitCost(),
							NumberFormatUtil.SIX_DECIMAL_PLACES));

					balanceAmount += NumberFormatUtil.multiplyWFP(scd.getQuantity() , scd.getUnitCost());
					scd.setBalanceAmount(NumberFormatUtil.roundOffTo2DecPlaces(balanceAmount));
					qtyBalance += scd.getQuantity();
					scd.setBalance(qtyBalance);
					logger.trace("Running balance: "+qtyBalance);
					stockcardRet.add(scd);
				}
				logger.debug("Successfully added "+stockcardRet.size()
						+" transactions to the Stockcard Report.");
			} else {
				logger.debug("Zero transactions for item: "+itemId+" of company: "+companyId+
						" between the date range: "+DateUtil.formatDate(dateFrom)+" to "+DateUtil.formatDate(dateTo));
			}

			currentPage = new Page<StockcardDto>(pageSetting, stockcardRet, stockcardResult.getTotalRecords());
			return currentPage;
		}
	}
}
