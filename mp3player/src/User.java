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
		listOfPlaylists = new ArrayList<Playlist>();
	}

	public User(String name, String pass, String type){
		this.userName = name;
		this.userType = type;
		this.password = pass;
		
		listOfPlaylists = new ArrayList<Playlist>();
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
	
	
	/**
	 * Adds a playlist to the user-specific list of playlists
	 * @param p
	 */
	public void addToListOfPlaylists(Playlist p){
		listOfPlaylists.add(p);
	}
	
	/**
	 * Removes a playlist from the user-specific list of playlists
	 * @param p
	 */
	public void removeFromListOfPlaylists(Playlist p){
		
		int plIndex = searchListOfPlaylists(p);
		
		if(plIndex == -1)
		{
			System.out.println("Playlist does not exist!");
		}
		else{
			listOfPlaylists.remove(plIndex); //remove the playlist
		}
			
	}
	
	
	/**
	 * A method that searches the user's list of playlists
	 * @param p
	 * @return
	 */
	public int searchListOfPlaylists(Playlist p){
		
		String playlistName = p.getName();
		for(int i = 0;i <  listOfPlaylists.size(); i++){
			
			//if the playlist exists in the array 
			if(playlistName.equals(listOfPlaylists.get(i).getName())){
				return i; //return the index of the playlist
			}
		}
		
		return -1;
	}
	

}