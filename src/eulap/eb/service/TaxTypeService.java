package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.TaxTypeDao;
import eulap.eb.domain.hibernate.TaxType;

/**
 * A service class that will handle business logic for {@link TaxType}

 */

@Service
public class TaxTypeService {
	@Autowired
	private TaxTypeDao taxTypeDao;

	/**
	 * Get the list of tax type objects
	 * @param taxTypeId The tax type id
	 * @return The list of tax type objects
	 */
	public List<TaxType> getTaxTypes(Integer taxTypeId) {
		return taxTypeDao.geTaxTypes(taxTypeId);
	}

	/**
	 * Get the list of AR tax types
	 * @param taxTypeId The tax type id
	 * @return The list of AR tax types
	 */
	public List<TaxType> getAcctReceivableTaxTypes(Integer taxTypeId) {
		return taxTypeDao.getAcctReceivableTaxTypes(taxTypeId);
	}

	/**
	 * Get the list of AP tax types
	 * @param taxTypeId The tax type id
	 * @return The list of AP tax types
	 */
	public List<TaxType> getAcctPayableTaxTypes(Integer taxTypeId) {
		return taxTypeDao.getAcctPayableTaxTypes(taxTypeId, false);
	}

	/**
	 * Get the list of AP tax types
	 * @param taxTypeId The tax type id
	 * @return The list of AP tax types
	 */
	public List<TaxType> getAcctPayableTaxTypesWithImportation(Integer taxTypeId, boolean isImportation) {
		return taxTypeDao.getAcctPayableTaxTypes(taxTypeId, isImportation);
	}
}
