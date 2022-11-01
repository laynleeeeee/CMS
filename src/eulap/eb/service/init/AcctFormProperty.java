package eulap.eb.service.init;

import eulap.eb.service.workflow.FormProperty;

/**
 * Accounting form property. 

 *
 */
public class AcctFormProperty {
	private final FormProperty formProperty;
	private final String jeSql;
	private final String jeAbSql;
	private final String jeAaSql;
	private final String jeJjSql;

	public AcctFormProperty(FormProperty formProperty, String jeSql,
			String jeAbSql, String jeAaSql, String jeJjSql) {
		this.formProperty = formProperty;
		this.jeSql = jeSql;
		this.jeAbSql = jeAaSql;
		this.jeAaSql = jeAaSql;
		this.jeJjSql = jeJjSql;
	}

	public FormProperty getFormProperty() {
		return formProperty;
	}

	public String getJeAaSql() {
		return jeAaSql;
	}

	public String getJeAbSql() {
		return jeAbSql;
	}

	public String getJeJjSql() {
		return jeJjSql;
	}

	public String getJeSql() {
		return jeSql;
	}

	@Override
	public String toString() {
		return "AcctFormProperty [formProperty=" + formProperty + ", jeSql="
				+ jeSql + ", jeAbSql=" + jeAbSql + ", jeAaSql=" + jeAaSql
				+ ", jeJjSql=" + jeJjSql + "]";
	}
}
