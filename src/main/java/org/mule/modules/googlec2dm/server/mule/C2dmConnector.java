package org.mule.modules.googlec2dm.server.mule;

import org.mule.modules.googlec2dm.server.PushService;
import org.mule.modules.googlec2dm.server.impl.PushServiceImpl;
import org.mule.modules.googlec2dm.server.messager.C2DMMessager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C2dmConnector {

	private static final Logger log = LoggerFactory.getLogger(C2dmConnector.class);

	protected PushService ps;
	
	/**
	 * Property source
	 */
	protected String source = null;
	public void setSource(String source) {
		log.debug("source injected");
		this.source = source;
	}

	/**
	 * Property username
	 */
	protected String username = null;
	public void setUsername(String username) {
		log.debug("username injected");
		this.username = username;
	}

	/**
	 * Property password
	 */
	protected String password = null;
	public void setPassword(String password) {
		log.debug("password injected");
		this.password = password;
	}
	
	public C2dmConnector() {
		
	}

	protected synchronized void initPushService() {
		log.info("...initiating PushService");
		C2DMMessager m = new C2DMMessager(
			username,
			password,
			"https://www.google.com/accounts/ClientLogin",
			"https://android.apis.google.com/c2dm/send",
			source);
			
		ps = new PushServiceImpl(m);
		log.info("PushService initiated");
	}
	
	public PushService getPushService() {
		if (ps == null) {
			initPushService();
		}
		return ps;
	}

	public void pushMessage(final String registrationId, final String subject, final String message) {
		log.info("Push subject: " + subject + " , message: " + message + ", to registration id: " + registrationId);
		ps.push(registrationId, subject, message);
	}
}