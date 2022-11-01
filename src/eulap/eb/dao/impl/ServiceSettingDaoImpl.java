package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ServiceSettingDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ServiceSetting;

/**
 * DAO implementation of {@link ServiceSettingDao}

 *
 */
public class ServiceSettingDaoImpl extends BaseDao<ServiceSetting> implements ServiceSettingDao {

	private static final int MAX_PAGE_RESULT = 10;
	
	@Override
	protected Class<ServiceSetting> getDomainClass() {
		return ServiceSetting.class;
	}

	@Override
	public boolean isUniqueService(ServiceSetting service) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(ServiceSetting.FIELD.id.name(), service.getId()));
		dc.add(Restrictions.eq(ServiceSetting.FIELD.name.name(), service.getName().trim()));

		DetachedCriteria aCDc = DetachedCriteria.forClass(AccountCombination.class);
		aCDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		aCDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), service.getCompanyId()));
		aCDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), service.getDivisionId()));

		dc.add(Subqueries.propertyIn(ServiceSetting.FIELD.accountCombinationId.name(), aCDc));
		return getAll(dc).size() == 0;
	}

	@Override
	public Page<ServiceSetting> searchServiceSettings(String name, Integer companyId, Integer divisionId,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		//Account combination
		DetachedCriteria accountCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		accountCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(ServiceSetting.FIELD.name.name(), StringFormatUtil.appendWildCard(name)));
		}
		if(companyId != -1 ) {
			accountCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		}
		if(divisionId != -1) {
			accountCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		}
		dc = DaoUtil.setSearchStatus(dc,ServiceSetting.FIELD.active.name(),status);
		dc.add(Subqueries.propertyIn(ServiceSetting.FIELD.accountCombinationId.name(), accountCombiCriteria));
		dc.addOrder(Order.asc(ServiceSetting.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ServiceSetting> getServiceSettings(String name, Integer arCustAcctId, Integer id, Boolean noLimit, 
			Boolean isExact, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (name != null && !name.trim().isEmpty() && id == null) {
			dc.add(Restrictions.like(ServiceSetting.FIELD.name.name(), "%" + name.trim() + "%"));
		} else if (name != null && !name.trim().isEmpty() && isExact) {
			dc.add(Restrictions.eq(ServiceSetting.FIELD.name.name(), name.trim()));
		} else if (id != null) {
			dc.add(Restrictions.eq(ServiceSetting.FIELD.id.name(), id));
		}
		if (arCustAcctId != null) {
			// Account Combination criteria
			DetachedCriteria accountCombinationDc = DetachedCriteria.forClass(AccountCombination.class);
			accountCombinationDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			if (divisionId != null) {
				accountCombinationDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			}
			// AR Customer Account criteria
			DetachedCriteria arCustomerAccountDc = DetachedCriteria.forClass(ArCustomerAccount.class);
			arCustomerAccountDc.setProjection(Projections.property(ArCustomerAccount.FIELD.companyId.name()));
			arCustomerAccountDc.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), arCustAcctId));
			accountCombinationDc.add(Subqueries.propertyIn(AccountCombination.FIELD.companyId.name(), arCustomerAccountDc));
			dc.add(Subqueries.propertyIn(ServiceSetting.FIELD.accountCombinationId.name(), accountCombinationDc));
		}
		dc.addOrder(Order.asc(ServiceSetting.FIELD.name.name()));
		dc.add(Restrictions.eq(ServiceSetting.FIELD.active.name(), true));//Only retrieve active services.
		if (noLimit == null)
			dc.getExecutableCriteria(getSession()).setMaxResults(MAX_PAGE_RESULT);
		return getAll(dc);
	}

	@Override
	public ServiceSetting getServiceSettingByName(String serviceSettingName) {
		DetachedCriteria dc = DetachedCriteria.forClass(ServiceSetting.class);
		dc.add(Restrictions.eq(ServiceSetting.FIELD.name.name(), serviceSettingName));
		return get(dc);
	}

	@Override
	public List<ServiceSetting> getServiceSettingByDivision(String name, Boolean noLimit, Boolean isExact, Integer divisionId, Integer companyId){
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null && companyId < 0) {
			dc.add(Restrictions.eq(ServiceSetting.FIELD.companyId.name(), companyId));
		}
		if (name != null && !name.trim().isEmpty()) {
			if(isExact) {
				dc.add(Restrictions.eq(ServiceSetting.FIELD.name.name(), name.trim()));
			} else {
				dc.add(Restrictions.like(ServiceSetting.FIELD.name.name(), StringFormatUtil.appendWildCard(name)));
			}
		}
		if (divisionId != null) {
			DetachedCriteria accountCombinationDc = DetachedCriteria.forClass(AccountCombination.class);
			accountCombinationDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			if (divisionId != null) {
				accountCombinationDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			}
			dc.add(Subqueries.propertyIn(ServiceSetting.FIELD.accountCombinationId.name(), accountCombinationDc));
		}
		dc.add(Restrictions.eq(ServiceSetting.FIELD.active.name(), true));
		dc.addOrder(Order.asc(ServiceSetting.FIELD.name.name()));
		if (noLimit == null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(MAX_PAGE_RESULT);
		}
		return getAll(dc);
	}
}
