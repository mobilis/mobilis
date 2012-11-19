package de.tudresden.inf.rn.mobsda.performance.client.exception;

import de.tudresden.inf.rn.mobsda.performance.client.TestNodeClient;

/**
 * This execption (or one of its subclasses) is thrown when
 * an error occures during invocation of {@link TestNodeClient#runMethod(String, String[], String[])}.
 * @author sven
 *
 */
public class RunMethodException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2557117981239418536L;

	@Override
	public String getMessage() {
		return "This execption (or one of its subclasses) is thrown when an error occures during invocation of {@link TestNodeClient#runMethod(String, String[], String[])}.";
	}
}
