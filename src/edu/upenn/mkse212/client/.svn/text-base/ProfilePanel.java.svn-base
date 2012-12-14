package edu.upenn.mkse212.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProfilePanel {
	PennBook parent;
	public ProfilePanel(PennBook theParent) {
		this.parent = theParent;
	}
	void display(String username) {
		final String USERNAME = username;
		final Label label = new Label("waiting...");
		
		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		p.setHeight("1000px");
		p.setWidth("900px");
		
	    final HTML info = new HTML("waiting...");	    
	    p.insertWest(info, 20, null);
		
		
		// UPDATE INTERESTS BOX 
		final DialogBox updateInfoBox = new DialogBox();
		final Label directions = new Label ("Type in your interests to update your interests");
		final Label interests = new Label ("Interests: ");
		final TextBox interestField = new TextBox();
		
		final Button submitUpdateInfo = new Button("Sumbit", new ClickHandler() {
			public void onClick(ClickEvent event) {
				final String interests = interestField.getText();
				if (!interests.equals("")) {
					parent.getDatabaseService().updateInterests(USERNAME, interests,
							new AsyncCallback<Boolean>() {
								public void onFailure(Throwable caught) {
									parent.popupBox("RPC failure", "updateInterests");
								} 
								public void onSuccess(Boolean success) {
									updateInfoBox.hide();
									displayProfileInfo(USERNAME, info);
								}
					});
				}
			}
		});
		
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(directions);
		vp.add(interests);
		vp.add(interestField);
		vp.add(submitUpdateInfo);
		updateInfoBox.add(vp);

		
		
		
		
		// BUTTONS ALONG TOP OF PAGE
		final Button updateInfo = new Button("UPDATE MY INFO", new ClickHandler() {
					public void onClick(ClickEvent sender) {
						updateInfoBox.center();
						updateInfoBox.show();
					}
				});
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
	    final Button feed = new Button("THEFEED");
	    final Button profile = new Button("MY PROFILE");
	    final Button signout = new Button("SIGN ME OUT");
	    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(",");
	    final SuggestBox search = new SuggestBox(oracle);
	    buttonPanel.add(feed);
	    buttonPanel.add(profile);
	    buttonPanel.add(signout);
	    buttonPanel.add(updateInfo);
	    buttonPanel.add(search);
	    p.insertNorth(buttonPanel, 5, null);
	    
	    updateOracle(USERNAME, oracle);
	    
	    	    
	 	  
	    p.add(new HTML("content"));

		RootPanel.get("rootPanelContainer").clear();
		RootPanel.get("rootPanelContainer").add(p);
		
		parent.getDatabaseService().incrementLogins(username,
		new AsyncCallback<Integer>() {
			public void onFailure(Throwable caught) {
				parent.popupBox("RPC failure", "incrementLogins");
			}
			public void onSuccess(Integer result) {
				label.setText(result+" login(s) so far");
			}
		});
		
		
		displayProfileInfo(USERNAME, info);


	}
		
	private void updateOracle(String username, final MultiWordSuggestOracle oracle) {
		parent.getDatabaseService().getSuggestions(username,
				new AsyncCallback<Map<String,String>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getSuggestions");
					}
					public void onSuccess(Map<String, String> result) {
						Collection<String> usernames = result.values();
						Set<String> names = result.keySet();
						oracle.addAll(usernames);
						oracle.addAll(names);
					}
				});
		
	}
	public void displayProfileInfo(String username, final HTML info) {
		// DISPLAY PROFILE INFORMATION.
		parent.getDatabaseService().getInfo(username,
					new AsyncCallback<String[]>() {
						public void onFailure(Throwable caught) {
							parent.popupBox("RPC failure", "getInfo");
						}
						public void onSuccess(String[] results) {
							info.setHTML("NAME: " + results[0] + " " + results[1] + " <br />" + 
									     "USERNAME: " + results[6] + " <br />" + 
									     "EMAIL: " + results[2] + " <br />" + 
									     "NETWORK: " + results[3] + " <br />" + 
									     "INTERESTS: " + results[4] + " <br />" + 
									     "BIRTHDAY: " + results[5]);
							// parent.popupBox("THIS IS THE CURRENT INTEREST", results[4]);
						}
					});
	}
	
	public static void wait (int n) {
		long s1;
		long s2;
		
		s1 = System.currentTimeMillis();
		
		do {
			s2 = System.currentTimeMillis();
		}
		while (s2 - s1 > n);
	}
}
