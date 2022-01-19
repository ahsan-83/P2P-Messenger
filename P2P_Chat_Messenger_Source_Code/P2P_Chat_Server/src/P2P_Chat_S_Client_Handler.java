import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class P2P_Chat_S_Client_Handler implements Runnable{
	
	//Globals Variables
	public  ArrayList<String> ALL_Friends = new ArrayList<String>();
	public  ArrayList<String> Online_Friends = new ArrayList<String>();
	public ArrayList<String> Other_Users = new ArrayList<String>();
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Object object;
	private Socket S_CLIENT_SOCK=null;
	private String Client_username;
	private boolean S_Client_State ;
	private boolean LogIn_Approve;
	private int User_Index;
	private boolean Same_User;
	
//Initialize Socket of Client Thread----------------------------------------------------------------------------------------------------------------------------------------------
	
	public P2P_Chat_S_Client_Handler(Socket new_sock) throws IOException{
		this.S_CLIENT_SOCK = new_sock;
		this.LogIn_Approve = false;
		this.S_Client_State = false;
		this.Same_User = false;
	}
	
// Calling Run Method ------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void run(){
		
			try{
				
				setStreams();
				
				CheckLogIn();
				
				if(LogIn_Approve){
					getInput();
				}
				  
			}catch(ClassNotFoundException ex){
				
				P2P_Chat_Server_Handler.Clients.remove(this);
				P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
				P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
				S_Client_State = false;
				
				} catch (IOException ex) {
					
					P2P_Chat_Server_Handler.Clients.remove(this);
					P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
					P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
					S_Client_State = false;
            }finally{
            	
            	try {
					output.close();
					input.close();
					S_CLIENT_SOCK.close();
					
					if(!(Client_username==null))P2P_Chat_Server_MainWindow.Update_Text_Box("---------" + Client_username + "  disconnected from server");
					
					//System.err.println("---------" + Client_username + "  disconnected from server");
				} catch (IOException e) {
					
					P2P_Chat_Server_Handler.Clients.remove(this);
					P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
					P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
					S_Client_State = false;
					return;
				}
            }				
	}
	
// Initialize Streams ----------------------------------------------------------------------------------------------------------------------------------
	
	public void setStreams() throws IOException{
		
		output = new ObjectOutputStream(S_CLIENT_SOCK.getOutputStream() );
        output.flush(); 
       
        input = new ObjectInputStream(S_CLIENT_SOCK.getInputStream());
	}
	
	public void CheckLogIn() throws ClassNotFoundException, IOException{
		
		object = input.readObject();
		
		LogIn_Packet log = (LogIn_Packet)object;
		
		String name = log.getName();
		char[] pass = log.getPass();
		
		for(int i=0;i<P2P_Chat_Server_Handler.Users_List.size();i++){
			
			if(P2P_Chat_Server_Handler.Users_List.get(i).Username.equals(name) && Check_Pass(i,pass)){
				
				if(!P2P_Chat_Server_Handler.CurrentUsers.contains(name)){
					User_Index = i;
					P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = true ;
					Client_username = name;
					LogIn_Approve = true;
					S_Client_State = true;
					P2P_Chat_Server_Handler.CurrentUsers.add(name);
					break;
			}else{
					Same_User = true;
					break;
				}
			}
		}
		
		if(LogIn_Approve){
			
			P2P_Chat_Server_MainWindow.Update_Text_Box("---------" + Client_username + "   Connected to Server");
			//System.out.println("---------" + Client_username + "   Connected to Server");
			SendApproveMessage();
			
			Update_Friends_Online_List(true);
			
			Update_Other_Users(true);
			
			//  for(int i=0;i<P2P_Chat_Server.Clients.size();i++){
				
				//System.out.println(P2P_Chat_Server.Clients.get(i).LogIn_Approve);
			//}
			
		}else{
			
			DisconnectClient();
		}
		
	}
	
	
	private boolean Check_Pass(int index , char[] pass){
		
		String DB_Pass = P2P_Chat_Server_Handler.Users_List.get(index).Password;
		char[] us_pass = pass;
		
		if(us_pass.length == DB_Pass.length()){
			
			for(int i=0;i<DB_Pass.length();i++){
				if(DB_Pass.charAt(i)!= us_pass[i]){
					return false;
				}
			}
			
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	public void SendApproveMessage() throws IOException{
		
		for(int i=0;i<P2P_Chat_Server_Handler.Users_List.get(User_Index).FriendList.size();i++){
			
			int index = P2P_Chat_Server_Handler.Users_List.get(User_Index).FriendList.get(i);
			
			ALL_Friends.add(P2P_Chat_Server_Handler.Users_List.get(index-1).Username);
			
			if(P2P_Chat_Server_Handler.Users_List.get(index-1).Online_State){
				
				Online_Friends.add(P2P_Chat_Server_Handler.Users_List.get(index-1).Username);
			}
		}
		
		for(int i=0;i<P2P_Chat_Server_Handler.Users_List.size();i++){
			
			if(P2P_Chat_Server_Handler.Users_List.get(i).Online_State){
				if(!P2P_Chat_Server_Handler.Users_List.get(i).Username.equals(Client_username)){
					if(!ALL_Friends.contains(P2P_Chat_Server_Handler.Users_List.get(i).Username)){
						
						Other_Users.add(P2P_Chat_Server_Handler.Users_List.get(i).Username);
					}
				}	
			}			
		}
		
		LogIn_Approved_Message Aprv = new LogIn_Approved_Message();
		Aprv.LogIn_OK = true;
		Aprv.setUsers_Info(ALL_Friends, Online_Friends,Other_Users);
		
		output.writeObject(Aprv);
		output.flush();
		
	}
	
	
	public void Update_Friends_Online_List(boolean Connect) throws IOException{
		
		if(Connect){
			for(int i=0;i<Online_Friends.size();i++){
				
				String name = Online_Friends.get(i);
				
				if(P2P_Chat_Server_Handler.CurrentUsers.contains(name)){
					
					P2P_Chat_Server_Handler.Clients.get(P2P_Chat_Server_Handler.CurrentUsers.indexOf(name)).Send_Online_List(Client_username,true);
				}
			}
		}else{
			for(int i=0;i<Online_Friends.size();i++){
							
				String name = Online_Friends.get(i);
				
				if(P2P_Chat_Server_Handler.CurrentUsers.contains(name)){
									
					P2P_Chat_Server_Handler.Clients.get(P2P_Chat_Server_Handler.CurrentUsers.indexOf(name)).Send_Online_List(Client_username,false);
				}
			}	
		}
	}
	
	public void Update_Other_Users(boolean Connect) throws IOException{
		
		if(Connect){
			for(int i=0;i<Other_Users.size();i++){
				
				String name = Other_Users.get(i);
				
				if(P2P_Chat_Server_Handler.CurrentUsers.contains(name)){
					P2P_Chat_Server_Handler.Clients.get(P2P_Chat_Server_Handler.CurrentUsers.indexOf(name)).Send_Other_User_List(Client_username,true);
				}
			}
		}else{
			for(int i=0;i<Other_Users.size();i++){
				
				String name = Other_Users.get(i);
				
				if(P2P_Chat_Server_Handler.CurrentUsers.contains(name)){
					P2P_Chat_Server_Handler.Clients.get(P2P_Chat_Server_Handler.CurrentUsers.indexOf(name)).Send_Other_User_List(Client_username,false);
				}
			}
		}
		
	}
	
	
	public void Send_Online_List(String name,boolean Connect) throws IOException{
		
		if(Connect){
			Online_Friends.add(name);
		}
		else {
			Online_Friends.remove(name);
		}
		
		Update_Online_List list = new Update_Online_List();
		list.setUsers_Info(Online_Friends);
		
		output.writeObject(list);
		output.flush();
	}
	
	public void Send_Other_User_List(String name,boolean Connect) throws IOException{
		
		if(Connect){
			Other_Users.add(name);
		}
		else {
			Other_Users.remove(name);
		}
		
		Update_OtherUser_List list = new Update_OtherUser_List();
		list.setUsers_Info(Other_Users);
		
		output.writeObject(list);
		output.flush();
	}
	
	
// Get Input from Clients -----------------------------------------------------------------------------------------------------------------------------

	private void getInput() throws IOException , ClassNotFoundException{
		
		while(S_Client_State){
			
			try{
				object = input.readObject();
			}catch(EOFException e){
				
				Update_Friends_Online_List(false);
				Update_Other_Users(false);
				
				P2P_Chat_Server_Handler.Clients.remove(this);
				P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
				P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
				S_Client_State = false;
				return;
			}catch(SocketException sokexception){
				
				Update_Friends_Online_List(false);
				Update_Other_Users(false);
				
				P2P_Chat_Server_Handler.Clients.remove(this);
				P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
				P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
				S_Client_State = false;
				return;			
			}
			if(object instanceof Client_Chat_Request_toServer){
				
				ProcessConnectionRequest(object);
			}
			
			else if(object instanceof Client_Chat_Reply_toServer){
				
				ProcessConnectionReply(object);
			}
			
			else if(object instanceof Client_Friend_Request_to_Server){
				
				ProcessFriendRequest(object);
			}
			
			else if(object instanceof Client_Friend_Reply_to_Server){
				
				ProcessFriendReply(object);
			}
			
			else if(object instanceof Friend_Delete){
				
				Delete_Friend(object);
			}
			else if(object instanceof Client_Disconnect){
				
				DisconnectClient(object);		
				break;
			}			
		}
	}
	
		
//Process Connection Request to another Client-Server-----------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void ProcessConnectionRequest(Object O) throws IOException{
		
		Client_Chat_Request_toServer Creq_object = (Client_Chat_Request_toServer)O;
		String Username = Creq_object.getUsername();
		
		Server_Chat_Request_toClient Sreq_object = new Server_Chat_Request_toClient();
		Sreq_object.setUserName(Client_username);
		
		for(int i=0;i<P2P_Chat_Server_Handler.Clients.size();i++){
			
			String name = (String) P2P_Chat_Server_Handler.Clients.get(i).Client_username;
			if(Username.equals(name)){
				
				P2P_Chat_Server_Handler.Clients.get(i).Send_Notification(Sreq_object);
				break;
			}
		}
		
	}
	
	private void ProcessFriendRequest(Object O) throws IOException{
		
		Client_Friend_Request_to_Server F_req = (Client_Friend_Request_to_Server)O;
		
		String friend_name = F_req.getName();	
		
		for(int i=0;i<P2P_Chat_Server_Handler.Clients.size();i++){
					
			String name = (String) P2P_Chat_Server_Handler.Clients.get(i).Client_username;
			if(friend_name.equals(name)){
				
				F_req.setName(Client_username);
				P2P_Chat_Server_Handler.Clients.get(i).Send_Notification(F_req);
				break;
			}
		}	
	}
	
	private void ProcessFriendReply(Object O) throws IOException{
		
		Client_Friend_Reply_to_Server F_rep = (Client_Friend_Reply_to_Server)O;
		
		String friend_name = F_rep.getName();
		
		if(F_rep.getApprove()){
				
			Other_Users.remove(friend_name);
			Online_Friends.add(friend_name);
			ALL_Friends.add(friend_name);
			
			for(int i=0;i<P2P_Chat_Server_Handler.Users_List.size();i++){
				
				if(P2P_Chat_Server_Handler.Users_List.get(i).Username.equals(friend_name)){
					
					P2P_Chat_Server_Handler.Users_List.get(i).FriendList.add(User_Index+1);
					P2P_Chat_Server_Handler.Users_List.get(User_Index).FriendList.add(i+1);
					break;
				}
			}		
		}
		
		for(int i=0;i<P2P_Chat_Server_Handler.Clients.size();i++){
					
			String name = (String) P2P_Chat_Server_Handler.Clients.get(i).Client_username;
			if(friend_name.equals(name)){
				
				if(F_rep.getApprove()){
					
					P2P_Chat_Server_Handler.Clients.get(i).Online_Friends.add(Client_username);
					P2P_Chat_Server_Handler.Clients.get(i).ALL_Friends.add(Client_username);
					P2P_Chat_Server_Handler.Clients.get(i).Other_Users.remove(Client_username);
				}
				F_rep.setName(Client_username);
				P2P_Chat_Server_Handler.Clients.get(i).Send_Notification(F_rep);
				break;
			}
		}	
	}
	
//Send Connection Request to another Client ----------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void Send_Notification(Object O) throws IOException{
		
		output.writeObject(O);
		output.flush();	
	}
	
//Reply Connection Request to another Client-Server--------------------------------------------------------------------------------------------------------------------------------------------------------

	private void ProcessConnectionReply(Object O) throws IOException{
		
		Client_Chat_Reply_toServer Crep_object = (Client_Chat_Reply_toServer)O;
		String Username = Crep_object.getClientNAME();
		
		Server_Chat_Reply_toClient Srep_object = new Server_Chat_Reply_toClient();
		if(Crep_object.getDoChat()){
					
			Srep_object.setClientIP(Crep_object.getClientIP());
			Srep_object.setClientPORT(Crep_object.getClientPORT());
		}
		
		Srep_object.setClientName(Client_username);
		Srep_object.setDoChat(Crep_object.getDoChat());
		
		for(int i=0;i<P2P_Chat_Server_Handler.Clients.size();i++){
			
			String name = (String) P2P_Chat_Server_Handler.Clients.get(i).Client_username;
			if(Username.equals(name)){
				
				P2P_Chat_Server_Handler.Clients.get(i).Send_Notification(Srep_object);
				break;
			}
		}
		
	}
	
	
// Disconnect Client and Stopping Client Thread ------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void DisconnectClient() throws IOException{
		
		P2P_Chat_Server_Handler.Clients.remove(this);
		LogIn_Denied dis  = new LogIn_Denied();
		dis.LogIn_Denied = true;
		if(Same_User){
			dis.Same_User = true;
		}else{
			dis.Same_User = false;
		}
		
		output.writeObject(dis);
		output.flush();
	}
	
	public void Delete_Friend(Object O){
		
		Friend_Delete F_del = (Friend_Delete)O;
		String client = F_del.get_Name();
		
		ALL_Friends.remove(client);
		
		for(int i=0;i<P2P_Chat_Server_Handler.Users_List.size();i++){
			
			if(P2P_Chat_Server_Handler.Users_List.get(i).Username.equals(client)){
				
				P2P_Chat_Server_Handler.Users_List.get(i).FriendList.remove(new Integer(User_Index+1));
				P2P_Chat_Server_Handler.Users_List.get(User_Index).FriendList.remove(new Integer(i+1));
				break;
			}
		}
		
		if(F_del.get_State()){
			
			Online_Friends.remove(client);
			Other_Users.add(client);
			
			for(int i=0;i<P2P_Chat_Server_Handler.Clients.size();i++){
				
				if(client.equals(P2P_Chat_Server_Handler.Clients.get(i).Client_username)){
					
					try {
						P2P_Chat_Server_Handler.Clients.get(i).Delete_Friend_Notification(Client_username);
					} catch (IOException e) {
					}
					break;
				}
			}
		}
	}
	
	
	public void Delete_Friend_Notification(String client) throws IOException{
		
		ALL_Friends.remove(client);
		Online_Friends.remove(client);
		Other_Users.add(client);
		
		Friend_Delete F_del = new Friend_Delete();
		F_del.set_Name(client);
		
		output.writeObject(F_del);
		output.flush();
	}
	
	
	public void DisconnectClient(Object O) throws IOException{
		
		Client_Disconnect dis = (Client_Disconnect)O;
		if(dis.Disconnected){
			
			Update_Friends_Online_List(false);
			Update_Other_Users(false);
			
			P2P_Chat_Server_Handler.Clients.remove(this);
			P2P_Chat_Server_Handler.CurrentUsers.remove(Client_username);
			P2P_Chat_Server_Handler.Users_List.get(User_Index).Online_State = false ;
			S_Client_State = false;
		}
	}

// Disconnecting Client Connection---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
		
}
