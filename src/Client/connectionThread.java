package Client;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import Common.Message;

public class connectionThread extends Thread {
	mainFrameClient parent;
	Socket connectionVar;
	public ObjectInputStream In;
	public ObjectOutputStream Out;
	public String serverIP;
	public int serverPort;
	
	private String storagePath = "client_storage/";
	
	public connectionThread(mainFrameClient parent, String serverIP, int serverPort){
		this.parent = parent;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
		try {
			connectionVar = new Socket(serverIP, serverPort);
			parent.Log("Connected to server!");
			Out = new ObjectOutputStream(connectionVar.getOutputStream());
			Out.flush();
			In = new ObjectInputStream(connectionVar.getInputStream());
			parent.connectionEstablished();
			
			sendMessage(new Message("coresUpdate", Runtime.getRuntime().availableProcessors() + ""));
			
			this.start();
		} catch (Exception e) {
			parent.Log("Error in connecting to server!");
			parent.Log("Error log: " + e.getMessage());
		}
	}
	
	public void run(){
		while (!this.isInterrupted()){  
			try{
				Message msg = (Message) In.readObject();
				//System.out.println("Recieved msg at client: " + msg.toString());
				messageHandler(msg);
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
	
	public void sendMessage(Message msg){
		try {
			//System.out.println("Sending msg at client: " + msg.toString());
			Out.writeObject(msg);
			Out.flush();
		} catch (IOException e) {
			parent.Log("Error in sending message to server!");
			parent.Log("Error log: " + e.getMessage());
			stopConnection();
		}
	}
	
	public void messageHandler(Message msg){
		String msgType = msg.type;
		String msgContent = msg.content;
		
		if(msgType.equals("update")){
			String[] temp = msgContent.split(";");
			parent.updateTotalNodes(temp[0]);
			parent.updateTotalCores(temp[1]);
		}
		
		else if(msgType.equals("updateOnScreen")){
			parent.Log(msgContent);
		}
		
		else if(msgType.equals("sendFileToCrack")){
			String[] temp = msgContent.split(";");
			sendFileToCrack(temp[0], Integer.parseInt(temp[1]));
		}
		
		else if(msgType.equals("recieveFile")){
			String[] temp = msgContent.split(";");
			recieveFileFromServer(temp[0], Integer.parseInt(temp[1]));
		}
		
		else if(msgType.equals("startCracking")){
			String[] temp = msgContent.split(";");
			findPasswordofZip finding = new findPasswordofZip(this, storagePath + temp[0], temp[1], 
					Long.parseLong(temp[2]), Long.parseLong(temp[3]), Integer.parseInt(temp[4]), 
					Integer.parseInt(temp[5]));
			try {
				sendMessage(new Message("startingCracking", ""));
				finding.start();
			} catch (InterruptedException e) {
				parent.Log("Error in starting brute force attack!");
				parent.Log("Error log: " + e.getMessage());
				stopConnection();
			}
		}
		
		else if(msgType.equals("passwordNotFound")){
			parent.Log("Cracking ended. Password not found!");
		}
		
		else if(msgType.equals("updateOfCracking")){
			//parent.Log("Cracking ...");
		}
		
		else if(msgType.equals("passwordFound")){
			parent.Log("Password Found: " + msgContent);
		}
	}
	
	public void requestServerToCrack(String filePath, String characters, String passLength){
		sendMessage(new Message("requestToCrackFile", filePath + ";" + characters + ";"  + passLength));
		parent.Log("Sent request to server to crack file!");
	}
	
	public void sendFileToCrack(String filePath, int serverPort){
		parent.Log("Server approve request to send file: " + filePath);
		try {
			File yourFile = new File(storagePath + (new File(filePath)).getName());
			yourFile.getParentFile().mkdirs();
			yourFile.createNewFile();
			Files.copy((new File(filePath)).toPath(), (new File(storagePath + (new File(filePath)).getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			parent.Log("Error in copy file to this client!");
			parent.Log("Error log: " + e.getMessage());
			stopConnection();
		}
		sendToServer thread = new sendToServer(this, filePath, this.serverIP, serverPort);
		thread.start();
	}
	
	public void recieveFileFromServer(String fileName, int serverPort){
		parent.Log("Server is sending file: " + fileName);
		recieveFromServer thread = new recieveFromServer(this, storagePath + fileName, this.serverIP, serverPort);
		thread.start();
	}
}
