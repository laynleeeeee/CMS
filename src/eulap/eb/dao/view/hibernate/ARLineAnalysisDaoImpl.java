package eulap.eb.dao.view.hibernate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import eulap.common.dao.BaseDao.QueryResultHandler;
import eulap.common.dao.PageDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.view.ARLineAnalysisDao;
import eulap.eb.domain.view.hibernate.ARLineAnalysis;
import eulap.eb.service.report.ArLineAnalysisReportParam;

/**
 * Implementation class of {@link ARLineAnalysisDao}


 *
 */
public class ARLineAnalysisDaoImpl extends PageDao<ARLineAnalysis> implements ARLineAnalysisDao {

	@Override
	protected Class<ARLineAnalysis> getDomainClass() {
		return ARLineAnalysis.class;
	}

	@Override
	public Page<ARLineAnalysis> getARLineAnalysisData(ArLineAnalysisReportParam param, PageSetting pageSetting) {
		String sql = "SELECT * FROM V_AR_LINE_ANALYSIS WHERE COMPANY_ID = ? "
				+(param.getSourceId() !=-1 ? "AND SOURCE_ID = ? " : "")
				+(param.getDivisionId() !=-1 ? "AND DIVISION_ID = ? " : "")
				+(param.getServiceId() !=-1 ? "AND SERVICE_ID = ? " : "")
				+(param.getCustomerId() != null ? "AND CUSTOMER_ID = ? " : "")
				+(param.getCustomerAcctId() !=-1 ? "AND CUSTOMER_ACCT_ID = ? " : "")
				+(param.getTransactionDateFrom() !=null && param.getTransactionDateTo() !=null
					? "AND RECEIPT_DATE BETWEEN ? AND ?" : "")
				+(param.getGlDateFrom() !=null && param.getGlDateTo() !=null
				? "AND MATURITY_DATE BETWEEN ? AND ?" : "")
				+"ORDER BY REF_DATE, REF_NUMBER, CUSTOMER, CUSTOMER_ACCT";
		ArLineAnalysisHandler handler = new ArLineAnalysisHandler(param);
		return getAllAsPage(sql, pageSetting, handler);
	}
	private static class ArLineAnalysisHandler implements QueryResultHandler<ARLineAnalysis>{
		private ArLineAnalysisReportParam param;

		private ArLineAnalysisHandler(ArLineAnalysisReportParam param) {
			this.param = param;
		}

		@Override
		public List<ARLineAnalysis> convert (List<Object[]> queryResult){
			List<ARLineAnalysis> arLineAnalysis = new ArrayList<ARLineAnalysis>();
			ARLineAnalysis arLineAnalysisObj = null;
			for (Object[]rowResult : queryResult) {
				int colNum = 0;
				arLineAnalysisObj = new ARLineAnalysis();
				arLineAnalysisObj.setSource((String) rowResult[colNum]);
				arLineAnalysisObj.setDivision((String) rowResult[++colNum]);
				arLineAnalysisObj.setService((String) rowResult[++colNum]);
				arLineAnalysisObj.setArCustomer((String) rowResult[++colNum]);
				arLineAnalysisObj.setArCustomerAcct((String) rowResult[++colNum]);
				arLineAnalysisObj.setReceiptDate((Date) rowResult[++colNum]);
				arLineAnalysisObj.setRefNumber((String) rowResult[++colNum]);
				arLineAnalysisObj.setMaturityDate((Date) rowResult[++colNum]);
				arLineAnalysisObj.setAmount((Double) rowResult[++colNum]);
				arLineAnalysis.add(arLineAnalysisObj);
			}
			return arLineAnalysis;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, param.getCompanyId());

			if (param.getSourceId() != -1) {
				query.setParameter(++index, param.getSourceId());
			}

			if (param.getDivisionId() != -1) {
				query.setParameter(++index, param.getDivisionId());
			}

			if (param.getServiceId() != -1) {
				query.setParameter(++index, param.getServiceId());
			}

			if (param.getCustomerId() != null) {
				query.setParameter(++index, param.getCustomerId());
			}

			if (param.getCustomerAcctId() != -1) {
				query.setParameter(++index, param.getCustomerAcctId());
			}
			if (param.getTransactionDateFrom() !=null && param.getTransactionDateTo() !=null) {
				query.setParameter(++index, param.getTransactionDateFrom());
				query.setParameter(++index, param.getTransactionDateTo());
			}
			if (param.getGlDateFrom() !=null && param.getGlDateTo() !=null) {
				query.setParameter(++index, param.getGlDateFrom());
				query.setParameter(++index, param.getGlDateTo());
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("SERVICE", Hibernate.STRING);
			query.addScalar("CUSTOMER", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCT", Hibernate.STRING);
			query.addScalar("RECEIPT_DATE", Hibernate.DATE);
			query.addScalar("REF_NUMBER", Hibernate.STRING);
			query.addScalar("MATURITY_DATE", Hibernate.DATE);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
		}

	}
}

