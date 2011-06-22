package org.ohmage.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.ohmage.cache.CacheMissException;
import org.ohmage.cache.PreferenceCache;
import org.ohmage.request.AwRequest;
import org.ohmage.request.InputKeys;
import org.ohmage.request.VisualizationRequest;
import org.ohmage.validator.AwRequestAnnotator;

/**
 * Sends the request to the visualization server and sets the result in the 
 * request to be returned to the original requester.
 * 
 * @author John Jenkins
 */
public class VizScatterPlotService extends AbstractAnnotatingService {
	private static final Logger _logger = Logger.getLogger(VizScatterPlotService.class);
	
	/**
	 * Builds this service.
	 * 
	 * @param annotator The annotator to use should the communication with the
	 * 					server fails.
	 */
	public VizScatterPlotService(AwRequestAnnotator annotator) {
		super(annotator);
	}
	
	/**
	 * Builds and sends the request to the visualization server and then 
	 * places the response in our internal request as as response.
	 */
	@Override
	public void execute(AwRequest awRequest) {
		_logger.info("Sending the request to the visualization server.");
		
		// Get the requested image's width.
		String width;
		try {
			width = (String) awRequest.getToProcessValue(InputKeys.VISUALIZATION_WIDTH);
		}
		catch(IllegalArgumentException e) {
			_logger.error("Missing required parameter: " + InputKeys.VISUALIZATION_WIDTH);
			throw new ServiceException(e);
		}
		
		// Get the requested image's height.
		String height;
		try {
			height = (String) awRequest.getToProcessValue(InputKeys.VISUALIZATION_HEIGHT);
		}
		catch(IllegalArgumentException e) {
			_logger.error("Missing required parameter: " + InputKeys.VISUALIZATION_HEIGHT);
			throw new ServiceException(e);
		}
		
		// Get the campaign ID.
		String campaignId = awRequest.getCampaignUrn();
		
		String promptId;
		try {
			promptId = (String) awRequest.getToProcessValue(InputKeys.PROMPT_ID);
		}
		catch(IllegalArgumentException e) {
			_logger.error("Missing required parameter: " + InputKeys.PROMPT_ID);
			throw new ServiceException(e);
		}
		
		String prompt2Id;
		try {
			prompt2Id = (String) awRequest.getToProcessValue(InputKeys.PROMPT2_ID);
		}
		catch(IllegalArgumentException e) {
			_logger.error("Missing required parameter: " + InputKeys.PROMPT2_ID);
			throw new ServiceException(e);
		}
		
		// Build the request.
		StringBuilder urlBuilder = new StringBuilder();
		try {
			String serverUrl = PreferenceCache.instance().lookup(PreferenceCache.KEY_VISUALIZATION_SERVER);
			urlBuilder.append(serverUrl);
			
			if(! serverUrl.endsWith("/")) {
				urlBuilder.append("/");
			}
		}
		catch(CacheMissException e) {
			_logger.error("Cache doesn't know about 'known' key: " + PreferenceCache.KEY_VISUALIZATION_SERVER);
			throw new ServiceException(e);
		}
		urlBuilder.append("scatterplot?");
		
		// Add the required parameters.
		urlBuilder.append("token='").append(awRequest.getUserToken()).append("'");
		urlBuilder.append("&server='").append("http://131.179.144.217/app").append("'");
		urlBuilder.append("&campaign_urn='").append(campaignId).append("'");
		urlBuilder.append("&prompt_id='").append(promptId).append("'");
		urlBuilder.append("&prompt2_id='").append(prompt2Id).append("'");
		urlBuilder.append("&!width=").append(width);
		urlBuilder.append("&!height=").append(height);
		String urlString = urlBuilder.toString();
	
		try {
			// Connect to the visualization server.
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			
			// Check that the response code was 200.
			if(urlConnection instanceof HttpURLConnection) {
				HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
				if(httpUrlConnection.getResponseCode() != 200) {
					getAnnotator().annotate(awRequest, "The server returned a non-200 response.");
				}
			}
			
			// Build the response.
			InputStream reader = urlConnection.getInputStream();
			
			StringBuilder builder = new StringBuilder();
			byte[] chunk = new byte[4096];
			int amountRead = 0;
			while((amountRead = reader.read(chunk)) != -1) {
				builder.append(new String(chunk), 0, amountRead);
			}
			
			// Set the response in the request.
			awRequest.addToReturn(VisualizationRequest.VISUALIZATION_REQUEST_RESULT, builder.toString(), true);
		}
		catch(MalformedURLException e) {
			_logger.error("Built a malformed URL: " + urlString);
			throw new ServiceException(e);
		}
		catch(IOException e) {
			getAnnotator().annotate(awRequest, "Error communicating with the visualization server: " + e.toString());
			awRequest.setFailedRequest(true);
			return;
		}
	}
}