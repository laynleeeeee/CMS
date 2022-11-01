package bp.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import bp.web.ar.CurrentSessionHandler;
/**
 * CBS customized dispatcher servlet.

 */
public class CBSDispatcherServlet extends DispatcherServlet {
	private static final Logger logger =  Logger.getLogger(CBSDispatcherServlet.class);

	private static final long serialVersionUID = 1L;
	private static final String LOGIN_URI = "/a/login";
	private static final String ADMIN_SETTING_URI = "/a/admin";
	private static final String ERROR_URI = "/a/error";
	private static final String WEBSERVICE_URI = "/a/webservice";
	private static final String REGISTER_USER_URI = "/a/registerUser";

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//Handle the validation here.
		String requestUri = request.getRequestURI();
		logger.debug(requestUri +" is the requested uri");
		boolean isWebService = requestUri.startsWith(request.getContextPath() + WEBSERVICE_URI);
		if (isWebService) {
			super.doService(request, response);
		} else {
			if (requestUri.startsWith(request.getContextPath() + LOGIN_URI)
					|| requestUri.startsWith(request.getContextPath() + REGISTER_USER_URI)) {
				super.doService(request, response);
				return;
			}

			HttpSession session = request.getSession();
			User user = CurrentSessionHandler.getLoggedInUser(session);
			if (user == null) { // redirect to login page
				response.sendRedirect(request.getContextPath());
				return;
			}

			UserGroup userGroup = user.getUserGroup();
			if(requestUri.contains(ADMIN_SETTING_URI) && !CurrentSessionHandler.isAdmin(session) && !userGroup.isHasAdminAccess()) {
				logger.debug("The logged user " + user.getUsername() + " does not have an access to admin settings.");
				response.sendRedirect(request.getContextPath()+ERROR_URI);
				return;
			}
			super.doService(request, response);
		}
	}
}
