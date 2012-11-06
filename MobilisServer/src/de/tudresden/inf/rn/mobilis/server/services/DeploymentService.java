package de.tudresden.inf.rn.mobilis.server.services;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainer;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainerState;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.InstallServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.RegisterServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.FileHelper;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.FileUploadInformation;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.deployment.PrepareServiceUploadBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.deployment.ServiceUploadConclusionBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanHelper;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;

/**
 * The Class DeploymentService for uploading new mobilis services as jar files.
 */
public class DeploymentService extends MobilisService {

	// private DateFormat _dateFormatter = null;
	/** The manager for file transfers. */
	private FileTransferManager _fileTransferManager = null;

	/** The default folder for uploaded jar files. */
	private String _uploadServiceFolder = "service";

	/**
	 * The list with expected jar files (a PrepareServiceUploadBean is required
	 * first).
	 */
	private Map< String, FileUploadInformation > _expectedUploads = Collections
			.synchronizedMap( new HashMap< String, FileUploadInformation >() );

	// public void log( String str ) {
	// System.out.println( "[" + _dateFormatter.format(
	// System.currentTimeMillis() ) + "] " + str );
	// }

	/**
	 * Creates a new incoming file. If a file of the same name already exists, a
	 * timestamp will be added at the end of the filename.
	 * 
	 * @param fileName
	 *            the file name for the incoming file
	 * @return the file which was stored
	 */
	private File createNewIncomingFile( String fileName ) {
		File inFile = new File( _uploadServiceFolder, fileName );

		// If file already exists, create a new file with timestamp
		if ( inFile.exists() )
			inFile = new File( _uploadServiceFolder, createNewFileName( fileName ) );

		return inFile;
	}

	/**
	 * Creates a new file name. A timestamp will be added to filename if it
	 * already exist.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return the new filename
	 */
	private String createNewFileName( String fileName ) {
		StringBuilder sb = new StringBuilder();

		int pointIndex = fileName.lastIndexOf( "." );
		int strLength = fileName.length();

		sb.append( fileName.subSequence( 0, pointIndex ) ).append( "_" )
				.append( System.currentTimeMillis() )
				.append( fileName.substring( pointIndex, strLength ) );

		return sb.toString();
	}

	/**
	 * Inits the file transfer manager.
	 */
	private void initFileTransferManager() {
		_fileTransferManager = new FileTransferManager( getAgent().getConnection() );
		FileTransferNegotiator.setServiceEnabled( getAgent().getConnection(), true );

		_fileTransferManager.addFileTransferListener( new FileTransferListener() {
			public void fileTransferRequest(final FileTransferRequest request ) {
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						String message = "";
						boolean transmissionSuccessful = false;
						File incomingFile = createNewIncomingFile( request.getFileName() );
						
						MobilisManager.getLogger().log(
								Level.INFO,
								String.format( "Incoming FileTransfer from: %s with filename: %s",
										request.getRequestor(), request.getFileName() ) );
						
						// log( "file expected: requestor="
						// + _expectedUploads.containsKey( request.getRequestor() ) +
						// " filename="
						// + _expectedUploads.get( request.getRequestor() ) );
						
						// Only if 'preparefile' bean was sent, requestor can upload the
						// service
						FileUploadInformation inf = _expectedUploads.get( request.getRequestor() );
						if ( _expectedUploads.containsKey( request.getRequestor() )
								&& request.getFileName().equals(
										inf.fileName )
								&& null != incomingFile ) {
							// Accept Filetransfer
							try {
								if ( request.getFileSize() > 0 ) {
									IncomingFileTransfer transfer = request.accept();
									InputStream recieveFileInputStream = transfer.recieveFile();
									FileHelper.createFileFromInputStream(recieveFileInputStream, incomingFile.getAbsolutePath());
									
		//							if (transfer.getStatus().equals(FileTransfer.Status.complete)) {
										transmissionSuccessful = true;
										message = String.format( "Successful FileTransfer of file: %s",
												incomingFile.getName() );
										MobilisManager.getLogger().log(
												Level.INFO,
												message );
		//							} else {
		//								message = String.format( "FileTransfer of file: %s failed: ",
		//										incomingFile.getName()); 
		//							}
		
								}
							} catch ( XMPPException e ) {
								transmissionSuccessful = false;
								message = String.format( "FileTransfer of file: %s failed: ",
										incomingFile.getName(), e.getMessage() );
							}
						} else {
							message = "File was not expected.";
		
							request.reject();
						}
		
						if ( transmissionSuccessful ) {
							synchronized ( _expectedUploads ) {
								_expectedUploads.remove( request.getRequestor() );
							}
		
							// Add a new uploaded service as a pending service which is
							// waiting for installation
							ServiceContainer serviceContainer = new ServiceContainer( incomingFile );
							MobilisManager.getInstance().addPendingService(
									serviceContainer );
							
							if (inf.autoDeploy) {
								try {
									// install
									serviceContainer.install();
									message += "\n***" + serviceContainer.getServiceNamespace() + " version " + serviceContainer.getServiceVersion() + "***";
									if ( serviceContainer.getContainerState() == ServiceContainerState.INSTALLED ) {
										MobilisManager.getInstance().addServiceContainer( serviceContainer );

										MobilisManager.getInstance().removePendingServiceByFileName(
												incomingFile.getName() );
										message += "\nService installation successful.";

									} else {
										message += "\nInstallation failed: Couldn't move service from pending to regular.";

										MobilisManager.getLogger().log( Level.WARNING, message );
									}

									
									// configure
									DoubleKeyMap< String, String, Object > configuration = new DoubleKeyMap< String, String, Object >(
											false );
									
									// use data from admins agent / default if nothing was set
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "name",
											serviceContainer.getServiceName() );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "mode",
											inf.singleMode?"single":"multi" );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "port",
											getAgent().getSettingString( "port" ) );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "username",
											getAgent().getSettingString( "username" ) );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "host",
											getAgent().getSettingString( "host" ) );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "start",
											"ondemand" );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "description",
											serviceContainer.getServiceName() );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "service",
												getAgent().getSettingString( "service" ) );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "resource",
											serviceContainer.getServiceName() );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "type",
											"de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent" );
									
									configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "password",
												getAgent().getSettingString( "password" ) );
									serviceContainer.configure(configuration);
									message += "\nService configuration successful.";
									
									// register
									serviceContainer.register();
									message += "\nService registration successful.";
								} catch (InstallServiceException e) {
									message += "\n" + e.getMessage();
									e.printStackTrace();
								} catch (RegisterServiceException e) {
									message += "\n" + e.getMessage();
									e.printStackTrace();
								}
							}
						} else if ( message.equals("") || message == null ) {
							message = "Unknown failure while uploading file";
						}
		
						if ( null != message && !message.equals("") ) {
							MobilisManager.getLogger().log( Level.INFO, message );
						}
		
						sendServiceUploadConclusionBeanSET( request.getRequestor(), transmissionSuccessful,
								incomingFile.getName(), message );
					}
				});
				t.start();
			}
		} );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.services.MobilisService#
	 * registerPacketListener()
	 */
	@Override
	protected void registerPacketListener() {
		XMPPBean prepareServiceUploadBean = new PrepareServiceUploadBean();
		XMPPBean serviceUploadConclusionBean = new ServiceUploadConclusionBean();

		( new BeanProviderAdapter( prepareServiceUploadBean ) ).addToProviderManager();
		( new BeanProviderAdapter( serviceUploadConclusionBean ) ).addToProviderManager();

		IQListener iqListener = new IQListener();
		PacketTypeFilter locFil = new PacketTypeFilter( IQ.class );
		getAgent().getConnection().addPacketListener( iqListener, locFil );
	}

	/**
	 * Sends a service upload conclusion bean.
	 * 
	 * @param requestor
	 *            the requestor which uploaded the jar file
	 * @param transmissionSuccessful
	 *            true if transmission was successful
	 * @param filename
	 *            the name of the stored file
	 * @param message
	 *            an optional message
	 */
	private void sendServiceUploadConclusionBeanSET( String requestor,
			boolean transmissionSuccessful, String filename, String message ) {
		ServiceUploadConclusionBean bean = new ServiceUploadConclusionBean( transmissionSuccessful,
				filename, message );
		bean.setTo( requestor );
		bean.setType( XMPPBean.TYPE_SET );

		getAgent().getConnection().sendPacket( new BeanIQAdapter( bean ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tudresden.inf.rn.mobilis.server.services.MobilisService#shutdown()
	 */
	@Override
	public void shutdown() throws Exception {
		_fileTransferManager = null;

		super.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tudresden.inf.rn.mobilis.server.services.MobilisService#startup(de
	 * .tudresden.inf.rn.mobilis.server.agents.MobilisAgent)
	 */
	@Override
	public void startup( MobilisAgent agent ) throws Exception {
		super.startup( agent );

		// _dateFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );

		checkServiceUploadFolder();
		initFileTransferManager();
	};

	/**
	 * Checks service upload folder and create it if necessary.
	 */
	private void checkServiceUploadFolder() {
		File uploadFolder = new File( _uploadServiceFolder );

		if ( !uploadFolder.exists() )
			uploadFolder.mkdir();
	}

	/**
	 * The listener interface for receiving IQ events. The class that is
	 * interested in processing a IQ event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addIQListener<code> method. When
	 * the IQ event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see IQEvent
	 */
	private class IQListener implements PacketListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jivesoftware.smack.PacketListener#processPacket(org.jivesoftware
		 * .smack.packet.Packet)
		 */
		@Override
		public void processPacket( Packet packet ) {
			if ( packet instanceof BeanIQAdapter ) {
				XMPPBean inBean = ( (BeanIQAdapter)packet ).getBean();

				if ( inBean instanceof PrepareServiceUploadBean ) {
					handlePrepareServiceUploadBean( (PrepareServiceUploadBean)inBean );
				} else if ( inBean instanceof ServiceUploadConclusionBean
						&& inBean.getType() == XMPPBean.TYPE_RESULT ) {
					// Do nothing, just ack
				} else {
					handleUnknownBean( inBean );
				}
			}
		}

		/**
		 * Handle unknown bean.
		 * 
		 * @param inBean
		 *            the unknown bean
		 */
		private void handleUnknownBean( XMPPBean inBean ) {
			getAgent().getConnection().sendPacket(
					new BeanIQAdapter( BeanHelper.CreateErrorBean( inBean, "wait",
							"unexpected-request", "This request is not supported" ) ) );
		}

		/**
		 * Handle prepare service upload bean.
		 * 
		 * @param inBean
		 *            the bean
		 */
		private void handlePrepareServiceUploadBean( PrepareServiceUploadBean inBean ) {
			XMPPBean outBean = null;

			// if no name was set, respond an error
			if ( null == inBean.Filename || inBean.Filename.length() < 1 ) {
				outBean = BeanHelper.CreateErrorBean( inBean, "modify", "not-acceptable",
						"File name is null or empty." );
			} else {
				// store information in expected upload collection
				synchronized ( _expectedUploads ) {
					_expectedUploads.put( inBean.getFrom(), new FileUploadInformation(inBean.Filename, inBean.autoDeploy, inBean.singleMode) );
				}

				outBean = BeanHelper
						.CreateResultBean( inBean, new PrepareServiceUploadBean( true ) );
			}

			getAgent().getConnection().sendPacket( new BeanIQAdapter( outBean ) );
		}

	}

}
