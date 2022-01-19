import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class P2P_Chat_Client_MainWindow {

		//Globals
		private static Client_Handler Main_Client=null;
		public static String Username = "Anonymous";
		public static int Client_Req_Index;
		public static String Client_Del_Name = null;
		
		//GUI-Globals Main Window
		public static JFrame MainWindow = new JFrame();
		
		private static JButton B_CONNECT = new JButton();
		private static JButton B_DISCONNECT = new JButton();
		private static JLabel L_ONLINE = new JLabel();
		private static JLabel L_LoggedInAs = new JLabel();
		private  static JLabel L_LoggedInAsBox = new JLabel();
		private static JLabel Add_friend = new JLabel();
		public static JComboBox <String>Other_Clients = new JComboBox<String>();
		public static DefaultComboBoxModel<String> ComboBox_Model;
		public static JButton B_ADD = new JButton();
		public static JLabel L_Pin_Label = new JLabel();
		public static JScrollPane SP_ONLINE = new JScrollPane();
		public static DefaultListModel<String> JL_Model; 
		public static JList<String> JL_ONLINE; 
		public static JPopupMenu Jpopup_Menu;
		public static JMenuItem J_Menu_1, J_Menu_2;
		public static JLabel S_label;
		public static JTextField S_field;
		public static String Server_IP;
		
		//GUI-Globals LogIn Window
		public static JFrame LogInWindow = new JFrame();
		public static JTextField TF_UsernameBox = new JTextField(20);
		public static JPasswordField TF_User_Password = new JPasswordField(20);
		public static JLabel L_EnterUserName = new JLabel("UserName");
		public static JLabel L_EnterPassword = new JLabel("Password");
		public static JButton B_LogIn = new JButton("LogIn");
		public static JButton B_About = new JButton("About");
		
		//GUI-Globals About Window
		public static JFrame AboutWindow = new JFrame();
		public static JLabel L_About = new JLabel("About P2P_Chat_Client");
		public static JTextArea T_About = new JTextArea();
		
		
	//------------------------------------------------------------------------------------------------------------------
		
		public void RunMainWindow(){
			BuildMainWindow();
			Initialize();
		}
	//--------------------------------------------------------------------------------------------------------------------
		
		public static void Connect(String username ,char[] password){
			
			try{
				
				final int PORT = 2424;
				final String HOST = Server_IP;
				Socket SOCK = new Socket(HOST,PORT);
				
				Main_Client = new Client_Handler(SOCK,username,password);
				Thread C_main_thread = new Thread(Main_Client);
				C_main_thread.start();
				S_field.setEnabled(false);
				
			}catch(Exception e ){
				JOptionPane.showMessageDialog(null, "Server not responding");
				System.exit(0);
			}
		}
	//------------------------------------------------------------------------------------------------------------------
		
		public static void BuildMainWindow(){
			
			MainWindow.setTitle("P2P Chat Messenger");
			MainWindow.setLocation(100,100);
			MainWindow.setSize(550,590);
			MainWindow.setResizable(false);
			ConfigureMainWindow();
			SetActionListener();
			MainWindow.setVisible(true);
		}
	//-----------------------------------------------------------------------------------------------------------------
		
		public static void Initialize(){
			B_DISCONNECT.setEnabled(false);
			B_CONNECT.setEnabled(true);
		}
	//-----------------------------------------------------------------------------------------------------------------
		
		public static void BuildLogInWindow(){
			
			ImageIcon background = new ImageIcon(P2P_Chat_Client_MainWindow.class.getClassLoader().getResource("login2.jpg"));
			ImageIcon img = new ImageIcon(P2P_Chat_Client_MainWindow.class.getResource("chat_icon.png"));
			
			LogInWindow.setIconImage(img.getImage());
			LogInWindow.setBounds(0, 0, 350, 500);
			LogInWindow.setLocationRelativeTo(MainWindow);
			LogInWindow.setContentPane(new JLabel(background));
			LogInWindow.getContentPane().setLayout(null);
			LogInWindow.setTitle("LogInWindow");
			LogInWindow.setResizable(false);
			
			TF_UsernameBox.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 14));
			TF_UsernameBox.setForeground(new Color(102, 0, 0));
			TF_UsernameBox.setBounds(81, 80, 167, 33);
			TF_UsernameBox.setBorder(new LineBorder(new Color(255, 0, 204)));
			LogInWindow.getContentPane().add(TF_UsernameBox);
			TF_UsernameBox.setColumns(10);
			TF_User_Password.setEchoChar('*');
			
			TF_User_Password.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 14));
			TF_User_Password.setBounds(81, 206, 167, 33);
			TF_User_Password.setForeground(new Color(102, 0, 0));
			TF_User_Password.setBorder(new LineBorder(new Color(255, 0, 204)));
			LogInWindow.getContentPane().add(TF_User_Password);
			TF_User_Password.setColumns(10);
			
			L_EnterUserName.setForeground(new Color(255, 0, 204));
			L_EnterUserName.setHorizontalAlignment(SwingConstants.CENTER);
			L_EnterUserName.setFont(new Font("Lucida Calligraphy", Font.BOLD, 17));
			L_EnterUserName.setBounds(100, 37, 117, 32);
			LogInWindow.getContentPane().add(L_EnterUserName);
			
			L_EnterPassword.setForeground(new Color(255, 0, 204));
			L_EnterPassword.setFont(new Font("Lucida Calligraphy", Font.BOLD, 17));
			L_EnterPassword.setHorizontalAlignment(SwingConstants.CENTER);
			L_EnterPassword.setBounds(100, 164, 117, 31);
			LogInWindow.getContentPane().add(L_EnterPassword);
			
			B_LogIn.setBackground(new Color(51, 51, 51));
			B_LogIn.setForeground(new Color(0, 153, 255));
			B_LogIn.setFont(new Font("Lucida Calligraphy", Font.BOLD, 17));
			B_LogIn.setBounds(100, 319, 117, 33);
			LogInWindow.getContentPane().add(B_LogIn);
			
			LogInWindow.setVisible(true);
			
		}
	//--------------------------------------------------------------------------------------------------------------------------
		
		public static void Build_About_Window(){
			
			ImageIcon background = new ImageIcon(P2P_Chat_Client_MainWindow.class.getClassLoader().getResource("login2.jpg"));
			ImageIcon img = new ImageIcon(P2P_Chat_Client_MainWindow.class.getResource("chat_icon.png"));
			
			AboutWindow.setIconImage(img.getImage());
			AboutWindow.setBounds(0, 0, 350, 500);
			AboutWindow.setLocationRelativeTo(MainWindow);
			AboutWindow.setContentPane(new JLabel(background));
			AboutWindow.getContentPane().setLayout(null);
			AboutWindow.setTitle("About P2P_Chat_Client");
			AboutWindow.setResizable(false);
			
			T_About.setForeground(Color.CYAN);
			T_About.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 13));
			T_About.setBackground(new Color(0,0,0,0));
			T_About.setLineWrap(true);
			T_About.setEditable(false);
			T_About.setEnabled(true);
			AboutWindow.getContentPane().add(T_About);
			T_About.setBounds(10,60,325,400);
			T_About.setText("P2P Chat Messenger is a term project of BUET CSE L-2 T-1, developed by  Ahsan Ali 1105083 under supervision    of Assistant Professor Mohammad    Saifur Rahman. " +
					"This project is    implemented in java jdk7 version and applicable for both windows and    ubuntu. Through this P2P Chat Messenger one can chat with another connected in same LAN network"+
					" and they can also transfer single file at a time.Login name and passwords and friend list are predefined in User.txt    file. Hope this will be usefull in  office work to chat with colleagues.\n\nThank you.\nAhsan Ali(1105083),BUET,CSE");
			
			L_About.setForeground(new Color(0, 153, 255));
			L_About.setHorizontalAlignment(SwingConstants.CENTER);
			L_About.setFont(new Font("Lucida Calligraphy", Font.BOLD, 15));
			L_About.setBounds(20,20, 280,25);
			AboutWindow.getContentPane().add(L_About);
			
			AboutWindow.setVisible(true);
		}
	//--------------------------------------------------------------------------------------------------------------------------
		
		public static void Update_MainWindow(String Login_Name){
			
			Username = Login_Name;
			L_LoggedInAsBox.setText(Username);
			B_DISCONNECT.setEnabled(true);
			B_CONNECT.setEnabled(false);
			if(Login_Name==null){
				Main_Client = null;
			}else{
				B_ADD.setEnabled(true);
			}
		}
		
		
	//------------------------------------------------------------------------------------------------------------------
		
		public static void ConfigureMainWindow(){
			
			ImageIcon background = new ImageIcon(P2P_Chat_Client_MainWindow.class.getClassLoader().getResource("back3.jpg"));
			JLabel label = new JLabel(background);
			MainWindow.setContentPane(label);
			
			ImageIcon img = new ImageIcon(P2P_Chat_Client_MainWindow.class.getResource("chat_icon.png"));
			MainWindow.setIconImage(img.getImage());
			MainWindow.getContentPane().setLayout(null);
			B_DISCONNECT.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
			
			B_DISCONNECT.setBackground(new Color(51, 51, 51));
			B_DISCONNECT.setForeground(new Color(0, 153, 255));
			B_DISCONNECT.setText("DISCONNECT");
			MainWindow.getContentPane().add(B_DISCONNECT);
			B_DISCONNECT.setBounds(155,250,135,25);
			B_CONNECT.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
			
			B_CONNECT.setBackground(new Color(51, 51, 51));
			B_CONNECT.setForeground(new Color(0, 153, 255));
			B_CONNECT.setText("CONNECT");
			MainWindow.getContentPane().add(B_CONNECT);
			B_CONNECT.setBounds(25,250,120,25);
			
			B_About.setFont(new Font("Lucida Calligraphy", Font.BOLD, 14));
			B_About.setBackground(new Color(51, 51, 51));
			B_About.setForeground(new Color(0, 153, 255));
			MainWindow.getContentPane().add(B_About);
			B_About.setBounds(20,500,120,25);
			
			S_label = new JLabel("Server IP address");
			S_label.setForeground(new Color(0, 153, 255));
			S_label.setFont(new Font("Lucida Calligraphy", Font.BOLD, 16));
			S_label.setHorizontalAlignment(SwingConstants.CENTER);
			S_label.setBounds(30, 100, 180, 25);
			MainWindow.getContentPane().add(S_label);
			
			S_field = new JTextField();
			S_field.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 14));
			S_field.setForeground(new Color(102, 0, 0));
			S_field.setBounds(30, 130, 180, 30);
			S_field.setBorder(new LineBorder(new Color(0, 153, 255)));
			MainWindow.getContentPane().add(S_field);
			S_field.setColumns(10);
			
			L_ONLINE.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
			L_ONLINE.setHorizontalAlignment(SwingConstants.CENTER);
			L_ONLINE.setText("Currently Online");
			L_ONLINE.setToolTipText("");
			L_ONLINE.setBackground(new java.awt.Color(116,36,120));
			L_ONLINE.setForeground(new Color(0, 153, 255));
			MainWindow.getContentPane().add(L_ONLINE);
			L_ONLINE.setBounds(320,130,180,20);
			
			JL_ONLINE = new JList<String>();
			JL_ONLINE.setFont(new Font("Lucida Calligraphy", Font.BOLD, 14));
			JL_ONLINE.setBackground(new Color(51, 51, 51));
			JL_ONLINE.setEnabled(false);
			JL_ONLINE.setForeground(new Color(0, 153, 255));
			
			Jpopup_Menu = new JPopupMenu();
			Jpopup_Menu.add(J_Menu_1= new JMenuItem("Delete"));
			Jpopup_Menu.add(new JPopupMenu.Separator());
			Jpopup_Menu.add(J_Menu_2 = new JMenuItem("Block"));
			
			SP_ONLINE.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			SP_ONLINE.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			SP_ONLINE.setViewportView(JL_ONLINE);
			MainWindow.getContentPane().add(SP_ONLINE);
			SP_ONLINE.setBounds(320,160,200,320);
			SP_ONLINE.setBorder(new LineBorder(new Color(0, 153, 255), 2));
			L_LoggedInAs.setForeground(new Color(0, 153, 255));
			
			L_LoggedInAs.setHorizontalAlignment(SwingConstants.CENTER);
			L_LoggedInAs.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
			L_LoggedInAs.setText("Currently Logged In As");
			L_LoggedInAs.setBackground(new Color(116, 36, 120));
			MainWindow.getContentPane().add(L_LoggedInAs);
			L_LoggedInAs.setBounds(330,10,180,20);
			
			L_LoggedInAsBox.setHorizontalAlignment(SwingConstants.CENTER);
			L_LoggedInAsBox.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
			L_LoggedInAsBox.setForeground(new java.awt.Color(255,0,0));
			L_LoggedInAsBox.setBorder(new LineBorder(new Color(0, 153, 255), 2));
			MainWindow.getContentPane().add(L_LoggedInAsBox);
			L_LoggedInAsBox.setBounds(330,30,180,20);
			
			Add_friend.setHorizontalAlignment(SwingConstants.CENTER);
			Add_friend.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
			Add_friend.setForeground(new Color(0, 153, 255));
			MainWindow.getContentPane().add(Add_friend);
			Add_friend.setBounds(315,55,150,25);
			Add_friend.setText("Add Friends");
			
			Other_Clients.setForeground(new Color(0, 153, 255));
			Other_Clients.setBackground(new Color(51, 51, 51));
			Other_Clients.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
			Other_Clients.setEnabled(false);
			Other_Clients.setBounds(320,85,140,25);
			MainWindow.getContentPane().add(Other_Clients);
			
			B_ADD.setBackground(new Color(51, 51, 51));
			B_ADD.setForeground(new Color(0, 153, 255));
			B_ADD.setText("ADD");
			B_ADD.setEnabled(false);
			MainWindow.getContentPane().add(B_ADD);
			B_ADD.setBounds(470,85,65,25);
			
			L_Pin_Label.setBackground(new Color(51, 51, 51));
			L_Pin_Label.setBounds(420, 537, 100, 25);
			MainWindow.getContentPane().add(L_Pin_Label);
			L_Pin_Label.setForeground(new Color(0, 153, 255));
			L_Pin_Label.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
			L_Pin_Label.setHorizontalAlignment(SwingConstants.CENTER);
			L_Pin_Label.setBorder(new LineBorder(new Color(255, 0, 0), 2));
			L_Pin_Label.setEnabled(false);
			L_Pin_Label.setVisible(false);
			
		}

//-----------------------------------------------------------------------------------------------------------------------
		
		public static void ACTION_B_LOGIN(){
			
			if(!TF_UsernameBox.getText().equals("") && !TF_User_Password.getPassword().equals("")){
				
				String username = TF_UsernameBox.getText();
				char[] password = TF_User_Password.getPassword();
//				System.out.println( username);
				
				TF_UsernameBox.setText("");
				TF_User_Password.setText("");
				
				Connect(username,password);
			}
			else{
				JOptionPane.showMessageDialog(LogInWindow,"Please enter both username and password");
			}
			
		}
		
	//-----------------------------------------------------------------------------------------------------------------
		
		public static void ACTION_B_DISCONNECT(){
			
			try{
				Main_Client.DisconnectClient();
				B_DISCONNECT.setEnabled(false);
				B_CONNECT.setEnabled(true);
				S_field.setEnabled(true);
			}catch(Exception Y){
			}
		}
		
		
	//-----------------------------------------------------------------------------------------------------------------
	
		public static void SetActionListener(){
			
			B_CONNECT.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					Server_IP = S_field.getText();
					if(!Server_IP.equals(""))BuildLogInWindow();
					else JOptionPane.showMessageDialog(MainWindow,"Please Enter Server IP");
					
				}
			});
			
			B_DISCONNECT.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					ACTION_B_DISCONNECT();
				}
			});
			
			B_LogIn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					ACTION_B_LOGIN();
				}
			});
			
			B_About.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					Build_About_Window();
				}
			});
			
			MainWindow.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent ev) {
	            	
	            	if(Main_Client!=null){
		            	if(Main_Client.connection_flag){
		            		
		            		Main_Client.DisconnectClient();
		            		B_DISCONNECT.setEnabled(false);
							B_CONNECT.setEnabled(true);
		            	}
	            	}
	            	System.exit(0);
	            }
	        });
			
		}
		
	
		public static void SetJListAction(){
		
			JL_ONLINE.addMouseListener(new MouseAdapter() {
			    public void mouseClicked(MouseEvent evt) {
			    	
			     if(!Client_Handler.All_Friends.isEmpty()){
			    	 
			        if (evt.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(evt)) {
			            int index = JL_ONLINE.locationToIndex(evt.getPoint());
			            Client_Req_Index = index;
			            String name = (String)JL_Model.getElementAt(index);
			            if(!Client_Handler.Current_Chat.contains(name)){
				            if(Client_Handler.Online_Friends.contains(name)){
				            	try {
									Main_Client.Connection_Request_toServer(Client_Req_Index);
								} catch (IOException e) {
									e.printStackTrace();
								}
				            }else{
				            	JOptionPane.showMessageDialog(MainWindow, "Friend is Offline");
				            }
			            }
			            
			          else{
			        	  
			        	  for(int i=0;i<Client_Handler.Client_Obj_List.size();i++){
			        		  
			        		  if(Client_Handler.Client_Obj_List.get(i).Client_name.equals(name)){
			        			  if(Client_Handler.Client_Obj_List.get(i).Random_Disconnect){
			        				  Client_Handler.Current_Chat.remove(name);
			        				  Client_Handler.Client_Obj_List.remove(Client_Handler.Client_Obj_List.get(i));
			        				  JOptionPane.showMessageDialog(MainWindow, "Friend is Offline");
			        			  }else{
			        				  Client_Handler.Client_Obj_List.get(i).new_Client_GUI.setVisible(true);
				        			  Client_Handler.Client_Obj_List.get(i).setChat_Window_State(true);
			        			  }
			        			  break;
			        		  }
			        	  }
			          }
			        }
			        
			        if(SwingUtilities.isRightMouseButton(evt)){
			        	int index = JL_ONLINE.locationToIndex(evt.getPoint());
			        	Client_Del_Name = (String)JL_Model.getElementAt(index);
			        	Jpopup_Menu.show(JL_ONLINE,evt.getX(),evt.getY());
			        }
			      }
			    }
			});
			
			
			J_Menu_1.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent event){
					
					if(Client_Del_Name!=null){
						try {
							Main_Client.Send_Unfriend_Notification(Client_Del_Name);
							Client_Del_Name = null; 
						} catch (IOException e) {
						}
					}
				}
			});
			
			
			L_Pin_Label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					
					String name = L_Pin_Label.getText();
					int i=0;
					for(i=0;i<Client_Handler.Client_Obj_List.size();i++){
						
						if(Client_Handler.Client_Obj_List.get(i).Client_name.equals(name)){
							
							Client_Handler.Client_Obj_List.get(i).new_Client_GUI.setVisible(true);
							Client_Handler.Client_Obj_List.get(i).setChat_Window_State(true);
							L_Pin_Label.setText(null);
							L_Pin_Label.setEnabled(false);
							L_Pin_Label.setVisible(false);
							break;
						}
						if(i==Client_Handler.Client_Obj_List.size()){
							L_Pin_Label.setText(null);
							L_Pin_Label.setEnabled(false);
							L_Pin_Label.setVisible(false);
						}
					}
					
				}
			});
			
			B_ADD.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					
					String item = (String) Other_Clients.getSelectedItem();
					
					if(item!=null){
						
						try {
							Main_Client.Send_Friend_Request(item);
						} catch (IOException e) {
						}
					}
				}
			});
			
	}
		
		
}
