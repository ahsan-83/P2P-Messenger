import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class P2P_Chat_Server_Handler implements Runnable{
	
	//Global ArrayList
	
	public static ArrayList<Users_Custom_Data> Users_List = new ArrayList<Users_Custom_Data>();
	public static ArrayList<String> CurrentUsers = new ArrayList<String>();
	public static ArrayList<P2P_Chat_S_Client_Handler> Clients = new ArrayList<P2P_Chat_S_Client_Handler>();
	
	// Global Server Variables
	
	private ServerSocket SERVER_SOCK;
	private Socket S_CLIENT_SOCK;
	private int Server_PORT;
	public Scanner scan;
	public Boolean Check_Flag = false;
	

	//Constructor of P2P_Chat_Server -------------------------------------------------------------------------------------------------------------------
	
	public  P2P_Chat_Server_Handler() throws IOException{
			
			Configure_User_Data();
			if(Check_Flag){
				Server_PORT = 2424;
				SERVER_SOCK = new ServerSocket(Server_PORT);
				
				P2P_Chat_Server_MainWindow.Update_Text_Box("--------- Waiting for Clients.....");
			}
			//System.out.println("Waiting for Clients.....");

	}
	
	//Configure UserData of P2P_Chat_Server---------------------------------------------------------------------------------------------------------------
	
	public void Configure_User_Data(){
		
		try{
			
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = dir.toString();
			scan = new Scanner(new File("src/Users.txt"));
			Check_Flag = true;
		}catch(Exception e){
			JOptionPane.showMessageDialog(P2P_Chat_Server_MainWindow.Server_MainWindow, "Can not find Users.txt in jar file location.");
			Check_Flag = false;
			return;
			//			System.out.println("Can not Find File");
		}	
		
			while(scan.hasNext()){
				
				Users_Custom_Data new_Data = new Users_Custom_Data();
				
				new_Data.id = scan.nextInt();
				new_Data.Username = scan.next();
				new_Data.Password = scan.next();
					
				while(!scan.hasNext("end")){
					
					int num = scan.nextInt();
					new_Data.FriendList.add(num);
				}
				
				scan.next();
				
				Users_List.add(new_Data);
			}
			
			scan.close();
	}
	
//Method to run server -----------------------------------------------------------------------------------------------------------------------------
	
	public void run(){
		
		if(Check_Flag)P2P_Chat_Server_MainWindow.Change_button();
		try{
						
            while(Check_Flag){

                S_CLIENT_SOCK = SERVER_SOCK.accept();
                
                P2P_Chat_S_Client_Handler new_Chat_client = new P2P_Chat_S_Client_Handler(S_CLIENT_SOCK);
                Thread Client_Thread = new Thread(new_Chat_client);
                Client_Thread.start();
                Clients.add(new_Chat_client);             
            }			
		}catch(Exception e){
			e.printStackTrace();
		}
	} 

}


// Custom Data Class Definition --------------------------------------------------------------------------------------------------------------------

class Users_Custom_Data{
	
	public int id;
	public String Username ;
	public String Password;
	public boolean Online_State = false;
	public ArrayList<Integer> FriendList = new ArrayList<Integer>();
	
}
