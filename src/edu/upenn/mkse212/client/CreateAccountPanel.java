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

public class CreateAccountPanel {
	

	PennBook parent;
	
	public CreateAccountPanel(PennBook theParent) {
		this.parent = theParent;
	}
	
	
	void display() {
		final TextBox usernameField = new TextBox();
		final TextBox passwordField = new TextBox();
		final TextBox firstnameField = new TextBox();
		final TextBox lastnameField = new TextBox();
		final TextBox emailField = new TextBox();
		final TextBox networkField = new TextBox();
		final TextBox birthdayField = new TextBox();
		final TextBox interestField = new TextBox();
		final Button signupButton = new Button("Sign Up");
		AbsolutePanel p = new AbsolutePanel();
		p.setWidth("500px");
		p.setHeight("500px");
		p.add(new Label("Username:"), 30, 35);
		p.add(usernameField, 155, 30);
		p.add(new Label("Password:"), 30, 85);
		p.add(passwordField, 155, 80);
		p.add(new Label("First Name:"), 30, 135);
		p.add(firstnameField, 155, 130);
		p.add(new Label("Last Name:"), 30, 185);
		p.add(lastnameField, 155, 180);
		p.add(new Label("Email:"), 30, 235);
		p.add(emailField, 155, 230);
		p.add(new Label("Network:"), 30, 285);
		p.add(networkField, 155, 280);
		p.add(new Label("Interests:"), 30, 335);
		p.add(interestField, 155, 330);
		p.add(new Label("Birthday:"), 30, 385);
		p.add(birthdayField, 155, 380);
		p.add(signupButton, 220, 430);
		RootPanel.get("rootPanelContainer").clear();
		RootPanel.get("rootPanelContainer").add(p);
		usernameField.setFocus(true);
		usernameField.selectAll();

		signupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final String username = usernameField.getText();
				parent.getDatabaseService().addUser(
					username, passwordField.getText(), firstnameField.getText(), lastnameField.getText(),
					emailField.getText(), networkField.getText(), interestField.getText(), birthdayField.getText(),
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								parent.popupBox("RPC failure", "Cannot communicate with the server");
							} 
							public void onSuccess(Boolean result) {
								if (!result.booleanValue()) {
									parent.popupBox("Error", "This username is already taken.");
								} else {
									parent.getWallPanel().display(username);
								}
							}
				});
			}
		});
	}
	

}
