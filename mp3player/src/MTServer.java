/*
 * 
 * Code retrieved from:
 * 
 * http://www.tutorialspoint.com/javaexamples/net_multisoc.htm * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MTServer implements Runnable {
   Socket clientSocket;
   
   
   MTServer(Socket csocket) {
      this.clientSocket = csocket;
   }

   
   public static void main(String args[]) throws Exception {
      
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
	   
	   while(true){
		   
		   line = inBuffer.readLine();
		   
		   if(line != null){
			   System.out.println("Client @ Port #"+ clientSocket.getPort() +": "+ line);
			   String[] splitCmd = line.split(" ", 2);
			   
			   
			   /** Play Command
			    * 
			    * Format: PLAY <SONG_NAME> 
			    */
			   if(splitCmd[0].equals("play"))
			   {
				   if(splitCmd.length == 1)
	               {
					   System.out.println("invalid command");
					   outBuffer.println("invalid command");
	               }
				   else{
					   String fileName = splitCmd[1];
					   
					   if(checkFileExists(fileName)){
						   System.out.println("song available");
						   outBuffer.println("song available");
						   streamAudio(fileName);   
					   }
					   else{
						   outBuffer.println("song unavailable");   
					   }
				   }
				   
			   } // end of PLAY
			   
			   else if(line.equals("logout")){
				   break;   
			   }
		   }
		   
		   
	   } //end of while loop
	   
		 //  clientSocket.close();   
	   }catch (IOException e) {
		   e.printStackTrace();
		   } 
	   
	   System.out.println("Server: End");
  }
   
   
  public void streamAudio(String fileName){
	   try{
		   
		   FileInputStream in = new FileInputStream(fileName);
		   
		   OutputStream out = clientSocket.getOutputStream(); //get the output stream to the client
		   byte buffer[] = new byte[8192];
		   int count;
		   while ((count = in.read(buffer)) != -1) //write the audio to the client
			   out.write(buffer, 0, count);
          
	   }catch (IOException e) {
		   System.out.println(e);
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
          if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(fileName))
          {
              return true;
          }
      }
      
      return false;
  }
   
   
  
  
  
}


