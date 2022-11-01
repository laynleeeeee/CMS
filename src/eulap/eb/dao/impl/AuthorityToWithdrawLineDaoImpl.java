package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.AuthorityToWithdrawLineDao;
import eulap.eb.domain.hibernate.AuthorityToWithdrawLine;

/**
 * DAO Implementation class of {@link AuthorityToWithdrawLineDao}

 *
 */
public class AuthorityToWithdrawLineDaoImpl extends BaseDao<AuthorityToWithdrawLine> implements AuthorityToWithdrawLineDao{

	@Override
	protected Class<AuthorityToWithdrawLine> getDomainClass() {
		return AuthorityToWithdrawLine.class;
	}
}
