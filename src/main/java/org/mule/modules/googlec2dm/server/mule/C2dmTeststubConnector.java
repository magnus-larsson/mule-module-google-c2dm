package org.mule.modules.googlec2dm.server.mule;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.modules.googlec2dm.server.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C2dmTeststubConnector extends C2dmConnector implements MuleContextAware {

	private static final Logger log = LoggerFactory.getLogger(C2dmTeststubConnector.class);

	/**
	 * Property muleContext 
	 */
	protected MuleContext muleContext = null;
	public void setMuleContext(MuleContext muleContext) {
		log.debug("MuleContext injected");
		this.muleContext = muleContext;
	}

	/**
	 * Property testEndpoint
	 */
	protected String testEndpoint = null;
	public void setTestEndpoint(String testEndpoint) {
		log.debug("TestEndpoint injected");
		this.testEndpoint = testEndpoint;
	}

	public C2dmTeststubConnector() {
	}

	protected synchronized void initPushService() {
		log.info("...initiating TestStub PushService");

		ps = new PushService() {
			public void push(String registrationId, String subject, String message) {
				log.info("Simulate push of subject [{}] and message [{}] to reg-id [{}].", new Object[] {subject, message, registrationId});
				
				if (testEndpoint != null) {
					log.info("Sends a notification message to endpoint: " + testEndpoint);
					try {
						Map<String, Object> properties = new HashMap<String, Object>();
						properties.put("registrationId", registrationId);
						properties.put("subject",        subject);
						properties.put("message",        message);
						muleContext.getClient().dispatch(testEndpoint, "Simulated push to c2dm", properties);
						
					} catch (Throwable ex) {
						log.warn("Failed to send a notification message to endpoint: " + testEndpoint + ", reason: " + ex.getMessage());
						log.debug("Stacktrace: ", ex);
					}
				}
			}
		};
		log.info("PushService TestStub initiated");
	}
}