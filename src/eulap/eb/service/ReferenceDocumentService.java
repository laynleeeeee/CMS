package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import eulap.common.domain.Domain;
import eulap.common.util.ReferenceDocumentUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Class that handles business logic for Reference Document

 */
@Service
public class ReferenceDocumentService {
	private final Logger logger = Logger.getLogger(ReferenceDocumentService.class);
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private EbObjectService ebObjectService;

	/**
	 * Get the list of reference document by ebObjectId.
	 * @param ebObjectId The eb object id.
	 * @return The list of reference documents.
	 */
	public List<ReferenceDocument> getReferenceDocuments(Integer ebObjectId) {
		logger.info("Getting the list of refence document by ebObject id.");
		List<ObjectToObject> objectToObjects = objectToObjectDao.getAllByRefId("fromObjectId", ebObjectId);
		List<ReferenceDocument> referenceDocuments = new ArrayList<ReferenceDocument>();
		for (ObjectToObject objectToObject : objectToObjects) {
			referenceDocuments.addAll(referenceDocumentDao.getAllByRefId("ebObjectId",
					objectToObject.getToObjectId()));
		}
		return referenceDocuments;
	}

	/**
	 * Validation for reference document description.
	 * @param referenceDocuments
	 * @param errors
	 */

	public void validateReferences(List<ReferenceDocument> referenceDocuments, Errors errors) {
		validateReferences(referenceDocuments, errors, "");
	}

	/**
	 * Validation for reference document description.
	 * @param referenceDocuments
	 * @param errors
	 * @param fieldPrepend
	 */
	public void validateReferences(List<ReferenceDocument> referenceDocuments, Errors errors, String fieldPrepend) {
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			int row = 0;
			for (ReferenceDocument referenceDocument : referenceDocuments) {
				row++;
				String file = referenceDocument.getFileName();
				String fileDesc = referenceDocument.getDescription();
				if (fileDesc == null || fileDesc.trim().isEmpty()) {
					errors.rejectValue(fieldPrepend+"referenceDocsMessage", null, null,
							String.format(ValidatorMessages.getString("ReferenceDocumentService.4"), row));
				} else if (StringFormatUtil.removeExtraWhiteSpaces(fileDesc).trim().length() > ReferenceDocument.MAX_DESCRIPTION) {
					errors.rejectValue(fieldPrepend+"referenceDocsMessage", null, null,
							String.format(ValidatorMessages.getString("ReferenceDocumentService.5"), ReferenceDocument.MAX_DESCRIPTION, row));
				} else if (file == null || file.trim().isEmpty()) {
					errors.rejectValue(fieldPrepend+"referenceDocsMessage", null, null,
							String.format(ValidatorMessages.getString("ReferenceDocumentService.3"), row));
				}
			}
		}
	}

	/**
	 * Save the reference document/s.
	 * @param user The logged user.
	 * @param isNew True if the record is new, otherwise false.
	 * @param refObjectId The eb object id of the reference object.
	 * @param referenceDocuments The list of reference documents to be saved.
	 * @param isChild True if reference document is considered to be child of the main object, otherwise false.
	 */
	public void saveReferenceDocuments(User user, boolean isNew, Integer refObjectId,
			List<ReferenceDocument> referenceDocuments, boolean isChild) {
		if (!isNew) {
			List<ReferenceDocument> savedRDocs = referenceDocumentDao.getRDsByEbObject(refObjectId);
			if (savedRDocs != null && !savedRDocs.isEmpty()) {
				List<Integer> ids = new ArrayList<>();
				for (ReferenceDocument rd : savedRDocs) {
					ids.add(rd.getId());
				}
				referenceDocumentDao.delete(ids);
				ids = null;
			}
		}
		List<Domain> toBeSaved = new ArrayList<>();
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			if (!isChild) {
				saveObjectRelationship(user, refObjectId, referenceDocuments);
			}
			for (ReferenceDocument doc : referenceDocuments) {
				doc.setFilePath(ReferenceDocumentUtil.writeBase64ToFile(doc.getFile(), doc.getFileExtension()));
				toBeSaved.add(doc);
			}
			referenceDocumentDao.batchSave(toBeSaved);
		}
	}

	private void saveObjectRelationship(User user, Integer refObjectId, List<ReferenceDocument> referenceDocuments) {
		List<Domain> o2os = new ArrayList<>();
		Date date = new Date();
		for (ReferenceDocument referenceDocument : referenceDocuments) {
			referenceDocument.setEbObjectId(ebObjectService.saveAndGetEbObjectId(user.getId(),
					ReferenceDocument.OBJECT_TYPE_ID, date));
			o2os.add(ObjectToObject.getInstanceOf(refObjectId, referenceDocument.getEbObjectId(),
					ReferenceDocument.OR_TYPE_ID, user, date));
		}
		if (!o2os.isEmpty()) {
			objectToObjectDao.batchSave(o2os);
		}
	}

	/**
	 * Process the list of reference documents
	 * @param parentObjectId The parent object id
	 * @return The processed list of reference documents
	 */
	public List<ReferenceDocument> processReferenceDocs(Integer parentObjectId) throws IOException {
		List<ReferenceDocument> referenceDocuments = getReferenceDocuments(parentObjectId);
		for (ReferenceDocument doc : referenceDocuments) {
			doc.setFile(ReferenceDocumentUtil.encodeFileToBase64Binary(doc.getFilePath()));
		}
		return referenceDocuments;
	}
}
