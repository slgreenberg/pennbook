package edu.upenn.mkse212.client;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface DatabaseAsync {
	
	void validateLogin(String username, String password, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;
	void incrementLogins(String username, AsyncCallback<Integer> callback)
			throws IllegalArgumentException;
	void addUser(String username, String password, String firstName,
		String lastName, String email, String network, String interests,
		String birthday, AsyncCallback<Boolean> callback)
		throws IllegalArgumentException;
	void addFriend(String username, String otherUsername, 
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	void addUpdate(String username, String otherUsername,
			String text, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;
	void getWall(String username, AsyncCallback<Boolean> callback)
		throws IllegalArgumentException;
	

}
