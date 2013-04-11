package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.LogRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AppCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ParameterType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StartType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StopType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationCommand;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationConnection;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationSession;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanSenderReceiver;

public class ScriptRunner extends XMLScriptExecutor {
	
	private EmulationConnection emuConnection;
	private EmulationSession session;
	private Map<String, String> instanceSelection;
	private Map<String, InstanceGroupExecutorInfo> instanceGroupSelection;
	
	public ScriptRunner(EmulationConnection emuConn, EmulationSession session, Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection) {
		super();
		this.emuConnection = emuConn;
		this.session = session;
		this.instanceSelection = instanceSelection;
		this.instanceGroupSelection = instanceGroupSelection;
	}

	@Override
	public void executeStartCommand(InstanceType instance, StartType startCommand) {
		
		String parameters = "";
		
		if(startCommand.getParameters() != null) {
			ParameterType params = startCommand.getParameters();
			List<Serializable> list = params.getIntOrStringOrBoolean();
			if(list != null) {
				for(int i=0;i<list.size();i++) {
					Serializable param = list.get(i);
					if(param instanceof Integer) {
						Integer intParam = (Integer)param;
						System.out.println("Integer-Param: " + intParam.toString());
						parameters += intParam + " ";
					}
					if(param instanceof String) {
						String stringParam = (String)param;
						System.out.println("String-Param: " + stringParam);
						parameters += stringParam + " ";
					}
					if(param instanceof Boolean) {
						Boolean boolParam = (Boolean)param;
						System.out.println("Boolean-Param: " + boolParam.toString());
						parameters += boolParam + " ";
					}
					if(param instanceof Double) {
						Double doubleParam = (Double)param;
						System.out.println("Double-Param: " + doubleParam.toString());
						parameters += doubleParam + " ";
					}
				}
				// removes last " "
				int count = parameters.length();
				parameters = parameters.substring(0, count-1);
			}
		}
		
		StartRequest startReq = new StartRequest(instance.getAppNS(), instance.getInstanceId(), parameters);
		//String sendTo = deviceAssignment.get(startCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(startCommand.getInstance())) {
			sendTo = instanceSelection.get(startCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(startCommand.getInstance())) {
			InstanceGroupExecutorInfo instanceGroupExecutorInfo = instanceGroupSelection.get(startCommand.getInstance());
			List<String> selections = instanceGroupExecutorInfo.getExecutors();
			sendTo = selections.get(instance.getInstanceId() - instanceGroupExecutorInfo.getFirstInstanceId());
		}
		
		startReq.setTo(sendTo);
		
		System.out.println("StartCommand -> " + sendTo);
		BeanSenderReceiver<StartRequest, StartAck> bsr = new BeanSenderReceiver<StartRequest, StartAck>(emuConnection.getConnection());
		XMPPBean result = bsr.exchange(startReq, new StartAck(), 1);
		
		EmulationCommand emuCmd = new EmulationCommand(instance.getAppNS(), "StartCommand", sendTo);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send StartCommand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
				session.getStatus().addNotFinishedCommand(emuCmd);
			} else {
				System.out.println("Successfully sent StartCommand to " + sendTo);
				session.getStatus().addFinishedCommand(emuCmd);
			}
		} else {
			System.err.println("Couldn't send StartCommand to " + sendTo);
			session.getStatus().addNotFinishedCommand(emuCmd);
		}
		System.out.println();
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)startReq));
	}

	@Override
	public void executeStopCommand(final InstanceType instance, StopType stopCommand) {
		
		LogRequest logReq = new LogRequest(instance.getAppNS(), instance.getInstanceId());
		StopRequest stopReq = new StopRequest(instance.getAppNS(), instance.getInstanceId());
		//String sendTo = deviceAssignment.get(stopCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(stopCommand.getInstance())) {
			sendTo = instanceSelection.get(stopCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(stopCommand.getInstance())) {
			InstanceGroupExecutorInfo instanceGroupExecutorInfo = instanceGroupSelection.get(stopCommand.getInstance());
			List<String> selections = instanceGroupExecutorInfo.getExecutors();
			sendTo = selections.get(instance.getInstanceId() - instanceGroupExecutorInfo.getFirstInstanceId());
		}
		
		logReq.setTo(sendTo);
		stopReq.setTo(sendTo);
		
		final String finalSendTo = new String(sendTo);
		
		final FileTransferManager ftm = new FileTransferManager(emuConnection.getConnection());
		FileTransferNegotiator.setServiceEnabled( emuConnection.getConnection(), true );
//		InBandBytestreamManager.getByteStreamManager(emuConnection.getConnection()).setStanza(StanzaType.MESSAGE);
		
		final CountDownLatch logTransferReceivedLatch = new CountDownLatch(1);
		final CountDownLatch logTransferFinishedLatch = new CountDownLatch(1);
		
		
		FileTransferListener fileTransferListener = new FileTransferListener() {
			
			@Override
			public void fileTransferRequest(final FileTransferRequest request) {
				if (request.getRequestor().equals(finalSendTo) && request.getFileSize() > 0) {
					Thread logReceiver = new Thread(new Runnable() {
						
						@Override
						public void run() {
							logTransferReceivedLatch.countDown();
							try {
								String logPath = session.getSessionDir() + "/logs/_" + instance.getAppNS() + "_" + instance.getInstanceId() + "/";
								File logFolder = new File(logPath);
								logFolder.mkdirs();
								System.out.println("Log file transfer for instance " + instance.getAppNS() + "_" + instance.getInstanceId() + " started.");
								IncomingFileTransfer fileTransfer = request.accept();
//							InputStream fileInputStream = fileTransfer.recieveFile();
								fileTransfer.recieveFile(new File(logPath + request.getFileName()));
								while (!fileTransfer.isDone()) {
									System.out.println("File transfer status: " + fileTransfer.getStatus());
									System.out.println("File transfer progress: " + fileTransfer.getProgress());
									
									if (fileTransfer.getStatus().equals(Status.error)) {
										System.err.println("Error during file transfer: " + fileTransfer.getError().getMessage());
										fileTransfer.cancel();
									}
									
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
//							FileHelper.createFileFromInputStream(fileInputStream, logPath + request.getFileName());
								if (fileTransfer.getStatus().equals(Status.error)) {
									System.err.print("Error during file transfer: " + fileTransfer.getError().getMessage());
									if (fileTransfer.getError() != null) {
										System.out.println(fileTransfer.getError().toString());
									} else {
										System.out.println("unknown error.");
									}
									fileTransfer.cancel();
								} else if (fileTransfer.getStatus().equals(Status.complete)){
									System.out.println("File transfer complete.");
								} else {
									System.out.println("File transfer finished with status: " + fileTransfer.getStatus());
								}
								
//							try {
//								fileInputStream.close();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							} catch (XMPPException e) {
								System.err.println("Log file transfer for session " + session.getId() + " failed!");
								e.printStackTrace();
							}
							logTransferFinishedLatch.countDown();
						}
					});
					logReceiver.start();
				}
			}
		};
		ftm.addFileTransferListener(fileTransferListener);
		
		System.out.println("LogRequest -> " + sendTo);
		emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean) logReq));
		
		try {
			if (logTransferReceivedLatch.await(5000, TimeUnit.MILLISECONDS)) {
				logTransferFinishedLatch.await();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ftm.removeFileTransferListener(fileTransferListener);
		
		System.out.println("StopCommand -> " + sendTo);
		BeanSenderReceiver<StopRequest, StopAck> bsr = new BeanSenderReceiver<StopRequest, StopAck>(emuConnection.getConnection());
		XMPPBean result = bsr.exchange(stopReq, new StopAck(), 1);
		
		EmulationCommand emuCmd = new EmulationCommand(instance.getAppNS(), "StopCommand", sendTo);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send StopCommand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
				session.getStatus().addNotFinishedCommand(emuCmd);
			} else {
				System.out.println("Successfully sent StopCommand to " + sendTo);
				session.getStatus().addFinishedCommand(emuCmd);
			}
		} else {
			System.err.println("Couldn't send StopCommand to " + sendTo);
			session.getStatus().addNotFinishedCommand(emuCmd);
		}
		System.out.println();
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)stopReq));
	}

	@Override
	public void executeAppCommand(InstanceType instance, AppCommandType appCommand) {
		
		String methodName = appCommand.getMethodName();
		List<String> parameters = new ArrayList<String>();
		List<String> parameterTypes = new ArrayList<String>();
		Integer commandId = instance.getInstanceId();
		Integer instanceId = instance.getInstanceId();
		String appNamespace = instance.getAppNS();
		Boolean async = appCommand.isAsync();
		
		if(appCommand.getParameter() != null) {
			ParameterType params = appCommand.getParameter();
			List<Serializable> list = params.getIntOrStringOrBoolean();
			if(list != null) {
				for(int i=0;i<list.size();i++) {
					Serializable param = list.get(i);
					if(param instanceof Integer) {
						Integer intParam = (Integer)param;
						System.out.println("Integer-Param: " + intParam.toString());
						parameters.add(intParam.toString());
						parameterTypes.add("java.lang.Integer");
					}
					if(param instanceof String) {
						String stringParam = (String)param;
						System.out.println("String-Param: " + stringParam);
						parameters.add(stringParam);
						parameterTypes.add("java.lang.String");
					}
					if(param instanceof Boolean) {
						Boolean boolParam = (Boolean)param;
						System.out.println("Boolean-Param: " + boolParam.toString());
						parameters.add(boolParam.toString());
						parameterTypes.add("java.lang.Boolean");
					}
					if(param instanceof Double) {
						Double doubleParam = (Double)param;
						System.out.println("Double-Param: " + doubleParam.toString());
						parameters.add(doubleParam.toString());
						parameterTypes.add("java.lang.Double");
					}
				}
			}
		}
		
		CommandRequest commReq = new CommandRequest(methodName, parameters, parameterTypes, commandId, instanceId, appNamespace, async);
		//String sendTo = deviceAssignment.get(appCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(appCommand.getInstance())) {
			sendTo = instanceSelection.get(appCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(appCommand.getInstance())) {
			InstanceGroupExecutorInfo instanceGroupExecutorInfo = instanceGroupSelection.get(appCommand.getInstance());
			List<String> selections = instanceGroupExecutorInfo.getExecutors();
			sendTo = selections.get(instance.getInstanceId() - instanceGroupExecutorInfo.getFirstInstanceId());
		}
		
		commReq.setTo(sendTo);
		
		System.out.println("AppComand: method->" + methodName + ", to->" + sendTo);
		BeanSenderReceiver<CommandRequest, CommandAck> bsr = new BeanSenderReceiver<CommandRequest, CommandAck>(emuConnection.getConnection());
		bsr.setTimeout(40000);
		XMPPBean result = bsr.exchange(commReq, new CommandAck(), 1);
		
		EmulationCommand emuCmd = new EmulationCommand(appNamespace, methodName, sendTo);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send AppComand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
				session.getStatus().addNotFinishedCommand(emuCmd);
			} else {
				System.out.println("Successfully sent AppComand to " + sendTo);
				session.getStatus().addFinishedCommand(emuCmd);
			}
		} else {
			System.err.println("Couldn't send AppComand to " + sendTo);
			session.getStatus().addNotFinishedCommand(emuCmd);
		}
		System.out.println();
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)commReq));
		
	}

}
