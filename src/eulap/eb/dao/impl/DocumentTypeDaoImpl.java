package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.DocumentTypeDao;
import eulap.eb.domain.hibernate.DocumentType;

/**
 * Implementation class of {@link DocumentTypeDao} interface.

 *
 */
public class DocumentTypeDaoImpl extends BaseDao<DocumentType> implements DocumentTypeDao{

	@Override
	protected Class<DocumentType> getDomainClass() {
		return DocumentType.class;
	}

	@Override
	public boolean isDuplicate(DocumentType documentType) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(DocumentType.FIELD.id.name(), documentType.getId()));
		dc.add(Restrictions.eq(DocumentType.FIELD.name.name(), documentType.getName()));
		return getAll(dc).size() > 0;
	}

	@Override
	public Page<DocumentType> searchDocumentTypes(String name, Integer status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.isEmpty()){
			dc.add(Restrictions.like(DocumentType.FIELD.name.name(),
					DaoUtil.handleMysqlSpecialCharacter(name) + "%"));
		}
		if(status != -1){
			boolean isActive = status == 1;
			dc.add(Restrictions.eq(DocumentType.FIELD.active.name(), isActive));
		}
		dc.addOrder(Order.asc(DocumentType.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<DocumentType> getActiveDocTypes() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DocumentType.FIELD.active.name(), true));
		dc.addOrder(Order.asc(DocumentType.FIELD.name.name()));
		return getAll(dc);
	}
}
