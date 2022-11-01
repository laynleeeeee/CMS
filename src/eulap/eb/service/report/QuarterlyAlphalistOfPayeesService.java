package eulap.eb.service.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.bir.AlphalistDetail;
import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;
import eulap.eb.service.bir.AlphalistWriter;
import eulap.eb.web.dto.QAPControlsC1Dto;
import eulap.eb.web.dto.QAPControlsC1DtoFinal;
import eulap.eb.web.dto.QAPControlsC2Dto;
import eulap.eb.web.dto.QAPControlsC2DtoFinal;
import eulap.eb.web.dto.QAPControlsC3DtoFinal;
import eulap.eb.web.dto.QAPDetailsD1Dto;
import eulap.eb.web.dto.QAPDetailsD1DtoFinal;
import eulap.eb.web.dto.QAPDetailsD2Dto;
import eulap.eb.web.dto.QAPDetailsD2DtoFinal;
import eulap.eb.web.dto.QAPDetailsD3DtoFinal;
import eulap.eb.web.dto.QAPHeaderDto;
import eulap.eb.web.dto.QAPHeaderDtoFinal;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDATDto;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDto;

/**
 * Alpha numeric tax code report service.

 *
 */
@Service
public class QuarterlyAlphalistOfPayeesService {
	@Autowired
	private ArMiscellaneousDao arMiscDao;
	@Autowired
	private CompanyDao companyDao;

	private static final String SCHEDULE_1 = "0,1";
	private static final String SCHEDULE_2 = "2";
	private static final String SCHEDULE_1_FINAL = "3";
	private static final String SCHEDULE_2_FINAL = "4";
	private static final String SCHEDULE_3_FINAL = "5";
	private static final String SCHED_EXPANDED = "1,2";
	private static final String SCHED_FINAL = "3,4,5"; //SCHEDULE_4,SCHEDULE_5,SCHEDULE_6;

	/**
	 * Get the list of quarterly alphalist of payees DTO
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param month The month
	 * @param year The year
	 * @param processTin True, if processed the TIN, otherwise false
	 * @return List of quarterly alphalist of payees DTO
	 */
	public List<QuarterlyAlphaListOfPayeesDto> getQuarterlyAlphaListOfPayees(Integer companyId, Integer divisionId,
			Integer month, Integer year, boolean processTin) {
		return arMiscDao.getQuarterlyAlphalistOfPayees(companyId, divisionId, month, year, processTin, SCHED_EXPANDED);
	}

	/**
	 * Get the list of Quarterly Alphalist of Payees Final Dto
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param month The month
	 * @param year The year
	 * @param processTin If processed tin
	 * @return List of Quarterly Alphalist of Payees Final Dto
	 */
	public List<QuarterlyAlphaListOfPayeesDto> getQuarterlyAlphaListOfPayeesFinal(Integer companyId, Integer divisionId, Integer month, Integer year, boolean processTin) {
		return arMiscDao.getQuarterlyAlphalistOfPayees(companyId, divisionId, month, year, processTin, SCHED_FINAL);
	}

	/**
	 * Generate the DAT file of {@link QuarterlyAlphaListOfPayeesDATDto}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param month The month
	 * @param year The year
	 * @return DAT file of {@link QuarterlyAlphaListOfPayeesDATDto}
	 */
	public Path generateBIRDatFile(Integer companyId, Integer divisionId, Integer month, Integer year) {
		List<QuarterlyAlphaListOfPayeesDATDto> sched1 = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		List<QuarterlyAlphaListOfPayeesDATDto> sched2 = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		String returnPeriod = "/"+year;
		for(int i = month ; i <= month+2 ; i++) {
			sched1 = arMiscDao.getAlphalistOfPayees(companyId, divisionId, i, i, year, SCHEDULE_1);
			sched2 = arMiscDao.getAlphalistOfPayees(companyId, divisionId, i, i, year, SCHEDULE_2);
			String text = (i < 10 ? "0" : "") + i;
			QAPHeaderDto header = createHeader(sched1, sched2, text+returnPeriod , companyId, i, year);
			headers.add(header);
		}
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		return path;
	}

	private QAPHeaderDto createHeader (List<QuarterlyAlphaListOfPayeesDATDto> sched1, List<QuarterlyAlphaListOfPayeesDATDto> sched2, String returnPeriod, Integer companyId, Integer month, Integer year) {
		Company company = companyDao.get(companyId);
		String tin = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());
		double actualAmtWthld = 0;
		double incomePayment = 0;
		List<AlphalistDetail> qaps = new ArrayList<AlphalistDetail>();
		QAPDetailsD1Dto s = null;
		Integer seq = 1;
		for (QuarterlyAlphaListOfPayeesDATDto sched : sched1) {
			s = new QAPDetailsD1Dto();
			actualAmtWthld += Double.parseDouble(sched.getFirstTaxWithheld());
			incomePayment += Double.parseDouble(sched.getFirstAmount());
			s.setSeqNum(seq++);
			s.setTin(sched.getTin());
			s.setBranchCode(sched.getBranchCode());
			s.setRegisteredName(sched.getCorporateName());
			s.setLastName(sched.getLastName());
			s.setFirstName(sched.getFirstName());
			s.setMiddleName(sched.getMiddleName());
			s.setRetrnPeriod(returnPeriod);
			s.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			s.setTaxRate(sched.getFirstTaxRate());
			s.setTaxBase(sched.getFirstAmount());
			s.setActualAmtWthld(sched.getFirstTaxWithheld());
			qaps.add(s);
		}
		List<AlphalistDetail> qaps2 = new ArrayList<AlphalistDetail>();
		QAPDetailsD2Dto ss = null;
		double incomePayment2 = 0;
		seq = 1;
		for (QuarterlyAlphaListOfPayeesDATDto sched : sched2) {
			ss = new QAPDetailsD2Dto();
			incomePayment2 += Double.parseDouble(sched.getFirstAmount());
			ss.setSeqNum(seq++);
			ss.setTin(StringFormatUtil.parseBIRTIN(sched.getTin()));
			ss.setBranchCode(StringFormatUtil.parseBranchCode(sched.getTin()));
			ss.setRegisteredName(sched.getCorporateName());
			ss.setLastName(sched.getLastName());
			ss.setFirstName(sched.getFirstName());
			ss.setMiddleName(sched.getMiddleName());
			ss.setRetrnPeriod(returnPeriod);
			ss.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			ss.setTaxBase(sched.getFirstAmount());
			qaps2.add(ss);
		}
		QAPControlsC1Dto control1 = new QAPControlsC1Dto();
		control1.setTin(tin);
		control1.setBranchCode(branchCode);
		control1.setRetrnPeriod(returnPeriod);
		control1.setTaxBase(NumberFormatUtil.convertDouble(incomePayment));
		control1.setActualAmtWthld(NumberFormatUtil.convertDouble(actualAmtWthld));
		QAPControlsC2Dto control2 = new QAPControlsC2Dto();
		control2.setTin(tin);
		control2.setBranchCode(branchCode);
		control2.setRetrnPeriod(returnPeriod);
		control2.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment2));
		String filename = company.getTin()+"_"+month+year+"1601EQ";
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		AlphalistSchedule ctrl1 = AlphalistSchedule.getInstance(qaps, control1);
		AlphalistSchedule ctrl2 = AlphalistSchedule.getInstance(qaps2, control2);
		if(s != null) { controls.add(ctrl1); }
		if(ss != null) { controls.add(ctrl2); }
		return QAPHeaderDto.getInstance(tin, branchCode, company.getName(), returnPeriod, filename, controls);
	}

	/**
	 * Generate the DAT file of {@link QuarterlyAlphaListOfPayeesDto}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param month The month
	 * @param year The year
	 * @return DAT file of {@link QuarterlyAlphaListOfPayeesDto}
	 */
	public Path generateBIRDatFileFinal(Integer companyId, Integer divisionId, Integer month, Integer year) {
		List<QuarterlyAlphaListOfPayeesDATDto> sched1 = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		List<QuarterlyAlphaListOfPayeesDATDto> sched2 = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		List<QuarterlyAlphaListOfPayeesDATDto> sched3 = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		String returnPeriod = "/"+year;
		for(int i = month ; i <= month+2 ; i++) {
			sched1 = arMiscDao.getAlphalistOfPayees(companyId, divisionId, i, i, year, SCHEDULE_1_FINAL);
			sched2 = arMiscDao.getAlphalistOfPayees(companyId, divisionId, i, i, year, SCHEDULE_2_FINAL);
			sched3 = arMiscDao.getAlphalistOfPayees(companyId, divisionId, i, i, year, SCHEDULE_3_FINAL);
			String text = (i < 10 ? "0" : "") + i;
			QAPHeaderDtoFinal header = createHeaderFinal(sched1, sched2, sched3, text+returnPeriod , companyId, i, year);
			headers.add(header);
		}
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		return path;
	}

	private QAPHeaderDtoFinal createHeaderFinal (List<QuarterlyAlphaListOfPayeesDATDto> sched1, List<QuarterlyAlphaListOfPayeesDATDto> sched2, List<QuarterlyAlphaListOfPayeesDATDto> sched3,
			String returnPeriod, Integer companyId, Integer month, Integer year) {
		Company company = companyDao.get(companyId);
		String tin = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());
		double actualAmtWthld = 0;
		double incomePayment = 0;
		List<AlphalistDetail> qaps = new ArrayList<AlphalistDetail>();
		QAPDetailsD1DtoFinal s = null;
		Integer seq = 1;
		for (QuarterlyAlphaListOfPayeesDATDto sched : sched1) {
			s = new QAPDetailsD1DtoFinal();
			actualAmtWthld += Double.parseDouble(sched.getFirstTaxWithheld());
			incomePayment += Double.parseDouble(sched.getFirstAmount()) ;
			s.setTin(sched.getTin());
			s.setBranchCode(sched.getBranchCode());
			s.setRegisteredName(sched.getCorporateName());
			s.setLastName(sched.getLastName());
			s.setFirstName(sched.getFirstName());
			s.setMiddleName(sched.getMiddleName());
			s.setRetrnPeriod(returnPeriod);
			s.setSeqNum(seq++);
			s.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			s.setTaxRate(sched.getFirstTaxRate());
			s.setIncomePayment(sched.getFirstAmount());
			s.setActualAmtWthld(sched.getFirstTaxWithheld());
			qaps.add(s);
		}
		List<AlphalistDetail> qaps2 = new ArrayList<AlphalistDetail>();
		QAPDetailsD2DtoFinal ss = null;
		double fringeBenifit2 = 0;
		double monetaryValue2 = 0;
		double actualAmtWthhld2 = 0;
		seq = 1;
		for (QuarterlyAlphaListOfPayeesDATDto sched : sched2) {
			ss = new QAPDetailsD2DtoFinal();
			ss.setTin(StringFormatUtil.parseBIRTIN(sched.getTin()));
			ss.setBranchCode(StringFormatUtil.parseBranchCode(sched.getTin()));
			ss.setLastName(sched.getLastName());
			ss.setFirstName(sched.getFirstName());
			ss.setMiddleName(sched.getMiddleName());
			ss.setRetrnPeriod(returnPeriod);
			ss.setSeqNum(seq++);
			ss.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			ss.setFringeBenifit(sched.getFirstAmount());
			ss.setActualAMTWTHLD(sched.getFirstTaxWithheld());
			ss.setMonetaryValue(ss.getFringeBenifit() + ss.getActualAMTWTHLD());
			fringeBenifit2 += Double.parseDouble(sched.getFirstAmount());
			monetaryValue2 += (Double.parseDouble(ss.getFringeBenifit()) + Double.parseDouble(ss.getActualAMTWTHLD()));
			actualAmtWthhld2 += Double.parseDouble(sched.getFirstTaxWithheld()) ;
			qaps2.add(ss);
		}
		List<AlphalistDetail> qaps3 = new ArrayList<AlphalistDetail>();
		QAPDetailsD3DtoFinal sss = null;
		double incomePayment3 = 0;
		seq = 1;
		for (QuarterlyAlphaListOfPayeesDATDto sched : sched3) {
			sss = new QAPDetailsD3DtoFinal();
			incomePayment3 += Double.parseDouble(sched.getFirstAmount());
			sss.setTin(StringFormatUtil.parseBIRTIN(sched.getTin()));
			sss.setBranchCode(StringFormatUtil.parseBranchCode(sched.getTin()));
			sss.setRegisteredName(sched.getCorporateName());
			sss.setLastName(sched.getLastName());
			sss.setFirstName(sched.getFirstName());
			sss.setMiddleName(sched.getMiddleName());
			sss.setRetrnPeriod(returnPeriod);
			sss.setSeqNum(seq++);
			sss.setStatusCode("A");
			sss.setAtcCode(sched.getAtcCode().replaceAll("\\s", ""));
			sss.setIncomePayment(sched.getFirstAmount());
			qaps3.add(sss);
		}
		QAPControlsC1DtoFinal control1 = new QAPControlsC1DtoFinal();
		control1.setTin(tin);
		control1.setBranchCode(branchCode);
		control1.setRetrnPeriod(returnPeriod);
		control1.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment));
		control1.setActualAmtWthld(NumberFormatUtil.convertDouble(actualAmtWthld));
		QAPControlsC2DtoFinal control2 = new QAPControlsC2DtoFinal();
		control2.setTin(tin);
		control2.setBranchCode(branchCode);
		control2.setRetrnPeriod(returnPeriod);
		control2.setFringeBenefit(NumberFormatUtil.convertDouble(fringeBenifit2));
		control2.setMonetaryValue(NumberFormatUtil.convertDouble(monetaryValue2));
		control2.setActualAmtWthhld(NumberFormatUtil.convertDouble(actualAmtWthhld2));
		QAPControlsC3DtoFinal control3 = new QAPControlsC3DtoFinal();
		control3.setTin(tin);
		control3.setBranchCode(branchCode);
		control3.setRetrnPeriod(returnPeriod);
		control3.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment3));
		String filename = company.getTin()+"_"+month+year+"1601FQ";
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		AlphalistSchedule ctrl1 = AlphalistSchedule.getInstance(qaps, control1);
		AlphalistSchedule ctrl2 = AlphalistSchedule.getInstance(qaps2, control2);
		AlphalistSchedule ctrl3 = AlphalistSchedule.getInstance(qaps3, control3);
		if(s != null) { controls.add(ctrl1); }
		if(ss != null) { controls.add(ctrl2); }
		if(sss != null) { controls.add(ctrl3); }
		return QAPHeaderDtoFinal.getInstance(tin, branchCode, company.getName(), returnPeriod, filename, controls);
	}
}
