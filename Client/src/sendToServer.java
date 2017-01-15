import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class sendToServer extends Thread{
	connectionThread parent;
	String filePath;
	String serverIP;
	int serverPort;
	
	private Socket socket;
	private FileInputStream In;
	private OutputStream Out;
	
	public sendToServer(connectionThread parent, String filePath, String serverIP, int serverPort){
		this.parent = parent;
		this.filePath = filePath;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public void run(){
		try {
			parent.parent.Log("Creating file transmission connection to server!");
			
			socket = new Socket(InetAddress.getByName(serverIP), serverPort);
			Out = socket.getOutputStream();
			In = new FileInputStream(filePath);
			
			parent.parent.Log("Connection Established! Sending file to server ...");
			
			byte[] buffer = new byte[1024];
			int count;

			while((count = In.read(buffer)) >= 0){
				Out.write(buffer, 0, count);
			}
			Out.flush();
			
			parent.parent.Log("File sent to server!");

			if(In != null){ In.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
		}
		catch (Exception e) {
			parent.parent.Log("Error in sending file to server!");
			parent.parent.Log("Error log: " + e.getMessage());
		}
	}
}
