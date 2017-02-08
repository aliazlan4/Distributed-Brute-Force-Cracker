package Server;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mainFrameServer extends JFrame implements ActionListener{
	private JButton startServerButton = new JButton("Start Server");
	private JTextArea screen = new JTextArea();
	private JLabel totalNodesLabel = new JLabel("Total Nodes: 0");
	private JLabel totalCoresLabel = new JLabel("Total Cores: 0");
	private JPanel northPanel = new JPanel();
	private JScrollPane scroll;
	
	private serverSocketThread socketServer;
	
	
	public mainFrameServer(){
		this.setTitle("Distributed Password Cracker Server");
		this.setSize(700, 500);
		this.setLayout(new BorderLayout());		
		this.setResizable(false);
		initComponents();
		this.setVisible(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}
	
	void initComponents(){
		startServerButton.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		totalCoresLabel.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		totalNodesLabel.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		screen.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		
		startServerButton.setSize(70, 40);
		startServerButton.addActionListener(this);
		
		screen.setEditable(false);
		screen.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret)screen.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scroll = new JScrollPane();
		scroll.setViewportView(screen);
		
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 5));
		northPanel.add(totalNodesLabel);
		northPanel.add(totalCoresLabel);
		northPanel.add(startServerButton);
		this.add(northPanel, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}
	
	void updateTotalNodes(int nodes){
		this.totalNodesLabel.setText("Total Nodes: " + nodes);
	}
	
	void updateTotalCores(int cores){
		this.totalCoresLabel.setText("Total Cores: " + cores);
	}
	
	void serverStarted(){
		startServerButton.setText("Stop Server");
	}
	
	void serverStoped(){
		startServerButton.setText("Start Server");
	}
	
	void Log(String text){
		String timeStamp = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").format(new Date());
		screen.append("[" + timeStamp + "] " + text + "\n");
	}

	public void actionPerformed(ActionEvent arg0) {
		if(startServerButton.getText().equals("Start Server")){
			Log("Launching Server Thread!");
			socketServer = new serverSocketThread(this);
		}
		else if(startServerButton.getText().equals("Stop Server")){
			Log("Stoping Server Thread!");
			socketServer.stopServer();
		}
	}
}