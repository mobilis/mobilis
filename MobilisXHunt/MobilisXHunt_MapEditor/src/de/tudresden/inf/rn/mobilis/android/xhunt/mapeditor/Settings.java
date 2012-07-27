package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * The Class Settings.
 */
public class Settings {
	
	/** The m default route color. */
	private Color mDefaultRouteColor;
	
	/** The m default route stroke. */
	private BasicStroke mDefaultRouteStroke;

	/**
	 * Instantiates a new settings.
	 */
	public Settings() {
		mDefaultRouteColor = new Color(0, 0, 255);
		mDefaultRouteStroke = new BasicStroke(3);
	}

	/**
	 * Gets the default route color.
	 *
	 * @return the default route color
	 */
	public Color getDefaultRouteColor() {
		return mDefaultRouteColor;
	}

	/**
	 * Sets the default route color.
	 *
	 * @param defaultRouteColor the new default route color
	 */
	public void setDefaultRouteColor(Color defaultRouteColor) {
		this.mDefaultRouteColor = defaultRouteColor;
	}

	/**
	 * Gets the default route stroke.
	 *
	 * @return the default route stroke
	 */
	public BasicStroke getDefaultRouteStroke() {
		return mDefaultRouteStroke;
	}

	/**
	 * Sets the default route stroke.
	 *
	 * @param thickness the new default route stroke
	 */
	public void setDefaultRouteStroke(int thickness) {
		this.mDefaultRouteStroke = new BasicStroke(thickness);
	}
	
	
}
