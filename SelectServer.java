/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class SelectServer {
    public static int BUFFERSIZE = 32;
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: UDPServer <Listening Port>");
            System.exit(1);
        }

        // Initialize buffers and coders for channel receive and send
        String line = "";
        Charset charset = Charset.forName( "us-ascii" );  
        CharsetDecoder decoder = charset.newDecoder();  
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer inBuffer = null;
        CharBuffer cBuffer = null;
        int bytesSent, bytesRecv;     // number of bytes sent or received
        
        // Initialize the selector
        Selector selector = Selector.open();
        // Create a server channel and make it non-blocking
        ServerSocketChannel channelTCP = ServerSocketChannel.open();
        channelTCP.configureBlocking(false);

        //Create a Datagram channel for the UDP channel ***
        DatagramChannel channelUDP = DatagramChannel.open();
        channelUDP.configureBlocking(false);


       
        // Get the port number and bind the socket
        InetSocketAddress isa = new InetSocketAddress(Integer.parseInt(args[0]));
        channelTCP.socket().bind(isa);

        //Bind to the same address for UDP channel ***
       // InetSocketAddress isa2 = new InetSocketAddress(Integer.parseInt(args[0]));
        channelUDP.socket().bind(isa);

        // Register that the server selector is interested in connection requests
        channelTCP.register(selector, SelectionKey.OP_ACCEPT);

        // Register the UDP channel with READ instead *** 
        channelUDP.register(selector, SelectionKey.OP_READ);


        //TODO Make this work with UDP as well 

        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            boolean isTCP = false;
            while (!terminated) 
            {
                if (selector.select(500) < 0)
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }
                
                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();

                // Walk through the ready set
                while (readyItor.hasNext()) 
                {
                    // Get key from set
                    SelectionKey key = (SelectionKey)readyItor.next();

                    // Remove current entry
                    readyItor.remove();

                    //general channel to use
                    Channel unknownChannel = (Channel)key.channel();

                    // Accept new connections, if any
                    if (key.isAcceptable() && unknownChannel == channelTCP)
                    {
                        isTCP = true;
                        SocketChannel cchannel = channelTCP.accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accept connection from " + cchannel.socket().toString());
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    } 
                    else if(key.isReadable() && unknownChannel == channelUDP){
                        isTCP = false;
                        System.out.println("Senpai, pls, be gentle");

                    }

                    else if(!key.isAcceptable() &&  isTCP) 
                    {
                        SocketChannel cchannel = (SocketChannel)key.channel();
                        if (key.isReadable())
                        {
                            Socket socket = cchannel.socket();
                        
                            // Open input and output streams
                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
                            cBuffer = CharBuffer.allocate(BUFFERSIZE);
                             
                            // Read from socket
                            bytesRecv = cchannel.read(inBuffer);
                            if (bytesRecv <= 0)
                            {
                                System.out.println("read() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }
                             
                            inBuffer.flip();      // make buffer available  
                            decoder.decode(inBuffer, cBuffer, false);
                            cBuffer.flip();
                            line = cBuffer.toString();
                            System.out.print("Client: " + line);
                   
                            // Echo the message back
                            inBuffer.flip();
                            bytesSent = cchannel.write(inBuffer); 
                            if (bytesSent != bytesRecv)
                            {
                                System.out.println("write() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }
                            
                            if (line.equals("terminate\n"))
                                terminated = true;
                            

                         }

                    }
                } // end of while (readyItor.hasNext()) 
            } // end of while (!terminated)
        }
        catch (IOException e) {
            System.out.println(e);
        }
 
        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if (key.isAcceptable())
                ((ServerSocketChannel)key.channel()).socket().close();
            else if (key.isValid())
                ((SocketChannel)key.channel()).socket().close();
        }
    }
}

/*************************** SelectServer functions**********************************/

if(line.equals("list")){
    string filePath = Paths.get("").toAbsolutePath().toString();
    
    File folder = new File(filePath);
    File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
            System.out.println("File " + listOfFiles[i].getName());
        }  
        else if (listOfFiles[i].isDirectory()) {
            System.out.println("Directory " + listOfFiles[i].getName());
        }
    }
                            
}