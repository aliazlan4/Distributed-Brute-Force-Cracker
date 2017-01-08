import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class serverSocketThread extends Thread{
	public mainFrame parent;
	private ServerSocket server = null;
	public int total_cores = 0;
	
	List<clientThread> clients;
	
	private int serverPort = 9999;
	
	public serverSocketThread(mainFrame parent){
		this.parent = parent;
		
		parent.Log("Starting Server!");
		try {
			server = new ServerSocket(serverPort);
			parent.Log("Server started. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
			parent.serverStarted();
			clients = new ArrayList<clientThread>();
			this.start();
		} catch (IOException e) {
			parent.Log("Error in starting server!");
			parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void stopServer(){
		try {
			parent.Log("Server stopped!");
			parent.serverStoped();
			this.interrupt();
			this.stop();
			server.close();
		}
		catch (IOException e) {
			parent.Log("Error in stoping server!");
			parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){  
		while (!this.isInterrupted()){  
			try{  
				parent.Log("Waiting for a client ...");
				addThread(server.accept()); 
			}
			catch(Exception ioe){
				if(!this.isInterrupted()){
					parent.Log("Error in accepting client!");
					parent.Log("Error log: " + ioe.getMessage());
				}
			}
		}
	}
	
	private void addThread(Socket socket){
		parent.Log("Client Accepted!");
		clients.add(new clientThread(this, socket));
	}
}

class clientThread extends Thread{
	serverSocketThread parent;
	public int cores;
	private Socket connectionVar;
	private ObjectInputStream In;
	private ObjectOutputStream Out;

	public clientThread(serverSocketThread parent, Socket socket) {
		this.parent = parent;
		this.connectionVar = socket;
		try {
			Out = new ObjectOutputStream(connectionVar.getOutputStream());
			Out.flush();
			In = new ObjectInputStream(connectionVar.getInputStream());
			
			this.start();
		} catch (Exception e) {
			parent.parent.Log("Error in accepting client!");
			parent.parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){
		while (!this.isInterrupted()){  
			try{
				In.readObject();
			}
			catch(Exception e){
				if(!this.isInterrupted()){
					parent.parent.Log("Error in recieving message from client!");
					parent.parent.Log("Error log: " + e.getMessage());
					stopConnection();
				}
			}
		}
	}

	private void stopConnection() {
		parent.clients.remove(this);
		try {
			In.close();
			Out.close();
			connectionVar.close();
			parent.parent.Log("Client removed!");
			this.interrupt();
			this.stop();
		} catch (Exception e) {
			parent.parent.Log("Error in stopping client!");
			parent.parent.Log("Error log: " + e.getMessage());
		}
	}
}
