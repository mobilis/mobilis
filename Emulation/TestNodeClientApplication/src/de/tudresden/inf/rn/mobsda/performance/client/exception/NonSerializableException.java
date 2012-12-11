package de.tudresden.inf.rn.mobsda.performance.client.exception;

import de.tudresden.inf.rn.mobsda.performance.client.TestNodeClient;

/**
 * This exception is thrown by {@link TestNodeClient#runMethod(String, String[], String[])}
 * when the invoked method returns a non-serializable object.
 * 
 * You can obtain the String value of the returned object by calling getStringValueRepresentation().
 * @author sven
 *
 */
public class NonSerializableException extends RunMethodException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2808737916734408065L;

	private String returnValue;
	
	public NonSerializableException(Object returnValue) {
		this.returnValue = returnValue.toString();
	}
	
	public String getStringValueRepresentation() {
		return returnValue;
	}
}
