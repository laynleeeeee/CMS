package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.CheckbookTemplateDao;
import eulap.eb.domain.hibernate.CheckbookTemplate;
import eulap.eb.web.dto.CheckPrintingDto;

/**
 * Handles the business logic of {@link CheckbookTemplate}

 */
@Service
public class CheckbookTemplateService {
	@Autowired
	private CheckbookTemplateDao checkbookTemplateDao;

	/**
	 * Set the check details.
	 * @param maxCharAmountInWords The maximum characters amount in word.
	 * @return The check printing dto.
	 */
	public CheckPrintingDto setCheckDetails(String name, Double amount, Date date,
			Integer maxCharAmountInWords, Integer maxCharName){
		return processCheckDetails(name, amount, date, maxCharAmountInWords, maxCharName, false);
	}

	private CheckPrintingDto processCheckDetails(String name, Double amount, Date date,
			Integer maxCharAmountInWords, Integer maxCharName, boolean removePesos) {
		//Amount in words
		amount = NumberFormatUtil.roundOffTo2DecPlaces(amount);
		String amountInWords = NumberFormatUtil.amountToWordsWithDecimals(amount);
		if(removePesos) {
			amountInWords = amountInWords.replace("Pesos", "");
		}
		String firstLine = "";
		String secondLine = "";
		List<String> amtInWords = setMaxCharToSecondLine(amountInWords, maxCharAmountInWords);
		for (String strAmtInWords : amtInWords) {
			if(firstLine == ""){
				firstLine = strAmtInWords;
			} else {
				secondLine = strAmtInWords;
			}
		}

		//Check name
		String nameFirstLine = "";
		String nameSecondLine = "";
		List<String> strings = setMaxCharToSecondLine(name, maxCharName);
		for (String strName : strings) {
			if(nameFirstLine == ""){
				nameFirstLine = strName;
			} else {
				nameSecondLine = strName;
			}
		}
		return CheckPrintingDto.getInstance(nameFirstLine, firstLine, secondLine, amount, date, nameSecondLine);
	}

	/**
	 * Set the check details.
	 * @param maxCharAmountInWords The maximum characters amount in word.
	 * @return The check printing dto.
	 */
	public CheckPrintingDto setCheckDetails(String name, Double amount, Date date,
			Integer maxCharAmountInWords, Integer maxCharName, boolean removePesos){
		return processCheckDetails(name, amount, date, maxCharAmountInWords, maxCharName, removePesos);
	}

	/**
	 * Get all checkbooks template.
	 */
	public Collection<CheckbookTemplate> getAllCheckTemplate() {
		return checkbookTemplateDao.getCheckbookTemplates();
	}

	/**
	 * Get the checkbook template by checkbookTemplateId
	 * @param checkbookTemplateId
	 * @return The checkbook template.
	 */
	public CheckbookTemplate getCheckTemplate(Integer checkbookTemplateId) {
		return checkbookTemplateDao.get(checkbookTemplateId);
	}

	/**
	 * Set strings to second line if the maximum number characters was reached
	 * @param str The string value
	 * @param maxChar The maximum character limit
	 * @return Returns two strings, the first line string and the second line string
	 */
	private List<String> setMaxCharToSecondLine(String str, int maxChar) {
		String firstLine = "";
		String secondLine = "";
		List<String> strings = new ArrayList<String>();
		if((str.length()-1) > maxChar) {
			String strArray[] = str.split(" ");
			str =  str.substring(0, maxChar);
			String firstLineArray[] = str.split(" ");
			int firstWordLastWordIndex = firstLineArray.length - 1;
			if(firstLineArray[firstWordLastWordIndex].trim() != strArray[firstWordLastWordIndex].trim()){
				str = "";
				int index = 0;
				for (String strValue : strArray) {
					if(firstWordLastWordIndex > index){
						firstLine += strValue + " ";
					} else {
						secondLine += " " + strValue;
					}
					index++;
				}
			}
		} else {
			firstLine = str;
		}
		strings.add(firstLine);
		if(!secondLine.isEmpty()){
			strings.add(secondLine);
		}
		return strings;
	}
}