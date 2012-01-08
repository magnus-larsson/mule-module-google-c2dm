package org.mule.modules.googlec2dm.server;

public interface PushService {

	/**
	 * Sends a push notification/message from third-party application server to an
	 * Android application.
	 * 
	 * A registration ID is issued by Google C2DM servers to the Android
	 * application that allows it to receive messages. Once the application has
	 * the registration ID, it sends it to the third-party application server,
	 * which uses it to identify each device that has registered to receive
	 * messages for a given application. In other words, a registration ID is
	 * tied to a particular application running on a particular device.
	 * 
	 * @param registrationId the C2DM registration ID issued by Google
	 * @param subject the notification/message subject
	 * @param message the message
	 */
	public void push(final String registrationId, final String subject,	final String message);

}
