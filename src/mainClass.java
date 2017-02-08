import javax.swing.JOptionPane;

import Client.mainFrameClient;
import Server.mainFrameServer;

public class mainClass {
	public static void main(String args[]){
	    String[] buttons = {"Server", "Client"};
	    int selection = JOptionPane.showOptionDialog(null, "What do you want to start?", "Distributed BruteForce Cracker",
	        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);

	    if(selection == 0)
	    	new mainFrameServer();
	    else if(selection == 1)
	    	new mainFrameClient();
	}
}
