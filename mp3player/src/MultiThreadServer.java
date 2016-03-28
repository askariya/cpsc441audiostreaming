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
      
	  ServerSocket serverSocket = new ServerSocket(9001);
      System.out.println("Listening");
      
      
      //while the server is running, keep accepting connections from TCP clients (if any are available)
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
		   
		   FileInputStream in = new FileInputStream("ocean_man.wav");
		   
		   OutputStream out = clientSocket.getOutputStream(); //get the output stream to the client
		   byte buffer[] = new byte[8192];
		   int count;
		   while ((count = in.read(buffer)) != -1) //write the audio to the client
			   out.write(buffer, 0, count);
           
	   }catch (IOException e) {
		   System.out.println(e);
	   }
	   
	   
      System.out.println("Server: End");
  }
   
   
   
}


