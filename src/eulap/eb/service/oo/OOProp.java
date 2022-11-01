package eulap.eb.service.oo;

/**
 * Object to object property configuration. 

 *
 */
public class OOProp {
	private final int parentToChild;
	private final int childToChild;
	
	private OOProp (int parentToChild, int childToChild) {
		this.parentToChild = parentToChild;
		this.childToChild = childToChild;
	}

	public static OOProp getInstanceOf(int parentToChild, int childToChild) {
		return new OOProp (parentToChild, childToChild);
	}

	public int getParentToChild() {
		return parentToChild;
	}

	public int getChildToChild() {
		return childToChild;
	}

	@Override
	public String toString() {
		return "OOProp [parentToChild=" + parentToChild + ", childToChild="
				+ childToChild + "]";
	}
}
