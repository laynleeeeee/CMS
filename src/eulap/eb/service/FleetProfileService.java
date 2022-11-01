package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.DriverDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.FleetDefaultAccountDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.FleetTypeDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FleetCategory;
import eulap.eb.domain.hibernate.FleetDefaultAccount;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class for {@link FleetProfile}

 *
 */
@Service
public class FleetProfileService {
	private final Logger logger = Logger.getLogger(FleetProfileService.class);
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private FleetDefaultAccountDao fleetDefaultAccountDao;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private FleetTypeDao fleetTypeDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DriverDao driverDao;

	/**
	 * Get the fleet profile object by id.
	 * @param fleetProfileId The fleet profile unique identifier.
	 */
	public FleetProfile getFleetProfile(int fleetProfileId) {
		FleetProfile fp = fleetProfileDao.get(fleetProfileId);
		processFleetProfile(fp);
		return fp;
	}

	/**
	 * 
	 * @param ebObjectId The Eb object id.
	 * @param isSetObjects True to set transient objects, otherwise false.
	 * @return
	 */
	public FleetProfile getFleetProfileByEbObject(int ebObjectId, boolean isSetObjects) {
		FleetProfile fp = fleetProfileDao.getByEbObjectId(ebObjectId);
		if (isSetObjects) {
			processFleetProfile(fp);
		}
		return fp;
	}

	private void processFleetProfile(FleetProfile fp) {
		Company company = companyDao.get(fp.getCompanyId());
		fp.setCompany(company);

		EBObject fTEbObject = objectToObjectDao.getOtherReference(fp.getEbObjectId(), FleetProfile.FP_TYPE_OR_TYPE_ID);
		if (fTEbObject != null) {
			fp.setRefObjectId(fTEbObject.getId());
			FleetType ft = fleetTypeDao.getByEbObjectId(fTEbObject.getId());
			fp.setFleetTypeId(ft.getId());
			fp.setFleetType(ft);
			fTEbObject = null;
		}
		EBObject divEbObject = objectToObjectDao.getOtherReference(fp.getEbObjectId(), FleetProfile.FP_DIVISION_OR_TYPE_ID);
		if (divEbObject != null) {
			Division division = divisionDao.getByEbObjectId(divEbObject.getId());
			fp.setDivisionId(division.getId());
			fp.setDivision(division);
			fTEbObject = null;
		}
	}

	/**
	 * Get the list of fleet profiles.
	 * @param codeVesselName The code or vessel name filter.
	 * @param user The logged user.
	 */
	public List<FleetProfile> getFleetProfiles(String codeVesselName, User user) {
		return fleetProfileDao.getFleetProfiles(codeVesselName, user);
	}


	/**
	 * Save the fleet profile header. 
	 * @param user The logged user.
	 * @param fp The fleet profile object to be saved.
	 */
	public void saveFleetProfile(User user, FleetProfile fp) {
		boolean isNew = fp.getId() == 0;
		// Create the combinations from FLEET_TYPE_EXPENSE accounts.
		logger.info("Saving account combinations.");
		createDivAndCombinations(user, fp, isNew);

		if (isNew) {
			fp.setEbObjectId(saveAndGetEbObject(user, FleetProfile.OBJECT_TYPE_ID, new Date()));
		}

		// Save the header.
		logger.info("Saving Fleet Profile.");
		AuditUtil.addAudit(fp, new Audit(user.getId(), isNew, new Date()));
		processSaving(fp);
		fleetProfileDao.saveOrUpdate(fp);
		logger.info("Done saving Fleet Profile.");

		// Save the object-object mapping.
		logger.info("Saving the reference for COMPANY.");
		Company company = companyDao.get(fp.getCompanyId());
		saveFleetProfileReference(company.getEbObjectId(),  fp.getEbObjectId(), FleetProfile.FP_COMPANY_OR_TYPE_ID, user);
		logger.info("Done saving the reference for COMPANY.");

		logger.info("Saving the reference for FLEET TYPE.");
		FleetType fleetType = fleetTypeDao.get(fp.getFleetTypeId());
		saveFleetProfileReference(fleetType.getEbObjectId(),  fp.getEbObjectId(), FleetProfile.FP_TYPE_OR_TYPE_ID, user);
		logger.info("Done saving the reference for FLEET TYPE.");

		logger.info("Saving the reference for DIVISION.");
		Division division = divisionDao.get(fp.getDivisionId());
		saveFleetProfileReference(division.getEbObjectId(),  fp.getEbObjectId(), FleetProfile.FP_DIVISION_OR_TYPE_ID, user);
		logger.info("Done saving the reference for DIVISION.");

	}

	private void saveFleetProfileReference(int refObjectId, int ebObjectId, int orTypeId, User user) {
		ObjectToObject oo = ObjectToObject.getInstanceOf(refObjectId, ebObjectId, orTypeId, user, new Date());
		objectToObjectDao.save(oo);
	}

	private void createDivAndCombinations(User user, FleetProfile fp, boolean isNew) {
		Division division = null;
		if (isNew) {
			logger.info("Creating division.");
			division = new Division();
			division.setActive(true);
			division.setNumber(generateDivNumber(user.getId()));
			logger.info("Saving division.");
			saveDivision(division, fp, user);
			fp.setDivisionId(division.getId());
			logger.info("Done saving division.");
		} else {
			// Get the division by branch.
			logger.info("Retrieving division.");
			division = divisionDao.get(fp.getDivisionId());
			logger.info("Saving changes of division.");
			saveDivision(division, fp, user);
		}

		logger.info("Retrieving all active accounts from FLEET_DEFAULT_ACCOUNT.");
		List<FleetDefaultAccount> defaultAccounts = fleetDefaultAccountDao.getAllActive();
		logger.info("Done retrieving all active accounts from FLEET_DEFAULT_ACCOUNT.");
		List<Domain> toBeSavedAcs = new ArrayList<>();
		AccountCombination ac = null;
		if (!defaultAccounts.isEmpty()) {
			logger.info("Creating the account combinations.");
			for (FleetDefaultAccount fda : defaultAccounts) {
				// Check if there is already existing account combination when editing to avoid duplicate entry.
				if (accountCombinationDao.getAccountCombination(fp.getCompanyId(), division.getId(), fda.getAccountId()) != null) {
					continue;
				}
				ac = new AccountCombination();
				ac.setCompanyId(fp.getCompanyId());
				ac.setDivisionId(division.getId());
				ac.setAccountId(fda.getAccountId());
				ac.setActive(true);
				ac.setServiceLeaseKeyId(1);
				AuditUtil.addAudit(ac, new Audit(user.getId(), true, new Date()));
				toBeSavedAcs.add(ac);
			}
			if (!toBeSavedAcs.isEmpty()) {
				logger.info("Done creating the account combinations.");
				logger.info("Saving the account combinations.");
				accountCombinationDao.batchSave(toBeSavedAcs);
				logger.info("Done saving the account combinations.");
			} else {
				logger.info("No account combination/s created.");
			}
		}
	}

	private void saveDivision(Division division, FleetProfile fp, User user) {
		division.setName(fp.getCodeVesselName());
		division.setDescription(fp.getCodeVesselName());
		divisionService.saveDivision(division, user);
	}

	/**
	 * Auto generate division number
	 * @param userId The current user logged
	 * @return The auto generate division number
	 */
	public String generateDivNumber(int userId) {
		Division division = divisionDao.getMaxDivisionByNumber();
		if (division != null) {
			String divNumber = division.getNumber();
			if (StringFormatUtil.isNumeric(divNumber)) {
				return processDivNumber(divNumber);
			} else {
				char c;
				String hexNum = "";
				for (int i=0; i<divNumber.length(); i++) {
					c = divNumber.charAt(i);
					hexNum += Character.isLetter(c) ? String.format("%04x", (int) c) : c;
				}
				hexNum = Integer.parseInt(hexNum) + "";
				if (hexNum.length() > Division.MAX_NUMBER) {
					hexNum = hexNum.substring(hexNum.length() - Division.MAX_NUMBER);
				}
				return processDivNumber(Integer.parseInt(hexNum) + "");
			}
		}
		return "";
	}

	private String processDivNumber(String divNumber) {
		divNumber = (Integer.parseInt(divNumber) + 1) + "";
		if (divNumber.length() < Division.MAX_NUMBER) {
			String prefix = "";
			for (int i=0; i<Division.MAX_NUMBER-divNumber.length();i++) {
				prefix += "0";
			}
			divNumber = prefix + divNumber;
		}
		return divNumber;
	}

	public void validate (FleetProfile fp, Errors errors, boolean isNew) {
		// Company/Branch 
		if (fp.getCompanyId() != null) {
			Company company = companyDao.get(fp.getCompanyId());
			if (company == null) {
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("FleetProfileService.0"));
			}
		} else {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("FleetProfileService.0"));
		}

		// Engine No.
		if (fp.getEngineNo() == null || fp.getEngineNo().trim().isEmpty()) {
			errors.rejectValue("engineNo", null, null, ValidatorMessages.getString("FleetProfileService.1"));
		} else if (fp.getEngineNo().trim().length() > FleetProfile.MAX_ENGINE_NO) {
			evalExceededChar("engineNo", ValidatorMessages.getString("FleetProfileService.2"), FleetProfile.MAX_ENGINE_NO, errors);
		}

		// Description
		if (fp.getDescription() != null && fp.getDescription().trim().length() > FleetProfile.MAX_DESCRIPTION) {
			evalExceededChar("description", ValidatorMessages.getString("FleetProfileService.3"), FleetProfile.MAX_DESCRIPTION, errors);
		}

		// Supplier
		if (fp.getSupplier() != null && fp.getSupplier().trim().length() > FleetProfile.MAX_SUPPLIER) {
			evalExceededChar("supplier", ValidatorMessages.getString("FleetProfileService.4"), FleetProfile.MAX_SUPPLIER, errors);
		}

		//Plate number.
		if(fp.getPlateNo() == null || fp.getPlateNo().trim().isEmpty()) {
			errors.rejectValue("plateNo", null, null, ValidatorMessages.getString("FleetProfileService.29"));
		} else if(!fleetProfileDao.isUniquePlateNo(fp.getPlateNo(), fp.getId())) {
			errors.rejectValue("plateNo", null, null, ValidatorMessages.getString("FleetProfileService.32"));
		}

		//Driver
		if(fp.getDriverId() == null) {
			errors.rejectValue("driverId", null, null, ValidatorMessages.getString("FleetProfileService.30"));
		} else if(!driverDao.get(fp.getDriverId()).isActive()) {
			errors.rejectValue("driverId", null, null, ValidatorMessages.getString("FleetProfileService.31"));
		}

		if (fp.getFleetTypeId() != null) {
			FleetType ft = fleetTypeDao.get(fp.getFleetTypeId());
			if (ft.getFleetCategoryId() == FleetCategory.FT_CONSTRUCTION) {
				// Code
				if (fp.getCodeVesselName() == null || fp.getCodeVesselName().trim().isEmpty()) {
					errors.rejectValue("codeVesselName", null, null, ValidatorMessages.getString("FleetProfileService.5"));
				} else {
					if (fp.getCodeVesselName().trim().length() > FleetProfile.MAX_CODE_VEESSEL_NAME) {
						evalExceededChar("codeVesselName", ValidatorMessages.getString("FleetProfileService.6"), FleetProfile.MAX_CODE_VEESSEL_NAME, errors);
					}
					if (!fleetProfileDao.isUniqueCode(fp)) {
						errors.rejectValue("codeVesselName", null, null, ValidatorMessages.getString("FleetProfileService.7"));
					}
				}

				// Make
				if (fp.getMake() == null || fp.getMake().trim().isEmpty()) {
					errors.rejectValue("make", null, null, ValidatorMessages.getString("FleetProfileService.8"));
				} else if (fp.getMake().trim().length() > FleetProfile.MAX_MAKE) {
					evalExceededChar("make", ValidatorMessages.getString("FleetProfileService.9"), FleetProfile.MAX_MAKE, errors);
				}

				// Model
				if (fp.getModel() != null && fp.getModel().trim().length() > FleetProfile.MAX_MODEL) {
					evalExceededChar("model", ValidatorMessages.getString("FleetProfileService.10"), FleetProfile.MAX_MODEL, errors);
				}

				// Chassis No.
				if (fp.getChassisNo() == null || fp.getChassisNo().trim().isEmpty()) {
					errors.rejectValue("chassisNo", null, null, ValidatorMessages.getString("FleetProfileService.11"));
				} else if (fp.getChassisNo().trim().length() > FleetProfile.MAX_CHASSIS_NO) {
					evalExceededChar("chassisNo", ValidatorMessages.getString("FleetProfileService.12"), FleetProfile.MAX_CHASSIS_NO, errors);
				}

				// Plate No.
				if (fp.getPlateNo() != null && fp.getPlateNo().trim().length() > FleetProfile.MAX_PLATE_NO) {
					evalExceededChar("plateNo", ValidatorMessages.getString("FleetProfileService.13"), FleetProfile.MAX_MODEL, errors);
				}

				// Driver
				if (fp.getDriver() != null && fp.getDriverName().trim().length() > FleetProfile.MAX_DRIVER) {
					evalExceededChar("driver", ValidatorMessages.getString("FleetProfileService.14"), FleetProfile.MAX_DRIVER, errors);
				}
			} else {
				// Vessel Name
				if (fp.getCodeVesselName() == null || fp.getCodeVesselName().trim().isEmpty()) {
					errors.rejectValue("codeVesselName", null, null, ValidatorMessages.getString("FleetProfileService.15"));
				} else {
					if (fp.getCodeVesselName().trim().length() > FleetProfile.MAX_CODE_VEESSEL_NAME) {
						evalExceededChar("codeVesselName", ValidatorMessages.getString("FleetProfileService.16"), FleetProfile.MAX_CODE_VEESSEL_NAME, errors);
					}
					if (!fleetProfileDao.isUniqueCode(fp)) {
						errors.rejectValue("codeVesselName", null, null, ValidatorMessages.getString("FleetProfileService.17"));
					}
				}

				// Official No.
				if (fp.getOfficialNo() == null || fp.getOfficialNo().trim().isEmpty()) {
					errors.rejectValue("officialNo", null, null, ValidatorMessages.getString("FleetProfileService.18"));
				} else if (fp.getOfficialNo().trim().length() > FleetProfile.MAX_OFFICIAL_NO) {
					evalExceededChar("officialNo", ValidatorMessages.getString("FleetProfileService.19"), FleetProfile.MAX_OFFICIAL_NO, errors);
				}

				// Call Sign
				if (fp.getCallSign() == null || fp.getCallSign().trim().isEmpty()) {
					errors.rejectValue("callSign", null, null, ValidatorMessages.getString("FleetProfileService.20"));
				} else if (fp.getCallSign().trim().length() > FleetProfile.MAX_CALL_SIGN) {
					evalExceededChar("callSign", ValidatorMessages.getString("FleetProfileService.21"), FleetProfile.MAX_CALL_SIGN, errors);
				}

				// Tonnage Weight
				if (fp.getTonnageWeight() != null && fp.getTonnageWeight().trim().length() > FleetProfile.MAX_TONNAGE_WEIGHT) {
					evalExceededChar("tonnageWeight", ValidatorMessages.getString("FleetProfileService.22"), FleetProfile.MAX_TONNAGE_WEIGHT, errors);
				}

				// VMS
				if (fp.getVms() != null && fp.getVms().trim().length() > FleetProfile.MAX_VMS) {
					evalExceededChar("vms", ValidatorMessages.getString("FleetProfileService.23"), FleetProfile.MAX_VMS, errors);
				}

				// Propeller
				if (fp.getPropeller() != null && fp.getPropeller().trim().length() > FleetProfile.MAX_PROPELLER) {
					evalExceededChar("propeller", ValidatorMessages.getString("FleetProfileService.24"), FleetProfile.MAX_PROPELLER, errors);
				}

				// Winch
				if (fp.getWinch() != null && fp.getPropeller().trim().length() > FleetProfile.MAX_WINCH) {
					evalExceededChar("winch", ValidatorMessages.getString("FleetProfileService.25"), FleetProfile.MAX_WINCH, errors);
				}

				// Captain
				if (fp.getCaptain() != null && fp.getCaptain().trim().length() > FleetProfile.MAX_CAPTAIN) {
					evalExceededChar("captain", ValidatorMessages.getString("FleetProfileService.26"), FleetProfile.MAX_CAPTAIN, errors);
				}
			}
		}
	}

	private void processSaving(FleetProfile fp) {
		// Engine No.
		if (fp.getEngineNo() != null && !fp.getEngineNo().trim().isEmpty()) {
			fp.setEngineNo(StringFormatUtil.removeExtraWhiteSpaces(fp.getEngineNo()));
		}

		// Description
		if (fp.getDescription() != null && !fp.getDescription().trim().isEmpty()) {
			fp.setDescription(StringFormatUtil.removeExtraWhiteSpaces(fp.getDescription()));
		}

		// Supplier
		if (fp.getSupplier() != null && !fp.getSupplier().trim().isEmpty()) {
			fp.setSupplier(StringFormatUtil.removeExtraWhiteSpaces(fp.getSupplier()));
		}

		// Code
		if (fp.getCodeVesselName() != null && !fp.getCodeVesselName().trim().isEmpty()) {
			fp.setCodeVesselName(StringFormatUtil.removeExtraWhiteSpaces(fp.getCodeVesselName()));
		}

		if (fp.getFleetTypeId() == FleetType.FT_CONSTRUCTION) {
			// Make
			if (fp.getMake() != null && !fp.getMake().trim().isEmpty()) {
				fp.setMake(StringFormatUtil.removeExtraWhiteSpaces(fp.getMake()));
			} 

			// Model
			if (fp.getModel() != null && !fp.getModel().trim().isEmpty()) {
				fp.setModel(StringFormatUtil.removeExtraWhiteSpaces(fp.getModel()));
			}

			// Chassis No.
			if (fp.getChassisNo() != null && fp.getChassisNo().trim().isEmpty()) {
				fp.setChassisNo(StringFormatUtil.removeExtraWhiteSpaces(fp.getChassisNo()));
			} 

			// Plate No.
			if (fp.getPlateNo() != null && !fp.getPlateNo().trim().isEmpty()) {
				fp.setPlateNo(StringFormatUtil.removeExtraWhiteSpaces(fp.getPlateNo()));
			}

			// Driver
			if (fp.getDriver() != null && !fp.getDriverName().trim().isEmpty()) {
				fp.setDriverName(StringFormatUtil.removeExtraWhiteSpaces(fp.getDriverName()));
			}
		} else {
			// Official No.
			if (fp.getOfficialNo() != null && !fp.getOfficialNo().trim().isEmpty()) {
				fp.setOfficialNo(StringFormatUtil.removeExtraWhiteSpaces(fp.getOfficialNo()));
			} 

			// Call Sign
			if (fp.getCallSign() != null && !fp.getCallSign().trim().isEmpty()) {
				fp.setCallSign(StringFormatUtil.removeExtraWhiteSpaces(fp.getCallSign()));
			}

			// Tonnage Weight
			if (fp.getTonnageWeight() != null && !fp.getTonnageWeight().trim().isEmpty()) {
				fp.setTonnageWeight(StringFormatUtil.removeExtraWhiteSpaces(fp.getTonnageWeight()));
			}

			// VMS
			if (fp.getVms() != null && !fp.getVms().trim().isEmpty()) {
				fp.setVms(StringFormatUtil.removeExtraWhiteSpaces(fp.getVms()));
			}

			// Propeller
			if (fp.getPropeller() != null && !fp.getPropeller().trim().isEmpty()) {
				fp.setPropeller(StringFormatUtil.removeExtraWhiteSpaces(fp.getPropeller()));
			}

			// Winch
			if (fp.getWinch() != null && !fp.getPropeller().trim().isEmpty()) {
				fp.setWinch(StringFormatUtil.removeExtraWhiteSpaces(fp.getWinch()));
			}

			// Captain
			if (fp.getCaptain() != null && !fp.getCaptain().trim().isEmpty()) {
				fp.setCaptain(StringFormatUtil.removeExtraWhiteSpaces(fp.getCaptain()));
			}
		}
	}

	/**
	 * Evaluate if a field exceeds the maximum allowed character.
	 * @param fieldName The field name.
	 * @param fieldLabel The field label.
	 * @param maxChar The maximum characters.
	 * @param errors The binding result.
	 */
	public void evalExceededChar(String fieldName, String fieldLabel, 
			int maxChar, Errors errors) {
		errors.rejectValue(fieldName, null, null, fieldLabel + ValidatorMessages.getString("FleetProfileService.27") +
			maxChar + ValidatorMessages.getString("FleetProfileService.28"));
	}

	/**
	 * Save and get the eb object id.
	 * @param user The logged user.
	 * @param objectTypeId The object type id.
	 * @param currentDate The current date.
	 * @return The eb object id.
	 */
	public Integer saveAndGetEbObject(User user, Integer objectTypeId, Date currentDate) {
		EBObject ebObject = new EBObject();
		ebObject.setObjectTypeId(objectTypeId);
		AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, currentDate));
		ebObjectDao.save(ebObject);
		return ebObject.getId();
	}

	/**
	 * Save the domains.
	 * @param o2os Object to object list.
	 */
	public void saveDomains(List<Domain> o2os) {
		objectToObjectDao.batchSave(o2os);
	}

	/**
	 * Get the list of Fleet profiles by company id.
	 * @param companyId The company id.
	 * @param isExact If exact code vessel name.
	 * @return {@link List<FleetProfile>}
	 */
	public List<FleetProfile> getFleetProfilesByCompanyId(String codeVesselName, Integer companyId, Boolean isExact) {
		List<FleetProfile> fleetProfiles = fleetProfileDao.getFleetProfilesByCompanyId(codeVesselName, companyId, isExact);
		EBObject divEbObject = null;
		EBObject fTEbObject = null;
		Division division = null;
		for (FleetProfile fp : fleetProfiles) {
			divEbObject = objectToObjectDao.getOtherReference(fp.getEbObjectId(), FleetProfile.FP_DIVISION_OR_TYPE_ID);
			if (divEbObject != null) {
				fp.setRefObjectId(divEbObject.getId());
				division = divisionDao.getByEbObjectId(divEbObject.getId());
				fp.setDivisionId(division.getId());
				division = null;
			}
			fTEbObject = objectToObjectDao.getOtherReference(fp.getEbObjectId(), FleetProfile.FP_TYPE_OR_TYPE_ID);
			if (fTEbObject != null) {
				fp.setRefObjectId(fTEbObject.getId());
				FleetType ft = fleetTypeDao.getByEbObjectId(fTEbObject.getId());
				fp.setFleetTypeId(ft.getId());
				fp.setFleetType(ft);
				fTEbObject = null;
			}
			divEbObject = null;
		}
		return fleetProfiles;
	}

	public Division getDivsionByFleet(int fpEbObject) {
		EBObject divEbObject = objectToObjectDao.getOtherReference(fpEbObject, FleetProfile.FP_DIVISION_OR_TYPE_ID);
		if (divEbObject != null) {
			return divisionDao.getByEbObjectId(divEbObject.getId());
		}
		return null;
	}

	public Integer getDivisionIdByFleet(int fpEbObject) {
		Division division = getDivsionByFleet(fpEbObject);
		if (division != null) {
			return division.getId();
		}
		return 0;
	}

	/**
	 * Get the list of {@link FleetProfile} by plate number.
	 * @param companyId The company id.
	 * @param plateNo The plate number.
	 * @param isExact True if exact plate number value, otherwise false.
	 * @return The list of {@link FleetProfile}.
	 */
	public List<FleetProfile> getFleetsByPlateNo(Integer companyId, String plateNo, Boolean isExact) {
		return fleetProfileDao.getFleetsByPlateNo(companyId, plateNo, isExact);
	}
}
