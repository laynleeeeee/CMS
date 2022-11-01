package eulap.eb.service.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.bir.AlphalistDetail;
import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;
import eulap.eb.service.bir.AlphalistWriter;
import eulap.eb.web.dto.AlphalistPayeesControlC4;
import eulap.eb.web.dto.AlphalistPayeesControlC5;
import eulap.eb.web.dto.AlphalistPayeesControlC6;
import eulap.eb.web.dto.AlphalistPayeesDetailD4;
import eulap.eb.web.dto.AlphalistPayeesDetailD5;
import eulap.eb.web.dto.AlphalistPayeesDetailD6;
import eulap.eb.web.dto.AlphalistPayeesHeaderDto;
import eulap.eb.web.dto.AnnualAlphalistPayeesDetailDto;

/**
 * Annual Alpha List of Final Withholding Taxes Service.

 *
 */
@Service
public class AnnualAlphaListWTFinalService {
	@Autowired
	private APInvoiceDao apDao;
	private static Logger logger = Logger.getLogger(AnnualAlphaListWTFinalService.class);
	@Autowired
	private CompanyDao companyDao;
	private static String SCHEDULE_4 = "3";
	private static String SCHEDULE_5 = "4";
	private static String SCHEDULE_6 = "5";
	private static final String wtTaxType = "3,4,5";//SCHEDULE_4,SCHEDULE_5,SCHEDULE_6;

	/**
	 * Generate the pdf or excel report of Annual ALphalist Withholding Taxes Expanded.
	 */
	public List<AnnualAlphalistPayeesDetailDto> generateAnnualALphalistWTFinal(Integer companyId, Integer divisionId, Integer year) {
		return apDao.getAnnualAlphalistWTFinal(companyId, divisionId, year, true, wtTaxType);
	}

	/**
	 *
	 * @param companyId
	 * @param divisionId
	 * @param year
	 * @param retrnPeriod
	 * @return the created dat file
	 */
	public Path generateBIRDatFile(Integer companyId, Integer divisionId, Integer year, String retrnPeriod) {
		AlphalistPayeesHeaderDto header = createHeader(companyId, divisionId, year, retrnPeriod);
		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		headers.add(header);
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		logger.info("successfully created : " + path);
		return path;
	}

	private AlphalistPayeesHeaderDto createHeader ( int companyId, int divisionId, int year, String retrnPeriod) {
		Company company = companyDao.get(companyId);
		String tin = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());
		List<AlphalistSchedule> schedules = new ArrayList<AlphalistSchedule>();
		double actualAmtWthld4 = 0;
		double incomePayment4 = 0;

		//Detail Schedule 4
		List<AnnualAlphalistPayeesDetailDto> sched4 = apDao.getAnnualAlphalistWTFinal(companyId, divisionId, year, false, SCHEDULE_4);
		List<AlphalistDetail> detail = new ArrayList<AlphalistDetail>();
		AlphalistPayeesDetailD4 s = null;
		Integer seq4 = 1;
		for (AnnualAlphalistPayeesDetailDto sched : sched4) {
			s = new AlphalistPayeesDetailD4();
			actualAmtWthld4 += new Double(sched.getActualAmtWthld());
			incomePayment4 += new Double(sched.getIncomePayment());
			s.setSeqNum(seq4++);
			s.setScheduleNum(AlphalistPayeesDetailD4.SCHEDULE_NUM_D4);
			s.setfTypeCode(AlphalistPayeesDetailD4.F_TYPE_CODE_1604F);
			s.setTinEmpyr(tin);
			s.setBranchCodeEmplyr(branchCode);
			s.setTin(sched.getTin());
			s.setBranchCode(sched.getBranchCode());
			s.setRegisteredName(sched.getRegisteredName());
			s.setLastName(sched.getLastName());
			s.setFirstName(sched.getFirstName());
			s.setMiddleName(sched.getMiddleName());
			s.setRetrnPeriod(retrnPeriod);
			s.setStatusCode(sched.getStatusCode());
			s.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			s.setTaxRate(sched.getTaxRate());
			s.setIncomePayment(NumberFormatUtil.convertDouble(sched.getIncomePayment()));
			s.setActualAmtWthld(NumberFormatUtil.convertDouble(sched.getActualAmtWthld()));
			detail.add(s);
		}

		//Detail Schedule 5
		List<AnnualAlphalistPayeesDetailDto> sched5 = apDao.getAnnualAlphalistWTFinal(companyId, divisionId, year, false, SCHEDULE_5);
		List<AlphalistDetail> detail2 = new ArrayList<AlphalistDetail>();
		AlphalistPayeesDetailD5 s1 = null;
		Integer seq5 = 1;
		double actualAmtWthld5 = 0;
		double fringeBenefit5 = 0;
		double monetaryValue5 = 0;
		for (AnnualAlphalistPayeesDetailDto sched : sched5) {
			s1 = new AlphalistPayeesDetailD5();
			actualAmtWthld5 += new Double(sched.getActualAmtWthld());
			fringeBenefit5 += new Double (sched.getIncomePayment());
			s1.setSeqNum(seq5++);
			s1.setScheduleNum(AlphalistPayeesDetailD5.SCHEDULE_NUM_D5);
			s1.setfTypeCode(AlphalistPayeesDetailD5.F_TYPE_CODE_1604F);
			s1.setTinEmpyr(tin);
			s1.setBranchCodeEmplyr(branchCode);
			s1.setTin(StringFormatUtil.parseBIRTIN(sched.getTin()));
			s1.setBranchCode(StringFormatUtil.parseBranchCode(sched.getTin()));
			s1.setLastName(sched.getLastName());
			s1.setFirstName(sched.getFirstName());
			s1.setMiddleName(sched.getMiddleName());
			s1.setRetrnPeriod(retrnPeriod);
			s1.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			s1.setFringeBenefit(NumberFormatUtil.convertDouble(sched.getIncomePayment()));
			s1.setActualAmtWthld(NumberFormatUtil.convertDouble(sched.getActualAmtWthld()));
			s1.setMonetaryValue(NumberFormatUtil.convertDouble(sched.getIncomePayment()+sched.getActualAmtWthld()));
			monetaryValue5 += new Double (s1.getMonetaryValue());
			detail2.add(s1);
		}

		//Detail Schedule 6
		List<AnnualAlphalistPayeesDetailDto> sched6 = apDao.getAnnualAlphalistWTFinal(companyId, divisionId, year, false, SCHEDULE_6);
		AlphalistPayeesDetailD6 s2 = null;
		Integer seq6 = 1;
		double incomePayment6 = 0;
		List<AlphalistDetail> detail3 = new ArrayList<AlphalistDetail>();
		for (AnnualAlphalistPayeesDetailDto sched : sched6) {
			s2 = new AlphalistPayeesDetailD6();
			incomePayment6 += new Double(sched.getIncomePayment());
			s2.setSeqNum(seq6++);
			s2.setScheduleNum(AlphalistPayeesDetailD6.SCHEDULE_NUM_D6);
			s2.setfTypeCode(AlphalistPayeesDetailD6.F_TYPE_CODE_1604F);
			s2.setTinEmpyr(tin);
			s2.setBranchCodeEmplyr(branchCode);
			s2.setTin(StringFormatUtil.parseBIRTIN(sched.getTin()));
			s2.setBranchCode(StringFormatUtil.parseBranchCode(sched.getTin()));
			s2.setRegisteredName(sched.getRegisteredName());
			s2.setLastName(sched.getLastName());
			s2.setFirstName(sched.getFirstName());
			s2.setMiddleName(sched.getMiddleName());
			s2.setRetrnPeriod(retrnPeriod);
			s2.setStatusCode(sched.getStatusCode());
			s2.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			s2.setIncomePayment(NumberFormatUtil.convertDouble(sched.getIncomePayment()));
			detail3.add(s2);
		}

		if(!detail.isEmpty()&&detail!=null) {
			AlphalistPayeesControlC4 control1 = new AlphalistPayeesControlC4();
			control1.setAlphaType(AlphalistPayeesControlC4.ALPHA_TYPE_C4);
			control1.setfTypeCode(AlphalistPayeesControlC4.F_TYPE_CODE_1604F);
			control1.setTinWa(tin);
			control1.setBranchCodeWa(branchCode);
			control1.setRetrnPeriod(retrnPeriod);
			control1.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment4));
			control1.setActualAmtWthld(NumberFormatUtil.convertDouble(actualAmtWthld4));
			AlphalistSchedule ctrl1 = AlphalistSchedule.getInstance(detail, control1);
			schedules.add(ctrl1);
		}

		if(!detail2.isEmpty()&&detail2!=null) {
			AlphalistPayeesControlC5 control2 = new AlphalistPayeesControlC5();
			control2.setAlphaType(AlphalistPayeesControlC5.ALPHA_TYPE_C5);
			control2.setfTypeCode(AlphalistPayeesControlC5.F_TYPE_CODE_1604F);
			control2.setTinWa(tin);
			control2.setBranchCodeWa(branchCode);
			control2.setRetrnPeriod(retrnPeriod);
			control2.setFringeBenefit(NumberFormatUtil.convertDouble(fringeBenefit5));
			control2.setActualAmtWthld(NumberFormatUtil.convertDouble(actualAmtWthld5));
			control2.setMonetaryValue(NumberFormatUtil.convertDouble(monetaryValue5));
			AlphalistSchedule ctrl2 = AlphalistSchedule.getInstance(detail2, control2);
			schedules.add(ctrl2);
		}

		if(!detail3.isEmpty()&&detail3!=null) {
			AlphalistPayeesControlC6 control3 = new AlphalistPayeesControlC6();
			control3.setAlphaType(AlphalistPayeesControlC6.ALPHA_TYPE_C6);
			control3.setfTypeCode(AlphalistPayeesControlC6.F_TYPE_CODE_1604F);
			control3.setTinWa(tin);
			control3.setBranchCodeWa(branchCode);
			control3.setRetrnPeriod(retrnPeriod);
			control3.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment6));
			AlphalistSchedule ctrl3 = AlphalistSchedule.getInstance(detail3, control3);
			schedules.add(ctrl3);
		}
		String fileName = tin+branchCode+"1231"+year+"1604f";//DAT File Name
		return AlphalistPayeesHeaderDto.getInstance(tin, branchCode, retrnPeriod, fileName, schedules);
	}
}

