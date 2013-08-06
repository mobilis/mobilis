package de.tudresden.inf.rn.mobilis.server.services.Coordination;

import java.util.HashSet;
import java.util.Random;

/**
 * This Class is a Helper Class of the CoordinatorService. It is implementing different Methods to handle clientside createServiceInstance Requests.
 * The Methods in this Class differ in how the best Runtime is choosed, where the new Serviceinstance should be created.
 */
public class loadBalancing {
	
	/**
	 * A Runtime is randomly choosen out of a given Set of Runtimes
	 * @return
	 */
	public static String randomRuntimeForCreateInstance(HashSet<String> jidsOfRuntimes){
		String[] jids = (String[]) jidsOfRuntimes.toArray();
		Random r = new Random();
		int randomNumber = r.nextInt(jids.length);
		String choosenRuntime = jids[randomNumber];
		return choosenRuntime;
	}
}
