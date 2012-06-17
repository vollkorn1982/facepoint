import java.util.Observable;


/**
 * Description: Observer for System.out.println (Feedback)<br>
 * Author: T. Tews <br>
 * Last change: 21.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class CollectOutput extends Observable {
	
	/**
	 * Collects all Syso's
	 * 
	 * @param str
	 */
	public void collectAllData (String str) {
		//System.out.println("CO Test: " + str);
		
		// Notify the system
		setChanged();
		notifyObservers(str);
	}

}
