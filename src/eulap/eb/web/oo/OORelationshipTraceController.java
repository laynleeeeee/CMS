package eulap.eb.web.oo;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OORelationshipTracer;

/**
 * A controller class that will trace the object relationship. 

 *
 */
@Controller
@RequestMapping("/trace")
public class OORelationshipTraceController {
	@Autowired
	public OORelationshipTracer ooRelationshipTracer;
	@RequestMapping (method = RequestMethod.GET)
	public void traceRelactionship (@RequestParam (value="objectId", required = true) Integer objectId, 
			HttpServletResponse response, HttpSession session) throws IOException, ClassNotFoundException {
		OutputStreamWriter writer = null;
		User user = CurrentSessionHandler.getLoggedInUser(session);
		try {
			writer = new OutputStreamWriter(response.getOutputStream());
			ooRelationshipTracer.trace(objectId, writer, user);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
