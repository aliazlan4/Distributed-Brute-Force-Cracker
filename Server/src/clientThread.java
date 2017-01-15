import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class clientThread extends Thread{
	serverSocketThread parent;
	private int cores;
	Socket connectionVar;
	ObjectInputStream In;
	ObjectOutputStream Out;

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
	
	public int getCores(){
		return cores;
	}
	
	public void setCores(int cores){
		this.cores = cores;
	}
	
	public void run(){
		Object obj = null;
		while (!this.isInterrupted()){  
			try{
				obj = In.readObject();
				System.out.println("In Recieving: " + obj.toString());
				Message msg = (Message) obj;
				//Message msg = (Message) In.readObject();
				//System.out.println("Recieved msg at server: " + msg.toString());
				parent.messageHandler(this, msg);
			}
			catch(Exception e){
				if(!this.isInterrupted()){
					String exception = obj.toString();
					if(!exception.contains("java.lang.String")){
						parent.parent.Log("Error in recieving message from client!");
						parent.parent.Log("Error log: " + e.getMessage());
						parent.stopThread(this);
					}
				}
			}
		}
	}
	
	public void sendMessage(Message msg){
		try {
			//System.out.println("Sending msg at server: " + msg.toString());
			Out.writeObject(msg);
			Out.flush();
		} catch (IOException e) {
			parent.parent.Log("Error in sending message to client!");
			parent.parent.Log("Error log: " + e.getMessage());
			parent.stopThread(this);
		}
	}
}
