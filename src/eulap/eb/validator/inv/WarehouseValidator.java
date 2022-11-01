package eulap.eb.validator.inv;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.ValidatorMessages;


/**
 * Validation class for Warehouse.

 *
 */
@Service
public class WarehouseValidator implements Validator{
	private static Logger logger = Logger.getLogger(WarehouseValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private DivisionService divisionService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Warehouse.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Warehouse warehouse = (Warehouse) obj;
		Integer companyId = warehouse.getCompanyId();
		logger.debug("Validating the company id:"+companyId);
		if(companyId == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("WarehouseValidator.0"));
		} else if(!companyService.getCompany(companyId).isActive()) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("WarehouseValidator.1"));
		}

		logger.debug("Validating the name: "+warehouse.getName());
		if(warehouse.getName() == null) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("WarehouseValidator.2"));
		}
		else if(warehouse.getName().trim().isEmpty()){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("WarehouseValidator.3"));
		}
		else if(warehouse.getName().length() > Warehouse.MAX_NAME){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("WarehouseValidator.4"));
		}
		else if(!warehouseService.isUniqueWarehouseName(warehouse.getName(), 
					companyId, warehouse.getId())) {
				errors.rejectValue("name", null, null, ValidatorMessages.getString("WarehouseValidator.5"));
		}
		if(warehouse.getAddress() == null) {
			errors.rejectValue("address", null, null, ValidatorMessages.getString("WarehouseValidator.6"));
		}
		else if(warehouse.getAddress().trim().isEmpty()){
			errors.rejectValue("address", null, null, ValidatorMessages.getString("WarehouseValidator.7"));
		}
		else if(warehouse.getAddress().length() > Warehouse.MAX_ADDRESS) {
			errors.rejectValue("address", null, null, ValidatorMessages.getString("WarehouseValidator.8"));
		}

		if(warehouse.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("WarehouseValidator.9"));
		} else if(!divisionService.getDivision(warehouse.getDivisionId()).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("WarehouseValidator.10"));
		}
	}
}
