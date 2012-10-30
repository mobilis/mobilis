package de.tudresden.inf.rn.mobsda.performance.client.exception;

import de.tudresden.inf.rn.mobsda.performance.client.TestNodeClient;

/**
 * This exception is thrown in case a non-recognized fully qualified class name was supplied as parameter type during a call to {@link TestNodeClient#runMethod(String, String[], String[])}.
 * @author sven
 *
 */
public class ParameterTypeException extends RunMethodException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5746270059473877483L;
	
	private String parameterType;
	
	public ParameterTypeException(String parameterType) {
		this.parameterType = parameterType;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return parameterType + " is unknown to the class loader.";
	}
}
