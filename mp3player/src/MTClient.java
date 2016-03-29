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
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class MTClient {

	public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
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
        
     
        /**************************Start of actual implementation*********************/
        
        while(true){
        	// Get user input and send to the server
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine();
        	
        	//split command into two parts (at the space)
        	String[] splitCmd = line.split(" ", 2);
        	
            /**
             * PLAY Command
             */
            if(splitCmd[0].contains("play")){
            	
            	outBuffer.println(line);
            	String response = inBuffer.readLine();
                
            	if(response.equals("song available")){
            		//Activate a PlayWAV thread to play the song
            		new Thread(player).start();
            	}
            	else if(response.equals("song unavailable")){
            		System.out.println("That song does not exist!");
            	}
            	else if(response.equals("invalid command")){
            		System.out.println("Invalid use of the 'play' command!");
            	}
            }
            
            /**
             * RESUME command
             */
            else if(line.contains("resume")){
            	player.resumeAudio();
            }
            
            /**
             * PAUSE command
             */
            else if(line.contains("pause")){
            	//TODO implement code to stop playback
            	player.pauseAudio();
            }
            
            //TODO Continue with Client execution (prompting new commands)
            
            else if(line.contains("logout")){
            	break;
            }
        }
         
        player.closeAudioInputStream();
        System.out.println("Client: END");
    }
	
}


/**
 * This class handles both the streaming and the playing of the audio file
 */
class PlayWAV extends Thread{
	
	//holds a copy of the current client socket
	private Socket clientSocketWAV;
	private SourceDataLine sdline;
	private volatile boolean isPlaying;
	private AudioInputStream din;
	
	public PlayWAV(Socket sock){
		this.clientSocketWAV = sock; 
		isPlaying = false;
		sdline = null;
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
    
    
    public void closeAudioInputStream(){
    	try {
			din.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Controls the actual streaming and playback of the audio
     */
    public void run(){
    	isPlaying = true;
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
 				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {	

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
 				// Stop
 				sdline.drain();
 				sdline.stop();
 				sdline.close();
 			}
 		
        }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e) //all the possible exceptions
        {
        	System.out.println(e);
        }
//        
    }
    
}