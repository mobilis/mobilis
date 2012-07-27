import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.Iterator;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.xmlpull.v1.XmlPullParser;

/**
 * Holds the connection to the xmpp server
 * and executes commands.
 * @author Christian Magenheimer
 *
 */
public class Connection {

	private String mXMPPUser;
	private String mPassword;
	private String mXMPPServerAdress;
	private XMPPConnection mConnection;
	
	
	public Connection(String user, String password, String serverAdress)
	{
		mXMPPUser=user;
		mPassword=password;
		mXMPPServerAdress=serverAdress;
	}
	
	/**
	 * Connect to XMPP Server and register listener for message, presence 
	 * and all the other stuff.
	 */
	public void connect()
	{
		try
		{
			ConnectionConfiguration conf= new ConnectionConfiguration("mobilis.inf.tu-dresden.de");
			
			mConnection= new XMPPConnection(conf);
			mConnection.connect();
			mConnection.login(mXMPPUser, mPassword);
			
			mConnection.addPacketListener(new MessageListener(), new PacketTypeFilter(Message.class));
			mConnection.addPacketListener(new IQListener(), new PacketTypeFilter(IQ.class));
			ProviderManager.getInstance().addIQProvider("ping", "urn:xmpp:ping", new PingIQProvder());
			ProviderManager.getInstance().addIQProvider("test", "mobilis:mxa:test", new TestIQProvder());
			FileTransferManager ftm= new FileTransferManager(mConnection);
			ftm.addFileTransferListener(new FileListener());
		}catch (XMPPException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private class MessageListener extends Thread implements PacketListener
	{

		@Override
		public void processPacket(Packet packet) {
			//process messages, the commands are in the body
			//to distinguish to normal payload, the commands start with #
			Message m=(Message)packet;
			String body=m.getBody();
			System.out.println(packet.toXML());
			if (body!=null && body.startsWith("#"))
			{
				//command!
				
				String command=body.substring(body.indexOf("#")+1,body.length()).toLowerCase();
				System.out.println("Command received: \""+command+"\"");
				if (command.equals("p"))
				{
					System.out.println("Presence Command");
					//set the presence randomly
					Presence presence = new Presence(Presence.Type.available);
					int random=(int) (Math.random()*Presence.Mode.values().length);
					presence.setMode(Presence.Mode.values()[random]);
					String[] stats={"here","there","everywhere","gone out","afk"};
					random=(int) (Math.random()*stats.length);
					presence.setStatus(stats[random]);
					mConnection.sendPacket(presence);
				}else if (command.equals("f"))
				{
					System.out.println("Filetransfer");
					FileTransferManager ftm= new FileTransferManager(mConnection);
					OutgoingFileTransfer oft=ftm.createOutgoingFileTransfer(packet.getFrom());
					try {
						oft.sendFile(new File("files/Essay.pdf"),"Hey, here is the file you requested");
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if (command.startsWith("p#"))
				{
					//=============================================
					//Pub Sub
					//=============================================
														
					//PubSub command, check if we should create, delete or publish information
					PubSubManager psmng= new PubSubManager(mConnection);
					if (command.startsWith("p#c"))
					{
						//create the nod
						try {
							psmng.createNode("echomxa");
							System.out.println("Created pubsub node: \"echomxa\"");
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}else if  (command.startsWith("p#d"))
					{
						try {
							psmng.deleteNode("echomxa");
							System.out.println("Deleted pubsub node: \"echomxa\"");
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}else if (command.startsWith("p#p"))
					{
						try {
							LeafNode node=(LeafNode) psmng.getNode("echomxa");
							Item i= new Item();
							node.publish(i);
							System.out.println("Sended PubSubItem:"+i.toXML());
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}else if (command.startsWith("sd"))
				{
					//=============================================
					//Service Discovery
					//=============================================
					System.out.println("Service Discovery");
					ServiceDiscoveryManager sdm= ServiceDiscoveryManager.getInstanceFor(mConnection);
					
					DiscoverItems items=null;
					try {
						items = sdm.discoverItems(packet.getFrom());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Iterator<DiscoverItems.Item> it=items.getItems();
					while(it.hasNext())
					{
						DiscoverItems.Item i=it.next();
						System.out.println(i.toXML());
					}
				}
			}else
			{
				//send back the Message
				if (m.getBody()!=null)
				{
					System.out.println("send it back");
					
					Message m2 = new Message(m.getFrom());
					m2.setType(Message.Type.chat);
					m2.setFrom(mConnection.getUser());
					m2.setBody("Echo: "+m.getBody());
					System.out.println(m2.toXML());
					mConnection.sendPacket(m2);
				}
			}
			
			
		}
		
	}

	private class IQListener extends Thread implements PacketListener
	{

		@Override
		public void processPacket(Packet packet) {
			//System.out.println("received iq: "+packet.toXML());
			if (packet instanceof PingIQ)
			{
				IQ iq= new IQ() {
					
					@Override
					public String getChildElementXML() {
						// TODO Auto-generated method stub
						return "";
					}
				};
				if (iq.getType()!=Type.GET) return;
				iq.setFrom(packet.getTo());
				iq.setTo(packet.getFrom());
				iq.setType(IQ.Type.RESULT);
				iq.setPacketID(packet.getPacketID());
				mConnection.sendPacket(iq);
				System.out.println("Send iq: "+iq.toXML());
				
			}else if(packet instanceof TestIQ)
			{
				TestIQ iq = new TestIQ(((TestIQ) packet).mNumber);
				if (iq.getType()!=Type.GET) return;
				iq.setFrom(packet.getTo());
				iq.setTo(packet.getFrom());
				iq.setType(IQ.Type.RESULT);
				iq.setPacketID(packet.getPacketID());
				mConnection.sendPacket(iq);
				System.out.println("Send iq: "+iq.toXML());
			}
			
		}
		
	}
	
	private class PingIQProvder implements IQProvider
	{

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			return new PingIQ();
		}
		
	}
	
	
	private class PingIQ extends IQ
	{

		@Override
		public String getChildElementXML() {
			return "";
		}
		
	}

	private class TestIQProvder implements IQProvider
	{

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
		//	String childElement = MobilisPingBean.CHILD_ELEMENT;
			int number=0;
			boolean done = false;
			do {
				switch (parser.getEventType()) {
				case XmlPullParser.TEXT:
					number=Integer.valueOf(parser.getText());
					done=true;
					break;
				case XmlPullParser.END_DOCUMENT:
				 	done=true;
					break;
				default:
					parser.next();
				}
			} while (!done);
			return new TestIQ(number);
		}
		
	}
	
	
	private class TestIQ extends IQ
	{
		public TestIQ(int n)
		{
			mNumber=n;
		}
		int mNumber;
		@Override
		public String getChildElementXML() {
			return String.valueOf(mNumber);
		}
		
	}

	
	private class FileListener extends Thread implements FileTransferListener
	{

		@Override
		public void fileTransferRequest(FileTransferRequest request) {
			System.out.println("File transfer");
			IncomingFileTransfer ift=request.accept();
			File file = new File("files/"+request.getFileName());
			try {
				ift.recieveFile(file);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("File transfer ended");
		}
		
	}
}
