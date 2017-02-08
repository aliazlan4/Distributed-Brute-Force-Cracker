package Server;
import java.io.File;

import Common.Message;

public class crackingController extends Thread{
	serverSocketThread parent;
	clientThread requester;
	String filePath;
	String fileName;
	String characters;
	int startLength;
	int endLength;
	long totalCombinations;
	public long doneCombinations = 0;
	int clientsStopped = 0;
	
	private String storagePath = "server_storage/";
	
	public crackingController(serverSocketThread parent, clientThread requester, String filePath, String characters, String passLength){
		this.parent = parent;
		this.requester = requester;
		this.filePath = filePath;
		this.characters = characters;
		
		File file = new File(filePath);
		this.fileName = file.getName();
		
		String[] temp = passLength.split("-");
		this.startLength = Integer.parseInt(temp[0]);
		this.endLength = Integer.parseInt(temp[1]);
		this.totalCombinations = calculateTotalCombinations(characters, this.startLength, this.endLength);
	}
	
	private long calculateTotalCombinations(String range, int minLength, int maxLength) {
		String smallAplhabetsArray = "abcdefghijklmnopqrstuvwxyz";
		String largeAlphabetsArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numericCharacterArray = "0123456789";
		
		String temp = "";
		if(range.contains("0-9"))
			temp += numericCharacterArray;
		if(range.contains("a-z"))
			temp += smallAplhabetsArray;
		if(range.contains("A-Z"))
			temp += largeAlphabetsArray;
		
		char[] chosenCharactersArray = temp.toCharArray();
		
		long temp1 = 0;
		for(int i = maxLength; i >= minLength; i--){
			temp1 += Math.pow(chosenCharactersArray.length, i);
		}

		return temp1;
	}
	
	public void run(){
		recieveFileFromClient recieve = new recieveFileFromClient(this, storagePath + fileName);
		recieve.start();
		requester.sendMessage(new Message("sendFileToCrack", filePath + ";" + recieve.server.getLocalPort()));
		try {
			recieve.join();
		} catch (InterruptedException e) {
			parent.parent.Log("Error in cracking controller: recieving file from client!");
			parent.parent.Log("Error log: " + e.getMessage());
		}
		
		parent.parent.Log("Sending file to clients for cracking!");
		
		for(int i = 0; i < parent.clients.size(); i++){
			clientThread client = parent.clients.get(i);
			if(!client.equals(requester)){
				sendFiletoClient send = new sendFiletoClient(this, storagePath + fileName);
				send.start();
				client.sendMessage(new Message("recieveFile", fileName + ";" + send.server.getLocalPort()));
				try {
					send.join();
				} catch (InterruptedException e) {
					parent.parent.Log("Error in cracking controller: sending file to clients!");
					parent.parent.Log("Error log: " + e.getMessage());
				}
			}
		}
		
		parent.parent.Log("File sent to all clients. Starting cracking!");
		
		int totalCores = parent.getTotalCores();
		long part = totalCombinations / totalCores;
		long remainder = 0;
		long last_end = 0;
		if((part * totalCores) < totalCombinations)
			remainder = totalCombinations % totalCores;
		
		parent.parent.Log("Total: " + totalCombinations);
		for(int i = 0; i < parent.clients.size(); i++){
			clientThread client = parent.clients.get(i);
			long start = last_end;
			long end = (client.getCores() * part) + last_end - 1;
			
			if(i == (parent.clients.size() - 1))
				end += remainder;
			System.out.println(fileName + ";" + characters + ";" + start
					+ ";" + end + ";" + startLength + ";" + endLength);
			client.sendMessage(new Message("startCracking", fileName + ";" + characters + ";" + start
					+ ";" + end + ";" + startLength + ";" + endLength));
			
			last_end = end + 1;
			
			//parent.parent.Log(i + ": [" + start + "][" + end + "]");
		}
	}
	
	public void passwordNotFound(clientThread sender){
		clientsStopped++;
		parent.parent.Log("Clients stopped: " + clientsStopped + "/" + parent.clients.size());
		requester.sendMessage(new Message("updateOnScreen", "Clients stopped: " + clientsStopped + "/" + parent.clients.size()));
		if(clientsStopped == parent.clients.size()){
			parent.parent.Log("Cracking ended. Password not found!");
			requester.sendMessage(new Message("passwordNotFound", ""));
		}
	}

	public void passwordFound(String password) {
		parent.parent.Log("Password Found: " + password);
		requester.sendMessage(new Message("passwordFound", password));
		
//		endCracking();
	}
	
	public void endCracking(){
		this.stop();
		this.destroy();
	}
}
