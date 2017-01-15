import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class sendFiletoClient extends Thread{
	crackingController parent;
	String filePath;
	
	public ServerSocket server;
	private Socket socket;
	private FileInputStream In;
	private OutputStream Out;
	
	public sendFiletoClient(crackingController parent, String filePath){
		this.parent = parent;
		this.filePath = filePath;
		
		try {
			server = new ServerSocket(9998);
			parent.parent.parent.Log("Ready to send file to client!");
		} catch (IOException e) {
			parent.parent.parent.Log("Error in sending file to client!");
			parent.parent.parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){
		try {
			parent.parent.parent.Log("Waiting for client to connect for data transmission ...");
			
			socket = server.accept();
			Out = socket.getOutputStream();
			In = new FileInputStream(filePath);
			
			parent.parent.parent.Log("Connection Established! Sending file to client ...");
			
			byte[] buffer = new byte[1024];
			int count;

			while((count = In.read(buffer)) >= 0){
				Out.write(buffer, 0, count);
			}
			Out.flush();
			
			parent.parent.parent.Log("File is sent to client!");

			if(In != null){ In.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
			if(server != null){ server.close(); }
		}
		catch (Exception e) {
			parent.parent.parent.Log("Error in sending file to client!");
			parent.parent.parent.Log("Error log: " + e.getMessage());
			parent.parent.stopThread(parent.requester);
		}
	}
}
