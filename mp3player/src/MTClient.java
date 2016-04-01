import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
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
            }
            
            
            /**
             * ADD_TO_PLAYLIST <song> <playlist>  
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
             * REMOVE_FROM_PLAYLIST <song> <playlist>  
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
            	
            	
            }
            
            
            /**
             * LOGOUT
             * Disconnects the client from the server
             */
            else if(line.contains("logout")){
            	outBuffer.println("logout");
            	break;
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
            }
            
            if(!authenticated) //if still not a positive response --> notify user
            	System.out.println("Invalid username or password");
        }
	}
	
	
	
}


/**
 * This class handles both the streaming and the playing of the audio file
 */
class PlayWAV extends Thread {
	
	//holds a copy of the current client socket
	private Socket clientSocketWAV;
	private SourceDataLine sdline;
	private volatile boolean isPlaying, stopped;
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
    
    /**
     * Controls the actual streaming and playback of the audio
     */
    public void run(){
    	isPlaying = true;
    	stopped = false;
    	din = null;
        
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