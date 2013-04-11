package de.treasurehunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import de.treasurehunt.proxy.GetLocationRequest;
import de.treasurehunt.proxy.GetLocationResponse;
import de.treasurehunt.proxy.ITreasureHuntIncoming;
import de.treasurehunt.proxy.ITreasureHuntOutgoing;
import de.treasurehunt.proxy.IXMPPCallback;
import de.treasurehunt.proxy.Location;
import de.treasurehunt.proxy.PickUpTreasureRequest;
import de.treasurehunt.proxy.PickUpTreasureResponse;
import de.treasurehunt.proxy.TreasureCollected;
import de.treasurehunt.proxy.TreasureHuntProxy;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.PingBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanHelper;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;

public class TreasureHunt extends MobilisService {

	private TreasureHuntProxy _proxy;
	private Map< String, IXMPPCallback< ? extends XMPPBean >> _waitingCallbacks 
		= new HashMap< String, IXMPPCallback< ? extends XMPPBean > >();
	
	// namespace, childelement, xmppbean
	private DoubleKeyMap< String, String, XMPPBean > _beanPrototypes
		= new DoubleKeyMap< String, String, XMPPBean >( false );
	

	public TreasureHunt() {
		_proxy = new TreasureHuntProxy( TreasureHuntOutgoingStub );
	}
	
	@Override
	public void startup() throws Exception {
		super.startup();
		
		startTreasureCollectedTimer();
		startGetLocationTimer();
	}
	
	private void startTreasureCollectedTimer(){
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("TreasureCollectedTimer");
				_proxy.TreasureCollected( "consoleclient@xhunt/JavaClient", "player1", new Location( 51033880, 13783272 ), 500 );
			}
		};
		
		Timer timer = new Timer();
		timer.schedule( task, 10000 );
	}
	
	private void startGetLocationTimer(){
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("GetLocationTimer");
				_proxy.GetLocation( "consoleclient@xhunt/JavaClient", new IXMPPCallback< GetLocationResponse >() {
					
					@Override
					public void invoke( GetLocationResponse xmppBean ) {
						System.out.println("GetLocationResponse in callback received: " + xmppBean.toXML());
					}
				} );
			}
		};
		
		Timer timer = new Timer();
		timer.schedule( task, 15000 );
	}

	private ITreasureHuntOutgoing TreasureHuntOutgoingStub = new ITreasureHuntOutgoing() {

		@Override
		public void TreasureCollected( TreasureCollected out ) {
			// Send XMPPBean TreasureCollected using the BeanIQAdapter and the
			// Servers XMPP connection
			getAgent().getConnection().sendPacket( new BeanIQAdapter( out ) );
		}

		@Override
		public void PickUpTreasure( PickUpTreasureResponse out ) {
			// Send XMPPBean PickUpTreasureResponse using the BeanIQAdapter and
			// the Servers XMPP connection
			getAgent().getConnection().sendPacket( new BeanIQAdapter( out ) );
		}

		@Override
		public void GetLocation( GetLocationRequest out,
				IXMPPCallback< GetLocationResponse > callback ) {
			// Send XMPPBean GetLocationRequest using the BeanIQAdapter and the
			// Servers XMPP connection
			// Store callback in a map to fire it on clients response
			_waitingCallbacks.put( out.getId(), callback );
			getAgent().getConnection().sendPacket( new BeanIQAdapter( out ) );
		}
	};

	@Override
	protected void registerPacketListener() {
		// Register all XMPPBeans
		
		_beanPrototypes.put( GetLocationRequest.NAMESPACE, GetLocationRequest.CHILD_ELEMENT, new GetLocationRequest() );
		_beanPrototypes.put( GetLocationResponse.NAMESPACE, GetLocationResponse.CHILD_ELEMENT, new GetLocationResponse() );
		_beanPrototypes.put( PickUpTreasureRequest.NAMESPACE, PickUpTreasureRequest.CHILD_ELEMENT, new PickUpTreasureRequest() );
		_beanPrototypes.put( PickUpTreasureResponse.NAMESPACE, PickUpTreasureResponse.CHILD_ELEMENT, new PickUpTreasureResponse() );
		_beanPrototypes.put( TreasureCollected.NAMESPACE, TreasureCollected.CHILD_ELEMENT, new TreasureCollected() );
		_beanPrototypes.put( PingBean.NAMESPACE, PingBean.CHILD_ELEMENT, new PingBean() );
		
		
		for ( XMPPBean prototype : _beanPrototypes.getListOfAllValues() ) {
			( new BeanProviderAdapter( 
					new ProxyBean( prototype.getNamespace(), prototype.getChildElement() ) ) ).addToProviderManager();
		}

		IQListener iqListener = new IQListener();
		PacketTypeFilter locFil = new PacketTypeFilter( IQ.class );
		getAgent().getConnection().addPacketListener( iqListener, locFil );
	}

	private class IQListener implements PacketListener {

		@Override
		public void processPacket( Packet packet ) {
			System.out.println("Incoming packet: " + packet.toXML());
			System.out.println("instanceOf: " + packet.getClass().toString());
			System.out.println("is BeanIQAdapter: " + (packet instanceof BeanIQAdapter));
			
			
			if ( packet instanceof BeanIQAdapter ) {
				System.out.println("is of type BeanIQAdapter");
				XMPPBean inBean = ( (BeanIQAdapter)packet ).getBean();
				System.out.println("XMPPBean: " + inBean.toXML());
				System.out.println("instanceOf: " + inBean.getClass().toString());

				if( inBean instanceof ProxyBean ){
					ProxyBean proxyBean = (ProxyBean)inBean;
					
					if( proxyBean.isTypeOf( PickUpTreasureRequest.NAMESPACE, PickUpTreasureRequest.CHILD_ELEMENT ) ){
						System.out.println("PickUpTreasureRequest detected: " + inBean.toXML());
						
						// Forward incoming Bean to ITreasureHuntIncoming and
						// receive response
						PickUpTreasureResponse response = (PickUpTreasureResponse)TreasureHuntIncomingHandler
								.onPickUpTreasure( (PickUpTreasureRequest)proxyBean.parsePayload( new PickUpTreasureRequest() ) );
	
						// Send response
						_proxy.PickUpTreasure( proxyBean.getFrom(),
								response.getTreasureValue() );
					} else if ( proxyBean.isTypeOf( GetLocationResponse.NAMESPACE, GetLocationResponse.CHILD_ELEMENT ) ) {
						// Forward incoming Bean to ITreasureHuntIncoming
						TreasureHuntIncomingHandler.onGetLocation( (GetLocationResponse)proxyBean.parsePayload( new GetLocationResponse() ) );
					} else if ( proxyBean.isTypeOf( GetLocationRequest.NAMESPACE, GetLocationRequest.CHILD_ELEMENT )
							&& inBean.getType() == XMPPBean.TYPE_ERROR ) {
						// Forward incoming Bean to ITreasureHuntIncoming
						TreasureHuntIncomingHandler.onGetLocationError( (GetLocationRequest)proxyBean.parsePayload( new GetLocationRequest() ) );
					} else if( proxyBean.isTypeOf( PingBean.NAMESPACE, PingBean.CHILD_ELEMENT ) ){
						System.out.println("PingBean arrived");
						
						PingBean pingBean = new PingBean( System.currentTimeMillis(), "Hello TreasureHunt Version 1" );
						pingBean.setTo( inBean.getFrom() );
						pingBean.setId( inBean.getId() );
						pingBean.setType( XMPPBean.TYPE_RESULT );
						
						getAgent().getConnection().sendPacket( new BeanIQAdapter(pingBean) );
					} else {
						getAgent().getConnection().sendPacket(
								new BeanIQAdapter( BeanHelper.CreateErrorBean( inBean, "wait",
										"unexpected-request", "This request is not supported" ) ) );
					}
				}
			}
		}

	}

	private ITreasureHuntIncoming TreasureHuntIncomingHandler = new ITreasureHuntIncoming() {

		@Override
		public XMPPBean onPickUpTreasure( PickUpTreasureRequest in ) {
			// Logic for picking up treasures here
			return new PickUpTreasureResponse( 500 );
		}

		@Override
		public void onGetLocation( GetLocationResponse in ) {
			IXMPPCallback< GetLocationResponse > callback 
				= (IXMPPCallback< GetLocationResponse >)_waitingCallbacks.get( in.getId() );

			if ( null != callback )
				callback.invoke( in );
		}

		@Override
		public void onGetLocationError( GetLocationRequest in ) {
			// Errorhandling here...
			_waitingCallbacks.remove( in.getId() );
		}
	};
}
