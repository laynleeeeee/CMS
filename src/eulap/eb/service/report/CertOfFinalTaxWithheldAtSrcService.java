package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.BirAtcDao;
import eulap.eb.domain.hibernate.BirAtc;
import eulap.eb.web.dto.CFTWSDto;
import eulap.eb.web.dto.CertFinalTaxWithheldMonthlyDto;
import eulap.eb.web.dto.CertificateOfFinalTaxWithheldAtSourceDto;
import eulap.eb.web.dto.TimePeriodMonth;

/**
 * Service for Certificate of final tax withheld at source

 */

@Service
public class CertOfFinalTaxWithheldAtSrcService {
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private BirAtcDao birAtcDao;

	/**
	 * Get the Certificate Final tax Withheld
	 * @param companyId The company Id
	 * @param divisionId The division Id
	 * @param year The selected Year
	 * @param monthFrom The Selected month from
	 * @param monthTo The Selected month to
	 * @param birAtcId The ATC Code
	 * @return The petty cash replenishment register report data
	 */
	public CertificateOfFinalTaxWithheldAtSourceDto getCertFinalTax(int companyId, int divisionId, int year, int fromMonth, int toMonth, int birAtcId) {
		CertificateOfFinalTaxWithheldAtSourceDto sourceDto = new CertificateOfFinalTaxWithheldAtSourceDto();
		List<CertFinalTaxWithheldMonthlyDto> cftmDto = arInvoiceDao.getFinalTaxWithheldMonthly(companyId, divisionId, year,
				fromMonth, toMonth, birAtcId, new PageSetting(PageSetting.START_PAGE));
		List<CFTWSDto> cftsDtos = setCFTWSDto(cftmDto);
		List<TimePeriodMonth> months = TimePeriodMonth.getMonths();

		if (cftsDtos != null && !cftsDtos.isEmpty()) {
			List<CFTWSDto> emptyCFTWS = new ArrayList<>();
			for (TimePeriodMonth month : months) {
				if (!isExisting(cftsDtos, month.getMonth())) {
					emptyCFTWS.add(createEmpty(month));
				}
			}
			cftsDtos.addAll(emptyCFTWS);
			// Sort the list order by month
			Collections.sort(cftsDtos, new Comparator<CFTWSDto>() {
				@Override
				public int compare(CFTWSDto o1, CFTWSDto o2) {
					return o1.getMonthId() - o2.getMonthId();
				}
			});
		}
		sourceDto.setCftwsMonthly(cftmDto);
		sourceDto.setCftwsSummary(cftsDtos);
		return sourceDto;
	}

	private List<CFTWSDto> setCFTWSDto(List<CertFinalTaxWithheldMonthlyDto> cftmDto) {
		List<CFTWSDto> cftsDtos = new ArrayList<CFTWSDto>();
		CFTWSDto cftsDto = null;
		if(cftmDto != null && !cftmDto.isEmpty()) {
			for(CertFinalTaxWithheldMonthlyDto cft : cftmDto) {
				cftsDto = new CFTWSDto();
				cftsDto.setMonthId(DateUtil.getMonth(cft.getDate())+1);
				cftsDto.setMonth(cft.getMonth());
				cftsDto.setAddFinalTaxWithheld(cft.getAddFinalTaxWithheld());
				cftsDtos.add(cftsDto);
			}
		}
		Map<Integer, CFTWSDto> CFTSHM = new HashMap<Integer, CFTWSDto>();
		Integer key = null;
		CFTWSDto cft = null;
		for(CFTWSDto cfts : cftsDtos) {
			key = cfts.getMonthId();
			cft = new CFTWSDto();;
			if(CFTSHM.containsKey(key)) {
				cft = CFTSHM.get(key);
				cft.setAddFinalTaxWithheld(cft.getAddFinalTaxWithheld() + cfts.getAddFinalTaxWithheld());
				CFTSHM.put(key, cft);
			} else {
				CFTSHM.put(key, cfts);
			}
		}
		return new ArrayList<CFTWSDto>(CFTSHM.values());
	}

	private CFTWSDto createEmpty (TimePeriodMonth month) {
		CFTWSDto emptyCFTW = new CFTWSDto();
		emptyCFTW.setMonthId(month.getMonth());
		emptyCFTW.setMonth(month.getName());
		emptyCFTW.setAddFinalTaxWithheld(0.0);
		return emptyCFTW;
	}

	private boolean isExisting(List<CFTWSDto> cftsDtos, int month) {
		for (CFTWSDto cftsDto : cftsDtos) {
			if (cftsDto.getMonthId() == month) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all BIR alphanumeric tax codes
	 * @return
	 */
	public List<BirAtc> getAlphaNumericTaxCodes() {
		return birAtcDao.getAllActive();
	}

	/**
	 * Get The {@link BirAtc} by wt type id
	 * @param wtTypeId The wt type id
	 * @return The list of {@link BirAtc}
	 */
	public List<BirAtc> getListOfBirAtcByWtType(Integer[] wtTypeId){
		return birAtcDao.getListOfBirAtcByWtType(wtTypeId);
	}
}
