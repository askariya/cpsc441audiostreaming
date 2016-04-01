/*
 * 
 * Code retrieved from:
 * 
 * http://www.tutorialspoint.com/javaexamples/net_multisoc.htm * 
 * 
 */
//TODO implement 'next' function
//TODO implement Playlists

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MTServer implements Runnable {
   Socket clientSocket;
   List <User> users; // A List of the users connecting to the Server
    
   
   MTServer(Socket csocket) {
      this.clientSocket = csocket;
   }

   
   public static void main(String args[]) throws Exception {
	  //loadUsers();
	  ServerSocket serverSocket = new ServerSocket(9001);
      System.out.println("Listening");
      
      
      //while the server is running, keep accepting connections from TCP clients (if any are available)
      while (true) {
         Socket sock = serverSocket.accept();
         System.out.println("Connected to client at Port #" + sock.getPort());
         new Thread(new MTServer(sock)).start();
      }
   }
   
   /**
    * Run method for the multithreaded Server
    */
   public void run(){
	   try{   
		   PrintWriter outBuffer = new PrintWriter(clientSocket.getOutputStream(), true);
		   BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		   String line = "";
		   StreamAudio streamer = null;
		   
		   
		   /*TODO Implement a login here
		    * 
		    * Prompt for username and password
		    * Check if it already exists
		    * If not; allow creation 
		   */
		   
		   while(true){
			   
			   line = inBuffer.readLine();
			   if(line != null){
				   System.out.println("Client@Port#"+ clientSocket.getPort() +": "+ line);
				   String[] splitCmd = line.split(" ", 3);
				   
				   /** Play Command
				    * 
				    * Format: PLAY <SONG_NAME> 
				    */
				   if(splitCmd[0].equals("play"))
				   {
					   if(splitCmd.length != 2)
		               {
						   System.out.println("invalid command");
						   outBuffer.println("invalid command");
		               }
					   
					   else if(splitCmd[1].length() < 1)
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command"); //send error back to client
		            	}
					   
					   else{
						   String fileName = splitCmd[1];
						   
						   if(checkFileExists(fileName)){
							   System.out.println("song available");
							   outBuffer.println("song available");  
							   streamer = new StreamAudio(fileName,clientSocket);
							   streamer.start();
						   }
						   else{
							   outBuffer.println("song unavailable");
							   System.out.println("song unavailable");
						   }
					   }
					   
				   } //end of PLAY
				   
				   /** Stop command
				    * Format: STOP 
				    */
				   else if(line.equals("stop")){
					   streamer.emptyBuffer();
				   }
				   
				   
				   /**List command
				    * Format: LIST 
				    */
				   else if(line.equals("list")){
					   listSongs(clientSocket, outBuffer);
				   }
				   
				   
				   /**
		             * CREATE_PLAYLIST
		             * create and name a playlist
		             */
				   else if(splitCmd[0].equals("create_playlist")){
					   
					   if((splitCmd.length != 2))
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command"); //send error back to client
		            	}
		            	else if(splitCmd[1].length() < 1)
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command"); //send error back to client
		            	}
		            	
		            	else{
		            		String playlistName = splitCmd[1]; // read the playlist name 
		            		Playlist p = new Playlist(playlistName); //create new Playlist object
		            		
		            		//TODO add to list of Playlists
		            		//currentUser.addToListOfPlaylists(p)
		            		
		            		
		            		System.out.println("valid playlist name");
		            		outBuffer.println("valid playlist name"); //send verification back to Client
		            	}
				   }
				   
				   /**
				    * REMOVE_PLAYLIST
				    * remove a user's playlist
				    */
				   else if(splitCmd[0].equals("remove_playlist")){
					   
					   if((splitCmd.length != 2))
					   {
						   System.out.println("invalid command");
		            	   outBuffer.println("invalid command"); //send error back to client
		            	   
					   }
		               else if(splitCmd[1].length() < 1)
		               {
		            	   System.out.println("invalid command");
		            	   outBuffer.println("invalid command"); //send error back to client
		            	   
		               }
		               else{
		            	   String playlistName = splitCmd[1]; // read the playlist name 
		            		
		            		//TODO Remove from list of Playlists
		            		//currentUser.removeFromListOfPlaylists(p)
		            	   
		            	   System.out.println("valid playlist name");
		            	   outBuffer.println("valid playlist name"); //send verification back to Client
		            	}
					   
				   }
				   
				   /**
		             * ADD_TO_PLAYLIST
		             * Adds a song to a playlist
		             */
				   else if(splitCmd[0].equals("add_to_playlist")){
					   
					   if(splitCmd.length != 3)
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command");
		            	}
		            	else if(splitCmd[1].length() < 1 || splitCmd[2].length() < 1 )
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command"); //send error back to client
		            	}
		            	
		            	else{
		            		String songName = splitCmd[1];
		            		String playlistName = splitCmd[2];
		            		
		            		/*TODO Check that the song and playlist are on the server
		            		 * Add an && statement that also checks for playlist specific to the User
		            		 */
		            		if(checkFileExists(songName)){
		            			
		            			//....
		            			
		            			System.out.println("valid playlist addition");
							}
		            		else{
		            			outBuffer.println("song or playlist unavailable");
		            			System.out.println("song or playlist unavailable");
		            		}
		            		
		            		
		            	}
		            }//End of add_to_playlist
		            
				   /**
		             * REMOVE_FROM_PLAYLIST
		             * Removes a song from a playlist
		             */
		            else if(splitCmd[0].equals("add_to_playlist")){
		            	
		            	if(splitCmd.length != 3)
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command");
		            	}
		            	else if(splitCmd[1].length() < 1 || splitCmd[2].length() < 1 )
		            	{
		            		System.out.println("invalid command");
		            		outBuffer.println("invalid command"); //send error back to client
		            	}
		            	
		            	/*TODO Check if the playlist (and song within) exists 
		            	 * 
		            	 * Call method to check that the playlist exists for current user
		            	 * Call method to check that the song exists within the playlist
		            	 */

		            }
				   
				   
				   
				   
				   /** Logout command
				    *  Format: LOGOUT
				    */
				   else if(line.equals("logout")){
					   break;   
				   }
			   }
			   
			   
		   } //end of while loop
		   
			   clientSocket.close();   
			   System.out.println("Client@Port#"+ clientSocket.getPort() +": Disconnected");
		   }catch (IOException e) {
			   e.printStackTrace();
			   } 
		   
   }
  
  
  public boolean checkFileExists(String fileName) throws IOException
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
	 * Method that lists all the songs in the Server
	 * @param clientChannel
	 * @param buff
	 * @throws IOException
	 */
	public void listSongs(Socket cSocket, PrintWriter outBuffer) throws IOException
	{
		String filePath = System.getProperty("user.dir");
		String outputString = "";
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
	      // Send each file name in server directory
	      for (int i = 0; i < listOfFiles.length; i++)
	      {
	          if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".wav"))
	          {
	              outputString = listOfFiles[i].getName();
	              outBuffer.println(outputString);
	          }
	      }
	      String eof = "eof";
	      outBuffer.println(eof);
	      
	}
	

	public void loadUsers() throws IOException
	{
      String filePath = System.getProperty("user.dir");
      File folder = new File(filePath);
      File[] listOfFiles = folder.listFiles();
      // Send each file name in server directory
      for (int i = 0; i < listOfFiles.length; i++)
      {
          if (listOfFiles[i].isFile() && listOfFiles[i].getName().matches("^USER-.+"))
          {
              User tempuser = new User(listOfFiles[i].getName().substring(5));
              tempuser.loadAccountData();
              users.add(tempuser);
          }
      }
      // Add default admin and user
       User admin = new User("admin", "pass", "admin");
       User freshuser = new User("user", "pass", "user");
       users.add(admin);
       users.add(freshuser);
       
	}
   
   
  
  
  
}


/**
 * Class for threading the server-side audio stream
 * @author askariya
 *
 */
class StreamAudio extends Thread{
	
	private Socket clientSocket;
	private String fileName;
	private OutputStream out;
	
	public StreamAudio(String fileName, Socket clientSocket){
		this.fileName = fileName;
		this.clientSocket = clientSocket;
	}
	
	
	public void run(){
		streamAudio(fileName);
	}
	
	
	public void streamAudio(String fileName){
		   try{
			   
			   FileInputStream in = new FileInputStream(fileName);
			   
			   out = clientSocket.getOutputStream(); //get the output stream to the client
			   byte buffer[] = new byte[4096];
			   int count;
			   while ((count = in.read(buffer)) != -1) //write the audio to the client
				   out.write(buffer, 0, count);
	          
		   }catch (IOException e) {
			   System.out.println(e);
		   }
	 }
	
	public void emptyBuffer() throws IOException{
		out.flush();
		
	}
}


