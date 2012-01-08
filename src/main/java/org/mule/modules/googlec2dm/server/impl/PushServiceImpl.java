package org.mule.modules.googlec2dm.server.impl;

import org.mule.modules.googlec2dm.server.PushService;
import org.mule.modules.googlec2dm.server.messager.C2DMMessager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushServiceImpl implements PushService {

	private static final Logger log = LoggerFactory.getLogger(PushServiceImpl.class);
	
	private C2DMMessager googleMessaging;
	
	public PushServiceImpl(C2DMMessager googleMessaging) {
		this.googleMessaging = googleMessaging;
	}
	
	public void push(String c2dmRegistrationId, String subject, String message) {
		
		log.debug("Push notification to client");
		
		if (c2dmRegistrationId == null) {
			throw new RuntimeException("The C2DM registration id cannot be null, it must be a valida registration id supplied by Google.");
		}
		
		/*
		 * Push through Google
		 */
		final String auth = this.googleMessaging.fetchGoogleAuthToken();
		this.googleMessaging.sendGooglePushNotification(auth, c2dmRegistrationId, subject, message, "", "");
	}
	
}
