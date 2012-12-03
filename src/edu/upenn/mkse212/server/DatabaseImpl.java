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
	public boolean addUser(String username, String password, String firstName, 
			String lastName, String email, String network, String interests,
			String birthday) {
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
		list.add(new ReplaceableAttribute("interests", ""+interests, false));
		list.add(new ReplaceableAttribute("birthday", ""+birthday,false));
		String friends = "";
		list.add(new ReplaceableAttribute("friends", ""+friends, true));
		db.putAttributes(new PutAttributesRequest("users", username, list, 
				new UpdateCondition()));
		
		//puts hashed password into passwords database
		List<ReplaceableAttribute> l = new ArrayList<ReplaceableAttribute>();
		list.add(new ReplaceableAttribute("password",
				""+String.valueOf(password.hashCode()),true));
		db.putAttributes(new PutAttributesRequest("passwords", username, l,
				new UpdateCondition()));
		return new Boolean(true);
	}
	
	//updates both the user's list of friends as well as the other user's
	//list of friends
	//and adds both to friends database
	public boolean addFriend(String username, String otherUsername) {
		//populates users' store of friends
		long time = System.currentTimeMillis();
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
		l.add(new ReplaceableAttribute("friend", ""+username,false));
		list.add(new ReplaceableAttribute("timestamp", ""+time,false));
		l.add(new ReplaceableAttribute("timestamp", ""+time,false));
		db.putAttributes(new PutAttributesRequest("friends", username, list,
				new UpdateCondition()));
		db.putAttributes(new PutAttributesRequest("friends", otherUsername, l,
				new UpdateCondition()));
		
		return new Boolean(check1&&check2);
	}
	
	//add an update to the database if novel post
	//else it just updates the associated text
	public boolean addUpdate(String username, String otherUsername,
			String text) {
		long time = System.currentTimeMillis();
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
		list.add(new ReplaceableAttribute("timestamp", ""+time,false));
		list.add(new ReplaceableAttribute("text", ""+t,true));
		db.putAttributes(new PutAttributesRequest("updates", username, list, 
				new UpdateCondition()));
		return new Boolean(true);
	}
	
	
	
	//updated given code to match implemented databases
	public boolean validateLogin(String username, String password) {
		GetAttributesResult result = db.getAttributes(
				//new GetAttributesRequest("passwords", username));
				new GetAttributesRequest("users", username));
		List<Attribute> attributeList = result.getAttributes();
		for (Attribute a : attributeList) {
			if (a.getName().equals("password")) {
				return new Boolean(a.getValue().equals(password));
						//String.valueOf(password.hashCode())));
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
