package org.mule.modules.googlec2dm.server.mule;

import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C2dmPush implements MuleContextAware, Callable {

	private static final Logger log = LoggerFactory.getLogger(C2dmPush.class);

	/**
	 * Property muleContext 
	 */
	protected MuleContext muleContext = null;
	public void setMuleContext(MuleContext muleContext) {
		log.debug("MuleContext injected");
		this.muleContext = muleContext;
	}

	/**
	 * Property c2dmConnector
	 */
	protected C2dmConnector c2dmConnector = null;
	public void setC2dmConnector(C2dmConnector c2dmConnector) {
		log.debug("C2dmConnector injected");
		this.c2dmConnector = c2dmConnector;
	}

	/**
	 * Property registrationIds
	 */
	protected String registrationIds = null;
	public void setRegistrationIds(String registrationIds) {
		log.debug("RegistrationIds injected");
		this.registrationIds = registrationIds;
	}

	/**
	 * Property subject
	 */
	protected String subject = null;
	public void setSubject(String subject) {
		log.debug("Subject injected");
		this.subject = subject;
	}

	/**
	 * Property message
	 */
	protected String message = null;
	public void setMessage(String message) {
		log.debug("Message injected");
		this.message = message;
	}

	public C2dmPush() {
	}

	public Object onCall(MuleEventContext eventContext) throws Exception {
		pushMessage(eventContext.getMessage());
		return eventContext.getMessage();
	}

	@SuppressWarnings("unchecked")
	protected void pushMessage(MuleMessage muleMessage) {
		
		String       evaluatedSubject         = evaluateExpression(subject, muleMessage).toString();
		String       evaluatedMessage         = evaluateExpression(message, muleMessage).toString();
		List<String> evaluatedRegistrationIds = (List<String>)evaluateExpression(registrationIds, muleMessage);
		
		log.info("Push subject: [" + evaluatedSubject + "] , message: [" + evaluatedMessage + "], to #registration ids: " + ((evaluatedRegistrationIds == null) ? 0 : evaluatedRegistrationIds.size()));
		
		if (evaluatedRegistrationIds != null) {
			for (String regId : evaluatedRegistrationIds) {
				c2dmConnector.getPushService().push(regId, evaluatedSubject, evaluatedMessage);
			}
		}
	}

	protected Object evaluateExpression(String expression, MuleMessage message) {
		Object eval; 
		try {
			if(isValidExpression(expression)) {
		    	String before = expression;
		    	eval = muleContext.getExpressionManager().evaluate(expression, message);

		    	if (eval == null) eval = "UNKNOWN";

		    	if (log.isDebugEnabled()) {
		    		log.debug("Evaluated: " + before + " ==> " + expression);
		    	}
		    } else {
		    	// Ok, so it is not an expression, then just return it as is...
		    	eval = expression;
		    }
		} catch (Throwable ex) {
			String errMsg = "Faild to evaluate expression: " + expression;
			log.warn(errMsg, ex);
			eval = errMsg + ", " + ex;
		}
		return eval;
	}

	private boolean isValidExpression(String expression) {
		try {
			return muleContext.getExpressionManager().isValidExpression(expression);
		} catch (Throwable ex) {
			ex.printStackTrace();
			return false;
		}
	}
}