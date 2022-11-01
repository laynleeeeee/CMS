package eulap.eb.service.oo.processing;

import java.util.List;

import eulap.common.domain.Domain;

/**
 * Individual Selection Item trail. Provide links of the domain to its source object. 



 *
 */
public class ISItemTrail {
	private final Domain header;
	private final List<ISItemTrail> sources;
	private final List<Domain> children;
	private final boolean isEntryPoint;

	private ISItemTrail (Domain header, List<ISItemTrail> source, List<Domain> children, boolean isEntryPoint) {
		this.header = header;
		this.sources = source;
		this.children = children;
		this.isEntryPoint = isEntryPoint;
	}

	protected static ISItemTrail getInstance (Domain header, List<ISItemTrail> source, List<Domain> children, boolean isEntryPoint) {
		return new ISItemTrail(header, source, children, isEntryPoint);
	}

	public Domain getHeader() {
		return header;
	}

	public List<Domain> getChildren() {
		return children;
	}

	public List<ISItemTrail> getSources() {
		return sources;
	}

	public boolean isEntryPoint() {
		return isEntryPoint;
	}

	@Override
	public String toString() {
		return "ISItemTrail [header=" + header + ", sources=" + sources + ", children=" + children + ", isEntryPoint="
				+ isEntryPoint + "]";
	}
}
