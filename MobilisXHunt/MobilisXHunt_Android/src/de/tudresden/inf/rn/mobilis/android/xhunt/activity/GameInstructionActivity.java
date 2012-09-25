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
package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class GameInstructionActivity presents a short instruction of the game.
 */
public class GameInstructionActivity extends Activity{

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
              
        initComponents();
    }
	
	/**
	 * Inits the components with the instructions.
	 */
	private void initComponents(){
		
		TextView tv_instructions = (TextView)findViewById(R.id.instructions_tv_text);
		tv_instructions.setText("This game is the real-time version of the famous board game Scotland Yard.\n");
		tv_instructions.setText(tv_instructions.getText() 
				+ "There are two kinds of characters: Mister X,"
				+ " a criminal, and some agents that haunt him. Mr. X tries not to be caught.\n");
		tv_instructions.setText(tv_instructions.getText() 
				+ "The players are in a city, e.g. Dresden," 
				+ " and have the possibility to take the public transports to get from one station to the next." 
				+ " Everyone only has a certain number of different tickets, for example 5 bus tickets," 
				+ " 5 tram tickets and 2 railway tickets.");
		tv_instructions.setText(tv_instructions.getText()
				+ "The game consists of 10 rounds, in each one first Mr. X has to choose his next target and then the agents."
				+ " After this it needs some time until everybody reaches the target station by bus, tram or railway."
				+ " If everybody arrived and Mr. X is not on the same station as one agent, the next round starts."
				+ " If Mr. X is caught the agents win, but if the ten rounds are over and Mr. X wasn\'t found, he wins.");
		
	}
}
