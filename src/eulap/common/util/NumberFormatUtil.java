package eulap.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;

/**
 * Utility class that handles the double formats

 */
public class NumberFormatUtil {
	public static final int SIX_DECIMAL_PLACES = 6;
	public static final double MAX_ALLOWABLE_AMOUNT = 99999999.99;
	private static final String[] tensNames = {""," Ten"," Twenty"," Thirty"," Forty"," Fifty"," Sixty"," Seventy"," Eighty"," Ninety"};
	private static final String[] numNames = {""," One"," Two"," Three"," Four"," Five"," Six"," Seven"," Eight"," Nine"," Ten",
		" Eleven"," Twelve"," Thirteen"," Fourteen"," Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};

	/**
	 * Format the double number to #,###,##
	 * @param num The number to be formatted
	 * @return The formatted number.
	 */
	public static String format (double num) {
		return formatNumber(num, 2);
	}

	/**
	 * Format the double number to #,###,##
	 * @param number The number to be formatted
	 * @param maxFractionDigits The maximum number of decimals for the number.
	 * @return The formatted number.
	 */
	public static String formatNumber (double number, int maxFractionDigits) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(maxFractionDigits);
		return nf.format(number);
	}


	/**
	 * Function that rounds off the number to 2 decimal places.
	 * @param number The number.
	 * @return The number rounded off to 2 decimal places.
	 */
	public static double roundOffTo2DecPlaces (double number) {
		return roundOffNumber(number, 2);
	}

	/**
	 * Function that rounds off the number to the desired decimal places.
	 * @param number The number.
	 * @param numOfDecimals The number of decimals.
	 * @return The number rounded off to the desired decimal places.
	 */
	public static double roundOffNumber (Double number, int numOfDecimals) {
		BigDecimal bd = new BigDecimal(number.toString()).setScale(numOfDecimals, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Round of number to 2 decimal places by rule of five.
	 * @param num The number to be rounded off.
	 * @return The rounded off number.
	 */
	public static double roundOffRuleOfFive(double num) {
		return roundOffRuleOfFive(num, 2, false);
	}

	/**
	 * Function that rounds off the number by rule of five.
	 * Ex:
	 * Round off the numbers to the nearest hundredth place.
	 * 6.997 = 7.00
	 * 7.485 = 7.48
	 * 7.4852007 = 7.49
	 * 6.4872 = 6.49
	 * @param num The number to be rounded off.
	 * @param noOfDecimals The desired number of decimals.
	 * @return The rounded off number.
	 */
	public static double roundOffRuleOfFive(double num, int noOfDecimals, boolean isRoundUp) {
		String strNum = Double.toString(num);
		String dec = strNum.split("\\.")[1];
		if (dec.length() > 2) {
			int lastDigit = Integer.parseInt(dec.substring(dec.length() - 1));
			if (lastDigit == 5) {
				int secondLast = Integer.parseInt(dec.substring(dec.length() - 2, dec.length() - 1));
				if (secondLast % 2 == 0) {
					if (noOfDecimals == 2) {
						num = Double.parseDouble(strNum.substring(0, strNum.length() - 1));
					}
				} else if (isRoundUp && secondLast > 5) {
					double power = Math.pow(10, noOfDecimals);
					num = roundOffNumber(Math.ceil(num * power) / power, noOfDecimals);
				}
			} 
		}
		return roundOffNumber(num, noOfDecimals);
	}

	/**
	 * Register the double format to the web data binder.
	 * <br>
	 * Format : #,###.##
	 * @param binder The web data binder.
	 */
	public static void registerDoubleFormat (WebDataBinder binder) {
		NumberFormat formatter = new DecimalFormat("#,###.##");
		CustomNumberEditor doubleNumberEditor = new CustomNumberEditor(Double.class, formatter, false);
        binder.registerCustomEditor(double.class, doubleNumberEditor);
        binder.registerCustomEditor(Double.class, doubleNumberEditor);
	}
	
	/**
	 * Format the number to 10 digit value. 
	 */
	public static String formatTo10Digit (int number) {
		return formatNumber(number, 10);
	}

	/**
	 * Format the integer value. Example: 1 -> 0001
	 * @param number The number.
	 * @param minDigits The number of digits to be formatted.
	 * @return
	 */
	public static String formatNumber(int number, int minDigits) {
		NumberFormat formatter = new DecimalFormat("####");
		formatter.setMinimumIntegerDigits(minDigits);
		return formatter.format(number);
	}

	/**
	 * Standard form format 0-000-000-000
	 * @param prefix The prefix of the form. null if no prefix  
	 * @param number The number to be formatted
	 * @return Formatted number
	 */
	public static String formNumberFormat (String prefix, int number) {
		DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
		unusualSymbols.setGroupingSeparator('-');
		if (prefix == null)
			prefix = "";
		else 
			prefix = prefix+" ";
		NumberFormat formatter = new DecimalFormat(prefix+" 0000000,000", unusualSymbols);
		return formatter.format(number);
	}

	private static String convertLessThanOneThousand(int number) {
	    String soFar;

	    if (number % 100 < 20){
	      soFar = numNames[number % 100];
	      number /= 100;
	    }
	    else {
	      soFar = numNames[number % 10];
	      number /= 10;

	      soFar = tensNames[number % 10] + soFar;
	      number /= 10;
	    }
	    if (number == 0) return soFar;
	    return numNames[number] + " Hundred" + soFar;
	  }

	/**
	 * Convert the number into words.
	 * @param number The double value.
	 * @return The number in word/s
	 */
	  public static String numbersToWords(Double num) {
		  long number = num.longValue();
	    // 0 to 999 999 999 999
	    if (number == 0) { return "zero"; }

	    String snumber = Long.toString(number);

	    // pad with "0"
	    String mask = "000000000000";
	    DecimalFormat df = new DecimalFormat(mask);
	    snumber = df.format(number);

	    // XXXnnnnnnnnn 
	    int billions = Integer.parseInt(snumber.substring(0,3));
	    // nnnXXXnnnnnn
	    int millions  = Integer.parseInt(snumber.substring(3,6)); 
	    // nnnnnnXXXnnn
	    int hundredThousands = Integer.parseInt(snumber.substring(6,9)); 
	    // nnnnnnnnnXXX
	    int thousands = Integer.parseInt(snumber.substring(9,12));    

	    String tradBillions;
	    switch (billions) {
	    case 0:
	      tradBillions = "";
	      break;
	    case 1 :
	      tradBillions = convertLessThanOneThousand(billions) 
	      + " Billion ";
	      break;
	    default :
	      tradBillions = convertLessThanOneThousand(billions) 
	      + " Billion ";
	    }
	    String result =  tradBillions;

	    String tradMillions;
	    switch (millions) {
	    case 0:
	      tradMillions = "";
	      break;
	    case 1 :
	      tradMillions = convertLessThanOneThousand(millions) 
	      + " Million ";
	      break;
	    default :
	      tradMillions = convertLessThanOneThousand(millions) 
	      + " Million ";
	    }
	    result =  result + tradMillions;

	    String tradHundredThousands;
	    switch (hundredThousands) {
	    case 0:
	      tradHundredThousands = "";
	      break;
	    case 1 :
	      tradHundredThousands = "One Thousand ";
	      break;
	    default :
	      tradHundredThousands = convertLessThanOneThousand(hundredThousands) 
	      + " Thousand ";
	    }
	    result =  result + tradHundredThousands;

	    String tradThousand;
	    tradThousand = convertLessThanOneThousand(thousands);
	    result =  result + tradThousand;

	    // remove extra spaces!
	    return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	  }

	  /**
	   * Function to negate amount.
	   * @param amount The amount to be nagated.
	   * @return The negated amount.
	   */
	  public static Double negateAmount(double amount) {
		  double negativeAmount = 0.0;
		  if(amount > 0.0) {
			  negativeAmount = Math.abs(amount) * -1;
		  }
		  return negativeAmount;
	  }

	/**
	 * Convert the amount into words.
	 * <br> This function can only handled amount in 2 decimal places.
	 * @param amount The amount to be converted into words.
	 * @return The amount in word/s
	 */
	  public static String amountToWordsWithDecimals(double amount) {
		String amountStr = new BigDecimal(String.valueOf(amount)).toPlainString();
		String strDecimal = "";
		// Split the string, whole numbers and decimals.
		String decimalStr[] = amountStr.split("\\.");
		// From the array of strings, get the decimals.
		if (decimalStr.length != 1) {
			int decimalValue = Integer.parseInt(decimalStr[1]);
			// For amount with decimals.
			if(decimalValue != 0) {
				if(decimalStr[1].length() == 1){
					strDecimal = " and "+decimalStr[1]+"0/100";
				} else {
					strDecimal = " and "+decimalStr[1]+"/100";
				}
			}
		}

		return NumberFormatUtil.numbersToWords(amount)+strDecimal+" Only";
	  }

	  /**
	   * Convert the instance of the object if it is BigDecimal to Double.
	   * @param object The object to be converted.
	   * @return The converted object, if neither BigDecimal nor Double, value to be returned is zero.
	   */
	  public static double convertBigDecimalToDouble (Object object) {
			if (object instanceof BigDecimal) {
				BigDecimal dc = (BigDecimal) object;
				return dc.doubleValue();
			} else if(object instanceof Double) {
				// If object is already double, cast object to Double class.
				return (Double) object;
			}
			return 0;
		}

	/**
	 * Multiplication method to avoid floating point result
	 * This will solve the problem when multiplying double that resulted to unnecessary floating points.
	 * Ex. 45 x 50.721 = 2282.4449999999997
	 * Using this utility will result to 2282.4450.
	 * @param multiplier The multiplier value
	 * @param multiplicand The multiplicand value
	 * @return The product of two (2) number values.
	 */
	public static double multiplyWFP(double multiplier, double multiplicand) {
		BigDecimal bdF1 = BigDecimal.valueOf(multiplier);
		BigDecimal bdF2 = BigDecimal.valueOf(multiplicand);
		return bdF1.multiply(bdF2).doubleValue();
	}

	/**
	 * Division method to avoid floating point result
	 * @param dividend The dividend value
	 * @param divisor The divisor value
	 * @return The quotient of two (2) number values
	 */
	public static double divideWFP(double dividend, double divisor) {
		return divideWFP(dividend, divisor, SIX_DECIMAL_PLACES);
	}

	/**
	 * Division method to avoid floating point result
	 * @param dividend The dividend value
	 * @param divisor The divisor value
	 * @param maxDecimalPlaces The max decimal places
	 * @return The quotient of two (2) number values
	 */
	public static double divideWFP(double dividend, double divisor, int maxDecimalPlaces) {
		if (divisor == 0) {
			return 0;
		}
		BigDecimal bdF1 = BigDecimal.valueOf(dividend);
		BigDecimal bdF2 = BigDecimal.valueOf(divisor);
		return bdF1.divide(bdF2, maxDecimalPlaces, RoundingMode.HALF_UP).doubleValue();
	}


	/**
	 * Use this utility if the double value is converted in scientific notation form. Java automatically converts the decimal format to scientific notation.
	 *
	 * @param num The double converted in exponential form
	 * @return The converted double from scientific notation form.
	 */
	public static String convertDouble(double num) {
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(2);
		return df.format(num);
	}

	/**
	 * Division method to avoid floating point result
	 * @param dividend The dividend value
	 * @param divisor The divisor value
	 * @return The quotient of two (2) number values
	 */
	public static double subtractWFP(double minuend, double subtrahend) {
		BigDecimal bdF1 = BigDecimal.valueOf(minuend);
		BigDecimal bdF2 = BigDecimal.valueOf(subtrahend);
		return bdF1.subtract(bdF2).doubleValue();
	}

	/**
	 * Use this method to convert exponential double to string
	 * @param amount the exponential amount.
	 * @return The converted string.
	 */
	public static String convertDoubleToString (double amount) {
		return BigDecimal.valueOf(roundOffTo2DecPlaces(amount)).toPlainString();
	}
}
