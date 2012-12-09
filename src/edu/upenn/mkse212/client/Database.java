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
package edu.upenn.mkse212.client;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Database")
public interface Database extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static DatabaseAsync instance;
		public static DatabaseAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(Database.class);
			}
			return instance;
		}
	}
	
	boolean validateLogin(String username, String password);

	boolean addUser(String username, String password, String firstName,
					String LastName, String email, String network, 
					String interests, String birthday);
	
	boolean addFriend(String username, String otherUsername);
	
	boolean addUpdate(String username, String otherUsername,
			String text);
	
	void updateOnline(String username);
	
	boolean updateInterests(String username, String update);
	
	List<String> getOnline(String username);
	
	List<String> getWall(String username);
	
	Integer incrementLogins(String username);
	
	Set<String> getSuggestions(String username, String query);
	
	void getAllConnections();
	
}
