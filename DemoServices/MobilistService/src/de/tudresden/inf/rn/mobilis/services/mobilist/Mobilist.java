package de.tudresden.inf.rn.mobilis.services.mobilist;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.services.mobilist.proxy.PingRequest;
import de.tudresden.inf.rn.mobilis.services.mobilist.proxy.PingResponse;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;

public class Mobilist extends MobilisService {

	@Override
	protected void registerPacketListener() {
		(new BeanProviderAdapter(new ProxyBean(PingRequest.NAMESPACE, PingRequest.CHILD_ELEMENT))).addToProviderManager();
		(new BeanProviderAdapter(new ProxyBean(PingResponse.NAMESPACE, PingResponse.CHILD_ELEMENT))).addToProviderManager();
		
		IQListener listener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		
		getAgent().getConnection().addPacketListener(listener, filter);
	}
	
	@Override
	public void startup(MobilisAgent agent) throws Exception {
		super.startup(agent);
	}

	class IQListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			System.out.println("Incoming packet: " + packet.toXML());
			
			if (packet instanceof BeanIQAdapter) {
				System.out.println("Packet is a BeanIQAdapter");
				
				PingRequest inBean = (PingRequest) ((BeanIQAdapter) packet).getBean();
				
				System.out.println("Assuming a PingRequest was sent…");
				
				PingResponse outBean = new PingResponse(inBean.getContent());
				
				getAgent().getConnection().sendPacket(new BeanIQAdapter(outBean));
			}
		}
		
	}

}