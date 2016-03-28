import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread{
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	public ServerThread(ServerSocket ServerSocket)
	{
		this.serverSocket = ServerSocket;
	}
	public ServerThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
}
