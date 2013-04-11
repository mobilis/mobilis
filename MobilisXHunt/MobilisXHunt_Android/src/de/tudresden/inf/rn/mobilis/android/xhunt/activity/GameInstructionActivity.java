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
		tv_instructions.setText("This game is a real-life version of the popular board game Scotland Yard.\n\n");
		tv_instructions.setText(tv_instructions.getText() 
				+ "Players can assume two roles: One has to play Mister X, a fugitive infamous villain, "
				+ "while the rest of the players act as Agents, trying to find him. This is complicated "
				+ "by the fact that Mister X can see all the Agents\' positions on his map, while they "
				+ "can only see each other.\n\n");
		tv_instructions.setText(tv_instructions.getText() 
				+ "The game is round-based, at the beginning of each round the players have to choose a "
				+ "target, which equals to a station of the local public transport. If an Agent happens "
				+ "to encounter Mister X at the target station, the Agents win the game, else everyone "
				+ "(including Mister X) moves on until the maximum number of rounds is reached.\n\n");
		tv_instructions.setText(tv_instructions.getText()
				+ "Hint: Mister X becomes visible on the Agents\' map every third round, so watch out!");
		
	}
}
