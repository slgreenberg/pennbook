package edu.upenn.mkse212.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	void addPost(String username, String otherUsername,
			String text, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;
	
	void addComment(String username, String text, String postID, 
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	
	void getWall(String username, AsyncCallback<List<List<String>>> callback)
		throws IllegalArgumentException;
	
	void updateOnline(String username, AsyncCallback<Void> callback)
		throws IllegalArgumentException;
	
	void updateInterests(String username, String update,
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	
	void getOnline(String username, AsyncCallback<Set<String>> callback)
			throws IllegalArgumentException;
	
	void getSuggestions(String username, AsyncCallback<Map<String,String>> callback)
			throws IllegalArgumentException;
	
	void getAllConnections(AsyncCallback<Boolean> callback) 
			throws IllegalArgumentException;
	
	//void visualizationFile(String username, AsyncCallback<Boolean> callback)
		//throws IllegalArgumentException;
	
	void getNetwork(String username, AsyncCallback<String> callback)
		throws IllegalArgumentException;
	
	void getInfo(String username, AsyncCallback<String[]> callback)
			throws IllegalArgumentException;
	
	void getPostAndComment(String postID, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;
	
	void getFeed(String username, AsyncCallback<List<List<String>>> callback) 
			throws IllegalArgumentException;
	
	void nameUsername(AsyncCallback<Map<String,String>> callback) 
			throws IllegalArgumentException;
	
	void usernameName(AsyncCallback<Map<String,String>> callback) 
			throws IllegalArgumentException;
	
	void isFriend(String username, String otherUsername, 
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;
	
	void getFriendsList(String username, AsyncCallback<Set<String>> callback) 
			throws IllegalArgumentException;
	
	void staticFriendReq(String username, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;
	
	void graphJSON(String username, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	

}
