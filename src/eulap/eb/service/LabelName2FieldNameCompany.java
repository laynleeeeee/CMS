package eulap.eb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eulap.eb.dao.impl.CompanyField;

/**
 * Enum class that holds the mapped value of labeled and field name

 *
 */
public enum LabelName2FieldNameCompany {
	NAME ("Name", CompanyField.name),
	EMAIL_ADDRESS ("Email Address", CompanyField.emailAddress);

	private final String uiField;
	private final CompanyField[] fields;
	
	private LabelName2FieldNameCompany (String uiField, CompanyField ...fields){
		this.uiField = uiField;
		this.fields = fields;
	}

	public String getLabelName () {
		return uiField; 
	}
	
	public List<CompanyField> getFields () {
		return Arrays.asList(fields);
	}
	
	public static List<String> getLabels () {
		List<String> labels = new ArrayList<String>();
		for (LabelName2FieldNameCompany ln2fnC : LabelName2FieldNameCompany.values())
			labels.add(ln2fnC.getLabelName());
		return labels;	
	}
	
	public static LabelName2FieldNameCompany getInstanceOf (String label) { 
		for (LabelName2FieldNameCompany ln2fnC : LabelName2FieldNameCompany.values())
			if (ln2fnC.getLabelName().equals(label)) {
				return ln2fnC;
			}
		throw new RuntimeException("label not found : " + label);
	}
}
