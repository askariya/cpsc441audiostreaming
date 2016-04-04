import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class MTClient {

	public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: MTClient <Server IP> <Server Port>");
            System.exit(1);
        }

        // Initialize a client socket connection to the server
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));

        // Initialize input and an output stream for the connection(s)
		PrintWriter outBuffer = new PrintWriter(clientSocket.getOutputStream(), true);
		
        BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        //Assign the audio player to the current client
        PlayWAV player = new PlayWAV(clientSocket);     
        
        
      promptAuthentication(inFromUser, inBuffer, outBuffer);
        
        /********************************************COMMAND PROMPT************************************************/
        
        while(true){
        	// Get user input and send to the server
            System.out.print("\nPlease enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine();
        	if(player.threadOver){
        		System.out.println("IT's DEAD");
        		player.interrupt();
        	}
        	//split command into two parts (at the space)
        	String[] splitCmd = line.split(" ", 3);
        	
            /**
             * PLAY Command
             */
            if(splitCmd[0].equals("play") && player.audioStopped()){
            	outBuffer.println(line);
            	String response = inBuffer.readLine();
                
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("song available")){
            		//Activate a PlayWAV thread to play the song
            		player = new PlayWAV(clientSocket);
            		player.start();
            	}
            	else if(response.equals("song unavailable")){
            		System.out.println("That song does not exist!");
            	}
            	else if(response.equals("invalid command")){
            		System.out.println("Invalid command: play <song name>");
            	}
            }
            
            /**
             * RESUME Command
             */
            else if(line.contains("resume") && !player.isPlaying()){
            	System.out.println("Resuming Playback...");
            	player.resumeAudio();
            }
            
            /**
             * PAUSE Command
             */
            else if(line.contains("pause") && player.isPlaying()){
            	System.out.println("Paused Playback...");
            	player.pauseAudio();
            }
            
            //TODO Continue with Client execution (prompting new commands)
            
            
            /**
             * STOP Command
             */
            else if(line.equals("stop")){
            	
            	System.out.println(player.audioStopped()); //TODO delete this
            	
            	if(!player.audioStopped())             	{
            		player.stopAudio();//stop audio playback
                	outBuffer.println(line); //send the stop command to the server
            	}
            	
            }
            
            /**
             * LIST Command
             */
            else if(line.equals("list")){
            	outBuffer.println("list");
            	
            	System.out.println("\n-----------SONGS AVAILABLE-------------\n");
            	String response;
            	while(!(response = inBuffer.readLine()).equals("eof")){
            		System.out.println(response); //print file names to the user
            	}
            	System.out.println("\n");
            	
            	
            	System.out.println("\n-----------PLAYLISTS AVAILABLE-------------\n");
            	
            	while(!(response = inBuffer.readLine()).equals("eof")){
            		System.out.println(response); //print playlist names to the user
            	}
            	System.out.println("\n");
            }
            
            /**
             * CREATE_PLAYLIST <playlist name>
             * create and name a playlist
             */
            else if(splitCmd[0].equals("create_playlist") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("valid playlist name"))
                {
                	System.out.println("Playlist " + splitCmd[1] + " created");
                }
            	
            	else if(response.equals("invalid command"))
                {
                	System.out.println("Invalid command: create_playlist <playlist name>");
                }
            	
            	else if(response.equals("playlist already exists")){
            		System.out.println("That playlist has already been created");
            	}

            }
            
            /**
             * VIEW <playlist name>
             * view a playlist
             */
            else if(splitCmd[0].equals("view") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("invalid command"))
                {
                	System.out.println("Invalid command: create_playlist <playlist name>");
                }
            	
            	else if(response.equals("invalid playlist")){
            		System.out.println("That playlist does not currently exist");
            	}
            	
            	else if(response.equals("valid playlist")){
            		
            		System.out.println("\n-----------Playlist: "+ splitCmd[1] +"-------------\n");
                	int i = 1;
                	while(!(response = inBuffer.readLine()).equals("eof")){
                		System.out.println(i + ". " + response); //print playlist names to the user
                		i++;
                	}
                	System.out.println("\n");
            	}
            }
            
            
            
            /**
             * REMOVE_PLAYLIST <playlist name>
             * remove a playlist
             */
            else if(splitCmd[0].equals("remove_playlist") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("valid playlist name"))
                {
                	System.out.println("Playlist " + splitCmd[1] + " removed");
                }
            	
            	else if(response.equals("invalid command"))
                {
                	System.out.println("Invalid command: remove_playlist <playlist name>");
                }
            	else if(response.equals("playlist unavailable")){
            		System.out.println("Playlist unavailable");
            	}
            }
            
            /**
             * PLAY_PLAYLIST <playlist name>
             * play a playlist
             */
            else if(splitCmd[0].equals("play_playlist") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("valid playlist"))
                {
            		String plistName = splitCmd[1];
            		Playlist playlist = new Playlist(plistName);
            		
            		while(!(response = inBuffer.readLine()).equals("eof")){
            			playlist.addSong(response); //save all the songs into a temporary playlist
                	}
            		
            		//TODO play playlist
                }
            	
            	else if(response.equals("invalid command"))
                {
                	System.out.println("Invalid command: play_playlist <playlist name>");
                }
            	else if(response.equals("playlist unavailable")){
            		System.out.println("Playlist unavailable");
            	}
            }
            
            
            
            /**
             * ADD_TO_PLAYLIST <song name> <playlist name>  
             */
            else if(splitCmd[0].equals("add_to_playlist") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("invalid command")){
            		System.out.println("invalid command: add_to_playlist <song name> <playlist name>");
            	}
            	
            	else if(response.equals("valid playlist addition")){
            		System.out.println("Added " + splitCmd[1] + " to " + splitCmd[2]);
            	}
            	
            	else if(response.equals("song or playlist unavailable")){
            		System.out.println("Song or playlist is unavailable");
            	}
            }
            
            
            
            
            
            /**
             * REMOVE_FROM_PLAYLIST <song name> <playlist name>  
             */
            else if(splitCmd[0].equals("remove_from_playlist") && player.audioStopped()){
            	
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("invalid command")){
            		System.out.println("invalid command: remove_from_playlist <song name> <playlist name>");
            	}
            	
            	else if(response.equals("valid playlist removal")){
            		System.out.println("Removed " + splitCmd[1] + " from " + splitCmd[2]);
            	}
            	
            	else if(response.equals("song or playlist unavailable")){
            		System.out.println("Song or playlist is unavailable");
            	}
            	
            	else if(response.equals("invalid playlist removal")){
            		System.out.println("Song or playlist is unavailable");
            	}
            	
            	
            }


            
            /**
             * ADD_SONG <song name>
             */
            else if(splitCmd[0].equals("add_song") && player.audioStopped()){
            
            	outBuffer.println(line); // send to Server
            	String response = inBuffer.readLine();
            	System.out.println("Server Response: " + response);
            	
            	if(response.equals("invalid command")){
            		System.out.println("invalid command: add_song <song name>");
            	}
            	
            	else if(response.equals("authenticated")){
            		
            		String songName = splitCmd[1];
            		
            		if(checkFileExists(songName)) //check that the song exists on the user side
            		{
            			sendAudioFile(songName, clientSocket); //
            		}
            	}
            	else if(response.equals("unauthorized")){
            		System.out.println("You do not have permission to perform this action");
            	}
            	
            }
            
            /**
             * REMOVE_SONG <song name>
             */
            else if(splitCmd[0].equals("remove_song") && player.audioStopped()){
                
                outBuffer.println(line); // send to Server
                String response = inBuffer.readLine();
                System.out.println("Server Response: " + response);
                
                if(response.equals("invalid command")){
                    System.out.println("invalid command: remove_song <song name>");
                }
                
                else if(response.equals("authenticated")){
                    String songName = splitCmd[1];
                    System.out.println("The song '" + songName + "' was deleted from the server");
                }
                else if(response.equals("unauthorized")){
                    System.out.println("You do not have permission to perform this action");
                }
            }


            /**
             * CREATE_USER <username> password
             */
            else if(splitCmd[0].equals("create_user") && player.audioStopped()){
                
                outBuffer.println(line); // send to Server
                String response = inBuffer.readLine();
                System.out.println("Server Response: " + response);
                
                if(response.equals("invalid command")){
                    System.out.println("invalid command: create_user <username> <password>");
                }
                
                else if(response.equals("account already exists")){
                    String account = splitCmd[1];
                    System.out.println("The user '" + account + "' already exists");
                }
                else if(response.equals("must be admin")){
                    System.out.println(response);
                }
                else if(response.equals("user created")){
                    String account = splitCmd[1];
                    System.out.println("The user '" + account + "' was created");
                }
            }
            

            /**
             * REMOVE_USER <username>
             */
            else if(splitCmd[0].equals("remove_user") && player.audioStopped()){
                
                outBuffer.println(line); // send to Server
                String response = inBuffer.readLine();
                System.out.println("Server Response: " + response);
                
                if(response.equals("invalid command")){
                    System.out.println("invalid command: remove_user <username>");
                }
                
                else if(response.equals("account doesn't exist")){
                    String account = splitCmd[1];
                    System.out.println("The user '" + account + "' already exists");
                }
                else if(response.equals("must be admin")){
                    System.out.println(response);
                }
                else if(response.equals("user removed")){
                    String account = splitCmd[1];
                    System.out.println("The user '" + account + "' was removed");
                }
            }
            
            /**
             * LOGOUT
             * Disconnects the client from the server
             */
            else if(line.contains("logout")){
            	outBuffer.println("logout");
            	break;
            }

            else {
                    System.out.println("unavailable command");
                }
        }
         
        System.out.println("Client: END");
        clientSocket.close();
    }// END OF MAIN
	
	
	/**
	 * Method for prompting the user for username/password and sending to the Server then awaiting response
	 * @param inFromUser
	 * @param inBuffer
	 * @param outBuffer
	 * @throws IOException
	 */
	public static void promptAuthentication(BufferedReader inFromUser, BufferedReader inBuffer, PrintWriter outBuffer) throws IOException{
		
		boolean authenticated = false;
        String authResponse = "";
        
        while(!authenticated) 
        {
        	System.out.print("Please enter a username: "); //prompt for username
            String username = inFromUser.readLine();
            
            System.out.print("Please enter a password: "); //prompt for password
            String password = inFromUser.readLine();
            
            outBuffer.println(username + " " + password); //send the user info to the server to be authenticated
            authResponse = "";
            
            while(authResponse.equals("")) //wait for a response
            {
                authResponse = inBuffer.readLine();
                
                if(authResponse.equals("authenticated"))
                	authenticated = true; //if the response is positive --> exit loop
                System.out.println(authResponse);
            }
            
            if(!authenticated) //if still not a positive response --> notify user
            	System.out.println("Invalid username or password");
        }
	}
	
	/**
	 * checks that a file exists in the current directory
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean checkFileExists(String fileName) throws IOException
	{
		String filePath = System.getProperty("user.dir");
		File folder = new File(filePath);
	    File[] listOfFiles = folder.listFiles();
	    // Send each file name in server directory
	    for (int i = 0; i < listOfFiles.length; i++)
	    {
	   	    //check that the file exists
	        if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(fileName) && fileName.contains(".wav"))
	        {
	            return true;
	        }
	    }
	      
	    return false; 
	}
	
	/**
	 * Transfers a song to the Server
	 * @param fileName
	 */
	public static void sendAudioFile(String songName, Socket clientSocket) throws IOException{

		File myFile = new File(songName);
		byte[] mybytearray = new byte[(int) myFile.length()];
	      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
	      bis.read(mybytearray, 0, mybytearray.length);
	      OutputStream os = clientSocket.getOutputStream();
	      os.write(mybytearray, 0, mybytearray.length);
	      os.flush();
		System.out.println("reached client end");
	}
	
	
}


/**
 * This class handles both the streaming and the playing of the audio file
 */
class PlayWAV extends Thread {
	
	//holds a copy of the current client socket
	private Socket clientSocketWAV;
	private SourceDataLine sdline;
	private volatile boolean isPlaying, stopped, finishedSong;
	private AudioInputStream din;
	public Boolean threadOver;
	
	public PlayWAV(Socket sock){
		this.clientSocketWAV = sock; 
		isPlaying = false;
		stopped = true;
		sdline = null;
		threadOver = false;
	}
	
	// The parent thread.
    private Thread parent;
    
    // Constructor to set the parent thread
    public PlayWAV(Thread parent)
    {
        setParentThread(parent);
    }
    
    // Hide the default constructor to force calling the other.
    private PlayWAV(){}
    
    // Set the parent thread, so we can test to see if the parent
    // thread of this thread is alive.  If it is not, we will exit
    // this thread after closing the line and the stream.
    public void setParentThread(Thread parent)
    {
        this.parent = parent;
    }
    
    /**
     * Pauses the playback of audio
     */
    public void pauseAudio(){
    	
    	if(isPlaying) //check that the audio needs to be paused
    		isPlaying = false;
    }
    
    /**
     * Resumes audio playback
     */
    public void resumeAudio(){
    	if(!isPlaying) //check that the audio is not already paused
    		isPlaying = true;
    }
    
    public void stopAudio(){
    		sdline.close();
    		stopped = true;
    }
    
    public boolean audioStopped(){
    	return stopped;
    }
    
    public boolean isPlaying(){
    	return isPlaying;
    }
    
    public boolean finishedSong(){
    	return finishedSong;
    }
    
    /**
     * Controls the actual streaming and playback of the audio
     */
    public void run(){
    	isPlaying = true;
    	stopped = false;
    	din = null;
    	finishedSong = false;
        
        try{
        	//Read the audio from the server
        	InputStream inFromServer = new BufferedInputStream(clientSocketWAV.getInputStream());
        	
            AudioInputStream ais = AudioSystem.getAudioInputStream(inFromServer); 
             
            
            //EXTERNAL CODE (Playing the audio) 
            //ONLY works properly for .wav files
            AudioFormat baseFormat = ais.getFormat();
		AudioFormat decodedFormat = new AudioFormat(
 					AudioFormat.Encoding.PCM_SIGNED,
 					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
 					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
 					false);
 			din = AudioSystem.getAudioInputStream(decodedFormat, ais);
 			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
 			sdline = (SourceDataLine) AudioSystem.getLine(info);
 			
 			if(sdline != null) {
 				sdline.open(decodedFormat);
 				byte[] data = new byte[4096];
 				// Start playing sound
 				sdline.start();
 				
 				int nBytesRead;
 				while (((nBytesRead = din.read(data, 0, data.length)) != -1)) {	

 					while(!isPlaying)
 					{
 					  try {
						Thread.sleep(1000);
 					  } catch (InterruptedException e) {
						e.printStackTrace();
 					  }
 					}
 					
 					sdline.write(data, 0, nBytesRead); 
 				}
 				sdline.drain();
 				sdline.stop();
 				sdline.close();
 				stopped = true;
 				finishedSong = true;
 				//din.close();
 			}
 		
        }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e) //all the possible exceptions
        {
        	System.out.println(e);
        }
        System.out.println("leaving thread");
        threadOver = true;
    }
    
}