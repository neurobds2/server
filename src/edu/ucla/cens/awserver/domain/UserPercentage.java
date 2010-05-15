package edu.ucla.cens.awserver.domain;

/**
 * Container for results from the successful location update query (or any other feature that needs a userName-percentage holder). 
 * 
 * @author selsky
 */
public class UserPercentage {
	private String _userName;
	private double _percentage;
	
	/**
	 * 
	 */
	public UserPercentage(String userName, double percentage) {
		_userName = userName;
		_percentage = percentage;
	}
	
	public String getUserName() {
		return _userName;
	}
	
//	public void setUserName(String userName) {
//		_userName = userName;
//	}
	
	public double getPercentage() {
		return _percentage;
	}
	
//	public void setSuccessPercentage(double successPercentage) {
//		_successPercentage = successPercentage;
//	}
}