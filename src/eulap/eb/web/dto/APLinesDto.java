package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.APLine;

/**
 * Data transfer object class for {@code APLine}

 */

public class APLinesDto {
	private List<APLine> apLines;
	private List<APLine> summarizedApLines;

	public List<APLine> getApLines() {
		return apLines;
	}
	public void setApLines(List<APLine> apLines) {
		this.apLines = apLines;
	}

	public List<APLine> getSummarizedApLines() {
		return summarizedApLines;
	}
	public void setSummarizedApLines(List<APLine> summarizedApLines) {
		this.summarizedApLines = summarizedApLines;
	}
}
