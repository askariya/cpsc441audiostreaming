import java.util.ArrayList;


public class Playlist {
	private String playlistName;
	private ArrayList<String> playlistArray;
	
	public Playlist(String name){
		this.playlistName = name;
		this.playlistArray = new ArrayList<String>();
	}
	
	/**
	 * Adds a song to the playlist
	 * @param songName
	 */
	public void addSong(String songName){
		playlistArray.add(songName);
	}
	
	
	/**
	 * Removes a song from playlist
	 * @param songName
	 */
	public void removeSong(String songName){
		
		int songIndex = searchPlaylist(songName);
		
		if(songIndex == -1)
			System.out.println("Song not in playlist");
		else{
			playlistArray.remove(songIndex);
		}
	}
	
	/**
	 * returns a song at a specific index
	 * @param i
	 * @return
	 */
	public String getSong(int i){
		return playlistArray.get(i);
	}
	
	/**
	 * @param songName
	 * @return index of song (or -1 if song doesn't exist in playlist)
	 */
	public int searchPlaylist(String songName){
		
		for(int i = 0; i < playlistArray.size(); i++){
			
			if(playlistArray.get(i).equals(songName)){
				return i;
			}
		}
		return -1; //No song found
	}
	
	
}

