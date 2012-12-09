/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.upenn.mkse212.server;

import edu.upenn.mkse212.client.Database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.amazonaws.services.simpledb.*;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.auth.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DatabaseImpl extends RemoteServiceServlet implements Database {
	
	private AmazonSimpleDBClient db;
	
	public DatabaseImpl() {
		String userID = "AKIAJQV2RJ5OZMECVPQA";
		String authKey = "PAlrjITfdQ+RmNxGpqfV8MXOqxSEf8ZLSJoNwA4w";
		db = new AmazonSimpleDBClient(new BasicAWSCredentials(userID, authKey));
	} 
	
	//checks to see if username already exists
	//if not, creates a new entry in the user database with info
	//and a new entry in the password database with the (hashed) password
	//adds user to online in database
	public boolean addUser(String username, String password, String firstName, 
			String lastName, String email, String network, String interests,
			String birthday) {
		String time = String.valueOf(System.currentTimeMillis());
		GetAttributesResult result = db.getAttributes(
				new GetAttributesRequest("users", username));
		List<Attribute> attributesList = result.getAttributes();
		//checks if username already exists
		for (Attribute a : attributesList) {
			if (a.getName().equals(username)) {
				return new Boolean(false);
			}
		}
		//creates a list with all attributes to be put into the database
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute("firstName", ""+firstName,false));
		list.add(new ReplaceableAttribute("lastName", ""+lastName,false));
		list.add(new ReplaceableAttribute("email", ""+email,false));
		list.add(new ReplaceableAttribute("network", ""+network,false));
		list.add(new ReplaceableAttribute("interests", ""+interests, true));
		list.add(new ReplaceableAttribute("birthday", ""+birthday,false));
		String friends = "";
		list.add(new ReplaceableAttribute("friends", ""+friends, true));
		list.add(new ReplaceableAttribute("lastLogin", ""+time,true));
		list.add(new ReplaceableAttribute("numLogins", "1",true));
		
		db.putAttributes(new PutAttributesRequest("users", username, list, 
				new UpdateCondition()));
		
		//puts hashed password into passwords database
		List<ReplaceableAttribute> l = new ArrayList<ReplaceableAttribute>();
		l.add(new ReplaceableAttribute("password",
				""+String.valueOf(password.hashCode()),true));
		db.putAttributes(new PutAttributesRequest("passwords", username, l,
				new UpdateCondition()));
		
		//puts user in online database
		List<ReplaceableAttribute> l1 = new ArrayList<ReplaceableAttribute>();
		l1.add(new ReplaceableAttribute("timestamp", String.valueOf(time), true));
		db.putAttributes(new PutAttributesRequest("online", username, l1,
				new UpdateCondition()));
		return new Boolean(true);
	}
	
	//updates both the user's list of friends as well as the other user's
	//list of friends
	//and adds both to friends database
	public boolean addFriend(String username, String otherUsername) {
		//populates users' store of friends
		updateOnline(username);
		String time = String.valueOf(System.currentTimeMillis());
		GetAttributesResult result = db.getAttributes(
				new GetAttributesRequest("users", username));
		List<Attribute> attributeList = result.getAttributes();
		String friend = "";
		Boolean check1 = false;
		Boolean check2 = false;
		for (Attribute a : attributeList) {
			if (a.getName().equals("friends")) {
				friend+=a.getValue()+"~";
				friend+=otherUsername;
				a.setValue(friend);
				check1 = true;
			}
		}
		result = db.getAttributes(new GetAttributesRequest("users", 
				otherUsername));
		attributeList = result.getAttributes();
		String f = "";
		for (Attribute a : attributeList) {
			if (a.getName().equals("friends")) {
				f+=a.getValue()+"~";
				f+=username;
				a.setValue(f);
				check2 = true;
			}
		}
		
		//populates friends database
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		List<ReplaceableAttribute> l = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute("friend", ""+otherUsername,false));
		list.add(new ReplaceableAttribute("timestamp", ""+time,false));
		l.add(new ReplaceableAttribute("friend", ""+username,false));
		l.add(new ReplaceableAttribute("timestamp", ""+time,false));
		db.putAttributes(new PutAttributesRequest("friends", username, list,
				new UpdateCondition()));
		db.putAttributes(new PutAttributesRequest("friends", otherUsername, l,
				new UpdateCondition()));
		
		return new Boolean(check1&&check2);
	}
	
	//key posted to
	//first column posted by
	//add an update to the database if novel post
	//else it just updates the associated text
	//username = posted to
	//otherUsername = posted by
	public boolean addUpdate(String username, String otherUsername,
			String text) {
		updateOnline(otherUsername);
		String time = String.valueOf(System.currentTimeMillis());
		String t = "";
		GetAttributesResult result = db.getAttributes(
				new GetAttributesRequest("updates", username));
		List<Attribute> attributeList = result.getAttributes();
		for (Attribute a : attributeList) {
			if (a.getName().equals("text")) {
				t+=otherUsername+"~"+a.getValue()+"|";
				t+=username+"~"+text;
				a.setValue(t);
				return new Boolean(true);
			}
		}
		t+=username+"~"+text;
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute("friend", ""+otherUsername, false));
		list.add(new ReplaceableAttribute("timestamp", ""+time,true));
		list.add(new ReplaceableAttribute("text", ""+t,true));
		db.putAttributes(new PutAttributesRequest("updates", username, list, 
				new UpdateCondition()));
		return new Boolean(true);
	}
	
	//returns a list of strings representing different posts on a user's
	//wall including status updates and comments
	public List<String> getWall(String username) {
		updateOnline(username);
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("updates", username));
		List<Attribute> aList = results.getAttributes();
		List<String> ret = new ArrayList<String>();
		for (Attribute a : aList) {
			if (a.getName().equals("text")) {
				ret.add(a.getValue());
			}
		}
		return ret;
	}
	
	/*public List<String> getUpdates(String username) {
		updateOnline(username);
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("updates",username));
		List<String> ret = new ArrayList<String>();
		return ret;
	}*/
	
	//method to be used throwout databaseimpl
	//updates the timestamp for a user's latest activity
	public void updateOnline(String username) {
		long time = System.currentTimeMillis();
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("online",username));
		List<Attribute> aList = results.getAttributes();
		for (Attribute a : aList) {
			if (a.getName().equals("timestamp")) {
				a.setValue(String.valueOf(time));
			}
		}
	}
	
	//returns a list of a user's friends that are currently online
	//get list of friends and iterates over the list to see if there
	//has been activity in the past 5 minutes
	public List<String> getOnline(String username) {
		List<String> online = new ArrayList<String>();
		long time = System.currentTimeMillis();
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("friends",username));
		List<Attribute> aList = results.getAttributes();
		String friends = "";
		for (Attribute a : aList) {
			if (a.getName().equals("friends")) {
				friends+=a.getValue();
			}
		}
		String[] arr = friends.split("~");
		for (String s : arr) {
			GetAttributesResult r = db.getAttributes(
					new GetAttributesRequest("online",s));
			List<Attribute> list = results.getAttributes();
			for (Attribute a : list) {
				if (a.getName().equals("timestamp")) {
					long ts = Long.parseLong(a.getValue());
					if ((time - ts) > 300000.0) {
						online.add(s);
					}
				}
			}
		}
		return online;
	}
	
	//given the querrying user and their query, returns a set of strings
	//representing the possible usernames they could be looking for
	public Set<String> getSuggestions(String username, String query) {
		Set<String> sugg = new HashSet<String>();
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("users",username));
		List<Attribute> list = results.getAttributes();
		String friends = "";
		for (Attribute a : list) {
			if (a.getName().equals("friends")) {
				friends+=a.getValue();
			}
		}
		String[] f = friends.split("~");
		for(String s : f) {
			if (s.toLowerCase().contains(query.toLowerCase())) {
				sugg.add(s);
			}
		}
		return sugg;
	}
	
	//allows the user the change what their interests are
	public boolean updateInterests(String username, String update) {
		updateOnline(username);
		GetAttributesResult results = db.getAttributes(
				new GetAttributesRequest("users",username));
		List<Attribute> aList = results.getAttributes();
		for (Attribute a : aList) {
			if (a.getName().equals("interests")) {
				a.setValue(update);
				return true;
			}
		}
		return false;
	}
	
	//creates the file for adsorption
	//gets all friendships from all users
	//TODO add network and interests
	public void getAllConnections() {
		try {
			BufferedWriter buff = new BufferedWriter(new FileWriter("adsorption.txt"));
			SelectResult results = db.select(
					new SelectRequest("select * from friends"));
			List<Item> list = results.getItems();
			for (Item i : list) {
				List<Attribute> aList = i.getAttributes();
				for (Attribute a : aList) {
					if(a.getName().equals("friends")) {
						buff.write(i.getName()+"\t"+a.getValue());
						buff.newLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//updated given code to match implemented databases
	//also adds user to online database
	public boolean validateLogin(String username, String password) {
		updateOnline(username);
		List<ReplaceableAttribute> list = 
				new ArrayList<ReplaceableAttribute>();
		long time = System.currentTimeMillis();
		list.add(new ReplaceableAttribute("timestamp", 
				String.valueOf(time), true));
		db.putAttributes(new PutAttributesRequest("online", 
				username, list, new UpdateCondition()));
		if (username.equals("ahae") || username.equals("susangr")) {
			GetAttributesResult result = db.getAttributes(
					new GetAttributesRequest("users", username));
			List<Attribute> attributeList = result.getAttributes();
			for (Attribute a : attributeList) {
				if (a.getName().equals("password")) {
					return new Boolean(a.getValue().equals(password));
				}
			}
		} else {
			GetAttributesResult result = db.getAttributes(
					new GetAttributesRequest("passwords", username));
			List<Attribute> attributeList = result.getAttributes();
			for (Attribute a : attributeList) {
				if (a.getName().equals("password")) {
					return new Boolean(a.getValue().equals(
							String.valueOf(password.hashCode())));
				}
			}
		}
		return new Boolean(false);
	}
	
	//given code
	public Integer incrementLogins(String username) {
		GetAttributesResult result = db.getAttributes(
			new GetAttributesRequest("users", username));
		List<Attribute> attributeList = result.getAttributes();
		int loginsSoFar = 0;
		for (Attribute a : attributeList) {
			if (a.getName().equals("loginsSoFar")) {
				loginsSoFar = Integer.valueOf(a.getValue()).intValue();
			}
		}
		loginsSoFar ++;
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute("loginsSoFar", ""+loginsSoFar, false));
		db.putAttributes(new PutAttributesRequest("users", username, list, 
				new UpdateCondition()));
		return new Integer(loginsSoFar);
	}
	

	
	


}
