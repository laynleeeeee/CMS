package eulap.eb.service.report;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.BirAtcDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.BirAtc;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.bir.AlphalistDetail;
import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;
import eulap.eb.service.bir.AlphalistWriter;
import eulap.eb.web.dto.SAWTControlsDto;
import eulap.eb.web.dto.SAWTDto;
import eulap.eb.web.dto.SAWTHeaderDto;
import eulap.eb.web.dto.SAWTPayeesDto;

/**
 * Alpha numeric tax code report service.

 *
 */
@Service
public class SAWTReportService {
	@Autowired
	private BirAtcDao birAlphaNumericTaxCodeDao;
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private CompanyDao companyDao;

	/**
	 * Get the BIR alpha numeric name
	 * @return The summary alphalist of witholding taxes BIR ATC
	 */
	public List<BirAtc> getAlphaNumericTaxCodes() {
		return birAlphaNumericTaxCodeDao.getAllActive();
	}

	/**
	 * Get the Summary Alphalist of Withholding Taxes
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param birAtcId The bir atc id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param year The year
	 * @return List of Summary Alphalist of Withholding Taxes
	 * @throws ParseException
	 */
	public List<SAWTDto> getSAWT(Integer companyId, Integer divisionId, Integer birAtcId, Integer monthFrom, Integer monthTo, Integer year, boolean processTin) {
		return arInvoiceDao.getSAWT(companyId, divisionId, birAtcId, monthFrom, monthTo, year, processTin);
	}

	/**
	 * Generate dat file based on SAWT result
	 * @param divisionId The division id
	 * @param birAtcId The bir atc id
	 * @param monthFrom The month from
	 * @param monthTo The month to
	 * @param year The year
	 * @param processTin If process tin
	 * @param companyId The company id
	 * @param birFormType The BIR Form Type
	 * @return Path
	 */
	public Path generateBIRDatFile(Integer divisionId, Integer birAtcId, Integer monthFrom, Integer monthTo, Integer year,
			boolean processTin, Integer companyId, String birFormType) {
		List<SAWTDto> dtos = null;
		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		String returnPeriod = "/"+year;
		for(int i = monthFrom ; i <= monthTo ; i++) {
			dtos = arInvoiceDao.getSAWT(companyId, divisionId, birAtcId, i, i, year, processTin);
			String month = (i < 10 ? "0" : "") + i;
			SAWTHeaderDto header = createHeader(dtos, month+returnPeriod , companyId, i, year, birFormType);
			headers.add(header);
		}
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		return path;
	}

	private SAWTHeaderDto createHeader (List<SAWTDto> sawts, String returnPeriod, Integer companyId, Integer month, Integer year, String birFormType) {
		Company company = companyDao.get(companyId);
		String tin = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());
		double actualAmtWthld = 0;
		double incomePayment = 0;
		List<AlphalistDetail> sawtps = new ArrayList<AlphalistDetail>();
		SAWTPayeesDto s = null;
		Integer seq = 1;
		for (SAWTDto sawt : sawts) {
			s = new SAWTPayeesDto();
			actualAmtWthld += Double.parseDouble(sawt.getStrTaxWitheld());
			incomePayment += Double.parseDouble(sawt.getStrAmount());
			s.setfTypeCode("D"+birFormType);//D;Detail
			s.setTin(StringFormatUtil.parseBIRTIN(sawt.getTin()));
			s.setBranchCode(StringFormatUtil.parseBranchCode(sawt.getTin()));
			s.setSeqNum(seq++);
			s.setRegisteredName(sawt.getCorporateName().replaceAll("[.,]", "").replace("&", "AND").replace("'",""));
			s.setLastName(sawt.getLastName().replaceAll("[.,]", ""));
			s.setFirstName(sawt.getFirstName().replaceAll("[.,]", ""));
			s.setMiddleName(sawt.getMiddleName().replaceAll("[.,]", ""));
			s.setRetrnPeriod(returnPeriod);
			s.setNatureOfIncomePayment("");
			s.setAtcCode(sawt.getBirAtcCode().replaceAll("\\s", ""));
			s.setStrTaxRate(sawt.getStrTaxRate());
			s.setStrTaxBase(sawt.getStrAmount());
			s.setStrActualAmtWthld(sawt.getStrTaxWitheld());
			sawtps.add(s);
		}
		SAWTControlsDto control = new SAWTControlsDto();
		control.setfTypeCode("C"+birFormType);//C;Control
		control.setTin(tin);
		control.setBranchCode(branchCode);
		control.setRetrnPeriod(returnPeriod);
		control.setStrTaxBase(NumberFormatUtil.convertDoubleToString(incomePayment));
		control.setStrActualAmtWthld(NumberFormatUtil.convertDoubleToString(actualAmtWthld));
		String filename = company.getTin()+"_"+month+year+birFormType;
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		AlphalistSchedule ctrl = AlphalistSchedule.getInstance(sawtps, control);
		controls.add(ctrl);
		return SAWTHeaderDto.getInstance("H"+birFormType, tin, branchCode, company.getName(), "", "", "", returnPeriod, filename, controls);//H;Header
	}
}
