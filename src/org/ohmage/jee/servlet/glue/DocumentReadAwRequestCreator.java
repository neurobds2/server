/*******************************************************************************
 * Copyright 2011 The Regents of the University of California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohmage.jee.servlet.glue;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.ohmage.request.AwRequest;
import org.ohmage.request.DocumentReadAwRequest;
import org.ohmage.request.InputKeys;
import org.ohmage.util.CookieUtils;

public class DocumentReadAwRequestCreator implements AwRequestCreator {
	private static Logger _logger = Logger.getLogger(DocumentReadAwRequestCreator.class);
	
	/**
	 * Default constructor.
	 */
	public DocumentReadAwRequestCreator() {
		// Do nothing.
	}
	
	/**
	 * Creates a request for document creation 
	 */
	@Override
	public AwRequest createFrom(HttpServletRequest httpRequest) {
		_logger.info("Creating request for document read.");
		
		try {
			String token;
			try {
				token = CookieUtils.getCookieValue(httpRequest.getCookies(), InputKeys.AUTH_TOKEN).get(0);
			}
			catch(IndexOutOfBoundsException e) {
				token = httpRequest.getParameter(InputKeys.AUTH_TOKEN);
			}
			
			DocumentReadAwRequest mRequest = new DocumentReadAwRequest(httpRequest.getParameter(InputKeys.DOCUMENT_PERSONAL_DOCUMENTS),
																	   httpRequest.getParameter(InputKeys.CAMPAIGN_URN_LIST),
																	   httpRequest.getParameter(InputKeys.CLASS_URN_LIST));
			mRequest.setUserToken(token);
			
			NDC.push("client=" + httpRequest.getParameter(InputKeys.CLIENT));
			
			return mRequest;
		}
		catch(IllegalArgumentException e) {
			_logger.error("Error creating document read request.", e);
			throw e;
		}
	}
}
