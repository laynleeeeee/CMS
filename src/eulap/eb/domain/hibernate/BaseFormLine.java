package eulap.eb.domain.hibernate;

import eulap.eb.service.oo.OOChild;

/**
 * Interface that defines the base form child. 

 *
 */
public abstract class BaseFormLine extends OOBaseDomain implements OOChild{

	@Override
	public Integer getRefenceObjectId() {
		throw new RuntimeException("sub class must implement this method");
	}	
}
