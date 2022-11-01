package eulap.eb.service.workflow;

import java.util.List;

import eulap.eb.service.oo.OOProp;

/**
 * A class that defines the property of the form based on form-workflow

 *
 */
public class FormProperty {
	/**
	 * Accounting receipt transaction type.
	 */
	public static final int ACCT_RECEIPT = 1;
	/**
	 * Accounting invoice transaction type.
	 */
	public static final int ACCT_INVOICE = 2;
	/**
	 * Accounting account receivable transactions.
	 */
	public static final int ACCT_AR_TRANSACTION = 3;
	/**
	 * Accounting Paid in advance delivery transactions.
	 */
	public static final int ACCT_PIAD = 4;
	private final String name;
	private final int typeId;
	private final String form;
	private final String edit;
	private final String editConf;
	private final String serviceClass;
	private final String print;
	private final String title;
	private final OOProp ooProp;
	private final List<FormStatusProp> formStatuses;
	private final List<Integer> acctTransactionTypes;
	public FormProperty(String name, String title, int typeId, String form, String editConf, String edit, String print,
			String serviceClass, OOProp ooProp, List<FormStatusProp> formStatuses, List<Integer> acctTransactionTypes) {
		this.name = name;
		this.typeId = typeId;
		this.form = form;
		this.edit = edit;
		this.editConf = editConf;
		this.serviceClass = serviceClass;
		this.formStatuses = formStatuses;
		this.print = print;
		this.title = title;
		this.acctTransactionTypes = acctTransactionTypes;
		this.ooProp = ooProp;
	}

	protected static FormProperty getInstanceOf(String name, String title, int typeId,
			String form, String editConf, String edit, String print, String serviceClass, OOProp ooProp,List<FormStatusProp> formStatuses, List<Integer> acctTransactionTypes) {
		return new FormProperty(name, title, typeId, form, editConf, edit, print, serviceClass, ooProp,formStatuses, acctTransactionTypes);
	}

	public String getName() {
		return name;
	}

	public int getTypeId() {
		return typeId;
	}

	public String getForm() {
		return form;
	}

	/**
	 * Get the edit uri for this form.
	 * @return
	 */
	public String getEdit() {
		return edit;
	}
	
	/**
	 * Get the edit configuration name. 
	 */
	public String getEditConf() {
		return editConf;
	}

	public String getPrint() {
		return print;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public List<FormStatusProp> getFormStatuses() {
		return formStatuses;
	}
	
	public String getTitle() {
		return title;
	}

	public List<Integer> getAcctTransactionTypes() {
		return acctTransactionTypes;
	}

	public OOProp getOoProp() {
		return ooProp;
	}

	@Override
	public String toString() {
		return "FormProperty [name=" + name + ", typeId=" + typeId + ", form="
				+ form + ", edit=" + edit + ", serviceClass=" + serviceClass
				+ ", print=" + print + ", title=" + title + ", formStatuses="
				+ formStatuses + ",acctTransactionTypes="+acctTransactionTypes+"]";
	}
}
