package org.ohmage.request.audit;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.ohmage.annotator.ErrorCodes;
import org.ohmage.domain.AuditInformation;
import org.ohmage.jee.servlet.RequestServlet;
import org.ohmage.jee.servlet.RequestServlet.RequestType;
import org.ohmage.request.InputKeys;
import org.ohmage.request.UserRequest;
import org.ohmage.service.AuditServices;
import org.ohmage.service.ServiceException;
import org.ohmage.service.UserServices;
import org.ohmage.validator.AuditValidators;
import org.ohmage.validator.AuditValidators.ResponseType;
import org.ohmage.validator.ValidationException;

public class AuditReadRequest extends UserRequest {
	private static final Logger LOGGER = Logger.getLogger(AuditReadRequest.class);
	
	private static final String RESULT_KEY = "audits";
	
	private final RequestType requestType;
	private final String uri;
	private final String client;
	private final String deviceId;
	private final ResponseType responseType;
	private final String errorCode;
	
	private final Date startDate;
	private final Date endDate;
	
	private List<AuditInformation> results;
	
	/**
	 * Creates an audit read request.
	 * 
	 * @param httpRequest The HttpServletRequest with the parameters.
	 */
	public AuditReadRequest(HttpServletRequest httpRequest) {
		super(httpRequest, TokenLocation.EITHER);
		
		LOGGER.info("Creating an audit read request.");
		
		RequestServlet.RequestType tRequestType = null;
		String tUri = null;
		String tClient = null;
		String tDeviceId = null;
		ResponseType tResponseType = null;
		String tErrorCode = null;
		Date tStartDate = null;
		Date tEndDate = null;
		
		if(! isFailed()) {
			try {
				tRequestType = AuditValidators.validateRequestType(this, httpRequest.getParameter(InputKeys.AUDIT_REQUEST_TYPE));
				if((tRequestType != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_REQUEST_TYPE).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_REQUEST_TYPE, "Multiple " + InputKeys.AUDIT_REQUEST_TYPE + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_REQUEST_TYPE + " parameters were given.");
				}
				
				tUri = AuditValidators.validateUri(this, httpRequest.getParameter(InputKeys.AUDIT_URI));
				if((tUri != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_URI).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_URI, "Multiple " + InputKeys.AUDIT_URI + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_URI + " parameters were given.");
				}
				
				tClient = AuditValidators.validateClient(this, httpRequest.getParameter(InputKeys.AUDIT_CLIENT));
				if((tClient != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_CLIENT).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_CLIENT, "Multiple " + InputKeys.AUDIT_CLIENT + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_CLIENT + " parameters were given.");
				}
				
				tDeviceId = AuditValidators.validateDeviceId(this, httpRequest.getParameter(InputKeys.AUDIT_DEVICE_ID));
				if((tDeviceId != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_DEVICE_ID).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_DEVICE_ID, "Multiple " + InputKeys.AUDIT_DEVICE_ID + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_DEVICE_ID + " parameters were given.");
				}
				
				tResponseType = AuditValidators.validateResponseType(this, httpRequest.getParameter(InputKeys.AUDIT_RESPONSE_TYPE));
				if((tResponseType != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_RESPONSE_TYPE).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_RESPONSE_TYPE, "Multiple " + InputKeys.AUDIT_RESPONSE_TYPE + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_RESPONSE_TYPE + " parameters were given.");
				}
				
				tErrorCode = AuditValidators.validateErrorCode(this, httpRequest.getParameter(InputKeys.AUDIT_ERROR_CODE));
				if((tErrorCode != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_ERROR_CODE).length > 1)) {
					setFailed(ErrorCodes.AUDIT_INVALID_ERROR_CODE, "Multiple " + InputKeys.AUDIT_ERROR_CODE + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_ERROR_CODE + " parameters were given.");
				}
				
				tStartDate = AuditValidators.validateStartDate(this, httpRequest.getParameter(InputKeys.AUDIT_START_DATE));
				if((tStartDate != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_START_DATE).length > 1)) {
					setFailed(ErrorCodes.SERVER_INVALID_DATE, "Multiple " + InputKeys.AUDIT_START_DATE + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_START_DATE + " parameters were given.");
				}
				
				tEndDate = AuditValidators.validateEndDate(this, httpRequest.getParameter(InputKeys.AUDIT_END_DATE));
				if((tEndDate != null) && (httpRequest.getParameterValues(InputKeys.AUDIT_END_DATE).length > 1)) {
					setFailed(ErrorCodes.SERVER_INVALID_DATE, "Multiple " + InputKeys.AUDIT_END_DATE + " parameters were given.");
					throw new ValidationException("Multiple " + InputKeys.AUDIT_END_DATE + " parameters were given.");
				}
			}
			catch(ValidationException e) {
				LOGGER.info(e.toString());
			}
		}
		
		requestType = tRequestType;
		uri = tUri;
		client = tClient;
		deviceId = tDeviceId;
		responseType = tResponseType;
		errorCode = tErrorCode;
		startDate = tStartDate;
		endDate = tEndDate;
		
		results = new LinkedList<AuditInformation>();
	}

	/**
	 * Services the request.
	 */
	@Override
	public void service() {
		LOGGER.info("Servicing the audit read request.");
		
		if(! authenticate(false)) {
			return;
		}
		
		try {
			LOGGER.info("Verifying the user is an admin.");
			UserServices.verifyUserIsAdmin(this, getUser().getUsername());
			
			LOGGER.info("Gathering the audit information.");
			results = AuditServices.getAuditInformation(this, requestType, uri, client, deviceId, responseType, errorCode, startDate, endDate);
		}
		catch(ServiceException e) {
			e.logException(LOGGER);
		}
	}

	/**
	 * Replies to the request.
	 */
	@Override
	public void respond(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		// Build the result object.
		JSONArray resultJson = new JSONArray();
		for(AuditInformation result : results) {
			resultJson.put(result.toJson());
		}
		
		super.respond(httpRequest, httpResponse, RESULT_KEY, resultJson);
	}
}