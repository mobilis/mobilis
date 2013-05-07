package de.tudresden.inf.rn.mobilis.mxa.provider;

import java.util.ArrayList;


public class ProviderRegistry extends ArrayList<DynamicContentProvider> {
	
	private static final long serialVersionUID = 1L;
	
	private static ProviderRegistry instance;
	
	public static ProviderRegistry get() {
		if (instance != null) {
			return instance;
		} else {
			return new ProviderRegistry();
		}
	}

	public ProviderRegistry() {
		instance = this;
	}

}
