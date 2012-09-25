/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
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
package de.tudresden.inf.rn.mobilis.xmpp.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class BeanExchanger<B extends XMPPBean> {

	private static final int TIMEOUT = 2000; 
	private Connection connection;
	private B beanOut;
	private B beanIn;
	private PacketCollector beanCollector;

	static private ExecutorService threads = Executors.newSingleThreadExecutor();
	
	public BeanExchanger(Connection connection) {
		this.connection = connection;
	}
	
	public B exchange(B bean) {
		synchronized (this) {
			BeanExchanger<B> owner = BeanExchanger.this;
			this.beanOut = bean;
			this.beanIn  = null;
			this.beanCollector = owner.connection.createPacketCollector(
					new BeanFilterAdapter(bean));
			Thread t = new Thread(new BeanWaiter());
			t.setName("Mobilis BeanExchanger");
			BeanExchanger.threads.execute(t);
			this.connection.sendPacket(new BeanIQAdapter(bean));
			try {
				if (this.beanIn == null) this.wait();
			} catch (InterruptedException e) {}
			return this.beanIn;
		}
	}
	
	private class BeanWaiter implements Runnable {
		@SuppressWarnings("unchecked")
		@Override public void run() {
			BeanExchanger<B> owner = BeanExchanger.this;
			synchronized (BeanExchanger.this) {
				BeanIQAdapter adapter = (BeanIQAdapter)(owner.beanCollector.nextResult());
				owner.beanIn = (B)(adapter.getBean());
				owner.notify();
			}
		}
	}
	
}
