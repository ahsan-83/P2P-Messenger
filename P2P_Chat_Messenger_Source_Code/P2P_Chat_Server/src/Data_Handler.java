import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
class LogIn_Packet implements Serializable{
	
	private String username ;
	private char[] password;
	
	public void setLogInPacket(String name , char[] pass){
		
		username = name;
		password = pass;
	}
	
	public String getName(){
		return username;
	}
	
	public char[] getPass(){
		return password;
	}
}

@SuppressWarnings("serial")
class LogIn_Approved_Message implements Serializable{

	public boolean LogIn_OK ;
	
	private  ArrayList <String> All_Friends = new ArrayList<String>();
	private  ArrayList <String> Online_Friends = new ArrayList<String>();
	private ArrayList<String> Other_Users = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public void setUsers_Info(ArrayList<String> all_users,ArrayList <String> online_users,ArrayList<String> other_users){
		
		
		All_Friends = (ArrayList<String>) all_users.clone();	
		Online_Friends = (ArrayList<String>) online_users.clone();
		Other_Users = (ArrayList<String>) other_users.clone();
	}
	
	public ArrayList<String> getAll_Users(){
		
		return All_Friends;
	}
	
	public ArrayList<String> getOnline_Users(){
		
		return Online_Friends;
	}
	
	public ArrayList<String> getOther_Users(){
		
		return Other_Users;
	}
}

@SuppressWarnings("serial")
class Update_Online_List implements Serializable{
	
	private  ArrayList <String> Online_Friends = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public void setUsers_Info(ArrayList <String> online_users){
			
		Online_Friends = (ArrayList<String>) online_users.clone();
	}
	
	public ArrayList<String> getOnline_Users(){
		
		return Online_Friends;
	}
	
}

@SuppressWarnings("serial")
class Update_OtherUser_List implements Serializable{
	
	private  ArrayList <String> Other_users = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public void setUsers_Info(ArrayList <String> other_users){
			
		Other_users = (ArrayList<String>) other_users.clone();
	}
	
	public ArrayList<String> getOther_Users(){
		
		return Other_users;
	}
	
}


@SuppressWarnings("serial")
class Client_Chat_Request_toServer implements Serializable {
	
	private String Requested_User ;
	
	public void setUserName(String name){
		Requested_User = name;
	}
	
	public String getUsername(){
		 
		return Requested_User;
	}
}

@SuppressWarnings("serial")
class Server_Chat_Request_toClient implements Serializable {
	
	private String Requested_User ;
	
	public void setUserName(String name){
		Requested_User = name;
	}
	
	public String getUsername(){
		 
		return Requested_User;
	}
}

@SuppressWarnings("serial")
class Client_Chat_Reply_toServer implements Serializable {
	
	 private boolean DoChat;
	 private String Client_IP = null;
	 private int Client_PORT;
	 private String Client_NAME = null;
	 
	 public void setClientIP(String ip){
		 
		 Client_IP = ip; 
	 }
	 
	 public void setClientPORT(int port){
		 
		 Client_PORT = port; 
	 }
	 
	 public void setClientName(String name){
		 
		 Client_NAME = name;
	 }
	 
	 public void setDoChat(boolean flag){
		 DoChat = flag;
	 }
	 
	 public String getClientIP(){
		 
		 return Client_IP;
	 }
	 
	 public int getClientPORT(){
		 
		 return Client_PORT;
	 }
	 
	 public String getClientNAME(){
		 
		 return Client_NAME;
	 }
	 public boolean getDoChat(){
		 return DoChat;
	 }
}


@SuppressWarnings("serial")
class Server_Chat_Reply_toClient implements Serializable {
	
	private boolean DoChat;
	private String Client_IP = null;
	private int Client_PORT = 0;
	private String Client_NAME = null;
	
	public void setClientIP(String ip){
		 
		 Client_IP = ip; 
	 }
	 
	 public void setClientPORT(int port){
		 
		 Client_PORT = port; 
	 }
	 
	 public void setClientName(String name){
		 
		 Client_NAME = name;
	 }
	 
	 public void setDoChat(boolean flag){
		 DoChat = flag;
	 }
	 
	 public String getClientIP(){
		 
		 return Client_IP;
	 }
	 
	 public int getClientPORT(){
		 
		 return Client_PORT;
	 }
	 
	 public String getClientNAME(){
		 
		 return Client_NAME;
	 }
	 public boolean getDoChat(){
		 return DoChat;
	 }
	
}

@SuppressWarnings("serial")
class Client_Disconnect implements Serializable {
	
	public boolean Disconnected;
	
} 


@SuppressWarnings("serial")
class LogIn_Denied implements Serializable {
	
	public boolean LogIn_Denied;
	public boolean Same_User;

}

@SuppressWarnings("serial")
class Chat_Disconnect implements Serializable{
	
	private boolean Disconnected;
	
	public void setDis_flag(){
		
		Disconnected = true;
	}
	
	public boolean getDis_flag(){
		
		return Disconnected;
	}
}

@SuppressWarnings("serial")
class File_Disconnect implements Serializable{
	
	private boolean Disconnected;
	
	public void setDis_flag(){
		
		Disconnected = true;
	}
	
	public boolean getDis_flag(){
		
		return Disconnected;
	}
}

@SuppressWarnings("serial")
class file_promp implements Serializable{
	
	public long  filesize;
	public String filename;
}

@SuppressWarnings("serial")
class file_recv implements Serializable{
	public boolean rec_OK;
}

@SuppressWarnings("serial")
class FileClass implements Serializable {

    public long off;
    public byte[] arr;
    
    FileClass(long g,byte[] b)
    {
        off=g;
        arr = b;
    }

}

@SuppressWarnings("serial")
class Client_Friend_Request_to_Server implements Serializable{
	
	private String Friend_Name;
	
	public void setName(String name){
		
		Friend_Name = name;
	}
	
	public String getName(){
		
		return Friend_Name;
	}
	
}


@SuppressWarnings("serial")
class Client_Friend_Reply_to_Server implements Serializable{
	
	private String Friend_Name;
	private boolean approve ;
	
	public void setName(String name){
		
		Friend_Name = name;
	}
	
	public String getName(){
		
		return Friend_Name;
	}
	
	public void setFlag(boolean flag){
		
		approve = flag;
	}
	
	public boolean getApprove(){
		
		return approve;
	}
	
}

@SuppressWarnings("serial")
class Friend_Delete implements Serializable{
	
	private String client;
	private boolean C_State;
	
	public void set_Name(String name){
		
		client = name;
	}
	
	public String get_Name(){
		return client;
	}
	
	public void set_State(boolean flag){
		
		C_State = flag;
	}
	public boolean get_State(){
		
		return C_State;
	}
}


