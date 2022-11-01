package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.FleetManningRequirementDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.FleetManningRequirement;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * DAO Implementation of {@link FleetManningRequirementDao}

 *
 */
public class FleetManningRequirementDaoImpl extends BaseDao<FleetManningRequirement> implements FleetManningRequirementDao {

	@Override
	protected Class<FleetManningRequirement> getDomainClass() {
		return FleetManningRequirement.class;
	}

	@Override
	public List<FleetManningRequirement> getAllFleetMRByRefObjectId(Integer refObjectId, Integer orTypeId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));

		dc.add(Subqueries.propertyIn(FleetManningRequirement.FIELD.ebObjectId.name(), obj2ObjDc));
		return getAll(dc);
	}

	@Override
	public Page<FleetManningRequirement> searchManningRequirements(String position, String license, String number,
			String remarks, String department, Integer refObjectId, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(position != null && !position.trim().isEmpty()){
			dc.add(Restrictions.like(FleetManningRequirement.FIELD.position.name(), "%"+position.trim()+"%"));
		}
		if(license != null && !license.trim().isEmpty()){
			dc.add(Restrictions.like(FleetManningRequirement.FIELD.license.name(), "%"+license.trim()+"%"));
		}
		if(number != null && !number.trim().isEmpty()){
			dc.add(Restrictions.like(FleetManningRequirement.FIELD.number.name(), "%"+number.trim()+"%"));
		}
		if(remarks != null && !remarks.trim().isEmpty()){
			dc.add(Restrictions.like(FleetManningRequirement.FIELD.remarks.name(), "%"+remarks.trim()+"%"));
		}
		if(refObjectId != null){

			DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
			obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
			obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
			if(department != null && !department.trim().isEmpty() && !department.equals("All")){
				obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), (department.equals(FleetManningRequirement.DEPARTMENT_DECK) ?
						FleetManningRequirement.MANNING_REQUIREMENT_DECK_OR_TYPE_ID : FleetManningRequirement.MANNING_REQUIREMENT_ENGINE_OR_TYPE_ID)));
			}
			dc.add(Subqueries.propertyIn(FleetManningRequirement.FIELD.ebObjectId.name(), obj2ObjDc));
		}
		dc = DaoUtil.setSearchStatus(dc,FleetManningRequirement.FIELD.active.name(), status);
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueManningRequirements(FleetManningRequirement manningRequirement, Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(manningRequirement.getPosition() != null && !manningRequirement.getPosition().trim().isEmpty()){
			dc.add(Restrictions.eq(FleetManningRequirement.FIELD.position.name(), manningRequirement.getPosition().trim()));
		}
		if(manningRequirement.getLicense() != null && !manningRequirement.getLicense().trim().isEmpty()){
			dc.add(Restrictions.eq(FleetManningRequirement.FIELD.license.name(), manningRequirement.getLicense().trim()));
		}
		if(manningRequirement.getNumber() != null && !manningRequirement.getNumber().trim().isEmpty()){
			dc.add(Restrictions.eq(FleetManningRequirement.FIELD.number.name(), manningRequirement.getNumber().trim()));
		}
		if(refObjectId != null) {
			DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
			obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
			obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
			if(manningRequirement.getDepartment() != null && !manningRequirement.getDepartment().trim().isEmpty() && !manningRequirement.getDepartment().equals("All")){
				obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), (manningRequirement.getDepartment().equals(FleetManningRequirement.DEPARTMENT_DECK) ?
						FleetManningRequirement.MANNING_REQUIREMENT_DECK_OR_TYPE_ID : FleetManningRequirement.MANNING_REQUIREMENT_ENGINE_OR_TYPE_ID)));
			}
			dc.add(Subqueries.propertyIn(FleetManningRequirement.FIELD.ebObjectId.name(), obj2ObjDc));

		}

		if(manningRequirement.getId() != 0){
			dc.add(Restrictions.ne(FleetManningRequirement.FIELD.id.name(), manningRequirement.getId()));
		}

		dc.add(Restrictions.eq(FleetManningRequirement.FIELD.active.name(), true));
		return getAll(dc).size() < 1;
	}
}
