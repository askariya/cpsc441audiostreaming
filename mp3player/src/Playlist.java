import java.util.ArrayList;

public class Playlist {
	private String playlistName;
	private ArrayList<String> songArray;
	
	public Playlist(String name){
		this.playlistName = name;
		this.songArray = new ArrayList<String>();
	}
	
	/**
	 * Gets the name of the playlist
	 * @return String of playlist name
	 */
	public String getName(){
		return playlistName;
	}
	
	public int getPlaylistSize(){
		return songArray.size();
	}
	
	
	/**
	 * Adds a song to the playlist
	 * @param songName
	 */
	public boolean addSong(String songName){
		int songIndex = searchPlaylist(songName);
		
		if(songIndex != -1) {
			System.out.println("Song in playlist");
			return false;
		}
		else{
			songArray.add(songName);
		}
		return true;
	}
	
	
	/**
	 * Removes a song from playlist
	 * @param songName
	 */
	public boolean removeSong(String songName){
		
		int songIndex = searchPlaylist(songName);
		
		if(songIndex == -1) {
			System.out.println("Song not in playlist");
			return false;
		}
		else{
			songArray.remove(songIndex);
		}
		return true;
	}
	
	/**
	 * returns a song at a specific index
	 * @param i
	 * @return
	 */
	public String getSong(int i){
		return songArray.get(i);
	}

	public String exportAllSongs(){
		String allSongs = "PLAYLIST:" + playlistName + "|";
			for (String s : songArray)
			{
				allSongs += s + "|";
			}

		return allSongs;
	}

		public void importAllSongs(String allsongs){
			if(allsongs == null || allsongs.isEmpty()){
				return;
			}
			for (String song: allsongs.split("|")){
         this.addSong(song);
      }
	}
	
	/**
	 * @param songName
	 * @return index of song (or -1 if song doesn't exist in playlist)
	 */
	public int searchPlaylist(String songName){
		
		for(int i = 0; i < songArray.size(); i++){
			
			if(songArray.get(i).equals(songName)){
				return i;
			}
		}
		return -1; //No song found
	}
	
	/**
	 * @param index
	 * @return the name of the previous song in the playlist (NULL if no song precedes the current one)
	 */
	public String getPreviousSong(int index){
		
		if((index > 0) && (index < getPlaylistSize())){ //check the song is not the first
			return getSong(--index); //return name of previous song
		}
		else{
			return null; //otherwise return null
		}
		
	}
	
	/**
	 * @param index
	 * @return the name of the next song in the playlist (NULL if no song follows the current one)
	 */
	public String getNextSong(int index){
		
		if((index < (getPlaylistSize()-1)) && (index >= 0)){ // check that the song is not the last one
			return getSong(++index); //return the name of the next song
		}
		else{
			return null;
		}
	}
	
	
} // END OF PLAYLIST CLASS




