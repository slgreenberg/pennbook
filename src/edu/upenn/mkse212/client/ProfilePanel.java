package edu.upenn.mkse212.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfilePanel {
	PennBook parent;
	Map<String, String> userToUsername;
	String friendUserName;
	String USERNAME;
	
	public ProfilePanel(PennBook theParent) {
		this.parent = theParent;
	}
	void display(String username) {
		USERNAME = username;
		final Label label = new Label("waiting...");
		
		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		p.setHeight("1000px");
		p.setWidth("1000px");
		
	    final HTML info = new HTML("Loading...");	
	    p.insertWest(info, 20, null);

	 	final VerticalPanel vpwall = new VerticalPanel();

	    
		// UPDATE INTERESTS BOX 
		final DialogBox updateInfoBox = new DialogBox();
		final Label directions = new Label ("Type in your interests to update your interests");
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
									displayWall(USERNAME, vpwall);
								}
					});
				}
			}
		});
		
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(directions);
		vp.add(interestField);
		vp.add(submitUpdateInfo);
		updateInfoBox.add(vp);

			
		
		// BUTTONS ALONG TOP OF PAGE
		HorizontalPanel buttonPanel = new HorizontalPanel();
		final Button updateInfo = new Button("UPDATE MY INFO", new ClickHandler() {
					public void onClick(ClickEvent sender) {
						updateInfoBox.center();
						updateInfoBox.show();
					}
				});	
	    final Button feed = new Button("THEFEED");
	    final Button profile = new Button("MY PROFILE", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		displayProfileInfo(USERNAME, info);
	    		displayWall(USERNAME, vpwall);
	    	}
	    });
	    final Button signout = new Button("SIGN OUT", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		parent.getLoginPanel().display();
	    	}
	    });
	    
	    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(",");
	    final SuggestBox searchBox = new SuggestBox(oracle);
	    buttonPanel.add(feed);
	    buttonPanel.add(profile);
	    buttonPanel.add(signout);
	    buttonPanel.add(updateInfo);
	    buttonPanel.add(searchBox);
	    
		final Button searchButton = new Button("SEARCH", new ClickHandler() {
			public void onClick(ClickEvent event) {
				String input = searchBox.getText();
				if (userToUsername.containsKey(input)) {
					String username = userToUsername.get(input);
					displayProfileInfo(username, info);
					displayWall(username, vpwall);
				}
				else if (userToUsername.containsValue(input)) {
					displayProfileInfo(input, info);
					displayWall(input, vpwall);
				}
				else {
					parent.popupBox("Sorry", "Either you are not friends with the user you searched, or the user does not exist");
				}
				
			}
		});
	    
	    buttonPanel.add(searchButton);
	    p.insertNorth(buttonPanel, 5, null);
	    
	    updateOracle(USERNAME, oracle);
	    
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
		
		
		final Button addFriendButton = new Button("HELL YEAH, I WANA BE FRIENDS!");
		addFriendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addFriend(USERNAME, friendUserName, addFriendButton);
			}

		});
		
		// NEED AN IF STATEMENT FOR IF TO DISPLAY WALL OR FRIEND BUTTON.
		
		// this must be last!
	 	p.add(vpwall);
			
		displayProfileInfo(USERNAME, info);
		displayWall(USERNAME, vpwall);


	}
		
	private void updateOracle(String username, final MultiWordSuggestOracle oracle) {
		parent.getDatabaseService().getSuggestions(username,
				new AsyncCallback<Map<String,String>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getSuggestions");
					}
					public void onSuccess(Map<String, String> result) {
						userToUsername = result;
						Collection<String> usernames = result.values();
						Set<String> names = result.keySet();
						oracle.addAll(usernames);
						oracle.addAll(names);
					}
				});
		
	}
	public void displayProfileInfo(final String username, final HTML info) {
		// DISPLAY PROFILE INFORMATION.
		parent.getDatabaseService().getInfo(username,
					new AsyncCallback<String[]>() {
						public void onFailure(Throwable caught) {
							parent.popupBox("RPC failure", "getInfo");
						}
						public void onSuccess(String[] results) {
							friendUserName = username;
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
	
	public void displayWall(String username, final VerticalPanel vpwall) {
		parent.getDatabaseService().getWall(username,
				new AsyncCallback<List<List<String>>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getWall");
					}
					public void onSuccess(List<List<String>> results) {
						vpwall.clear();
						final VerticalPanel holder = new VerticalPanel();
						final TextBox postBox = new TextBox();
						final Button postButton = new Button("POST THIS SWEET THANG!");
						postButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								if (!postBox.getText().equals("")) {
									addPost(USERNAME, friendUserName, postBox.getText(), vpwall);
								}
							}
						});
						holder.add(postBox);
						holder.add(postButton);
						vpwall.add(holder);
						
						for (List<String> result : results) {
							String wallString = "";
							HTML wall = new HTML();
							final String postId = result.get(0);
							Date date = new Date(System.currentTimeMillis());
							String time = date.toString();
							String postedBy = result.get(1);
							String post = result.get(2);
							String commentString = result.get(3);

							wallString +=
								"<br />" +
								"<div>" + 
									"<strong>" + postedBy + "</strong>" + "<br />" +
									"<i>" + time + "</i><br />" +
									"<span>" + post + "</span><br />";
							if (!commentString.equals("")) {
								String[] comments = commentString.split("~~");
								for (String comment : comments) {
									String[] commentFeatures = comment.split("~");
									String commenter = commentFeatures[0];
									String commentText = commentFeatures[1];
									wallString +=
										"<div style='margin-left:15px'>" +
											"<strong>" + commenter + "</strong>" + "<br />" +
											"<span>" + commentText + "</span><br />"+
										"</div>";
								}
							}
							wallString +=
								"</div>";
							wall.setHTML(wallString);
							vpwall.add(wall);
							final TextBox commentBox = new TextBox();
							commentBox.setText("Write a comment...");
							commentBox.addClickListener(new ClickListener() {
								public void onClick(Widget sender) {
									commentBox.setText("");
									
								}
							});
							commentBox.addBlurHandler(new BlurHandler() {
								public void onBlur(BlurEvent event) {
									if (commentBox.getText().equals("")) {
										commentBox.setText("Write a comment...");
									}	
								}
							});
							final Button commentButton = new Button("Comment");
							commentButton.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									addComment(USERNAME, commentBox.getText(), postId, vpwall);
									
								}
							});
							vpwall.add(commentBox);
							vpwall.add(commentButton);
						}
					}
				});
	}
	

	private void addFriend(String username, String friendUserName, final Button addFriendButton) {
		parent.getDatabaseService().addFriend(username, friendUserName,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getWall");
					}
					public void onSuccess(Boolean results) {
						if (results) {
							addFriendButton.setHTML("FRIENDED, I hope they say yes!");
							addFriendButton.setEnabled(false);
						}
					}
				});
	}
	
	private void addPost(final String username, String friendUserName, String text, final VerticalPanel vpwall) {
		parent.getDatabaseService().addPost(friendUserName, username, text,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getWall");
					}
					public void onSuccess(Boolean results) {
						// parent.popupBox("POST", "you posted successfully");
						displayWall(username, vpwall);
					}
				});
	}
	

	private void addComment(String username, final String text, String postId, final VerticalPanel vpwall) {
		parent.getDatabaseService().addComment(username, text, postId,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getWall");
					}
					public void onSuccess(Boolean results) {
						parent.popupBox("COMMENT", "you commented " + text + " successfully");
						displayWall(friendUserName, vpwall);
					}
				});
	}
}
