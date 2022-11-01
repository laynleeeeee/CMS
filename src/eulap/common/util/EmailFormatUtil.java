package eulap.common.util;

/**
 * A utility class that handles the validation of email address.

 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFormatUtil {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_\u00F1\u00D1A-Za-z0-9-\\+]+(\\.[_\u00F1\u00D1A-Za-z0-9-]+)*@[\u00F1A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /*    
       ^					: start of the line
  	   [_A-Za-z0-9-\\+]+	: must start with string in the bracket [ ], must contains one or more (+)
  	   (					: start of group #1
       \\.[_A-Za-z0-9-]+	: follow by a dot "." and string in the bracket [ ], must contains one or more (+)
  	   )*					: end of group #1, this group is optional (*)
       @					: must contains a "@" symbol
       [A-Za-z0-9-]+      	: follow by string in the bracket [ ], must contains one or more (+)
       (					: start of group #2 - first level TLD checking
       \\.[A-Za-z0-9]+  	: follow by a dot "." and string in the bracket [ ], must contains one or more (+)
       )*					: end of group #2, this group is optional (*)
       (					: start of group #3 - second level TLD checking
       \\.[A-Za-z]{2,}  	: follow by a dot "." and string in the bracket [ ], with minimum length of 2
       )					: end of group #3
	   $					: end of the line
      \u00F1			    : unicode for ñ
      \u00D1 				: unicode for Ñ   */
    
    /** 
     * Validate if the email address.
     * @param str The string to be evaluated
     * @return True if valid, otherwise false.
     */
    public static boolean validate(final String str) {
    	
    	if (str.isEmpty() || str == null){
    		return true;
    	}else{
    		pattern = Pattern.compile(EMAIL_PATTERN);
    		matcher = pattern.matcher(str);
    		return matcher.matches();    		
    	}
    }
}

