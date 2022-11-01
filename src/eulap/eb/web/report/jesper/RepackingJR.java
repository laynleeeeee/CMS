package eulap.eb.web.report.jesper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.RepackingService;

/**
 * Controller class to generate the printout of Repacking form.

 */

@Controller
@RequestMapping("/repackingJr")
public class RepackingJR {
	private static Logger logger = Logger.getLogger(RepackingJR.class);
	@Autowired
	private RepackingService repService;

	@RequestMapping(value="{typeId}/pdf", method=RequestMethod.GET)
	public String generateReport(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId") int pId, Model model) {
		Repacking rp = repService.getRepacking(pId, typeId);
		JRDataSource dataSource = null;
		boolean isRepacking = typeId == RepackingType.TYPE_REPACKING;
		if (isRepacking) {
			dataSource = new JRBeanCollectionDataSource(rp.getrItems());
		} else {
			List<Repacking> repackings = new ArrayList<Repacking>();
			repackings.add(rp);
			dataSource = new JRBeanCollectionDataSource(repackings);
		}
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		logger.debug("Successfully generated the datasource of the printout.");

		Company company = rp.getCompany();
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("warehouse", rp.getWarehouse().getName());
		model.addAttribute("rNumber", rp.getrNumber());
		model.addAttribute("date", DateUtil.formatDate(rp.getrDate()));
		model.addAttribute("remarks", rp.getRemarks());
		model.addAttribute("divisionName", rp.getDivision().getName());

		FormWorkflow workflowLog = rp.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy",
						user.getUserFullName());
				model.addAttribute("approvedByPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getUserFullName());
				model.addAttribute("createdByPosition", position.getName());
			}
		}
		logger.debug("Successfully generated the parameters of the printout.");
		logger.info("Showing the printout of repacking form: "+pId);
		if(isRepacking && rp.getDivisionId() != null) {
			return "ReclassPDF.jasper";
		} else {
			if (isRepacking) {
				return "RepackingPDF.jasper";
			}
			return "ItemConversionPDF.jasper";
		}
	}
}
