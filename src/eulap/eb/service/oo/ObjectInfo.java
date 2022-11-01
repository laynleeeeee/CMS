package eulap.eb.service.oo;

/**
 * The object information. 

 *
 */
public class ObjectInfo {

	private final int objectId;
	private final String title;
	private final String latestStatus;
	private final String shortDescription;
	private final String fullDescription;
	private final String popupLink;
	private final String printOutLink;

	private ObjectInfo (int objectId, String title, String latestStatus, String shortDescription,
			String fullDescription, String popupLink, String printOutLink) {
		this.objectId = objectId;
		this.title = title;
		this.latestStatus = latestStatus;
		this.shortDescription = shortDescription;
		this.fullDescription = fullDescription;
		this.popupLink = popupLink;
		this.printOutLink = printOutLink;
	}

	public static ObjectInfo getInstance (int objectId, String title, String latestStatus, String shortDescription,
			String fullDescription, String popupLink, String printOutLink) {
		return new ObjectInfo(objectId, title, latestStatus, shortDescription, fullDescription, popupLink, printOutLink);
	}

	/**
	 * The object id. 
	 * @return
	 */
	public int getObjectId () {
		return objectId;
	}
	/**
	 * Get the title of the object. 
	 * @return The label or title of this object. 
	 */
	public String getTitle () {
		return title;
	}

	/**
	 * Get the latest status of this object.
	 * This will show the work-flow status of the object for FORM based object. 
	 */
	public String getLatestStatus() {
		return latestStatus;
	}
	
	/**
	 * Get the short description of this object. 
	 * @return the short description of this object.
	 */
	public String getShortDescription () {
		return shortDescription;
	}
	/**
	 * Get the full description of this object. 
	 * @return the full description of this object. 
	 */
	public String getFullDescription () {
		return fullDescription;
	}

	/**
	 * Get the pop-up link of this object. 
	 * @return the pop-up link. 
	 */
	public String getPopupLink () {
		return popupLink;
	}

	/**
	 * Get the print out link.
	 * @return The print out link.
	 */
	public String getPrintOutLink() {
		return printOutLink;
	}
	
	@Override
	public String toString() {
		return "[objectId = "+objectId+", title = "+title+"]";
	}
}
