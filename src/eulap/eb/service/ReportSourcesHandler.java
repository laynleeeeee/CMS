package eulap.eb.service;

import java.util.List;

import eulap.eb.web.dto.PaymentStatus;


/**
 * Interface that handle report resources.

 *
 */
public interface ReportSourcesHandler {

	/**
	 * Get the list of Journal Entries Register sources.
	 */
	public List<String> getAllJESources();

	/**
	 * Get the list of AR Line Analysis sources.
	 */
	public List<PaymentStatus> getAllARLineAnalysisSources();

	/**
	 * Get the list of AR Receipt Register sources.
	 */
	public List<String> getAllArReceiptRegisterSources();

	/**
	 * AR Receipt Register source id.
	 */
	public int getArReceiptRegisterSourceId(String source);

}
