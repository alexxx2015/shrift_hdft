package CommunicationManager;

/**
 * @author Tobias
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public interface IPDP2PEP {

	/**
	 * 
	 * @param executeActions
	 */
	public Status execute(Event[0..*] executeActions);

}