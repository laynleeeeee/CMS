package eulap.eb.service.report;
import java.nio.file.Path;
import java.text.DecimalFormat;
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
import eulap.eb.web.dto.AnnualAlphaWTESchedFourDetaisDto;
import eulap.eb.web.dto.AnnualAlphaWTESchedThreeDetailsDto;
import eulap.eb.web.dto.AnnualAlphalistWTEHeaderDto;
import eulap.eb.web.dto.AnnualAlphalsitWTEDetailsDto;
import eulap.eb.web.dto.bir.AnnualAlphaWTESchedFourCtrlDto;
import eulap.eb.web.dto.bir.AnnualAlphaWTESchedThreeCtrlDto;

/**
 * Service class that will handle business logic for annual alphalist WT expanded report

 */

@Service
public class AnnualAlphalistWTExpandedService {
	private static Logger logger = Logger.getLogger(AnnualAlphalistWTExpandedService.class);
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private APInvoiceDao apinvoicedao;

	private static final String SCHEDULE_THREE_ID = "1";
	private static final String SCHEDULE_FOUR_ID = "2";
	private static final String SCHED_TYPE_ID = "1,2";
	private static final String pattern = "###.##";
	private static final DecimalFormat df = new DecimalFormat(pattern);

	/**
	 * Get the annual alphalist of withholding taxes expanded
	 * @param companyId The company Id
	 * @param divisionId The division Id
	 * @param year The selected year
	 * @param SCHED_TYPE_ID The schedule type
	 * @return The annual alphalist of withholding taxes expanded
	 */

	public List<AnnualAlphalsitWTEDetailsDto> generateAnnualALphalistWTExpanded(int companyId, Integer divisionId, Integer year ){
		return apinvoicedao.getAnnualAlphalistWTExpanded(companyId, divisionId, 1, 12, year, SCHED_TYPE_ID, true);
	}

	/**
	 * Generate BIR DAT file
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param year The year
	 * @param retrnPeriod The period
	 * @return The DAT file generated
	 */
	public Path generateBIRDatFile(Integer companyId, Integer divisionId, Integer year, String retrnPeriod) {
		AnnualAlphalistWTEHeaderDto header = createHeader(companyId, divisionId, year, retrnPeriod);
		List<AlphalistHeader> headers = new ArrayList<AlphalistHeader>();
		headers.add(header);
		AlphalistWriter writer = new AlphalistWriter(headers);
		Path path = writer.write2Zip();
		logger.info("successfully created : " + path);
		return path;
	}

	private AnnualAlphalistWTEHeaderDto createHeader (int companyId, int divisionId, int year, String retrnPeriod) {
		Company company = companyDao.get(companyId);
		String tIN = StringFormatUtil.parseBIRTIN(company.getTin());
		String branchCode = StringFormatUtil.parseBranchCode(company.getTin());

		//Withholding Tax schedule 3
		List <AnnualAlphalsitWTEDetailsDto> sched3Data = apinvoicedao.getAnnualAlphalistWTExpanded(companyId, divisionId,1, 12,  year, SCHEDULE_THREE_ID, false);
		List<AnnualAlphaWTESchedThreeDetailsDto> sched3Details = new ArrayList<AnnualAlphaWTESchedThreeDetailsDto>();
		// Alphalist Detials Schedule 3
		AnnualAlphaWTESchedThreeDetailsDto sched3Detail = null;
		Double totalWtAmount = 0.00;
		int sequenceNum = 0;
		if(sched3Data != null && !sched3Data.isEmpty())
		{
			for(AnnualAlphalsitWTEDetailsDto detail3 : sched3Data) {
			sequenceNum++;
			sched3Detail = new AnnualAlphaWTESchedThreeDetailsDto();
			sched3Detail.setEmployersTin(tIN);
			sched3Detail.setEmployerBranchCode(branchCode);
			sched3Detail.setReturnPeriod(retrnPeriod);
			sched3Detail.setSequenceNumber(sequenceNum);
			sched3Detail.setTin(StringFormatUtil.parseBIRTIN(detail3.getTin()));
			sched3Detail.setBranchCode(StringFormatUtil.parseBranchCode(detail3.getTin()));
			sched3Detail.setRegistName(detail3.getRegistName());
			sched3Detail.setLastName(detail3.getLastName());
			sched3Detail.setFirstName(detail3.getFirstName());
			sched3Detail.setMiddleName(detail3.getMiddleName());
			sched3Detail.setAtcCode(detail3.getAtcCode().replaceAll(" ", ""));
			sched3Detail.setTaxRate(detail3.getTaxRate());
			sched3Detail.setIncomePayment(NumberFormatUtil.convertDouble(detail3.getIncomePayment()));
			totalWtAmount += Double.valueOf(detail3.getAmountTaxheld());
			sched3Detail.setAmountTaxheld(NumberFormatUtil.convertDouble(detail3.getAmountTaxheld()));
			sched3Details.add(sched3Detail);
			}
		}

		//Exempt from with holding Schedule 4
		List <AnnualAlphalsitWTEDetailsDto> sched4Data = apinvoicedao.getAnnualAlphalistWTExpanded(companyId, divisionId, 1, 12, year, SCHEDULE_FOUR_ID, false);
		List<AnnualAlphaWTESchedFourDetaisDto> sched4Details = new ArrayList<AnnualAlphaWTESchedFourDetaisDto>();
		// Alphalist Detials Schedule 4
		AnnualAlphaWTESchedFourDetaisDto sched4Detail = null;
		Double incomePayment = 0.00;
		sequenceNum = 0;
		if(sched4Data != null && !sched4Data.isEmpty()) {
			for(AnnualAlphalsitWTEDetailsDto detail4 : sched4Data) {
				sequenceNum++;
				sched4Detail = new AnnualAlphaWTESchedFourDetaisDto();
				sched4Detail.setEmployersTin(tIN);
				sched4Detail.setEmployerBranchCode(branchCode);
				sched4Detail.setReturnPeriod(retrnPeriod);
				sched4Detail.setSequenceNumber(sequenceNum);
				sched4Detail.setTin(StringFormatUtil.parseBIRTIN(detail4.getTin()));
				sched4Detail.setBranchCode(StringFormatUtil.parseBranchCode(detail4.getTin()));
				sched4Detail.setRegistName(detail4.getRegistName());
				sched4Detail.setLastName(detail4.getLastName());
				sched4Detail.setFirstName(detail4.getFirstName());
				sched4Detail.setMiddleName(detail4.getMiddleName());
				sched4Detail.setAtcCode(detail4.getAtcCode().replaceAll(" ", ""));
				incomePayment += Double.valueOf(detail4.getIncomePayment());
				sched4Detail.setIncomePayment(df.format(detail4.getIncomePayment()));
				sched4Details.add(sched4Detail);
			}
		}
		// Alphalist Control Schedule 3
		List<AlphalistSchedule> controls = new ArrayList<AlphalistSchedule>();
		if(sched3Details != null && !sched3Details.isEmpty()) {
			AnnualAlphaWTESchedThreeCtrlDto ctrlSchedThree = new AnnualAlphaWTESchedThreeCtrlDto();
			ctrlSchedThree.setAgentTin(tIN);
			ctrlSchedThree.setBranchCode(branchCode);
			ctrlSchedThree.setReturnPeriod(retrnPeriod);
			ctrlSchedThree.setTotalAmountWh(NumberFormatUtil.convertDouble(totalWtAmount));
			controls.add(AlphalistSchedule.getInstance(new ArrayList<AlphalistDetail>(sched3Details), ctrlSchedThree));
		}
		// Alphalist Control Schedule 4
		if(sched4Details != null && !sched4Details.isEmpty()) {
			AnnualAlphaWTESchedFourCtrlDto ctrlSchedFour = new AnnualAlphaWTESchedFourCtrlDto();
			ctrlSchedFour.setAgentTin(tIN);
			ctrlSchedFour.setBranchCode(branchCode);
			ctrlSchedFour.setReturnPeriod(retrnPeriod);
			ctrlSchedFour.setIncomePayment(NumberFormatUtil.convertDouble(incomePayment));
			controls.add(AlphalistSchedule.getInstance(new ArrayList<AlphalistDetail>(sched4Details), ctrlSchedFour));
		}
		String fileName = tIN+branchCode+1231+year+"1604E";//DAT File Name //retrnPeriod
		return AnnualAlphalistWTEHeaderDto.getInstance(tIN, branchCode, retrnPeriod, fileName, controls);
	}
}