import java.util.ArrayList;

public class User {
	private String userType;
	private String userName;

	private String password;
	private Playlist[] listOfPlaylists;

/* User Account Creation */
	public User(String name, String pass, String type){
		this.userName = name;
		this.userType = type;
		this.password = pass;
	}

/* Authentication */
	public String getUserName(){
		return userName;
	}

	public boolean checkUserName(String input){
		return input.matches(userName);
	}

	public boolean checkPassword(String input){
		return input.matches(password);
	}

	public String getUserType(){
		return userType;
	}

/* Persistent Account Data */
	public void saveAccountData(){
		// Overwrite file called USER-userName.data
		// Write as CSV
	}

	public void loadAccountData(){
		// read file called USER-userName.data
		// store each CSV into private variables
	}

/* Persistent Account Data */

}