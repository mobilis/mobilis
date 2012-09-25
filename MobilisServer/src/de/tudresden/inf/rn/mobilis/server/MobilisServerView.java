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
package de.tudresden.inf.rn.mobilis.server;

import java.awt.Color;
import java.awt.Cursor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

/**
 * The application's main frame.
 */
public class MobilisServerView extends FrameView implements MobilisView {

	private class LoginTask extends org.jdesktop.application.Task<Object, Void> {

		LoginTask(org.jdesktop.application.Application app) {
			// Runs on the EDT.  Copy GUI state that
			// doInBackground() depends on from parameters
			// to LoginTask fields, here.
			super(app);

			getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// set inactive
			statusMessageLabel.setText("Connecting...");
			btnConnect.setEnabled(false);
		}

		@Override
		protected Object doInBackground() {
			// Your Task's code here.  This method runs
			// on a background thread, so don't reference
			// the Swing GUI from here.

			MobilisManager.getInstance().startup();

			return null;  // return your result
		}

		@Override
		protected void succeeded(Object result) {
			// Runs on the EDT.  Update the GUI based on
			// the result computed by doInBackground().
			btnDisconnect.setEnabled(true);
			getComponent().setCursor(Cursor.getDefaultCursor());
			statusMessageLabel.setText("Connected");
		}
	}

	private class LogoutTask extends org.jdesktop.application.Task<Object, Void> {

		LogoutTask(org.jdesktop.application.Application app) {
			// Runs on the EDT.  Copy GUI state that
			// doInBackground() depends on from parameters
			// to LogoutTask fields, here.
			super(app);
			btnDisconnect.setEnabled(false);
			getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusMessageLabel.setText("Disconnecting...");
		}

		@Override
		protected Object doInBackground() {
			// Your Task's code here.  This method runs
			// on a background thread, so don't reference
			// the Swing GUI from here.

			MobilisManager.getInstance().shutdown();

			return null;  // return your result
		}

		@Override
		protected void succeeded(Object result) {
			// Runs on the EDT.  Update the GUI based on
			// the result computed by doInBackground().
			btnConnect.setEnabled(true);
			getComponent().setCursor(Cursor.getDefaultCursor());
			statusMessageLabel.setText("Disconnected");
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnConnect;
	private javax.swing.JButton btnDisconnect;

	private javax.swing.JPanel mainPanel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JLabel statusMessageLabel;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JTextPane jtpConsole;
	private javax.swing.JScrollPane console;

	private Map<Level, SimpleAttributeSet> mFonts = Collections.synchronizedMap(new HashMap<Level, SimpleAttributeSet>());
	SimpleAttributeSet mDefaultFont = new SimpleAttributeSet();

	public MobilisServerView(SingleFrameApplication app) {
		super(app);
		
		initFonts();
		initComponents();

		btnConnect.setText("Startup");
		btnDisconnect.setText("Shutdown");		
	}
	
	private void initFonts() {
		synchronized(mFonts) {
			// 300
			SimpleAttributeSet finest = new SimpleAttributeSet();
			StyleConstants.setForeground(finest, Color.LIGHT_GRAY);
			StyleConstants.setFontFamily(finest, "Helvetica");
			StyleConstants.setFontSize(finest, 10);
			mFonts.put(Level.FINEST, finest);
			
			// 400
			SimpleAttributeSet finer = new SimpleAttributeSet();
			StyleConstants.setForeground(finer, Color.GRAY);
			StyleConstants.setFontFamily(finer, "Helvetica");
			StyleConstants.setFontSize(finer, 10);
			mFonts.put(Level.FINER, finer);
			
			// 500
			SimpleAttributeSet fine = new SimpleAttributeSet();
			StyleConstants.setForeground(fine, Color.DARK_GRAY);
			StyleConstants.setFontFamily(fine, "Helvetica");
			StyleConstants.setFontSize(fine, 10);
			mFonts.put(Level.FINE, fine);
			
			// 700
			SimpleAttributeSet config = new SimpleAttributeSet();
			StyleConstants.setForeground(config, Color.BLACK);
			StyleConstants.setFontFamily(config, "Helvetica");
			StyleConstants.setFontSize(config, 10);
			mFonts.put(Level.CONFIG, config);
			
			// 800
			SimpleAttributeSet info = new SimpleAttributeSet();
			StyleConstants.setForeground(info, Color.BLUE);
			StyleConstants.setFontFamily(info, "Helvetica");
			StyleConstants.setFontSize(info, 10);
			mFonts.put(Level.INFO, info);

			// 900
			SimpleAttributeSet warning = new SimpleAttributeSet();
			StyleConstants.setForeground(warning, Color.MAGENTA);
			StyleConstants.setFontFamily(warning, "Helvetica");
			StyleConstants.setFontSize(warning, 10);
			mFonts.put(Level.WARNING, warning);

			// 1000
			SimpleAttributeSet severe = new SimpleAttributeSet();
			StyleConstants.setForeground(severe, Color.RED);
			StyleConstants.setFontFamily(severe, "Helvetica");
			StyleConstants.setBold(severe, true);
			StyleConstants.setFontSize(severe, 10);
			mFonts.put(Level.SEVERE, severe);
			
			// set default font
			mDefaultFont = info;
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new javax.swing.JPanel();
		btnConnect = new javax.swing.JButton();
		btnDisconnect = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		jtpConsole = new javax.swing.JTextPane();
		console = new JScrollPane(jtpConsole); 
		jtpConsole.setBackground(Color.white);
		javax.swing.JMenu fileMenu = new javax.swing.JMenu();
		javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
		statusPanel = new javax.swing.JPanel();
		javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
		statusMessageLabel = new javax.swing.JLabel();

		mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.setName("mainPanel"); // NOI18N
		mainPanel.setLayout(new java.awt.GridBagLayout());


		javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(de.tudresden.inf.rn.mobilis.server.MobilisServer.class).getContext().getActionMap(MobilisServerView.class, this);
		btnConnect.setAction(actionMap.get("login")); // NOI18N
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(de.tudresden.inf.rn.mobilis.server.MobilisServer.class).getContext().getResourceMap(MobilisServerView.class);
		btnConnect.setText(resourceMap.getString("btnConnect.text")); // NOI18N
		btnConnect.setName("btnConnect"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(btnConnect, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		mainPanel.add(console,gridBagConstraints);

		btnDisconnect.setAction(actionMap.get("logout")); // NOI18N
		btnDisconnect.setText(resourceMap.getString("btnDisconnect.text")); // NOI18N
		btnDisconnect.setEnabled(false);
		btnDisconnect.setName("btnDisconnect"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainPanel.add(btnDisconnect, gridBagConstraints);

		menuBar.setName("menuBar"); // NOI18N

		fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
		fileMenu.setName("fileMenu"); // NOI18N

		exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
		exitMenuItem.setName("exitMenuItem"); // NOI18N
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		statusPanel.setName("statusPanel"); // NOI18N

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(statusMessageLabel)
						.addContainerGap(432, Short.MAX_VALUE))
		);
		statusPanelLayout.setVerticalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(statusMessageLabel)
						.addGap(3, 3, 3))
		);

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(statusPanel);
	}

	@Action
	public Task<?, ?> login() {
		return new LoginTask(getApplication());
	}

	@Action
	public Task<?, ?> logout() {
		return new LogoutTask(getApplication());
	}

	protected void appendText(String text, AttributeSet set)
	{
		try {	          
			jtpConsole.getDocument().insertString(
					jtpConsole.getDocument().getLength(), text, set);
			if (jtpConsole.getDocument().getLength()>=10000) {
				String x = jtpConsole.getDocument().getText(0,jtpConsole.getDocument().getLength());
				int i = x.indexOf("\n",text.length());
				jtpConsole.getDocument().remove(0,i+1);
			}

		}
		catch (BadLocationException e) {
			System.err.println("Bei dem Versuch einen Text an das JTextPane anzuhï¿½ngen ist eine Bad Location Exception aufgetreten");
			System.exit(-1);
		}
	}

	@Override
	//shows log messages in Console in GUI
	public void showLogMessage(Level level, String message) {
		System.err.println("(" + level + ")   " + message );
		synchronized(mFonts) {
			if (mFonts.containsKey(level)) {
				appendText("(" + level + ")   " + message + "\n", mFonts.get(level));
			} else {
				appendText("(" + level + ")   " + message + "\n", mDefaultFont);
			}
		}
		
		statusMessageLabel.setText("(" + level + ")   " + message);
		//put focus on last line in Panel
		console.getVerticalScrollBar().setValue(jtpConsole.getDocument().getLength());
	}

	@Override
	public void setStarted(Boolean started) {
		if (started) {
			btnConnect.setEnabled(false);
			btnDisconnect.setEnabled(true);
			statusMessageLabel.setText("Connected");
		} else {
			btnConnect.setEnabled(true);
			btnDisconnect.setEnabled(false);
			statusMessageLabel.setText("Disconnected");
		}
	}
	
	/**
	 * Get the Button to start the MobilisServer
	 * @return the Startup button of the MobilisServer GUI
	 */
	public javax.swing.JButton getStartupButton() {
		return btnConnect;
	}
}
