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

import org.apache.log4j.NDC;
import org.ohmage.domain.User;
import org.ohmage.request.AwRequest;
import org.ohmage.request.CampaignReadAwRequest;
import org.ohmage.request.InputKeys;
import org.ohmage.util.CookieUtils;


/**
 * @author selsky
 */
public class CampaignReadAwRequestCreator implements AwRequestCreator {

	public CampaignReadAwRequestCreator() {
		
	}
	
	public AwRequest createFrom(HttpServletRequest httpRequest) {
		// required
		String client = httpRequest.getParameter("client");
		String outputFormat = httpRequest.getParameter("output_format");
		
		String token;
		try {
			token = CookieUtils.getCookieValue(httpRequest.getCookies(), InputKeys.AUTH_TOKEN).get(0);
		}
		catch(IndexOutOfBoundsException e) {
			token = httpRequest.getParameter(InputKeys.AUTH_TOKEN);
		}
		
		// optional
		String userName = httpRequest.getParameter("user");
		String password = httpRequest.getParameter("password");
		String campaignUrnListAsString = httpRequest.getParameter("campaign_urn_list");
		String startDate = httpRequest.getParameter("start_date");
		String endDate = httpRequest.getParameter("end_date");
		String privacyState = httpRequest.getParameter("privacy_state");
		String runningState = httpRequest.getParameter("running_state");
		String userRole = httpRequest.getParameter("user_role");
		String classUrnListAsString = httpRequest.getParameter("class_urn_list");
		 
		NDC.push("client=" + client); // push the client string into the Log4J NDC for the currently executing thread - this means that it
		                              // will be in every log message for the thread
		
		CampaignReadAwRequest awRequest = new CampaignReadAwRequest();
		awRequest.setClient(client);
		awRequest.setUserToken(token);
		awRequest.setOutputFormat(outputFormat);
		awRequest.setCampaignUrnListAsString(campaignUrnListAsString);
		awRequest.setStartDate(startDate);
		awRequest.setEndDate(endDate);
		awRequest.setPrivacyState(privacyState);
		awRequest.setRunningState(runningState);
		awRequest.setUserRole(userRole);
		awRequest.setClassUrnListAsString(classUrnListAsString);
		User user = new User();
	    user.setUserName(userName);
	    user.setPassword(password);
	    awRequest.setUser(user);
	    
	    
	    awRequest.addToValidate(InputKeys.CLASS_URN_LIST, classUrnListAsString, true);
	    awRequest.addToValidate(InputKeys.CAMPAIGN_URN_LIST, campaignUrnListAsString, true);
	    awRequest.addToValidate(InputKeys.START_DATE, startDate, true);
	    awRequest.addToValidate(InputKeys.END_DATE, endDate, true);
	    awRequest.addToValidate(InputKeys.OUTPUT_FORMAT, outputFormat, true);
	    awRequest.addToValidate(InputKeys.RUNNING_STATE, runningState, true);
	    awRequest.addToValidate(InputKeys.PRIVACY_STATE, privacyState, true);
				
		return awRequest;
	}
}