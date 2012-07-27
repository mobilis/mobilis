/*
 * Copyright (C) 2009 Niall 'Rivernile' Scott
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors or contributors be held liable for
 * any damages arising from the use of this software.
 *
 * The aforementioned copyright holder(s) hereby grant you a
 * non-transferrable right to use this software for any purpose (including
 * commercial applications), and to modify it and redistribute it, subject to
 * the following conditions:
 *
 *  1. This notice may not be removed or altered from any file it appears in.
 *
 *  2. Any modifications made to this software, except those defined in
 *     clause 3 of this agreement, must be released under this license, and
 *     the source code of any modifications must be made available on a
 *     publically accessible (and locateable) website, or sent to the
 *     original author of this software.
 *
 *  3. Software modifications that do not alter the functionality of the
 *     software but are simply adaptations to a specific environment are
 *     exempt from clause 2.
 */

package de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging.ServerlessMessagingService.ServerThread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * The ConnectionHandler class deals with the individual client connections to
 * the Bus Tracker Server and the requests that each of these clients may have.
 * 
 * @author Niall Scott
 */
public class ConnectionHandler implements Runnable {

	// TAG for logging
	private static final String TAG = "ConnectionHandler";

	private Socket clientSocket;
	private BufferedReader clientIn;
	private PrintWriter clientOut;
	private String mLocalName;
	private String mRemoteName;

	private ServerThread mHandler;

	/**
	 * Create a new ConnectionHandler.
	 * 
	 * @param clientSocket
	 *            The clientSocket of this particular client.
	 * @param socketHandler
	 *            The call back to the IncomingSocketHandler instance so we can
	 *            remove this object from the connection list once this task is
	 *            finished.
	 */
	public ConnectionHandler(final Socket socket, ServerThread serverThread) {
		Log.i(TAG, "new connection handler");
		mHandler = serverThread;
		if (socket == null)
			throw new IllegalArgumentException("The socket "
					+ "instance must not be null.");
		try {
			clientIn = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			clientOut = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Exception while setting up socket "
					+ "input/output streams:");
			System.err.println(e.toString());
			System.err.println("Client IP address: "
					+ socket.getInetAddress().getHostAddress());
		}
	}

	/**
	 * This is the entry point when the new clientSocket thread is created for
	 * this class.
	 */
	@Override
	public void run() {
		try {
			String inputLine = "";
			while (!(inputLine.endsWith("/streams\">"))) {
				inputLine += (char) clientIn.read();
			}
			// stream opened
			Log.i(TAG, inputLine);
			// parse local and remote names
			int toPos = inputLine.indexOf(" to=\"") + 6;
			mLocalName = inputLine.substring(toPos, inputLine.indexOf("\"", toPos));
			int fromPos = inputLine.indexOf(" from=\"") + 7;
			mRemoteName = inputLine.substring(fromPos, inputLine.indexOf("\"", fromPos));
			inputLine = "";
			
			// now open stream back
			clientOut
					.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><stream:stream to=\"" + mRemoteName + "\" from=\"" + mLocalName + "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">");
			clientOut.flush();

			// wait for next message
			while (true) {
				while (!(inputLine.endsWith("</message>"))) {
					inputLine += (char) clientIn.read();
				}
				Log.i(TAG, "received message: " + inputLine);
				mHandler.onMessageReceived(inputLine);
				inputLine = "";
			}
		} catch (IOException e) {
			// The clientSocket has probably been closed.
		} finally {
			try {
				clientIn.close();
				clientOut.close();
				clientSocket.close();
			} catch (IOException e) {
				// Assume the socket is already closed.
			}
			// TODO: remove from within ServerlessMessagingService
		}
	}
	
	public void sendMessage(String content) {
		StringBuffer buf = new StringBuffer();
		buf.append("<message from=\"");
		buf.append(mLocalName);
		buf.append("\" type=\"chat\" to=\"");
		buf.append(mRemoteName);
		buf.append("\">");
		buf.append(content);
		buf.append("</message>");
		String message = buf.toString();
		Log.i(TAG, "Sending message: " + message);
		clientOut.write(message);
		clientOut.flush();
	}
}