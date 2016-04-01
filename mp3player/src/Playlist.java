import java.util.ArrayList;

public class Playlist {
	private String playlistName;
	private ArrayList<String> songArray;
	
	public Playlist(String name){
		this.playlistName = name;
		this.songArray = new ArrayList<String>();
	}
	
	/**
	 * Adds a song to the playlist
	 * @param songName
	 */
	public void addSong(String songName){
		songArray.add(songName);
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
			songArray.remove(songIndex);
		}
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
		String allSongs = "PLAYLIST:" + playlistName + "\0";
			for (String s : songArray)
			{
				allSongs += s + "\0";
			}

		return allSongs;
	}

		public void importAllSongs(String allsongs){
		String allSongs = "PLAYLIST:" + playlistName + "\0";
			for (String song: allSongs.split("\0")){
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
	
	
}

