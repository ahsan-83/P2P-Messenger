import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class Client_Handler implements Runnable{
	
	public static ArrayList<String> All_Friends = new ArrayList<String>();
	public static ArrayList<String> Online_Friends = new ArrayList<String>();
	public static ArrayList<String> Other_Users = new ArrayList<String>();
	public static ArrayList<String> Current_Chat = new ArrayList<String>();
	public static ArrayList<Client_Chat_Handler> Client_Obj_List = new ArrayList<Client_Chat_Handler>();
	public  Map<String,Boolean> JList_Map = new HashMap<String,Boolean>();
	
	private static int Port_Number;
	private Socket Client_sock = null;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	public boolean connection_flag ;
	private Object object;
	private String Requested_Clientname;
	private String LogIn_Name;
	private char[] LogIn_Pass;
	private boolean LogIn_Approve;
	
	public Client_Handler(){
		
	}

	public Client_Handler(Socket sock,String name, char[] pass){
		
		Port_Number = 3000;
		this.Client_sock = sock;
		this.LogIn_Name = name;
		this.LogIn_Pass = pass;
		this.LogIn_Approve = false;
		this.connection_flag = false;
	}
	

	public void run() {
		
		try{
			
			setStreams();
			
			Check_LogIn();
			
			if(LogIn_Approve){
				getInput();
			}
		}catch(IOException e){
			
		}catch(ClassNotFoundException ex){
			
		}finally{
			try {
				output.close();
				input.close();
				Client_sock.close();
				System.err.println(LogIn_Name + " disconnected ");
			} catch (IOException e) {
				System.err.println(LogIn_Name + " disconnected ");
				return;
			}
		}
	}
	
	
	private void setStreams() throws IOException{
		
        output = new ObjectOutputStream(Client_sock.getOutputStream());
        output.flush(); 
        
        input = new ObjectInputStream(Client_sock.getInputStream());
       
	}
	
	
	private void Check_LogIn() throws IOException, ClassNotFoundException{
		
		LogIn_Packet log = new LogIn_Packet();
		log.setLogInPacket(LogIn_Name, LogIn_Pass);
		
		output.writeObject(log);
		output.flush();
		
		Get_LogIn_Approve();
		
	}

	
	private void Get_LogIn_Approve() throws ClassNotFoundException, IOException{
		
		Object object = input.readObject();
		
		if(object instanceof LogIn_Approved_Message){
			
			LogIn_Approved_Message login = (LogIn_Approved_Message)object;
			if(login.LogIn_OK){
				
				All_Friends.addAll((ArrayList<String>)login.getAll_Users());
				Online_Friends.addAll((ArrayList<String>)login.getOnline_Users());
				Other_Users.addAll((ArrayList<String>)login.getOther_Users());
				
				P2P_Chat_Client_MainWindow.JL_Model = new DefaultListModel<String>();
				P2P_Chat_Client_MainWindow.ComboBox_Model = new DefaultComboBoxModel<String>();
				
				for(int i=0;i<All_Friends.size();i++){
					
					P2P_Chat_Client_MainWindow.JL_Model.addElement(All_Friends.get(i));
				}
				
				for(int i=0;i<Other_Users.size();i++){
					
					P2P_Chat_Client_MainWindow.ComboBox_Model.addElement(Other_Users.get(i));
				}
				 
				P2P_Chat_Client_MainWindow.JL_ONLINE = new JList<String>(P2P_Chat_Client_MainWindow.JL_Model);
				P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
				P2P_Chat_Client_MainWindow.Other_Clients.setEnabled(true);
				
				set_JList();
				
				Update_JList_Map();
				
				P2P_Chat_Client_MainWindow.SetJListAction();
			}
			
			LogIn_Approve = true;
			connection_flag = true;
			
			P2P_Chat_Client_MainWindow.LogInWindow.setVisible(false);
			P2P_Chat_Client_MainWindow.Update_MainWindow(LogIn_Name);
			System.out.println(LogIn_Name + " Connected");
		}
		
		else if(object instanceof LogIn_Denied){
			
			LogIn_Denied login = (LogIn_Denied)object;
			if(login.LogIn_Denied){
				
				if(login.Same_User){
					JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.LogInWindow, "This User already Logged in to the Server");
				}else{
					JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.LogInWindow, "Enter Correct Username and Password");
				}
				
			}
		}
		
	}
	
	
	private void getInput () throws IOException, ClassNotFoundException{
		
		while(connection_flag){
			
			try{
				object = input.readObject();		
			}catch(EOFException e){
				return;
			}
			
			if(object instanceof Update_Online_List){
				
				Update_JList_Map(object);
			}
			
			else if(object instanceof Update_OtherUser_List){
				
				Update_Combo_Box(object);
			}
			
			else if(object instanceof Server_Chat_Request_toClient){
				
				Reply_Client_Request(object);
			}
			
			else if(object instanceof Server_Chat_Reply_toClient){
				
				Processing_Reply_ofClient(object);
			}
			
			else if(object instanceof Client_Friend_Request_to_Server){
				
				Process_Friend_Request(object);
			}
			
			else if(object instanceof Client_Friend_Reply_to_Server){
				
				Process_Friend_Reply(object);
			}
			else if(object  instanceof Friend_Delete){
				
				Process_Friend_Delete(object);
			}
		}
		
	}
	
	public synchronized void DisconnectClient(){
		
		
		for(int i=0;i<Client_Obj_List.size();i++){
			
			Client_Obj_List.get(i).Chat_Disconnect();
		}
		
		Client_Disconnect dis = new Client_Disconnect();
		dis.Disconnected = true;
		
		try {
			output.writeObject(dis);
			output.flush();
		}
		catch (IOException e) {
			All_Friends.clear();
			Online_Friends.clear();
			Other_Users.clear();
			Current_Chat.clear();
			Client_Obj_List.clear();
			connection_flag = false;
			JList_Map.clear();
			P2P_Chat_Client_MainWindow.JL_Model.clear();
			P2P_Chat_Client_MainWindow.ComboBox_Model.removeAllElements();
			P2P_Chat_Client_MainWindow.Other_Clients.setEnabled(false);
			P2P_Chat_Client_MainWindow.B_ADD.setEnabled(false);
			P2P_Chat_Client_MainWindow.JL_ONLINE.setEnabled(false);
			P2P_Chat_Client_MainWindow.Update_MainWindow(null);
			return;
		}
		
		All_Friends.clear();
		Online_Friends.clear();
		Other_Users.clear();
		Current_Chat.clear();
		Client_Obj_List.clear();
		connection_flag = false;
		JList_Map.clear();
		P2P_Chat_Client_MainWindow.JL_Model.clear();
		P2P_Chat_Client_MainWindow.ComboBox_Model.removeAllElements();
		P2P_Chat_Client_MainWindow.Other_Clients.setEnabled(false);
		P2P_Chat_Client_MainWindow.B_ADD.setEnabled(false);
		P2P_Chat_Client_MainWindow.JL_ONLINE.setEnabled(false);
		P2P_Chat_Client_MainWindow.Update_MainWindow(null);
	}
	
	private void Update_Combo_Box(Object O){
		
		Update_OtherUser_List list  = (Update_OtherUser_List)O;
		Other_Users.clear();
		Other_Users.addAll((ArrayList<String>)list.getOther_Users());
		
		P2P_Chat_Client_MainWindow.ComboBox_Model.removeAllElements();
		for(int i=0;i<Other_Users.size();i++){
			
			P2P_Chat_Client_MainWindow.ComboBox_Model.addElement(Other_Users.get(i));
		}
		
		P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
	}
	
	
	private void Update_JList_Map(Object O){
		
		Update_Online_List list = (Update_Online_List)O;
		Online_Friends.clear();
		Online_Friends.addAll((ArrayList<String>)list.getOnline_Users());
		
		JList_Map.clear();
		for(int i=0;i<All_Friends.size();i++){
			
			if(Online_Friends.contains(All_Friends.get(i))){
				
				JList_Map.put(All_Friends.get(i), true);
			}else{
				JList_Map.put(All_Friends.get(i), false);
			}
		}
		
		P2P_Chat_Client_MainWindow.JL_ONLINE.setCellRenderer(new IconListRenderer(JList_Map));		
	}
	
	
	private void Update_JList_Map(){
		
		P2P_Chat_Client_MainWindow.JL_ONLINE.setEnabled(true);
		
		JList_Map.clear();
		for(int i=0;i<All_Friends.size();i++){
			
			if(Online_Friends.contains(All_Friends.get(i))){
				
				JList_Map.put(All_Friends.get(i), true);
			}else{
				JList_Map.put(All_Friends.get(i), false);
			}
		}
		
		P2P_Chat_Client_MainWindow.JL_ONLINE.setCellRenderer(new IconListRenderer(JList_Map));
	}
	
	
	public void set_JList(){
		
		P2P_Chat_Client_MainWindow.JL_ONLINE.setFont(new Font("Lucida Calligraphy", Font.BOLD, 14));
		P2P_Chat_Client_MainWindow.JL_ONLINE.setBackground(new Color(51, 51, 51));
		P2P_Chat_Client_MainWindow.JL_ONLINE.setEnabled(true);
		P2P_Chat_Client_MainWindow.JL_ONLINE.setForeground(new Color(0, 153, 255));
		
		P2P_Chat_Client_MainWindow.SP_ONLINE.setViewportView(P2P_Chat_Client_MainWindow.JL_ONLINE);
	}
	
	
	public void Reply_Client_Request(Object O){
		
		Server_Chat_Request_toClient S_req = (Server_Chat_Request_toClient)O;
		Requested_Clientname = S_req.getUsername();
		try {
			int port = getAvailable_Port();
			Send_Client_Reply_toServer(true,port);
		} catch (IOException e) {
			return;
		}
	}
	
	
	public void Send_Client_Reply_toServer(boolean Reply, int Client_Server_Socket_Port) throws IOException{
		
		Client_Chat_Reply_toServer C_reply = new Client_Chat_Reply_toServer();
		
		if(Reply == true){
			
			String IP = getIPAddress();
			System.out.println(IP);
			
			C_reply.setDoChat(Reply);
			C_reply.setClientIP(IP);
			C_reply.setClientPORT(Client_Server_Socket_Port);
			C_reply.setClientName(Requested_Clientname);
			
			output.writeObject(C_reply);
			output.flush();
			
			Current_Chat.add(Requested_Clientname);
			Start_Chatting_as_Server(Client_Server_Socket_Port,Requested_Clientname);
		}
		
		else{
			
			C_reply.setDoChat(Reply);
			C_reply.setClientName(Requested_Clientname);
			output.writeObject(C_reply);
			output.flush();
		}
		
	}
	
	
/*This Method is used to get the local IP of the connected LAN */
	
	
	private static String getIPAddress(){
		
		 	String ipAddress = null;
		    Enumeration<NetworkInterface> net = null;
		    try {
		        net = NetworkInterface.getNetworkInterfaces();
		    }catch(IOException e){
		    	return ipAddress;
		    }

		    while(net.hasMoreElements()){
		        NetworkInterface element = net.nextElement();
		        Enumeration<InetAddress> addresses = element.getInetAddresses();
		        while (addresses.hasMoreElements()){
		            InetAddress ip = addresses.nextElement();
		            if (ip instanceof Inet4Address){

		                if (ip.isSiteLocalAddress()){

		                    ipAddress = ip.getHostAddress();
		                }
		            }
		        }
		    }
		   
		    if(ipAddress==null){
            	ipAddress = "127.0.0.1";
            }
	    return ipAddress;
	}
	
	
	private static int getAvailable_Port(){
		
		int available_Port;
		ServerSocket sock = null;
		
		while(true){
			
			 try {
					sock = new ServerSocket(Port_Number);
				} catch (UnknownHostException e) {
				} catch (IOException e) {
					Port_Number++;
					Port_Number++;
				}if(sock!=null){
					 try {
						sock.close();
						available_Port = Port_Number;
						Port_Number++;
						Port_Number++;

						break;
					} catch (IOException e) {
					}
			}
			
		}
		
		return available_Port;		
	}
	
	public void Send_Friend_Request(String name) throws IOException{
		
		Client_Friend_Request_to_Server Request = new Client_Friend_Request_to_Server();
		Request.setName(name);
		
		output.writeObject(Request);
		output.flush();
	}
	
	public void Send_Unfriend_Notification(String client) throws IOException{		
		
		if(Current_Chat.contains(client)){
			
			for(int i=0;i<Client_Obj_List.size();i++){
				
				if(client.equals(Client_Obj_List.get(i).Client_name)){
					
					Client_Obj_List.get(i).Chat_Disconnect();
					Current_Chat.remove(client);
					Client_Obj_List.remove(i);
					break;
				}
			}
		}
		
		Friend_Delete F_del = new Friend_Delete();
		F_del.set_Name(client);
		
		if(Online_Friends.contains(client)){
			
			F_del.set_State(true);
			Other_Users.add(client);
			P2P_Chat_Client_MainWindow.ComboBox_Model.addElement(client);
			P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
			Online_Friends.remove(client);
		}else{
			F_del.set_State(false);
		}
		
		output.writeObject(F_del);
		output.flush();
		
		All_Friends.remove(client);
		
		JList_Map.remove(client);
		P2P_Chat_Client_MainWindow.JL_Model.removeElement(client);
		P2P_Chat_Client_MainWindow.JL_ONLINE.setModel(P2P_Chat_Client_MainWindow.JL_Model);
		
//		Update_JList_Map();
		
		JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.MainWindow, client + "is Removed from FriendList");
		
	}
	public void Processing_Reply_ofClient(Object O){
		
		Server_Chat_Reply_toClient S_reply = (Server_Chat_Reply_toClient)O;
		
		if(S_reply.getDoChat()){
			
			String Client_IP = S_reply.getClientIP();
			int Client_PORT = S_reply.getClientPORT();
			String Client_name = S_reply.getClientNAME();
			
			Current_Chat.add(Client_name);
			Start_Chatting_as_Client(Client_IP,Client_PORT,Client_name);
		}
		
		else{
			
			String Client_name = S_reply.getClientNAME();
			JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.MainWindow, Client_name + "is busy,cannot chat right now");
			
		}
	}
	
	public void Process_Friend_Request(Object O) throws IOException{
		
		Client_Friend_Request_to_Server F_req = (Client_Friend_Request_to_Server)O;
		
		String friend_name = F_req.getName();
		String Message = friend_name + " wants to be your friend";
		int result = JOptionPane.showConfirmDialog(P2P_Chat_Client_MainWindow.MainWindow,Message,"Friend Request",JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION){
			
			All_Friends.add(friend_name);
			Online_Friends.add(friend_name);
			Other_Users.remove(friend_name);
			P2P_Chat_Client_MainWindow.ComboBox_Model.removeElement(friend_name);
			P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
			
			JList_Map.put(friend_name,true);
			P2P_Chat_Client_MainWindow.JL_Model.addElement(friend_name);
			P2P_Chat_Client_MainWindow.JL_ONLINE.setModel(P2P_Chat_Client_MainWindow.JL_Model);
			
//			Update_JList_Map();
			
			Client_Friend_Reply_to_Server F_rep = new Client_Friend_Reply_to_Server();
			F_rep.setName(friend_name);
			F_rep.setFlag(true);
			
			output.writeObject(F_rep);
			output.flush();
		}else{
			
			Client_Friend_Reply_to_Server F_rep = new Client_Friend_Reply_to_Server();
			F_rep.setName(friend_name);
			F_rep.setFlag(false);
			
			output.writeObject(F_rep);
			output.flush();	
		}
		
	}
	
	private void Process_Friend_Reply(Object O){ 
		
		Client_Friend_Reply_to_Server F_rep = (Client_Friend_Reply_to_Server)O;
		String friend_name = F_rep.getName();
		
		if(F_rep.getApprove()){
			
			All_Friends.add(friend_name);
			Online_Friends.add(friend_name);
			Other_Users.remove(friend_name);
			P2P_Chat_Client_MainWindow.ComboBox_Model.removeElement(friend_name);
			P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
			
			JList_Map.put(friend_name, true);
			P2P_Chat_Client_MainWindow.JL_Model.addElement(friend_name);
			P2P_Chat_Client_MainWindow.JL_ONLINE.setModel(P2P_Chat_Client_MainWindow.JL_Model);
			
//			Update_JList_Map();
			
			JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.MainWindow,friend_name + " Accepted your friend request");
		}else{
			
			JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.MainWindow, friend_name + " Rejected your friend request");
		}
	}
	
	private void Process_Friend_Delete(Object O){
		
		Friend_Delete F_del = (Friend_Delete)O;
		String client = F_del.get_Name();
		
		All_Friends.remove(client);
		Online_Friends.remove(client);
		Other_Users.add(client);
		P2P_Chat_Client_MainWindow.ComboBox_Model.addElement(client);
		P2P_Chat_Client_MainWindow.Other_Clients.setModel(P2P_Chat_Client_MainWindow.ComboBox_Model);
		
		JList_Map.remove(client);
		P2P_Chat_Client_MainWindow.JL_Model.removeElement(client);
		P2P_Chat_Client_MainWindow.JL_ONLINE.setModel(P2P_Chat_Client_MainWindow.JL_Model);
		
//		Update_JList_Map();
		
		JOptionPane.showMessageDialog(P2P_Chat_Client_MainWindow.MainWindow," You are no more friend with " + client);
	}
	
	public void Connection_Request_toServer(int index) throws IOException{
		
		String name = All_Friends.get(index);
		Client_Chat_Request_toServer  new_request = new Client_Chat_Request_toServer();
		new_request.setUserName(name);
		
		output.writeObject(new_request);
		output.flush();
		
	}
	
	private void Start_Chatting_as_Client(String client_ip , int client_port , String client_name){
		
			try {
				Client_Chat_Handler Client_Chat_Obj;
				Client_Chat_Obj = new Client_Chat_Handler (client_ip,client_port,client_name);
				Thread C_Client_thread = new Thread(Client_Chat_Obj);
				C_Client_thread.start();
				
				Client_Obj_List.add(Client_Chat_Obj);
				
				Client_Chat_Obj.new_Client_GUI.setVisible(true);
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
	} 
	
	private void Start_Chatting_as_Server(int server_port,String Client_name){
		
		Client_Chat_Handler SClient_Chat_Obj;
		try {
			SClient_Chat_Obj = new Client_Chat_Handler (server_port,Client_name);
			Thread S_Client_thread = new Thread(SClient_Chat_Obj);
			S_Client_thread.start();
			
			Client_Obj_List.add(SClient_Chat_Obj);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
}




@SuppressWarnings("serial")
class IconListRenderer extends DefaultListCellRenderer {

	public Map<String,Boolean> JList_Map = null;

	public IconListRenderer(Map<String, Boolean> list) {
	this.JList_Map = list;
	}

	
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel label = null;
		
		if(!this.JList_Map.isEmpty()){
			
			label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
		Icon icon1 = new ImageIcon(getClass().getResource("online-icon-1.png"));
		Icon icon2 = new ImageIcon(getClass().getResource("online-icon-2.png"));
		
		if(JList_Map.get(value)){
			label.setIcon(icon1);
		}else{
			label.setIcon(icon2);
		}
		
		}
		
		return label;
	}
}