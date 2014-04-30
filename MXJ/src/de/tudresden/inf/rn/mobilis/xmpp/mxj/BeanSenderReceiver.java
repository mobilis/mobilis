/*******************************************************************************
 * Copyright (C) 2012 Technische Universität Dresden
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
package de.tudresden.inf.rn.mobilis.xmpp.mxj;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.provider.ProviderManager;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

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

	private int timeout = 10000; 
	private XMPPConnection connection;
	private B beanOut;
	private PacketCollector beanCollector;
	private ResultBeanType resultBeanPrototype;
	private int retryCount = 0;
	private int maxRetries = 0;

	public BeanSenderReceiver(XMPPConnection connection) {
		this.connection = connection;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public XMPPBean exchange(B bean, ResultBeanType resultBeanPrototype, int retries) {
		synchronized (this) {
			this.maxRetries = retries;
			this.beanOut = bean;
			this.resultBeanPrototype = resultBeanPrototype;
			try {
				// add IQ provider if necessary
				if (ProviderManager.getInstance().getIQProvider(resultBeanPrototype.getChildElement(), resultBeanPrototype.getNamespace()) == null) {
					(new BeanProviderAdapter(resultBeanPrototype.getClass().newInstance())).addToProviderManager();
				}
				if (ProviderManager.getInstance().getIQProvider(bean.getChildElement(), bean.getNamespace()) == null) {
					(new BeanProviderAdapter(bean.getClass().newInstance())).addToProviderManager();
				} 
			} catch (InstantiationException | IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // catch response bean and error bean
			this.beanCollector = connection.createPacketCollector(
					new OrFilter(new BeanFilterAdapter(resultBeanPrototype), new BeanFilterAdapter(bean)));
			return sendAndWaitForResult(bean);
		}
	}
	
	@SuppressWarnings("unchecked")
	private XMPPBean sendAndWaitForResult(B bean) {
		if (bean != null) {
			this.connection.sendPacket(new BeanIQAdapter(bean));
		}
		while(true) {
			BeanIQAdapter adapter = (BeanIQAdapter)(beanCollector.nextResult(timeout));
			XMPPBean resultBean = null;
			if (adapter != null) {
				if (adapter.getBean().getNamespace().equals(resultBeanPrototype.getNamespace())) {
					resultBean = (ResultBeanType)(adapter.getBean());
				} else {
					resultBean = (B)(adapter.getBean());
				}
			}
			if (adapter == null || resultBean == null) {
				if (retryCount >= maxRetries) {
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

