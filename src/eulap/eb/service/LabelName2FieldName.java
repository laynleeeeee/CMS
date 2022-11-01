package eulap.eb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eulap.eb.dao.impl.UserField;

/**
 * Enum class that holds the mapped value of labeled and field name

 *
 */
public enum LabelName2FieldName {
	NAME ("Name", UserField.firstName, UserField.lastName),
	USERNAME ("User Name", UserField.username);

	private final String uiField;
	private final UserField[] fields;
	
	private LabelName2FieldName (String uiField, UserField ...fields){
		this.uiField = uiField;
		this.fields = fields;
	}

	public String getLabelName () {
		return uiField; 
	}
	
	public List<UserField> getFields () {
		return Arrays.asList(fields);
	}
	
	public static List<String> getLabels () {
		List<String> labels = new ArrayList<String>();
		for (LabelName2FieldName ln2fn : LabelName2FieldName.values())
			labels.add(ln2fn.getLabelName());
		return labels;
	}
	
	public static LabelName2FieldName getInstanceOf (String label) { 
		for (LabelName2FieldName ln2fn : LabelName2FieldName.values())
			if (ln2fn.getLabelName().equals(label)) {
				return ln2fn;
			}
		throw new RuntimeException("label not found : " + label);
	}
}