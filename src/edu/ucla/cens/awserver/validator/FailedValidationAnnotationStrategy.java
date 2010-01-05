package edu.ucla.cens.awserver.validator;

import edu.ucla.cens.awserver.datatransfer.AwRequest;

/**
 * An implementation of AnnotateAwRequestStrategy for validation failures.
 * 
 * @author selsky
 */
public class FailedValidationAnnotationStrategy implements AnnotateAwRequestStrategy {

	/**
	 * Pushes the provided message into the payload of the AwRequest with the key <code>errorMessage</code>.
	 * Also pushes <code>failedRequest</code> into the payload of the AwRequest with the value <code>true</code>. 
	 */
	public void annotate(AwRequest request, String message) {
		request.getPayload().put("errorMessage", message);
		request.getPayload().put("failedRequest", "true");
	}
}
