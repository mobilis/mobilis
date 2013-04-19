package de.tudresden.inf.rn.mobilis.services.mobilist;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.services.mobilist.proxy.AddListEntryResponse;
import de.tudresden.inf.rn.mobilis.services.mobilist.proxy.ListEntry;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;

public class Mobilist extends MobilisService {

	@Override
	protected void registerPacketListener() {
		IQListener listener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		
		getAgent().getConnection().addPacketListener(listener, filter);
	}
	
	class IQListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			System.out.println("Incoming packet: " + packet.toXML());
			
			if (packet instanceof BeanIQAdapter) {
				System.out.println("Packet is a BeanIQAdapter");
				
				//XMPPBean inBean = ((BeanIQAdapter) packet).getBean();
				
				System.out.println("Assuming an AddListEntryRequest was sent…");
				ListEntry entry = new ListEntry("abc", "titel", "mein erster listeneintrag", 3742587L);
				AddListEntryResponse out = new AddListEntryResponse(entry);
				
				getAgent().getConnection().sendPacket(new BeanIQAdapter(out));
			}
		}
		
	}

}