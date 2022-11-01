package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.UnitMeasurement;

/**
 * A class that handles all the business logic of {@link ArLine}

 *
 */
@Service
public class ArLineService {
	@Autowired
	private ArLineDao arLineDao;
	@Autowired
	private ArLineSetupService arLineSetupService;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	/**
	 * Get the list of AR Lines.
	 * @param arTransactionId The unique ar transaction id.
	 * @return The list of AR Lines.
	 */
	public List<ArLine> getArLines(int arTransactionId) {
		return arLineDao.getArLines(arTransactionId);
	}

	/**
	 * Processes the Ar Lines that has not set up.
	 * @param arLines The Ar Lines.
	 * @param companyId The company id.
	 * @return List of AR Lines.
	 */	
	public List<ArLine> processArLines (List<ArLine> arLines, Integer companyId) {
		List<ArLine> ret = new ArrayList<ArLine>();
		if (arLines != null && !arLines.isEmpty()) {
			boolean hasArLine = false;
			boolean hasQuantity = false;
			boolean hasUOM = false;
			boolean hasUP = false;
			boolean hasTaxType = false;
			boolean hasVATAmt = false;
			boolean hasAmount = false;
			for (ArLine arl : arLines) {
				hasArLine = arl.getArLineSetupName() != null && !arl.getArLineSetupName().trim().isEmpty();
				hasQuantity = arl.getAmount() != null && arl.getAmount() != 0.0;
				hasUOM = arl.getUnitOfMeasurementId() != null;
				hasUP = arl.getUpAmount() != null && arl.getUpAmount() != 0.0;
				hasTaxType = arl.getTaxTypeId() != null;
				hasVATAmt = arl.getVatAmount() != null && arl.getVatAmount() != 0.0;
				hasAmount = arl.getAmount() != null && arl.getAmount() != 0.0;
				if (hasArLine || hasQuantity || hasUOM || hasUP || hasTaxType || hasVATAmt || hasAmount) {
					if (hasArLine) {
						// TODO : please add division id from the AR transaction here setting the field to default null ATM
						ArLineSetup arLineSetup = arLineSetupService.getALSetupByNameAndCompany(arl.getArLineSetupName(), companyId, null);
						if (arLineSetup != null) {
							arl.setArLineSetupId(arLineSetup.getId());
							arLineSetup = null;
						}
					}
					if (hasUOM) {
						UnitMeasurement unitMeasurement = unitMeasurementDao.getUMByName(arl.getUnitMeasurementName());
						if (unitMeasurement != null) {
							arl.setUnitOfMeasurementId(unitMeasurement.getId());
							unitMeasurement = null;
						}
					}
					ret.add(arl);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Checks if the ar line contains invalid Ar Line Setups.
	 * @param arReceiptTransactions List of Ar Receipt Transactions.
	 * @return True if has invalid Ar Line Setup/s, otherwise false. 
	 */
	public boolean hasInvalidArLineSetup (List<AROtherCharge> otherCharges) {
		if (otherCharges != null) {
			for (AROtherCharge oc : otherCharges) {
				if (oc.getArLineSetupName() == null)
					return true;
				else if (oc.getArLineSetupName().trim().isEmpty())
					return true;
				else {
					ArLineSetup arLineSetup = arLineSetupService.getArLineSetupByName(oc.getArLineSetupName());
					if (arLineSetup == null)
						return true;
					else if(!arLineSetup.isActive())
						return true;
				}
			}	
		}
		return false;
	}

	/**
	 * Checks if the ar line contains invalid Unit of measures.
	 * @param otherCharges List of other charges.
	 * @return True if has invalid Unit of measures, otherwise false. 
	 */
	public boolean hasInvalidUOM (List<AROtherCharge> otherCharges) {
		if (otherCharges != null) {
			for (AROtherCharge oc : otherCharges) {
				if (oc.getUnitMeasurementName() == null)
					return false;
				else if (!oc.getUnitMeasurementName().trim().isEmpty()) {
					UnitMeasurement unitMeasurement = 
							unitMeasurementDao.getUMByName(oc.getUnitMeasurementName());
					if (unitMeasurement == null)
						return true;
				}
			}	
		}
		return false;
	}

	public String validateArLines(List<AROtherCharge> otherCharges, Integer customerAcctId) {
		return validateArLines(otherCharges, customerAcctId, null);
	}

	/**
	 * Checks the ar line amount if has zero or exceed 10,000,000,000.00
	 * @param amount The amount of the ar line
	 * @return True if the amount has zero or exceed 10,000,000,000.00, otherwise false
	 */
	private boolean isValidAmount(Double amount) {
		if (amount != null) {
				if(amount == 0.0 || amount >= 10000000000.00) {
					return false;
			}
		}
		return true;
	}

	/**
	 * Validate the AR Lines/Other Charges.
	 * @param otherCharges List of other charges to be validated.
	 * @param customerAcctId The id of the customer account.
	 * @return The error message if charges have errors, otherwise null.
	 */
	public String validateArLines(List<AROtherCharge> otherCharges, Integer customerAcctId, Integer companyId) {
		if(otherCharges.isEmpty()) {
			return null;
		}

		String amountErrorMsg = "Amount must not be equal to zero or exceed 10,000,000,000.00.";
		for (AROtherCharge oc : otherCharges) {
			if(oc.getArLineSetupId() != null && oc.getArLineSetupName() != null) {
				if(oc.getAmount() == null) {
					return "Amount is required.";
				} else {
					if (!isValidAmount(oc.getAmount())) {
						return amountErrorMsg;
					}
				}
			} else if(oc.getAmount() != null) {
				//Without AR Line but with amount
				return "Ar Line Setup is required.";
			}
		}

		String diffCompErrorMsg = "Other Charges must have the same company.";
		if(companyId != null) {
			if(!isValidCompany(otherCharges, companyId)) {
				if (!isSameCompany(otherCharges, customerAcctId)) {
					return diffCompErrorMsg;
				}
			}
		} else {
			if (!isSameCompany(otherCharges, customerAcctId)) {
				return diffCompErrorMsg;
			}
		}
		if (hasInvalidArLineSetup(otherCharges)) {
			return "Invalid AR Line Setup selected.";
		}
		if (hasInvalidUOM(otherCharges)) {
			return "Invalid Unit of Measurement.";
		}
		return null;
	}

	/**
	 * Validates the AR line Setup if it has the same company id as the companyId parameter.
	 * @param otherCharges The list of Other Charges.
	 * @param companyId The id of the company.
	 * @return True if the list of Other Charges have the same company id as the comp
	 */
	private boolean isValidCompany(List<AROtherCharge> otherCharges, Integer companyId) {
		if(otherCharges != null) {
			ArLineSetup arLineSetup = null;
			for (AROtherCharge oc : otherCharges) {
				// TODO : please add division id from the AR transaction here setting the field to default null ATM
				arLineSetup = arLineSetupService.getALSetupByNameAndCompany(oc.getArLineSetupName(), companyId, null);
				if(arLineSetup == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if all company id of all ar lines are the same.
	 * @param otherCharges List of other charges.
	 * @param customerAcctId Id of the customer account.
	 * @return True if all company id of all ar lines are the same, otherwise false.
	 */
	public boolean isSameCompany (List<AROtherCharge> otherCharges, Integer customerAcctId) {
		if (otherCharges != null) {
			if (customerAcctId != null) {
				int companyId = arCustomerAcctDao.get(customerAcctId).getCompanyId();
				for (AROtherCharge oc : otherCharges) {
					if (oc.getArLineSetupName() != null) {
						ArLineSetup arLineSetup = arLineSetupService.getArLineSetupByName(oc.getArLineSetupName());
						if (arLineSetup != null) {
							int currentCompanyId = arLineSetup.getAccountCombination().getCompanyId();
							if (companyId != currentCompanyId)
								return false;
						}
					}
				}
			}
		}
		return true;
	}
}
