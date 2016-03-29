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
         System.out.println("Connected to client at Port #" + sock.getPort());
         new Thread(new MultiThreadServer(sock)).start();
      }
   }
   
   /**
    * Run method for the multithreaded Server
    */
   public void run() {
	   
	   streamAudio();
	   
	   try {
		clientSocket.close();
	   }catch (IOException e) {
		   e.printStackTrace();
		} 
	   
	   System.out.println("Server: End");
  }
   
   
  public void streamAudio(){
	   try{
		   
		   FileInputStream in = new FileInputStream("king_rat.wav");
		   
		   OutputStream out = clientSocket.getOutputStream(); //get the output stream to the client
		   byte buffer[] = new byte[8192];
		   int count;
		   while ((count = in.read(buffer)) != -1) //write the audio to the client
			   out.write(buffer, 0, count);
          
	   }catch (IOException e) {
		   System.out.println(e);
	   }
  }
   
   
}


