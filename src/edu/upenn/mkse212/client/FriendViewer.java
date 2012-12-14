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

import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FriendViewer implements EntryPoint {
	

	//NEED TO DEFINE WHO THE USER IS
	private String username;
	private String ret;
	private JavaScriptObject graph;
	
	//creates a string to be parsed into json of a node and all of
	//its respective children. if it is the first time the graph is 
	//created, it creates a new graph, otherwise it updates the current
	//graph.
	public void drawNodeAndNeighbors(final String text) {
		Database.Util.getInstance().getFriendsList(text, new AsyncCallback<Set<String>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Unable to talk to server");
			}
			public void onSuccess(Set<String> result) {
				ret = "{\"id\": \"" + text + "\", \"name\": \"" + text 
						+ "\", \"children\": [\n";
				for (String s : result) {
					ret += "\t{\"id\": \"" + s + "\", \"name\": \"" + s 
							+ "\", \"children\": []},\n";
				}
				ret = ret.substring(0, ret.length() - 2);
				ret += "\n]}";
				if (graph == null) {
					graph = FriendVisualization.createGraph(ret, FriendViewer.this);
				} else {
					FriendVisualization.addToGraph(graph, ret);
				}
			}
			
		});
		
	}
	
	
	
	
	private Button queryButton;
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		
		//tells the user what to do, noninteractive
		Label l1 = new Label("Click to see you network! "); 
		rootPanel.add(l1); 
		

		//adds a button that which clicked runs drawNodeAndNeighbors
		//with the text entered in the box
		queryButton = new Button();
		rootPanel.add(queryButton);
		queryButton.setText("Visualize");
		queryButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				drawNodeAndNeighbors(username);
			}
		});
		
		
	}
}
