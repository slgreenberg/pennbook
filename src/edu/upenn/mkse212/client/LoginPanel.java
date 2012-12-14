package edu.upenn.mkse212.client;

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
import com.google.gwt.user.client.ui.AbsolutePanel;

public class LoginPanel {
	

	PennBook parent;
	
	public LoginPanel(PennBook theParent) {
		this.parent = theParent;
	}
	
	
	void display() {
		final TextBox usernameField = new TextBox();
		final TextBox passwordField = new TextBox();
		final Button loginButton = new Button("Login");
		final Button signupButton = new Button("Don't have an account? Signup!");
		AbsolutePanel p = new AbsolutePanel();
		p.setWidth("500px");
		p.setHeight("300px");
		p.add(new Label("Username:"), 30, 35);
		p.add(usernameField, 155, 30);
		p.add(new Label("Password:"), 30, 85);
		p.add(passwordField, 155, 80);
		p.add(loginButton, 220, 130);
		p.add(signupButton, 400, 140);
		RootPanel.get("rootPanelContainer").clear();
		RootPanel.get("rootPanelContainer").add(p);
		usernameField.setFocus(true);
		usernameField.selectAll();
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final String username = usernameField.getText();
				parent.getDatabaseService().validateLogin(username, passwordField.getText(),
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								parent.popupBox("RPC failure", "Cannot communicate with the server");
							} 
							public void onSuccess(Boolean result) {
								if (!result.booleanValue()) {
									parent.popupBox("Error", "Login incorrect");
								} else {
									parent.getWallPanel().display(username);
								}
							}
				});
			}
		});
		
		signupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				parent.getCreateAccountPanel().display();
			}
		});
		
	}
	

}
