package Server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class recieveFileFromClient extends Thread{
	crackingController parent;
	public ServerSocket server;
	private Socket socket;
	private String filePath;
	private String fileName;
	private FileOutputStream Out;
	private InputStream In;
	
	private String storagePath = "server_storage/";
	
	public recieveFileFromClient(crackingController parent, String filePath){
		this.parent = parent;
		this.filePath = filePath;
		
		File temp = new File(filePath);
		this.fileName = temp.getName();
		
		try {
			server = new ServerSocket(9998);
			parent.parent.parent.Log("Ready to recieve file from client!");
		} catch (IOException e) {
			parent.parent.parent.Log("Error in recieving file from client!");
			parent.parent.parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){
		try {
			parent.parent.parent.Log("Waiting for client to connect for data transmission ...");
			
			socket = server.accept();
			In = socket.getInputStream();
			
			File yourFile = new File(storagePath + fileName);
			yourFile.getParentFile().mkdirs();
			yourFile.createNewFile();
			
			Out = new FileOutputStream(storagePath + fileName, false);
			
			parent.parent.parent.Log("Connection Established! Recieving file from client ...");
			
			byte[] buffer = new byte[1024];
			int count;

			while((count = In.read(buffer)) >= 0){
				Out.write(buffer, 0, count);
			}
			Out.flush();
			
			parent.parent.parent.Log("File is recieved from client!");

			if(In != null){ In.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
			if(server != null){ server.close(); }
		}
		catch (Exception e) {
			parent.parent.parent.Log("Error in recieving file from client!");
			parent.parent.parent.Log("Error log: " + e.getMessage());
			parent.parent.stopThread(parent.requester);
		}
	}
}
