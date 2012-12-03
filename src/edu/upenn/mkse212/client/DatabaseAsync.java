package edu.upenn.mkse212.client;

import java.sql.Timestamp;

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
	//void addFriend(String username, String otherUsername, Timestamp time) 
	//		throws IllegalArgumentException;
	//void addUpdate(String username, String otherUsername, Timestamp time,
	//		String text) throws IllegalArgumentException;


}
