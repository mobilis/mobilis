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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * The Class InputDialog.
 */
public class DialogInput {
	
	/** The 'AlertDialog' builder. */
	private AlertDialog.Builder mAlertBuilder;
	
	/** The 'EditText' to input some text. */
	private EditText mViewInputText;
	
	/** Default InputDialog text for positive button. */
	private String mBtn_InputDialogPosText = "OK";
	
	/** Default InputDialog text for negative button. */
	private String mBtn_InputDialogNegText = "Cancel";

	/**
	 * Instantiates a new 'InputDialog'.
	 *
	 * @param c the context of the application
	 * @param message the message to show in the body of this view
	 */
	public DialogInput(Context c, String message) {
		mAlertBuilder = new AlertDialog.Builder(c);
		mAlertBuilder.setMessage(message);

		mViewInputText = new EditText(c);
		mAlertBuilder.setView(mViewInputText);

		mAlertBuilder.setPositiveButton(mBtn_InputDialogPosText,
					new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {}
		});

		mAlertBuilder.setNegativeButton(mBtn_InputDialogNegText,
					new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {}
		});
	}
	
	/**
	 * Gets the 'AlertDialog' builder.
	 *
	 * @return the alter builder
	 */
	public AlertDialog.Builder getAlertBuilder(){
		return mAlertBuilder;
	}
	
	/**
	 * Gets the input text.
	 *
	 * @return the input text
	 */
	public String getInputText(){
		return mViewInputText.getText().toString();
	}
	
	/**
	 * Sets the input text.
	 *
	 * @param text the new input text
	 */
	public void setInputText(String text){
		mViewInputText.setText(text);
	}
	
	/**
	 * Sets the negative button.
	 *
	 * @param text the text of the button
	 * @param onClickListener the on click listener of the button
	 */
	public void setNegativeButton(String text, DialogInterface.OnClickListener onClickListener){
		mAlertBuilder.setNegativeButton(text, onClickListener);
	}
	
	/**
	 * Sets the positive button.
	 *
	 * @param text the text of the button
	 * @param onClickListener the on click listener of the button
	 */
	public void setPositiveButton(String text, DialogInterface.OnClickListener onClickListener){
		mAlertBuilder.setPositiveButton(text, onClickListener);
	}
	
	/**
	 * Shows the dialog.
	 */
	public void show(){
		mAlertBuilder.show();
	}
	
	/**
	 * Creates the dialog without showing it, for further modification.
	 * @return the created AlertDialog object
	 */
	public AlertDialog create() {
		return mAlertBuilder.create();
	}
	
}
