import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JOptionPane;

public class Client_Chat_Handler implements Runnable {

	public Client_Chat_GUI new_Client_GUI;
	private File_Transfer_1 new_file_transfer;
	private ServerSocket SS_Chat_Sock = null;
	private Socket new_Client_chat_sock = null;
	private ObjectOutputStream C_output;
	private ObjectInputStream C_input;
	private boolean Client_connection_flag;
	public boolean Chat_Window_State;
	public boolean Random_Disconnect = false;
	private Object Cobject;
	public String Client_name;
	public int Knock_State = 1;
	private  Player player;
	

	public Client_Chat_Handler(String ip , int port ,String Client_name) throws UnknownHostException, IOException{
		
		new_Client_chat_sock = new Socket(ip,port);
		this.Client_name = Client_name;
		this.Client_connection_flag = true; 
		setChat_Window_State(true);
		new_Client_GUI = new Client_Chat_GUI(this.Client_name);
		
		new_file_transfer = new File_Transfer_1(ip,++port,new_Client_GUI,this);
		Thread file_transfer = new Thread(new_file_transfer);
		file_transfer.start();
		
		Set_Action();
	}
	
	public  Client_Chat_Handler(int port,String Client_name) throws IOException{
		
		SS_Chat_Sock = new ServerSocket(port);
		new_Client_chat_sock = SS_Chat_Sock.accept();
			
		this.Client_name = Client_name;
		this.Client_connection_flag = true;
		new_Client_GUI = new Client_Chat_GUI(this.Client_name);
		
		new_file_transfer = new File_Transfer_1(++port,new_Client_GUI,this);
		Thread file_transfer = new Thread(new_file_transfer);
		file_transfer.start();
		
		Set_Action();
	}
	
	public void Set_Action(){
		
		new_Client_GUI.CB_SEND.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String message = new_Client_GUI.CTF_Message.getText();
				if(!message.equals("")){
					new_Client_GUI.CTA_Conversation.append("\n" + "ME: " + message);
					Send_Message(message);
				}
			}
		});
		
		new_Client_GUI.CTF_Message.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent ev){
		        if(ev.getKeyCode() == KeyEvent.VK_ENTER){
		        	ev.consume();
		        	String message = new_Client_GUI.CTF_Message.getText();
					if(!message.equals("")){
						new_Client_GUI.CTA_Conversation.append("\n" + "ME: " + message);
						Send_Message(message);
					}
		            }
			}

			public void keyReleased(KeyEvent ev) {
			}
			public void keyTyped(KeyEvent ev) {
			}
		});
		
		
		new_Client_GUI.CB_FILE.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){	
				new_Client_GUI.Open_File();
				if(new_Client_GUI.Selected_File != null){
					try {
						new_file_transfer.Send_File_Notification(new_Client_GUI.Selected_File);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		
		new_Client_GUI.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	if(Client_connection_flag){
            		setChat_Window_State(false);
            		Knock_State = 1;
            	}
            }
        });
		
	}
	
	public void run() {
			
		try{
			setStreams();
			try{
				getInput();
			}catch(ClassNotFoundException ex){
			}
		}catch(IOException e){
		}
		finally{
			
			try {
				C_input.close();
				C_output.close();
				new_Client_chat_sock.close();
				if(SS_Chat_Sock!=null){
					SS_Chat_Sock.close();
				}
				
				System.err.println("chat scoket connection closed");
				
			} catch (IOException e) {
				return;
			}
		}
	}
	
	private void setStreams() throws IOException{
		
		C_output = new ObjectOutputStream(new_Client_chat_sock.getOutputStream() );
        C_output.flush(); 
       
        C_input = new ObjectInputStream(new_Client_chat_sock.getInputStream());
	}
	
	
	private void getInput () throws IOException, ClassNotFoundException{
		
		while(Client_connection_flag){
			try{
			Cobject = C_input.readObject();
			}catch (EOFException eofException) {
				return;
			}catch(IOException ioexception){
				if(Chat_Window_State){
					Chat_Window_State = false;
				}
				Random_Disconnect = true;
				Client_connection_flag = false;
				new_file_transfer.Connection_flag = false;
				new_Client_GUI.CB_SEND.setEnabled(false);
				new_Client_GUI.CTF_Message.setEnabled(false);
				new_Client_GUI.CTA_Conversation.append("\n\n   " + Client_name + " Disconnected");
				new_file_transfer.Close_All_Components();
				return;
			}
			if(Cobject instanceof String){
				
				Update_ChatField(Cobject);
			}
			
			else if(Cobject instanceof Chat_Disconnect){
				
				if(Chat_Window_State){
					Chat_Window_State = false;
				}
				Client_connection_flag = false;
				new_file_transfer.Connection_flag = false;
				Client_Handler.Current_Chat.remove(Client_name);
				Client_Handler.Client_Obj_List.remove(this);
//				System.out.println(Client_name + "Disconnected");
				new_Client_GUI.CB_SEND.setEnabled(false);
				new_Client_GUI.CTA_Conversation.append("\n\n   " + Client_name + " Disconnected");	
				break;
			}
		}
	}
	
	
	private void Update_ChatField(Object O) throws MalformedURLException{
		
		if(Chat_Window_State == false){
			
			Play_Notification_Sound();
			
			if(Knock_State==1){
				P2P_Chat_Client_MainWindow.L_Pin_Label.setText(Client_name);
				P2P_Chat_Client_MainWindow.L_Pin_Label.setEnabled(true);
				P2P_Chat_Client_MainWindow.L_Pin_Label.setVisible(true);
			}
			
			Knock_State++;
		}
		
		String message = (String)O;
		new_Client_GUI.CTA_Conversation.append("\n" + Client_name + ": " + message);
	}
	
	public void setConnectionflag(boolean flag){
		
		Client_connection_flag = flag;
	}
	
	public boolean getConnectionflag(){
		
		return Client_connection_flag;
	}
	
	public void setChat_Window_State(boolean flag){
		
		Chat_Window_State = flag;
	}
	
	public boolean getChat_Window_State(){
		
		return Chat_Window_State;
	}
	
	
	private void Send_Message(String msg){
		
		try {
			
			C_output.writeObject(msg);
			C_output.flush();
			new_Client_GUI.CTF_Message.setText("");
		} catch (IOException e) {
			new_Client_GUI.CB_SEND.setEnabled(false);
			new_Client_GUI.CTF_Message.setEnabled(false);
			return;
		}
	}
	
	public synchronized void Chat_Disconnect(){
		
		if(Chat_Window_State){
			new_Client_GUI.setVisible(false);
		}
		
		Chat_Disconnect dis = new Chat_Disconnect();
    	dis.setDis_flag();
    	
    	try {
			C_output.writeObject(dis);
			C_output.flush();
		}catch (IOException e) {
			Client_connection_flag = false; 
			new_file_transfer.File_disconnect();
			new_file_transfer.Connection_flag = false;	
			new_Client_GUI.CB_SEND.setEnabled(false);
			new_Client_GUI.CTF_Message.setEnabled(false);
			return;
		}
    	
    	Client_connection_flag = false; 
    	new_file_transfer.File_disconnect();
    	new_file_transfer.Connection_flag = false;	
    	new_Client_GUI.CB_SEND.setEnabled(false);
    	new_Client_GUI.CTF_Message.setEnabled(false);
	}
	
	public void Play_Notification_Sound() throws MalformedURLException{
			
	    try {
	    	File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = dir.toString();
			//System.out.println(path);
			//String path_new = "";
			/*for(int i=0;i<path.length();i++){
				if(path.charAt(i)==';')break;
				path_new+=path.charAt(i);
			}
			path="";
			for(int i=0;i<path_new.length()-4;i++){
				path+=path_new.charAt(i);
			}
			System.out.println(path);*/
			player = Manager.createPlayer(new MediaLocator(new File("src/chat_sound.mp3").toURI().toURL()));
		}catch (NoPlayerException | IOException e) {
			JOptionPane.showMessageDialog(new_Client_GUI, "chat_sound.mp3 not found.");	
			return;
			} 
        player.addControllerListener(new ControllerListener() {
           public void controllerUpdate(ControllerEvent event) {
             if (event instanceof EndOfMediaEvent) {
                player.stop();
                player.close();
             }
           }
       });
       
       player.realize();
       player.start();
	}
	
}
