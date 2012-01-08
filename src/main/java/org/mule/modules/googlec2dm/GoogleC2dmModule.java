/**
 * Mule Development Kit
 * Copyright 2010-2011 (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.mule.modules.googlec2dm;

import org.mule.api.annotations.Module;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Processor;
import org.mule.modules.googlec2dm.server.mule.C2dmConnector;

/**
 * Generic module
 *
 * @author MuleSoft, Inc.
 */
@Module(name="googlec2dm", schemaVersion="1.0.0-SNAPSHOT")
public class GoogleC2dmModule
{
	C2dmConnector connector = null;

	/**
     * Configurable
     */
    @Configurable
    private String source;

    /**
     * Set property
     *
     * @param source The c2dm source
     */
    public void setSource(String source)
    {
    	System.err.println("*** SET SOURCE");
        this.source = source;
    }

	/**
     * Configurable
     */
    @Configurable
    private String username;

    /**
     * Set property
     *
     * @param username The c2dm username
     */
    public void setUsername(String username)
    {
    	System.err.println("*** SET username");
        this.username = username;
    }

	/**
     * Configurable
     */
    @Configurable
    private String password;

    /**
     * Set property
     *
     * @param password The c2dm password
     */
    public void setPassword(String password)
    {
    	System.err.println("*** SET password");
        this.password = password;
    }

	/**
     * Configurable
     */
    @Configurable
    private boolean test;

    /**
     * Set property
     *
     * @param test True means that c2dm will not be called
     */
    public void setTest(boolean test)
    {
    	System.err.println("*** SET TEST");
        this.test = test;
    }

    public GoogleC2dmModule () {
    	System.err.println("*** GoogleC2dmModule instance created, b1");    	
    }

    protected C2dmConnector getConnector() {
    	if (connector == null) {
        	System.err.println("*** Creates an C2dmConnector instance");    	
	    	connector = new C2dmConnector();
	    	connector.setSource(source);
	    	connector.setUsername(username);
	    	connector.setPassword(password);
    	}
    	return connector;
    }
    
    /**
     * Custom processor
     *
     * {@sample.xml ../../../doc/GoogleC2dm-connector.xml.sample googlec2dm:my-processor}
     *
     * @param registrationId The id of the Android device that shall receive the notification
     * @param subject The subject
     * @param message The message
     * @return Some string
     */
    @Processor
    public String push(String registrationId, String subject, String message)
    {
        if (test) {
        	System.err.println("*** PUSH TEST");
        	return message;
        }

    	System.err.println("*** PUSH");
      	getConnector().getPushService().push(registrationId, subject, message);
        return message;
    }
}
