package eulap.common.util;

import java.util.Date;

import org.springframework.ui.Model;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.User;

/**
 * Utility class for reports.

 *
 */
public class ReportUtil {

	/**
	 * Get the print details of the report with user name format.
	 * @param model The model.
	 * @param loggedUser The logged user.
	 * @param isFirstNameFirst True if the name format is first name first, otherwise false.
	 */
	public static void getPrintDetails(Model model, User loggedUser, boolean isFirstNameFirst) {
		String printedDate = DateUtil.formatDateWithTime(new Date());
		String fullName = isFirstNameFirst ? loggedUser.getUserFullName() : loggedUser.getFullName();
		model.addAttribute("printedDate", printedDate);
		model.addAttribute("printedBy", fullName);
	}

	/**
	 * Get the print details of the report.
	 * @param model The model.
	 * @param loggedUser The logged user.
	 */
	public static void getPrintDetails(Model model, User loggedUser) {
		getPrintDetails(model, loggedUser, false);
	}
}
