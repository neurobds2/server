/*******************************************************************************
 * Copyright 2012 The Regents of the University of California
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
package org.ohmage.domain.campaign.response;

import org.ohmage.domain.campaign.PromptResponse;
import org.ohmage.domain.campaign.prompt.SingleChoicePrompt;
import org.ohmage.exception.DomainException;

/**
 * Creates a single choice prompt response.
 * 
 * @author John Jenkins
 */
public class SingleChoicePromptResponse extends PromptResponse {
	/**
	 * Creates a single choice prompt response.
	 * 
	 * @param prompt The HoursBeforeNowPrompt used to generate this response.
	 * 
	 * @param repeatableSetIteration If the prompt was part of a repeatable 
	 * 								 set, this is the iteration of that 
	 * 								 repeatable set on which this response was
	 * 								 made.
	 * 
	 * @param response The response from the user. See
	 * 				   {@link SingleChoicePrompt#validateValue(Object)}.
	 * 
	 * @throws DomainException The repeatable set iteration is null and this 
	 * 						   was part of a repeatable set, the repeatable set
	 * 						   iteration is not null and this was not part of a
	 * 						   repeatable set, or the response could not be 
	 * 						   properly decoded. 
	 * 
	 * @see SingleChoicePrompt#validateValue(Object) Validation Rules
	 */
	public SingleChoicePromptResponse(
			final SingleChoicePrompt prompt,
			final Integer repeatableSetIteration, 
			final Object response) 
			throws DomainException {
		
		super(prompt, repeatableSetIteration, response);
	}
	
	/**
	 * Returns the choice response from the user.
	 * 
	 * @return The choice response from the user.
	 * 
	 * @throws DomainException The prompt does not have a response.
	 */
	public String getText() throws DomainException {
		if(wasNotDisplayed()) {
			throw new DomainException("The prompt was not displayed.");
		}
		else if(wasSkipped()) {
			throw new DomainException("The prompt was skipped.");
		}
	
		return ((SingleChoicePrompt) getPrompt())
					.getChoices()
					.get((Integer) getResponse())
					.getLabel();
	}
}
