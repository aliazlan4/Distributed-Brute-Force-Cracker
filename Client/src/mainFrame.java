import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mainFrame extends JFrame implements ActionListener{
	private JButton connectToServerButton = new JButton("Connect");
	private JTextField serverIP = new JTextField("127.0.0.1");
	private JTextField serverPort = new JTextField("9999");
	private JTextArea screen = new JTextArea();
	private JLabel totalNodes = new JLabel("Total Nodes: 0");
	private JLabel totalCores = new JLabel("Total Cores: 0");
	private JLabel serverIPLabel = new JLabel("Server IP: ");
	private JLabel serverPortLabel = new JLabel("Server Port: ");
	private JLabel filePathLabel = new JLabel("File Path: ");
	private JButton selectFileButton = new JButton("Select file to Crack");
	private JButton crackButton = new JButton("Crack");
	private JTextField filePath = new JTextField();
	private JPanel northPanel = new JPanel();
	private JPanel northSubPanel1 = new JPanel();
	private JPanel northSubPanel2 = new JPanel();
	private JPanel northSubPanel3 = new JPanel();
	private JScrollPane scroll;
	
	connectionThread connThread;
	
	
	public mainFrame(){
		this.setTitle("Distributed Password Cracker Client");
		this.setSize(700, 500);
		this.setLayout(new BorderLayout());		
		this.setResizable(false);
		initComponents();
		this.setVisible(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}
	
	void initComponents(){
		connectToServerButton.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		totalCores.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		totalNodes.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		screen.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		serverIP.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		serverPort.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		filePathLabel.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		selectFileButton.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		crackButton.setFont(new Font("Centuary Gothic", Font.PLAIN, 14));
		
		connectToServerButton.addActionListener(this);
		selectFileButton.addActionListener(this);
		crackButton.addActionListener(this);

		serverIP.setColumns(10);
		serverPort.setColumns(5);
		
		screen.setEditable(false);
		screen.setLineWrap(true);
		scroll = new JScrollPane();
		scroll.setViewportView(screen);
		
		filePath.setColumns(20);
		filePath.setEditable(false);
		selectFileButton.setEnabled(false);
		crackButton.setEnabled(false);
		
		northSubPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
		northSubPanel1.add(serverIPLabel);
		northSubPanel1.add(serverIP);
		northSubPanel1.add(serverPortLabel);
		northSubPanel1.add(serverPort);
		northSubPanel1.add(connectToServerButton);
		
		northSubPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		northSubPanel2.add(filePathLabel);
		northSubPanel2.add(filePath);
		northSubPanel2.add(selectFileButton);
		northSubPanel2.add(crackButton);
		
		northSubPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 5));
		northSubPanel3.add(totalNodes);
		northSubPanel3.add(totalCores);
		
		northPanel.setLayout(new BorderLayout());
		northPanel.add(northSubPanel1, BorderLayout.NORTH);
		northPanel.add(northSubPanel2, BorderLayout.CENTER);
		northPanel.add(northSubPanel3, BorderLayout.SOUTH);
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}
	
	void updateTotalNodes(int nodes){
		this.totalNodes.setText("Total Nodes: " + nodes);
	}
	
	void updateTotalCores(int cores){
		this.totalCores.setText("Total Cores: " + cores);
	}
	
	void connectionEstablished(){
		connectToServerButton.setText("Disconnect");
		serverIP.setEditable(false);
		serverPort.setEditable(false);
		filePath.setEditable(true);
		selectFileButton.setEnabled(true);
		crackButton.setEnabled(true);
	}
	
	void connectionEnded(){
		connectToServerButton.setText("Connect");
		serverIP.setEditable(true);
		serverPort.setEditable(true);
		filePath.setEditable(false);
		selectFileButton.setEnabled(false);
		crackButton.setEnabled(false);
	}
	
	void startingCracking(){
		filePath.setEditable(false);
		selectFileButton.setEnabled(false);
		crackButton.setEnabled(false);
	}
	
	void endingCracking(){
		filePath.setEditable(true);
		selectFileButton.setEnabled(true);
		crackButton.setEnabled(true);
	}
	
	void Log(String text){
		String timeStamp = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").format(new Date());
		screen.append("[" + timeStamp + "] " + text + "\n");
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(connectToServerButton)){
			if(connectToServerButton.getText().equals("Connect")){
				Log("Connecting with server ...");
				connThread = new connectionThread(this, serverIP.getText(), Integer.parseInt(serverPort.getText()));
			}
			else if(connectToServerButton.getText().equals("Disconnect")){
				Log("Disconnecting ...");
				connThread.stopConnection();
			}
		}
		else if(arg0.getSource().equals(selectFileButton)){
			JFileChooser jf = new JFileChooser();
			jf.showOpenDialog(this);
			String path = jf.getSelectedFile().getPath();
			
			if(path != null && !path.equals("")){
				filePath.setText(path);
			}
		}
		else if(arg0.getSource().equals(crackButton)){
			
		}
	}
}