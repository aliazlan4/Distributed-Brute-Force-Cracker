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
	
	crackingController CC = null;
	
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
	
	public void stopThread(clientThread thread){
		clients.remove(thread);
		updateCoresNodes();
		
		try {
			thread.In.close();
			thread.Out.close();
			thread.connectionVar.close();
			parent.Log("Client removed!");
			thread.interrupt();
			thread.stop();
		} catch (Exception e) {
			parent.Log("Error in stopping client!");
			parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void updateCoresNodes(){
		parent.updateTotalNodes(clients.size());
		parent.updateTotalCores(getTotalCores());
		broadcastMessage(new Message("update", clients.size() + ";" + getTotalCores()));
	}
	
	public int getTotalCores(){
		int total = 0;
		for(int i = 0; i < clients.size(); i++)
			total += clients.get(i).getCores();
		return total;
	}
	
	public void broadcastMessage(Message msg){
		for(int i = 0; i < clients.size(); i++)
			clients.get(i).sendMessage(msg);
	}
	
	public void requestToCrackFile(String filePath, String characters, String passLength){
		
	}
	
	public void messageHandler(clientThread sender, Message msg){
		String msgType = msg.type;
		String msgContent = msg.content;
		
		if(msgType.equals("coresUpdate")){
			sender.setCores(Integer.parseInt(msgContent));
			updateCoresNodes();
		}
		
		else if(msgType.equals("requestToCrackFile")){
			if(CC == null){
				String[] temp = msgContent.split(";");
				CC = new crackingController(this, sender, temp[0], temp[1], temp[2]);
				CC.start();
			}
			else if(!CC.isAlive()){
				String[] temp = msgContent.split(";");
				CC = new crackingController(this, sender, temp[0], temp[1], temp[2]);
				CC.start();
			}
			else{
				sender.sendMessage(new Message("updateOnScreen", "Error: Another cracking is already in process!"));
			}
		}
		
		else if(msgType.equals("startingCracking")){
			parent.Log("A client has started cracking!");
		}
		
		else if(msgType.equals("updateOfCracking")){
			//parent.Log("Cracking ...");
			CC.requester.sendMessage(new Message("updateOfCracking", ""));
		}
		
		else if(msgType.equals("passwordNotFound")){
			CC.passwordNotFound(sender);
		}
		
		else if(msgType.equals("passwordFound")){
			CC.passwordFound(msgContent);
		}
	}
}