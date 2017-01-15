import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class recieveFromServer extends Thread{
	private connectionThread parent;
	private String serverIP;
	private int serverPort;
	private Socket socket;
	private String filePath;
	private FileOutputStream Out;
	private InputStream In;
	
	public recieveFromServer(connectionThread parent, String filePath, String serverIP, int serverPort){
		this.parent = parent;
		this.filePath = filePath;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public void run(){
		try {
			parent.parent.Log("Creating file transmission connection to server!");
			
			socket = new Socket(InetAddress.getByName(serverIP), serverPort);
			In = socket.getInputStream();
			
			File yourFile = new File(filePath);
			yourFile.getParentFile().mkdirs();
			yourFile.createNewFile();
			
			Out = new FileOutputStream(filePath);
			
			parent.parent.Log("Connection Established! Recieving file from server ...");
			
			byte[] buffer = new byte[1024];
			int count;

			while((count = In.read(buffer)) >= 0){
				Out.write(buffer, 0, count);
			}
			Out.flush();
			
			parent.parent.Log("File is recieved from server!");

			if(In != null){ In.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
		}
		catch (Exception e) {
			parent.parent.Log("Error in recieving file from server!");
			parent.parent.Log("Error log: " + e.getMessage());
		}
	}
}
