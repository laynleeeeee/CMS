package eulap.eb.service.jr;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;

/**
 * Date source of that will handle the streaming behaviour in generating Jasper Reports. 

 *
 */
public class EBDataSource<T> implements JRDataSource{
	private final EBJRServiceHandler<T> serviceHandler;
	private Page<T> currentPage;
	private List<T> data;
	private int indexInPage;
	private T currentData;
	private PageSetting pageSetting;
	public EBDataSource (EBJRServiceHandler<T> serviceHandler) {
		this.serviceHandler = serviceHandler;
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		
		String fieldName = jrField.getName();
		return FieldValueGetter.getValue(currentData, fieldName);
	}

	@Override
	public boolean next() throws JRException {
		if (currentPage == null) {
			pageSetting = new PageSetting(1, PageSetting.MAX_RECORD_100); 
			currentPage = serviceHandler.nextPage(pageSetting);
			data = new ArrayList<T>(currentPage.getData());
		} else {
			// index is zero base
			// For cases when data size is greater than page setting max result.
			if (data.size() == (indexInPage)) {
				// If the current page is last page return false.
				if (!((currentPage.getCurrentPage() + 1) <= currentPage.getLastPage())) {
					close();
					return false;
				}
				pageSetting = new PageSetting(currentPage.getNextPage(), PageSetting.MAX_RECORD_100);
				currentPage = serviceHandler.nextPage(pageSetting);
				indexInPage = 0;
				data = new ArrayList<T>(currentPage.getData());
			} else if (data.size() == (indexInPage)){
				close();
				return false;
			}
		}
		
		if(data.size() != 0) {
			currentData = data.get (indexInPage++);
			return true;
		} else {
			close();
			return false;
		}
	}
	
	private void close () {
		try {
			serviceHandler.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class FieldValueGetter {

		private static Object getValue(Object target, String property) {
			int index = property.indexOf(".");
			if (index != -1) {
				target = getFieldValue(target, property.substring(0, index));
				return target != null ? getValue(target,
						property.substring(index + 1)) : null;
			} else {
				return target != null ? getFieldValue(target, property) : null;
			}
		}

		private static Object getFieldValue(Object target, String property) {
			try {
				Object[] obj = null;
				Class<?> classes[] = {};
				Class<?> clz = target.getClass();
				property = convertToGetter(property);
				Method method = clz.getMethod(property, classes);
				return method.invoke(target, obj);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		private static String convertToGetter(String property) {
			StringBuffer buf = new StringBuffer();
			buf.append("get");
			buf.append(property.substring(0, 1).toUpperCase());
			buf.append(property.substring(1));
			return buf.toString();
		}
	}
}
