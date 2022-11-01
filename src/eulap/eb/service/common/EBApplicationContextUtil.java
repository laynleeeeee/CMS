package eulap.eb.service.common;

import java.io.InvalidClassException;
import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * Elasticbooks application context utility class.

 *
 */
public class EBApplicationContextUtil {

	/**
	 * Get the instance of the bean from application context.
	 * @param applicationContext The current application Context.
	 * @param className The class name
	 * @return The bean instance.
	 */
	public static Object getInstanceof (ApplicationContext applicationContext, String className) throws InvalidClassException, ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		Map<String, ?> clazz2Obj = applicationContext.getBeansOfType(clazz);
		String key = clazz.getSimpleName();
		Object object = null;
		for (String objKey : clazz2Obj.keySet()) {
			if (objKey.equalsIgnoreCase(key)) {
				object = clazz2Obj.get(objKey);
				break;
			}
		}

		if (object == null) {
			throw new InvalidClassException("Unknown class : " + className);
		}
		return object;
	}
}
