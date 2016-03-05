/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 * Modified by: Group 33 - W2016
 */


import java.io.*; 
import java.nio.file.*;
import java.net.*; 

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


class TCPClient { 

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
        // Display the echo meesage from the server
        System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
        line = inFromUser.readLine(); 
        
        
        while (!line.equals("logout"))
        {
        	outBuffer.println(line); 
        	String[] splitCmd = line.split(" ", 2); //Split user input into 2 parts: command and filename
        	
			if (line.equals("list")) {
				while (true) {
					
					String temp = inBuffer.readLine();

					if (!temp.equals("eof")) {
						System.out.println(temp);
					} else {
						break;
					}
				}
			}
			
			/*************************************AUDIO STREAMING CODE*************************************************/
            
            // SPECIAL CASE: PLAY COMMAND
            //TODO splitCmd[0].contains("play") <-- use for selecting song later
            else if(splitCmd[0].contains("play")){
            	
            	String initialResponse = inBuffer.readLine();
				if (initialResponse.equals("Invalid use of play")) {
						System.out.println(initialResponse);
					
				}
				
				else if(initialResponse != null){
					
					AudioInputStream din = null;
	            	
	            	try {
	                	InputStream inFromServer = new BufferedInputStream(clientSocket.getInputStream());
	                	
	                	// Clip clip = AudioSystem.getClip();
	                    AudioInputStream ais = AudioSystem.getAudioInputStream(inFromServer); 
	                     
	                    
	                    //EXTERNAL CODE
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
	         				// Start
	         				sdline.start();
	         				
	         				int nBytesRead;
	         				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {	
	         					
	         					//TODO Code never exits this loop (I think)
	         					//if I break out of the loop manually, server sends a 'read error' message"
	         					sdline.write(data, 0, nBytesRead); 
	         				}
	         				System.out.println("exited loop");
	         				// Stop
	         				sdline.drain();
	         				sdline.stop();
	         				sdline.close();
	         				din.close();
	         			}
	         			
	            	}
	            	catch(Exception e) {
	        			e.printStackTrace();
	        		}
	        		finally {
	        			if(din != null) {
	        				try { din.close(); } catch(IOException e) { }
	        			}
	        		}
	            	
	     			
	     			System.out.println("Finished client side");
				}
            	
            	
            }
            /********************************************************************************************************/
			

			else if (splitCmd[0].equals("get")) {

				String initialResponse = inBuffer.readLine();
				
				if (initialResponse.equals("Invalid use of get")) {
					System.out.println(initialResponse);
				}
				else {
					
					String fileName = splitCmd[1]; //set fileName equal to the file name
					int portNum = clientSocket.getLocalPort();
					String pNum = String.valueOf(portNum);
					
					int numBytes = 0;
					
					String fileNameNew = fileName + "-" + pNum;
					
					// TODO create file here
					PrintWriter writerToFile = new PrintWriter(fileNameNew, "UTF-8");
					
					// TODO copy contents into file
					while (true) {
						String temp = inBuffer.readLine();
						if (!temp.equals("eof")) {
							writerToFile.println(temp);
							
							final byte[] utf8Bytes = temp.getBytes("UTF-8");
							numBytes += utf8Bytes.length;
							
						}
						else{
							writerToFile.close();
							System.out.println("File saved in " + fileNameNew + "(" + numBytes + " bytes)");
							break;
						}
					}
					

				}

			}

			// Send to the server

			else {

				// Getting response from the server
				line = inBuffer.readLine();
				System.out.println("Server: " + line);
			}
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine(); 
            
        	
        	
        }

        // Close the socket
        clientSocket.close();           
    } 
} 
