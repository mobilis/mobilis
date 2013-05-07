package de.tudresden.inf.rn.mobilis.mxa.provider;

import android.content.ContentProvider;

public abstract class DynamicContentProvider extends ContentProvider {

	public DynamicContentProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onCreate() {
		registerAtProviderRegistry();
		return false;
	}
	
	public abstract void loadUriMatcherAuthority();
	
	private void registerAtProviderRegistry() {
		ProviderRegistry.get().add(this);
	}

}
