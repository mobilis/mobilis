package de.tudresden.inf.rn.mobilis.xmpp.server;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanFilterAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;

/**
 * Use this class for simple request/result IQ handling. Code adapted (and hopefully improved)
 * from Mobilis server's BeanExchanger class.
 * 
 * @author bendel
 *
 * @param <B>
 * @param <ResultBeanType>
 */
public class BeanSenderReceiver<B extends XMPPBean, ResultBeanType extends XMPPBean> {

	private int timeout = 2000; 
	private Connection connection;
	private B beanOut;
	private PacketCollector beanCollector;
	private int retryCount = 0;
	private int maxRetries = 0;

	public BeanSenderReceiver(Connection connection) {
		this.connection = connection;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public ResultBeanType exchange(B bean, ResultBeanType resultBeanPrototype, int retries) {
		synchronized (this) {
			this.maxRetries = retries;
			BeanSenderReceiver<B, ResultBeanType> owner = BeanSenderReceiver.this;
			this.beanOut = bean;
			this.beanCollector = owner.connection.createPacketCollector(
					new BeanFilterAdapter(resultBeanPrototype));
			return sendAndWaitForResult(bean);
		}
	}
	
	@SuppressWarnings("unchecked")
	private ResultBeanType sendAndWaitForResult(B bean) {
		this.connection.sendPacket(new BeanIQAdapter(bean));
		while(true) {
			BeanIQAdapter adapter = (BeanIQAdapter)(beanCollector.nextResult(timeout));
			ResultBeanType resultBean = null;
			if (adapter != null) {
				resultBean = (ResultBeanType)(adapter.getBean());
			}
			if (adapter == null || resultBean == null) {
				if (retryCount > maxRetries) {
					return null;
				} else {
					retryCount++;
					sendAndWaitForResult(bean);
				}
			} else if (resultBean.getId().equals(beanOut.getId())) {
				return resultBean;
			}
		}
	}
	
}
