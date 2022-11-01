package eulap.eb.service.oo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will trace the relationship of the objects. 

 *
 */
@Service
public class OORelationshipTracer {
	@Autowired
	private OOServiceHandler ooServiceHandler;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	
	/**
	 * Trace the relationship of the object. 
	 * @param objectId the object id. 
	 * @param writer output stream writer for logging. 
	 */
	public void trace (int objectId, OutputStreamWriter writer, User user) throws ClassNotFoundException, IOException {
		ObjectInfoProcessor objectInfoProcessor = ooServiceHandler.getObjectInfoProcessor(objectId, user);
		EBObject ebObject = ebObjectDao.get(objectId);
		Domain parent = objectInfoProcessor.getDomain(ebObject);
		List<Domain> children = objectInfoProcessor.getChildren(ebObject, objectToObjectDao);
		
		writer.write("Parent Object : " + parent);
		writer.write(System.lineSeparator());
		writer.write("========================================================================================================================");
		writer.write("=================================================CHILDREN===============================================================");
		writer.write("========================================================================================================================");
		writer.write(System.lineSeparator());
		writer.write("Total Children: "+children.size());
		writer.write(System.lineSeparator());
		for (Domain child : children) {
			writer.write("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			writer.write(System.lineSeparator());
			writer.write(child.toString());
			writer.write(System.lineSeparator());
			writer.write("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			writer.write(System.lineSeparator());
		}
		writer.write("========================================================================================================================");
		writer.write("========================================================================================================================");
		writer.flush();
	}
}
