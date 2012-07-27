package de.tudresden.inf.rn.mobilis.mxa.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.ObservableReader;
import org.jivesoftware.smack.util.ObservableWriter;
import org.jivesoftware.smack.util.ReaderListener;
import org.jivesoftware.smack.util.WriterListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.StatFs;
import android.util.Log;

public class Debugger implements SmackDebugger {

	private static String TAG = "XMPPDebugger";

	private Connection connection = null;
	private PacketListener listener = null;

	private Writer writer;
	private Reader reader;

	private ReaderListener readerListener;
	private WriterListener writerListener;

	private ConnectionListener connectionListener;
	private PacketListener packetListenerIn;
	private PacketListener packetListenerOut;

	private SharedPreferences mPreferences;

	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"hh:mm:ss aaa");

	private static boolean mEnabled = false;
	private static String mDirectory = null;

	private File in;
	private File out;
	private File con;
	private BufferedWriter writerOut;
	private BufferedWriter writerIn;
	private BufferedWriter writerCon;

	private int flushCountIn = 0, flushCountOut = 0;

	public Debugger(Connection connection, Writer w, Reader r) {

		this.connection = connection;
		writer = w;
		reader = r;
		create();

	}

	public void create() {
		ObservableReader debugReader = new ObservableReader(reader);
		readerListener = new ReaderListener() {
			public void read(String str) {
				Log.d("SMACK", dateFormatter.format(new Date()) + " RCV  ("
						+ connection.hashCode() + "): " + str);
			}
		};
		debugReader.addReaderListener(readerListener);

		ObservableWriter debugWriter = new ObservableWriter(writer);
		writerListener = new WriterListener() {
			public void write(String str) {
				Log.d("SMACK", dateFormatter.format(new Date()) + " SENT ("
						+ connection.hashCode() + "): " + str);
			}
		};
		debugWriter.addWriterListener(writerListener);

		reader = debugReader;
		writer = debugWriter;

		// read the directory where to store

		if (mDirectory != null) {
			long time = System.currentTimeMillis();
			in = new File(mDirectory + File.separator + "out_"
					+ String.valueOf(time));
			out = new File(mDirectory + File.separator + "in_"
					+ String.valueOf(time));
			con = new File(mDirectory + File.separator + "con_"
					+ String.valueOf(time));
			try {
				writerIn = new BufferedWriter(new FileWriter(in));
				writerOut = new BufferedWriter(new FileWriter(out));
				writerCon = new BufferedWriter(new FileWriter(con));
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				mEnabled = false;
			}

		}
		// open files

		packetListenerIn = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Log.d("SMACK IN", "( class=" + packet.getClass().getName()
						+ " " + packet.toXML());
				write(writerOut, packet);
			}
		};
		packetListenerOut = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Log.d("SMACK OUT", "( class=" + packet.getClass().getName()
						+ " " + packet.toXML());
				write(writerIn, packet);
			}
		};

		connectionListener = new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				write(writerCon,
						"Reconnection successful: " + connection.getHost());

			}

			@Override
			public void reconnectionFailed(Exception e) {
				write(writerCon,
						"Reconnection failed, Reason: " + e.getMessage());

			}

			@Override
			public void reconnectingIn(int s) {
				write(writerCon, "Reconnection in " + s + " s");

			}

			@Override
			public void connectionClosedOnError(Exception e) {
				write(writerCon,
						"Connection closed on error, Reason: " + e.getMessage());

			}

			@Override
			public void connectionClosed() {
				write(writerCon, "Connection Closed");

			}
		};

	}

	@Override
	public Reader getReader() {
		return reader;
	}

	@Override
	public PacketListener getReaderListener() {
		return packetListenerIn;
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	@Override
	public PacketListener getWriterListener() {
		return packetListenerOut;
	}

	@Override
	public Reader newConnectionReader(Reader newReader) {
		((ObservableReader) reader).removeReaderListener(readerListener);
		ObservableReader debugReader = new ObservableReader(newReader);
		debugReader.addReaderListener(readerListener);
		reader = debugReader;
		return reader;
	}

	@Override
	public Writer newConnectionWriter(Writer newWriter) {
		((ObservableWriter) writer).removeWriterListener(writerListener);
		ObservableWriter debugWriter = new ObservableWriter(newWriter);
		debugWriter.addWriterListener(writerListener);
		writer = debugWriter;
		return writer;
	}

	@Override
	public void userHasLogged(String user) {
		connection.addConnectionListener(connectionListener);
		Log.v(TAG, "USER HAST LOGGED");
		write(writerCon, "User has logged on to:" + connection.getHost());
	}

	public void write(BufferedWriter bw, String text) {
		if (mEnabled) {
			if (!checkDiskSize()) {
				mEnabled = false;
				return;
			}
			try {

				bw.append(System.currentTimeMillis() + ";" + text + "\n");

				bw.flush();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage() + " Stopping debugging log");
				mEnabled = false;
			}

		}
	}

	public static void setDirectory(String directory) {
		mDirectory = directory;
	}

	public static void setEnabled(boolean enable) {
		mEnabled = enable;
	}

	private long lastFlushOut = 0;
	private long lastFlushIn = 0;
	private static long FLUSH_INTERVAL = 10000;

	public void write(BufferedWriter bw, Packet packet) {

		if (mEnabled) {
			if (!checkDiskSize()) {
				mEnabled = false;
				return;
			}
			try {

				bw.append(System.currentTimeMillis() + ";" + packet.toXML()
						+ "\n");

				bw.flush();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage() + " Stopping debugging log");
				mEnabled = false;
			}

		}
	}

	/**
	 * Test if there is enough space left on the device (more than 10 mb)
	 * 
	 * @return true if there is enough, else false
	 */
	private boolean checkDiskSize() {
		StatFs stat = new StatFs(out.getAbsolutePath());
		long availableBlocks = stat.getAvailableBlocks();
		long blockSize = stat.getBlockSize();
		long freeSpace = availableBlocks * blockSize;
		// Log.v(TAG,"Available space for "+out.getAbsolutePath()+" is "+freeSpace+
		// " B");
		if (freeSpace > 10000)
			return true;
		else
			return false;
	}

}
