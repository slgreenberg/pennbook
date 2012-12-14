package edu.upenn.mkse212.client;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfilePanel {
	PennBook parent;
	Map<String, String> nameToUsername;
	Map<String, String>usernameToName;
	String friendUserName;
	String USERNAME;
	Button addFriendButton;
	
	public ProfilePanel(PennBook theParent) {
		this.parent = theParent;
	}
	void display(String username) {
		USERNAME = username;
		

		
		
		final Label label = new Label("waiting...");
		
		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		p.setHeight("8000px");
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
									displayWall(USERNAME, vpwall, info, addFriendButton);
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
	    final Button feed = new Button("FEED", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		displayFeed(USERNAME, vpwall, info);
	    	}
	    });
	    final Button profile = new Button("MY PROFILE", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		displayProfileInfo(USERNAME, info);
	    		displayWall(USERNAME, vpwall, info, addFriendButton);
	    	}
	    });
	    final Button signout = new Button("SIGN OUT", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		parent.getLoginPanel().display();
	    	}
	    });
	    
	    final Button visualize = new Button("VISUALIZE!", new ClickHandler() {
	    	public void onClick(ClickEvent sender) {
	    		parent.getDatabaseService().graphJSON(USERNAME,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								parent.popupBox("RPC failure", "Visualize");
							}
							public void onSuccess(String results) {
								parent.getFriendViewer().drawNodeAndNeighbors(USERNAME);
							}
						});		
	    	}
	    });
	    
	    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(",");
	    final SuggestBox searchBox = new SuggestBox(oracle);
		populateMaps(oracle);

	    buttonPanel.add(feed);
	    buttonPanel.add(profile);
	    buttonPanel.add(signout);
	    buttonPanel.add(visualize);
	    buttonPanel.add(updateInfo);
	    buttonPanel.add(searchBox);
	    
		final Button searchButton = new Button("SEARCH", new ClickHandler() {
			public void onClick(ClickEvent event) {
				String input = searchBox.getText();
				if (nameToUsername.containsKey(input)) {
					String username = nameToUsername.get(input);
					displayProfileInfo(username, info);
					displayWall(username, vpwall, info, addFriendButton);
				}
				else if (nameToUsername.containsValue(input)) {
					displayProfileInfo(input, info);
					displayWall(input, vpwall, info, addFriendButton);
				}
				else {
					parent.popupBox("Sorry", "The user you searched does not exist");
				}
				
			}
		});
	    
	    buttonPanel.add(searchButton);
	    p.insertNorth(buttonPanel, 5, null);
	    
	    VerticalPanel friendReccs = new VerticalPanel();
	    p.insertEast(friendReccs, 20, null);
	    
	    VerticalPanel onlineFriends = new VerticalPanel();
	    p.insertEast(onlineFriends, 20, friendReccs);
	    

	    displayFriendReccs(friendReccs);
	    displayOnlineFriends(onlineFriends);
	    
	    
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
		
		
		addFriendButton = new Button("HELL YEAH, I WANA BE FRIENDS!");
		addFriendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addFriend(USERNAME, friendUserName, addFriendButton);
			}

		});
		
		// NEED AN IF STATEMENT FOR IF TO DISPLAY WALL OR FRIEND BUTTON.
		
		// this must be last!
	 	p.add(vpwall);
			
		displayProfileInfo(USERNAME, info);
		displayWall(USERNAME, vpwall, info, addFriendButton);


	}

	public void displayOnlineFriends(final VerticalPanel onlineFriends) {
		parent.getDatabaseService().getOnline(USERNAME,
				new AsyncCallback<Set<String>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getOnline");
					}
					public void onSuccess(Set<String> results) {
						HTML friends = new HTML();
						String html = "These friends are currently online: <br />";
						for (String result : results) {
							html += result + "<br />";
						}
						friends.setHTML(html);
						onlineFriends.add(friends);
					}
				});
		
	}
/*
	private void updateOracle(final MultiWordSuggestOracle oracle) {
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
	
*/
	public void displayProfileInfo(final String username, final HTML info) {
		// DISPLAY PROFILE INFORMATION.
		parent.getDatabaseService().getInfo(username,
					new AsyncCallback<String[]>() {
						public void onFailure(Throwable caught) {
							parent.popupBox("RPC failure", "getInfo");
						}
						public void onSuccess(String[] results) {
							info.setVisible(true);
							friendUserName = username;
							info.setHTML("NAME: " + results[0] + " " + results[1] + " <br />" + 
									     "USERNAME: " + results[6] + " <br />" + 
									     "EMAIL: " + results[2] + " <br />" + 
									     "NETWORK: " + results[3] + " <br />" + 
									     "INTERESTS: " + results[4] + " <br />" + 
									     "BIRTHDAY: " + results[5]);
						}
					});
	}
	
	public void displayWall(final String username, final VerticalPanel vpwall, final HTML info, final Button addFriendButton) {
		parent.getDatabaseService().isFriend(USERNAME, username,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "isFriend");
					}
					@Override
					public void onSuccess(Boolean result) {
						if (result || USERNAME.equals(username)) {
							reallyDisplayWall(username, vpwall, info);
						} else {
							vpwall.clear();
							vpwall.add(addFriendButton);
						}
						
					}

		});
	}
		
		
		
	public void reallyDisplayWall(String username, final VerticalPanel vpwall, final HTML info) {	
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
									addPost(USERNAME, friendUserName, postBox.getText(), vpwall, info);
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

							String time = "";
							if (postId != null) {
								Long dateTime = Long.parseLong(postId);
								Date date = new Date(dateTime);
								time = date.toString();
							}

							final String postedBy = result.get(1);
							String post = result.get(2);
							String commentString = result.get(3);

							final Anchor poster = new Anchor(usernameToName.get(postedBy));
							System.out.println(usernameToName.get(postedBy));
							poster.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									System.out.println("AM I HERE?");
									displayProfileInfo(postedBy, info);
									displayWall(postedBy, vpwall, info, addFriendButton);
								}
							});

							wallString +=
									"<br /><strong>" + poster + "</strong><br />" +
									"<i>" + time + "</i><br />" +
									"<span>" + post + "</span><br />";
							if (!commentString.equals("")) {
								String[] comments = commentString.split("~~");
								for (String comment : comments) {
									String[] commentFeatures = comment.split("~");
									final String commenter = commentFeatures[0];
									String commentText = commentFeatures[1];
									final Anchor posterCommenter = new Anchor(usernameToName.get(commenter));
									posterCommenter.addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											displayProfileInfo(commenter, info);
											displayWall(commenter, vpwall, info, addFriendButton);
										}
									});
									wallString +=
										"<div style='margin-left:15px'>" +
											"<strong>" + posterCommenter + "</strong>" + "<br />" +
											"<span>" + commentText + "</span><br />"+
										"</div>";
								}
							}
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
									addComment(USERNAME, commentBox.getText(), postId, vpwall, info);
									
								}
							});
							vpwall.add(commentBox);
							vpwall.add(commentButton);
						}
					}
				});
	}
	
	private void populateMaps(final MultiWordSuggestOracle oracle) {
		parent.getDatabaseService().nameUsername(
				new AsyncCallback<Map<String,String>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "nameUsername");
					}
					public void onSuccess(Map<String, String> result) {
						nameToUsername = result;
						oracle.addAll(nameToUsername.keySet());
					}
				});
		parent.getDatabaseService().usernameName(
				new AsyncCallback<Map<String,String>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "usernameName");
					}
					public void onSuccess(Map<String, String> result) {
						usernameToName = result;
					    oracle.addAll(usernameToName.keySet());
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
	
	private void addPost(final String username, final String friendUserName, String text, final VerticalPanel vpwall, final HTML info) {
		parent.getDatabaseService().addPost(friendUserName, username, text,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "addPost");
					}
					public void onSuccess(Boolean results) {
						// parent.popupBox("POST", "you posted successfully");
						displayWall(friendUserName, vpwall, info, addFriendButton);
					}
				});
	}
	

	private void addComment(String username, final String text, String postId, final VerticalPanel vpwall, final HTML info) {
		parent.getDatabaseService().addComment(username, text, postId,
				new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "addComment");
					}
					public void onSuccess(Boolean results) {
						displayWall(friendUserName, vpwall, info, addFriendButton);
					}
				});
	}
	
	private void displayFeed(String username, final VerticalPanel vpwall, final HTML info) {
		parent.getDatabaseService().getFeed(username,
				new AsyncCallback<List<List<String>>>() {
					public void onFailure(Throwable caught) {
						parent.popupBox("RPC failure", "getFeed");
					}
					public void onSuccess(List<List<String>> results) {
						info.setVisible(false);
						vpwall.clear();
						for (List<String> result : results) {
							String wallString = "";
							HTML wall = new HTML();
							final String postId = result.get(0);
							System.out.println(postId);
							Date date = new Date(System.currentTimeMillis());
							String time = date.toString();
							final String postedBy = result.get(1);
							String namePostedBy = usernameToName.get(postedBy);
							final String postedTo = result.get(2);
							String namePostedTo = usernameToName.get(postedTo);
							String post = result.get(3);
							String commentString = result.get(4);
							final Anchor poster = new Anchor(namePostedBy);
							poster.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									displayProfileInfo(postedBy, info);
									displayWall(postedBy, vpwall, info, addFriendButton);
								}
							});
							final Anchor postee = new Anchor(namePostedTo);
							poster.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									displayProfileInfo(postedBy, info);
									displayWall(postedTo, vpwall, info, addFriendButton);
								}
							});
							wallString +=
									"<br /><strong>" + poster + "</strong>" + " > " + "<strong>" + postee + "<br />" +
									"<i>" + time + "</i><br />" +
									"<span>" + post + "</span><br />";
							if (!commentString.equals("")) {
								String[] comments = commentString.split("~~");
								for (String comment : comments) {
									String[] commentFeatures = comment.split("~");
									final String commenter = commentFeatures[0];
									String commentText = commentFeatures[1];
									final Anchor posterCommenter = new Anchor(usernameToName.get(commenter));
									posterCommenter.addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											displayProfileInfo(commenter, info);
											displayWall(commenter, vpwall, info, addFriendButton);
										}
									});
									wallString +=
										"<div style='margin-left:15px'>" +
											"<strong>" + posterCommenter + "</strong>" + "<br />" +
											"<span>" + commentText + "</span><br />"+
										"</div>";
								}
							}
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
									addComment(USERNAME, commentBox.getText(), postId, vpwall, info);
									
								}
							});
							vpwall.add(commentBox);
							vpwall.add(commentButton);
						}
					}
				});
		
	}
	
	
	private void displayFriendReccs(final VerticalPanel friendReccs) {
		parent.getDatabaseService().staticFriendReq(USERNAME,
		new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				parent.popupBox("RPC failure", "staticFriendReq");
			}
			public void onSuccess(List<String> results) {
				HTML friends = new HTML();
				String friendRecc = "We think you should be friends with: <br />";
				for (String result : results) {
					friendRecc += result + "<br />";
				}
				friends.setHTML(friendRecc);
				friendReccs.add(friends);
				
			}
		});
	
	}
	
}
