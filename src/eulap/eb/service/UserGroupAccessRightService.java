package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.UserGroupAccessRightDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroupAccessRight;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.FormStatusProp;
import eulap.eb.service.workflow.WorkflowPropertyGen;
import eulap.eb.web.dto.FormStatusDto;
import eulap.eb.web.dto.ModuleConf;
import eulap.eb.web.dto.UGAccessRightDto;
import eulap.eb.web.dto.UGAccessRightFormDto;
import eulap.eb.web.dto.UGAccessRightRAdminDto;
import eulap.eb.web.dto.UGAccessRightWorkflowDto;

/**
 * A class that handles all business logic of {@link UserGroupAccessRight}

 *
 */
@Service
public class UserGroupAccessRightService {
	@Autowired
	private UserGroupAccessRightDao userGroupAccessRightDao;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private String CMSFormPath; 
	/**
	 * Get all user group access rights.
	 * @return The list of all user group access rights.
	 */
	public List<UserGroupAccessRight> getAllUserGroupAccessRights() {
		return new ArrayList<UserGroupAccessRight>(userGroupAccessRightDao.getAll());
	}
	
	/**
	 * Get the user group access rights per product.
	 * @param userGroupId The unique id of user group.
	 * @return The list of user group access rights.
	 * @throws ConfigurationException 
	 */
	public UGAccessRightDto getUGAccessRightDto(Integer userGroupId) throws ConfigurationException {
		List<UserGroupAccessRight> dBUGAccessRights = userGroupId == null ? 
				new ArrayList<UserGroupAccessRight>() : 
			userGroupAccessRightDao.getUserGroupAccessRights(userGroupId);
				
		return new UGAccessRightDto(userGroupId, getUGARRAdminDtos(dBUGAccessRights, 
				EBModuleGenerator.ADMIN),  
				getUGARFormDtos(dBUGAccessRights), 
				getUGARRAdminDtos(dBUGAccessRights, EBModuleGenerator.REPORTS), 
				getUGARWorkflowDtos(dBUGAccessRights));
	}
	
	
	private List<UGAccessRightFormDto> getUGARFormDtos (List<UserGroupAccessRight> dBUGAccessRights) throws ConfigurationException {
		List<ModuleConf> formModules = EBModuleGenerator.getALLModules(CMSFormPath, EBModuleGenerator.FORMS);
		List<UGAccessRightFormDto> uGAccessRightFormDtos = new ArrayList<UGAccessRightFormDto>();
		for (ModuleConf fm : formModules) {
			UGAccessRightFormDto uGAccessRightFormDto = new UGAccessRightFormDto();
			uGAccessRightFormDto.setFormName(fm.getTitle());
			UserGroupAccessRight uGAccessRight = new UserGroupAccessRight();
			uGAccessRight.setProductKey(fm.getProductCode());
			if (!dBUGAccessRights.isEmpty()) {
				for (UserGroupAccessRight dBGAccessRight : dBUGAccessRights) {
					if (fm.getProductCode() == dBGAccessRight.getProductKey()) {
						int moduleKey = dBGAccessRight.getModuleKey();
						uGAccessRight.setId(dBGAccessRight.getId());
						uGAccessRight.setCreatedBy(dBGAccessRight.getCreatedBy());
						uGAccessRight.setCreatedDate(dBGAccessRight.getCreatedDate());
						uGAccessRightFormDto.setAdd((moduleKey & UGAccessRightFormDto.ADD) == UGAccessRightFormDto.ADD);
						uGAccessRightFormDto.setEdit((moduleKey & UGAccessRightFormDto.EDIT) == UGAccessRightFormDto.EDIT);
						uGAccessRightFormDto.setSearch((moduleKey & UGAccessRightFormDto.SEARCH) == UGAccessRightFormDto.SEARCH);
						uGAccessRightFormDto.setApproval((moduleKey & UGAccessRightFormDto.APPROVAL) == UGAccessRightFormDto.APPROVAL);
						break;
					}
				}
			}
			uGAccessRightFormDto.setUserGroupAccessRight(uGAccessRight);
			uGAccessRightFormDtos.add(uGAccessRightFormDto);
		}
		return uGAccessRightFormDtos;
	}
	
	private List<UGAccessRightRAdminDto> getUGARRAdminDtos (List<UserGroupAccessRight> dBUGAccessRights, 
			int modGeneratorCode) throws ConfigurationException {
		List<ModuleConf> reportModules = EBModuleGenerator.getALLModules(CMSFormPath, modGeneratorCode);
		List<UGAccessRightRAdminDto> uGAccessRightReportDtos = new ArrayList<UGAccessRightRAdminDto>();
		for (ModuleConf rm : reportModules) {
			UGAccessRightRAdminDto uGAccessRightReportDto = new UGAccessRightRAdminDto();
			uGAccessRightReportDto.setReportName(rm.getTitle());
			uGAccessRightReportDto.setModuleCode(rm.getModuleCode());
			UserGroupAccessRight uGAccessRight = new UserGroupAccessRight();
			uGAccessRight.setProductKey(rm.getProductCode());
			if (!dBUGAccessRights.isEmpty()) {
				for (UserGroupAccessRight dBGAccessRight : dBUGAccessRights) {
					if (rm.getProductCode() == dBGAccessRight.getProductKey()) {
						int moduleKey = dBGAccessRight.getModuleKey();
						uGAccessRight.setId(dBGAccessRight.getId());
						uGAccessRight.setCreatedBy(dBGAccessRight.getCreatedBy());
						uGAccessRight.setCreatedDate(dBGAccessRight.getCreatedDate());
						uGAccessRightReportDto.setAllowAccess((moduleKey & rm.getModuleCode()) == rm.getModuleCode());
						break;
					}
				}
			}
			uGAccessRightReportDto.setUserGroupAccessRight(uGAccessRight);
			uGAccessRightReportDtos.add(uGAccessRightReportDto);
		}
		return uGAccessRightReportDtos;
	}
	
	private List<UGAccessRightWorkflowDto> getUGARWorkflowDtos (List<UserGroupAccessRight> dBUGAccessRights) throws ConfigurationException {
		String formPath = CMSFormPath;
		List<ModuleConf> workflowModules = EBModuleGenerator.getALLModules(formPath, EBModuleGenerator.APPROVAL);
		List<UGAccessRightWorkflowDto> uGAccessRightWorkflowDtos = new ArrayList<UGAccessRightWorkflowDto>();
		for (ModuleConf wm : workflowModules) {
			UGAccessRightWorkflowDto uGAccessRightWorkflowDto = new UGAccessRightWorkflowDto();
			uGAccessRightWorkflowDto.setWorkflowName(wm.getTitle());
			uGAccessRightWorkflowDto.setModuleCode(
					wm.getModuleCode() == UGAccessRightFormDto.APPROVAL ? 0 : wm.getModuleCode());
			UserGroupAccessRight uGAccessRight = new UserGroupAccessRight();
			uGAccessRight.setProductKey(wm.getProductCode());

			if (!dBUGAccessRights.isEmpty()) {
				for (UserGroupAccessRight dBGAccessRight : dBUGAccessRights) {
					if (wm.getProductCode() == dBGAccessRight.getProductKey()) {
						uGAccessRight.setId(dBGAccessRight.getId());
						uGAccessRight.setCreatedBy(dBGAccessRight.getCreatedBy());
						uGAccessRight.setCreatedDate(dBGAccessRight.getCreatedDate());
						break;
					}
				}
			}
			FormProperty formProperty = WorkflowPropertyGen.getFormProperty(formPath, wm.getWorkflow(), null, null);
			List<FormStatusDto> statuses = new ArrayList<FormStatusDto>();
			
			for (FormStatusProp fsp : formProperty.getFormStatuses()) {
				FormStatusDto fsd = new FormStatusDto(fsp.getModuleCode(), formStatusDao.get(fsp.getStatusId()));
				if (!dBUGAccessRights.isEmpty()) {
					for (UserGroupAccessRight dBGAccessRight : dBUGAccessRights) {
						if (fsp.getProductCode() == dBGAccessRight.getProductKey()) {
							int moduleKey = dBGAccessRight.getModuleKey();
							fsd.setAllowAccess((moduleKey & fsp.getModuleCode()) == fsp.getModuleCode());
							break;
						}
					}
				}
				statuses.add(fsd);
			}
			uGAccessRightWorkflowDto.setStatuses(processFSD(statuses));
			uGAccessRightWorkflowDto.setUserGroupAccessRight(uGAccessRight);
			uGAccessRightWorkflowDtos.add(uGAccessRightWorkflowDto);			
		}
		return uGAccessRightWorkflowDtos;
	}
	
	private List<FormStatusDto> processFSD (List<FormStatusDto> statuses) {
		List<FormStatusDto> processedFSDs = new ArrayList<FormStatusDto>();
		Map<Integer, FormStatusDto> formStatuses = new HashMap<Integer, FormStatusDto>();
		for (FormStatusDto fsd : statuses) {
			if (formStatuses.containsKey(fsd.getModuleKey())) {
				FormStatusDto fsd2 = formStatuses.get(fsd.getModuleKey());
				FormStatus fs = fsd.getFormStatus();
				FormStatus fs2 = fsd2.getFormStatus();
				if (!fs.getDescription().equals(fs2.getDescription())) {
					fs2.setDescription(fs.getDescription() + " / " + fs2.getDescription() );
					fsd2.setFormStatus(fs2);
				}
				formStatuses.put(fsd.getModuleKey(), fsd2);
			} else {
				formStatuses.put(fsd.getModuleKey(), fsd);
			}
		}
		
		for (Map.Entry<Integer, FormStatusDto> e : formStatuses.entrySet())
			processedFSDs.add(e.getValue());
		
		Collections.sort(processedFSDs, new Comparator<FormStatusDto>() {
			@Override
			public int compare(FormStatusDto fsd1, FormStatusDto fsd2) {
				return fsd1.getModuleKey().compareTo(fsd2.getModuleKey());
			}
		});
		
		return processedFSDs;
	}
	
	/**
	 * Save the user group access right object in the database.
	 * @param uGAccessRightDto The domain object to be saved in the database.
	 * @param user The logged user.
	 */
	public void saveUGAccessRight (UGAccessRightDto uGAccessRightDto, 
			User user) {
		List<UGAccessRightRAdminDto> uGARAdmins = uGAccessRightDto.getuGarAdminDtos();
		List<UGAccessRightFormDto> uGARForms = uGAccessRightDto.getuGARFormDtos();
		List<UGAccessRightRAdminDto> uGARReports = uGAccessRightDto.getuGARReportDtos();
		List<UGAccessRightWorkflowDto> uGARWorkflows = uGAccessRightDto.getuGARWorkflowDtos();
		Collection<UserGroupAccessRight> uGARs = new ArrayList<>();

		// Adding admins to the computation of module key.
		if (uGARAdmins != null && !uGARAdmins.isEmpty()) {
			processRAdminDtos(uGARAdmins, uGARs, uGAccessRightDto);
		}

		// Adding reports to the computation of module key.
		if (uGARReports != null && !uGARReports.isEmpty()) {
			processRAdminDtos(uGARReports, uGARs, uGAccessRightDto);
		}

		Collection<UserGroupAccessRight> uGARFrms = new ArrayList<>();
		// Adding forms to the computation of module key.
		if (uGARForms != null && !uGARForms.isEmpty()) {
			for (UGAccessRightFormDto uGAR : uGARForms) {
				UserGroupAccessRight uGAccessRight = uGAR.getUserGroupAccessRight();
				uGAccessRight.setUserGroupId(uGAccessRightDto.getUserGroupId());
				int moduleKey = 0;
				if (uGAR.isAdd())
					moduleKey += UGAccessRightFormDto.ADD;
				if (uGAR.isEdit())
					moduleKey += UGAccessRightFormDto.EDIT;
				if (uGAR.isSearch())
					moduleKey += UGAccessRightFormDto.SEARCH;
				if (uGAR.isApproval())
					moduleKey += UGAccessRightFormDto.APPROVAL;

				if (moduleKey == 0)
					continue;
				uGAccessRight.setModuleKey(moduleKey);
				uGARFrms.add(uGAccessRight);
			}
		}

		// Adding workflows to the computation of module key.
		if (uGARWorkflows != null && !uGARWorkflows.isEmpty()) {
			for (UGAccessRightWorkflowDto uGAR : uGARWorkflows) {
				UserGroupAccessRight uGAccessRight = uGAR.getUserGroupAccessRight();
				uGAccessRight.setUserGroupId(uGAccessRightDto.getUserGroupId());
				int totalModuleCode = uGAR.getModuleCode();
				if (totalModuleCode == 0)
					continue;
				uGAccessRight.setModuleKey(totalModuleCode);
				uGARFrms.add(uGAccessRight);
			}
		}
		
		uGARs.addAll(groupByProductCode(uGARFrms));
		List<UserGroupAccessRight> savedAccessRights =
				userGroupAccessRightDao.getUserGroupAccessRights(uGAccessRightDto.getUserGroupId());

		boolean isAdmin =
				userGroupAccessRightDao.isAdmin(uGAccessRightDto.getUserGroupId());

		// Access rights to be deleted.
		List<Integer> toBeDeleted = new ArrayList<Integer>();
		for (UserGroupAccessRight toBeDeletedAr : savedAccessRights) {
			// special case: do not delete the set up data for the access right of administrator group code.
			if (toBeDeletedAr.getProductKey() == 0 && isAdmin)
				continue;
			toBeDeleted.add(toBeDeletedAr.getId());
		}
		userGroupAccessRightDao.delete(toBeDeleted);

		// Access rights to be saved.
		List<Domain> toBeSaved = new ArrayList<Domain>();
		for (UserGroupAccessRight uGAR : uGARs) {
			if (uGAR.getProductKey() != 0) {
				uGAR.setId(0);
				uGAR.setUserGroupId(uGAccessRightDto.getUserGroupId());
			}
			AuditUtil.addAudit(uGAR, new Audit (user.getId(), true, new Date ()));
			toBeSaved.add (uGAR);
		}
		userGroupAccessRightDao.batchSave(toBeSaved);
	}
	
	private Collection<UserGroupAccessRight> groupByProductCode (Collection<UserGroupAccessRight> uGARs) {
		Collection<UserGroupAccessRight> processedUGARs = new ArrayList<>();
		Map<Integer, UserGroupAccessRight> sums = new HashMap<Integer, UserGroupAccessRight>();
		
		// compute sums
        for (UserGroupAccessRight uGAR: uGARs) {
        	int modKey = uGAR.getModuleKey() < 0 ? 0 : uGAR.getModuleKey();
            if (sums.containsKey(uGAR.getProductKey())) {
            	UserGroupAccessRight ugar = sums.get(uGAR.getProductKey());
            	ugar.setModuleKey(ugar.getModuleKey() + modKey);
                sums.put(uGAR.getProductKey(), ugar);
            } else {
            	uGAR.setModuleKey(modKey);
                sums.put(uGAR.getProductKey(), uGAR);
            }
        }
       
        for (Map.Entry<Integer, UserGroupAccessRight> e : sums.entrySet()) 
        	processedUGARs.add(e.getValue());
		return processedUGARs;
	}
	
	private void processRAdminDtos(List<UGAccessRightRAdminDto> lsUGARAdmins, 
			Collection<UserGroupAccessRight> uGARs, UGAccessRightDto uGAccessRightDto) {
		for (UGAccessRightRAdminDto uGAR : lsUGARAdmins) {
			UserGroupAccessRight uGAccessRight = uGAR.getUserGroupAccessRight();
			uGAccessRight.setUserGroupId(uGAccessRightDto.getUserGroupId());		
			uGAccessRight.setModuleKey(uGAR.isAllowAccess() ? uGAR.getModuleCode() : 0);
			if (uGAccessRight.getModuleKey() == Integer.valueOf(0))
				continue;
			uGARs.add(uGAccessRight);
		}
	}
	
	/**
	 * Check if the user group contains one or more admin property from user group access right.
	 * @param userGroupId the user group id. 
	 * @return True if the user group has admin property, otherwise false.
	 * @throws ConfigurationException 
	 */
	public boolean hasAdminModule (int userGroupId) throws ConfigurationException {
		List<Integer> adminProductKeys = new ArrayList<Integer>();
		List<ModuleConf> adminModules = 
				EBModuleGenerator.getALLModules(CMSFormPath, 
						EBModuleGenerator.ADMIN);
		if (adminModules != null && !adminModules.isEmpty()) {
			for (ModuleConf am : adminModules) {
				adminProductKeys.add(am.getProductCode());
			}
		}
		return userGroupAccessRightDao.hasAdminModule(userGroupId, adminProductKeys);
	}

	public boolean hasAccessRight(Integer userGroupId, Integer productKey) {
		return userGroupAccessRightDao.getUGARByPKAndUG(userGroupId, productKey)
				== null ? false : true;
	}
}
