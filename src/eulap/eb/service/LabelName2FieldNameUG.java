package eulap.eb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eulap.eb.dao.impl.UserGroupField;

/**
 * Enum class that holds the mapped value of labeled and field name

 *
 */
public enum LabelName2FieldNameUG {
	NAME ("Name", UserGroupField.name),
	DESCRIPTION ("Description", UserGroupField.description);

	private final String uiField;
	private final UserGroupField[] fields;
	
	private LabelName2FieldNameUG (String uiField, UserGroupField ...fields){
		this.uiField = uiField;
		this.fields = fields;
	}

	public String getLabelName () {
		return uiField; 
	}
	
	public List<UserGroupField> getFields () {
		return Arrays.asList(fields);
	}
	
	public static List<String> getLabels () {
		List<String> labels = new ArrayList<String>();
		for (LabelName2FieldNameUG ln2fnC : LabelName2FieldNameUG.values())
			labels.add(ln2fnC.getLabelName());
		return labels;	
	}
	
	public static LabelName2FieldNameUG getInstanceOf (String label) { 
		for (LabelName2FieldNameUG ln2fnC : LabelName2FieldNameUG.values())
			if (ln2fnC.getLabelName().equals(label)) {
				return ln2fnC;
			}
		throw new RuntimeException("label not found : " + label);
	}
}
