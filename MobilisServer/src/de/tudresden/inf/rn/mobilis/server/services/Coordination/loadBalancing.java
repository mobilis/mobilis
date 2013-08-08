package de.tudresden.inf.rn.mobilis.server.services.Coordination;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Philipp Grubitzsch
 * This Class is a Helper Class of the CoordinatorService. It is implementing different Methods to handle clientside createServiceInstance Requests.
 * The Methods in this Class differ in how the best Runtime is choosed, where the new Serviceinstance should be created.
 */
public class loadBalancing {
	
	/**
	 * A Runtime is randomly choosen out of a given Set of Runtimes
	 * @return
	 */
	public static String randomRuntimeForCreateInstance(HashSet<String> jidsOfRuntimes){
		Iterator<String> iter = jidsOfRuntimes.iterator();
		int i = 0;
		String[] jids = new String[jidsOfRuntimes.size()];
		while(iter.hasNext()){
			jids[i] = iter.next();
			i++;
		}
		Random r = new Random();
		int randomNumber = r.nextInt(jids.length);
		String choosenRuntime = jids[randomNumber];
		return choosenRuntime;
	}
}
