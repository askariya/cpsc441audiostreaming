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
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MultiThreadServer implements Runnable {
   Socket clientSocket;
   
   
   MultiThreadServer(Socket csocket) {
      this.clientSocket = csocket;
   }

   
   public static void main(String args[]) throws Exception {
      
	  ServerSocket serverSocket = new ServerSocket(1234);
      System.out.println("Listening");
      
      
      //while the server is running, keep accepting connections from TCP clients
      while (true) {
         Socket sock = serverSocket.accept();
         System.out.println("Connected");
         new Thread(new MultiThreadServer(sock)).start();
      }
   }
   
   /**
    * Run method for the multithreaded Server
    */
   public void run() {
      
	   
	   /******* OLD Code *******/
//	   try {
//    	  
//         PrintStream pstream = new PrintStream(clientSocket.getOutputStream());
//         for (int i = 100; i >= 0; i--) {
//            pstream.println(i + 
//            " bottles of beer on the wall");
//         }
//         pstream.close();
//         clientSocket.close();
//      }
//      catch (IOException e) {
//         System.out.println(e);
//      }
	   
	   
	   /******* NEW Code *******/
	   try{
		   
		   FileInputStream in = new FileInputStream("why.mp3");
		   
		   OutputStream out = clientSocket.getOutputStream(); //get the output stream to the client
		   byte buffer[] = new byte[4096];
		   int count;
		   while ((count = in.read(buffer)) != -1) //write the audio to the client
			   out.write(buffer, 0, count);
           
	   }catch (IOException e) {
		   System.out.println(e);
	   }
	   
	   
      System.out.println("Server: End");
  }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   /*****************************************************ServerSelect Code*********************************************/
   
   
   
   /**
    * Method for listing all the files in a server 
    * 
    * @param clientChannel
    * @param buff
    * @throws IOException
    */
   public static void tcp_list(SocketChannel clientChannel, ByteBuffer buff) throws IOException
   {
       String filePath = System.getProperty("user.dir");
       String outputString = "";
       File folder = new File(filePath);
       File[] listOfFiles = folder.listFiles();
       // Send each file name in server directory
       for (int i = 0; i < listOfFiles.length; i++)
       {
           if (listOfFiles[i].isFile())
           {
               outputString = listOfFiles[i].getName() + "\n";
               buff = ByteBuffer.wrap(outputString.getBytes());
               clientChannel.write(buff); 
           }
       }
       String eof = "eof\n";
       buff = ByteBuffer.wrap(eof.getBytes());
       clientChannel.write(buff);
   }
   
   /*************************************AUDIO STREAMING CODE*************************************************/
  
   /**
    * Code for streaming audio
    * @param clientChannel
    * @param buff
    * @throws IOException
    */
   public static void tcp_playsong(String fileName, SocketChannel clientChannel, ByteBuffer buff) throws IOException
   {
   	//parse string to appear in mp3 format
   	String songName = fileName.substring(0, fileName.length()-1).concat(".mp3");
   	
   	//find the mp3 file
   	File soundFile = new File(songName); 
   	
   	//Check that the mp3 file exists
   	//TODO send message back to the client if the song doesn't exist instead of throwing an exception
   	 if (!soundFile.exists() || !soundFile.isFile()) 
            throw new IllegalArgumentException("Not a file: " + soundFile);
   	 
   	 FileInputStream in = new FileInputStream(soundFile); //use this to read the file
//   	 OutputStream out = clientChannel.socket().getOutputStream(); // <-- This didn't work
   	 
   	 
   	 
   	 int count;
   	 byte buffer[] = new byte[4096];
   	 
   	 //read the contents of the audio file and write into a buffer
        while ((count = in.read(buffer)) != -1){
       	 buff = ByteBuffer.wrap(buffer);
       	 clientChannel.write(buff);
       	 
//       	 out.write(buffer, 0, count); //write contents to client <-- This didn't work
        }
        System.out.println("Finished server side 'play'");
   }
   
   /********************************************************************************************************/
   
   
   
}