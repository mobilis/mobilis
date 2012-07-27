/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
/**
 * Activity that shows the Map of the city with all stations and its
 * connections as well as the actual positions of the players.
 */
package de.tudresden.inf.rn.mobilis.android.xhunt.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class DialogGameDetails.
 */
public class DialogGameDetails extends Dialog {
	
	/** The Constant DIALOG_ID. */
	public static final int DIALOG_ID = 303;
	
	/** The TableLayout which holds the game details. */
	private TableLayout mTableLayout;
	
	/** The applications context. */
	private Context mContext;
	
	/** The display of the players device. */
	private Display mDisplay;
	
	/** True if dialog already has content. */
	private boolean mHasContent = false;

	/**
	 * Instantiates a new dialog game details.
	 *
	 * @param context the context of the application
	 * @param display the display of the own device
	 */
	public DialogGameDetails(Context context, Display display) {
		super(context);
		mContext = context;
		this.mDisplay = display;
		
		this.setContentView(R.layout.dialog_body);		
    	
    	mTableLayout = (TableLayout)findViewById(R.id.dialog_list);
	}
	
	/**
	 * Clear content.
	 */
	public void clearContent(){
		mTableLayout.removeAllViews();
		mHasContent = false;
	}
	
	/**
	 * Add the details.
	 *
	 * @param gameName the game name
	 * @param requirePassword the requires password
	 * @param countRounds the amount of rounds
	 * @param startTimer the start timer
	 * @param playernames the players names
	 */
	public void addDetails(String gameName, boolean requirePassword, int countRounds,
			int startTimer, List<String> playernames){

		this.setTitle("Details of: " + gameName);
      	
		String players = "";
		for(String name : playernames)
			players += name + "; ";
    	
    	mTableLayout.addView(generateTextView("Require Pass: " + requirePassword));
    	mTableLayout.addView(generateTextView("Rounds: " + countRounds));
    	mTableLayout.addView(generateTextView("Start Timer: " + startTimer));
    	mTableLayout.addView(generateTextView("Players: " + players));

    	mHasContent = true;
	}
	
	/**
	 * Generate a TextView with the specific text.
	 *
	 * @param text the text to display
	 * @return the TextView
	 */
	private TextView generateTextView(String text){
    	TextView textView = new TextView(mContext);
    	textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	textView.setText(text);
    	textView.setPadding(0, 0, 0, 10);
    	
    	return textView;
	}
	
	/**
	 * Checks if dialog has content.
	 *
	 * @return true, if content is available
	 */
	public boolean hasContent(){
		return this.mHasContent;
	}
}
