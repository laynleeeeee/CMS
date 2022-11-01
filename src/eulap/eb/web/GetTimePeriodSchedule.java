package eulap.eb.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.service.PayrollTimePeriodService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * A controller class that retrieves the time period schedule.


 *
 */
@Controller
@RequestMapping ("getTPSchedules")
public class GetTimePeriodSchedule {
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	private static Logger logger = Logger.getLogger(GetTimePeriodSchedule.class);

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getSchedules (@RequestParam(value="month") Integer month,
			@RequestParam(value="year") Integer year,
			@RequestParam(value="payrollTimePeriodScheduleId", required=false) Integer payrollTimePeriodScheduleId) {
		logger.info("Loading time period schedule!!");
		List<PayrollTimePeriodSchedule> schedules = payrollTimePeriodService.getTimePeriodSchedules(month, year, payrollTimePeriodScheduleId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(schedules, jConfig);
		return jsonArray.toString();
	}
}
