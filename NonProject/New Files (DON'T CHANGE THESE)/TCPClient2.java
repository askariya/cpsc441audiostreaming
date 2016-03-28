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
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class TCPClient2 {

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
		PrintWriter outBuffer =
		new PrintWriter(clientSocket.getOutputStream(), true);
		
        BufferedReader inBuffer = 
          new BufferedReader(new
          InputStreamReader(clientSocket.getInputStream())); 

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = 
        new BufferedReader(new InputStreamReader(System.in)); 

        // Get user input and send to the server
        System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
        line = inFromUser.readLine(); 
        
   
        AudioInputStream din = null;
        
        try{
        	//Read the audio from the server
        	InputStream inFromServer = new BufferedInputStream(clientSocket.getInputStream());
        	
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
 			SourceDataLine sdline = (SourceDataLine) AudioSystem.getLine(info);
 			
 			if(sdline != null) {
 				sdline.open(decodedFormat);
 				byte[] data = new byte[4096];
 				// Start playing sound
 				sdline.start();
 				
 				int nBytesRead;
 				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {	
 				
 					sdline.write(data, 0, nBytesRead); 
 				}
 				System.out.println("exited loop");
 				// Stop
 				sdline.drain();
 				sdline.stop();
 				sdline.close();
 				din.close();
 			}
 		
        }catch(IOException e)
        {
        	System.out.println(e);
        }
//        
        
        System.out.println("Client: END");
        
        
    }
	
}



class PlayWAV extends Thread{
	
	// The parent thread.
    private Thread parent;
    
    // Constructor to set the parent thread
    public PlayWAV(Thread parent)
    {
        setParentThread(parent);
    }
    
    // Hide the default constructer to force calling the other.
    private PlayWAV(){}
    
    // Set the parent thread, so we can test to see if the parent
    // thread of this thread is alive.  If it is not, we will exit
    // this thread after closing the line and the stream.
    public void setParentThread(Thread parent)
    {
        this.parent = parent;
    }
    
    
    //run method (Where the audio can be played)
    public void run(){
    	
    }
    
}