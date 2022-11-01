package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DocumentTypeDao;
import eulap.eb.domain.hibernate.DocumentType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Handles the business logic of Document Type Setting.

 *
 */
@Service
public class AdminDocumentTypeService {

	@Autowired
	private DocumentTypeDao dTypeDao;

	/**
	 * Handles saving of Document Type
	 * @param documentType The Document Type to be saved.
	 * @param user The current logged user.
	 * @param result The inputs to be validated.
	 */
	public void saveDocumentType(DocumentType documentType, User user, BindingResult result){
		boolean isNew = documentType.getId() == 0;
		AuditUtil.addAudit(documentType, new Audit(user.getId(), isNew, new Date()));
		documentType.setName(StringFormatUtil.removeExtraWhiteSpaces(documentType.getName()));
		validate(documentType, result);
		if(!result.hasErrors()){
			dTypeDao.saveOrUpdate(documentType);
		}
	}

	/**
	 * Get the Document Types by criteria.
	 * @param name The name of the Document Type.
	 * @param status The status of the document Type.
	 * @param pageNumber The pageNumber
	 * @return The paged collection of Document Type Objects.
	 */
	public Page<DocumentType> getDocumentTypes(String name, Integer status, Integer pageNumber){
		return dTypeDao.searchDocumentTypes(name, status, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the Document Type Object By Id.
	 * @param documentTypeId The Id of the document type to be retrieved.
	 * @return The Document type object.
	 */
	public DocumentType getDocumentType(Integer documentTypeId){
		return dTypeDao.get(documentTypeId);
	}

	/**
	 * Evaluates whether an entry has corresponding duplicate.
	 * @param documentType The document type object to be evaluated.
	 * @return True if a duplicate entry has been detected, otherwise false.
	 */
	public boolean isDuplicate(DocumentType documentType){
		if(documentType.getId() == 0)
			return dTypeDao.isDuplicate(documentType);
		DocumentType oldDT = dTypeDao.get(documentType.getId());
		if(StringFormatUtil.removeExtraWhiteSpaces(documentType.getName()).equalsIgnoreCase(StringFormatUtil.removeExtraWhiteSpaces(oldDT.getName())))
			return false;
		return dTypeDao.isDuplicate(documentType);
	}

	/**
	 * Handles validation of invalid entries.
	 * @param documentType The Document Type Object to be validated.
	 * @param errors The detected errors.
	 */
	private void validate(DocumentType documentType, Errors errors){
		if(documentType.getName() == null || documentType.getName().isEmpty()){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminDocumentTypeService.0"));
		} else {
			if(isDuplicate(documentType)){
				errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminDocumentTypeService.1"));
			}
			if(documentType.getName().length() > DocumentType.MAX_DOCUMENT_TYPE_NAME){
				errors.rejectValue("name", null, null, ValidatorMessages.getString("AdminDocumentTypeService.2"));
			}
		}
	}
}
