import java.util.ArrayList;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class User {
	private String userType;
	private String userName;

	private String password;
	private List <Playlist> listOfPlaylists;

/* User Account Creation */
	public User(String name){
		this.userName = name;
	}

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
	public void saveAccountData() throws IOException {
		try{   
			FileWriter fw = new FileWriter("USER-" + userName);
		fw.write(password + "\n");
		fw.write(userType + "\n");
		for (Playlist list: listOfPlaylists) {
			fw.write(list.exportAllSongs() + "\n");
		}
	} catch (IOException e) {
			   e.printStackTrace();
			   } 
	}

// FIX REGEX
	public void loadAccountData() throws IOException {
			try {
			BufferedReader br = new BufferedReader(new FileReader("USER-" + userName));
		    password = br.readLine();
		    userType = br.readLine();
		    String list = br.readLine();

		    while (list != null) {
		    	Pattern pattern = Pattern.compile("^PLAYLIST:(.*)\\0(.*)$");
					Matcher matcher = pattern.matcher(list);
					if (matcher.find())
					{
						Playlist tempList = new Playlist (matcher.group(0));
						tempList.importAllSongs(matcher.group(1));
						listOfPlaylists.add(tempList);
					}
		        list = br.readLine();
		    }
		    br.close();
		   }
		    catch (IOException e) {
			   e.printStackTrace();
			   } 
	}

}