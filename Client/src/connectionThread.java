import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class connectionThread extends Thread {
	mainFrame parent;
	Socket connectionVar;
	public ObjectInputStream In;
	public ObjectOutputStream Out;
	
	public connectionThread(mainFrame parent, String serverIP, int serverPort){
		this.parent = parent;
		
		try {
			connectionVar = new Socket(serverIP, serverPort);
			parent.Log("Connected to server!");
			Out = new ObjectOutputStream(connectionVar.getOutputStream());
			Out.flush();
			In = new ObjectInputStream(connectionVar.getInputStream());
			parent.connectionEstablished();
			this.start();
		} catch (Exception e) {
			parent.Log("Error in connecting to server!");
			parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){
		while (!this.isInterrupted()){  
			try{
				In.readObject();
			}
			catch(Exception e){
				if(!this.isInterrupted()){
					parent.Log("Error in recieving message from server!");
					parent.Log("Error log: " + e.getMessage());
					stopConnection();
				}
			}
		}
	}
	
	public void stopConnection(){
		this.interrupt();
		if(connectionVar.isConnected()){
			try {
				connectionVar.close();
			} catch (IOException e) {
				parent.Log("Error in closing connection!");
				parent.Log("Error log: " + e.getMessage());
			}
		}
		parent.connectionEnded();
		parent.Log("Connection Closed!");
		this.stop();
	}
}
