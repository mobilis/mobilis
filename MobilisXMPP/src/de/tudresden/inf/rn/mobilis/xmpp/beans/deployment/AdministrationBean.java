package de.tudresden.inf.rn.mobilis.xmpp.beans.deployment;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public abstract class AdministrationBean extends XMPPBean {

	/**
	 * generalization of all administrative beans to handle them easier in deployment service
	 * @author Philipp Grubitzsch
	 */
	private static final long serialVersionUID = 1L;
	public String ServiceNamespace = null;
	public int ServiceVersion = -1;
	
	public AdministrationBean() {
		// TODO Auto-generated constructor stub
	}

	public AdministrationBean(String errorType, String errorCondition,
			String errorText) {
		super(errorType, errorCondition, errorText);
		// TODO Auto-generated constructor stub
	}

}
