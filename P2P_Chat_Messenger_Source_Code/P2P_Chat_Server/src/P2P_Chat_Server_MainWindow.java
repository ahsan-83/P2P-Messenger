import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class P2P_Chat_Server_MainWindow {
	
	public static JFrame Server_MainWindow = new JFrame();
	private static JButton SB_CONNECT = new JButton();
	private static JButton SB_DISCONNECT = new JButton();
	private static JLabel SL_label = new JLabel("Server Status");
	public static JTextArea ST_Area = new JTextArea();
	private static JScrollPane ST_Spane = new JScrollPane();
	public static JButton B_About = new JButton("About");
	
	private static JFrame AboutWindow = new JFrame();
	private static JLabel L_About = new JLabel("About P2P_Chat_Server");
	private static JTextArea T_About = new JTextArea();
	
	public void RunMainWindow(){
		
		BuildMainWindow();
		Initialize();
		Build_About_Window();
	}
	
	public static void BuildMainWindow(){
		
		Server_MainWindow.setTitle("P2P Chat Server");
		Server_MainWindow.setLocation(100,100);
		Server_MainWindow.setSize(550,590);
		Server_MainWindow.setResizable(false);
		ConfigureMainWindow();
		SetActionListener();
		Server_MainWindow.setVisible(true);
	}
	
	public static void Build_About_Window(){
		
		ImageIcon background = new ImageIcon(P2P_Chat_Server_MainWindow.class.getClassLoader().getResource("login2.jpg"));
		ImageIcon img = new ImageIcon(P2P_Chat_Server_MainWindow.class.getResource("chat_icon2.png"));
		
		AboutWindow.setIconImage(img.getImage());
		AboutWindow.setBounds(0, 0, 350, 500);
		AboutWindow.setLocationRelativeTo(Server_MainWindow);
		AboutWindow.setContentPane(new JLabel(background));
		AboutWindow.getContentPane().setLayout(null);
		AboutWindow.setTitle("About P2P_Chat_Server");
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
		
		AboutWindow.setVisible(false);
	}
	
	public static void Initialize(){
		SB_DISCONNECT.setEnabled(false);
		SB_CONNECT.setEnabled(true);
	}
	
	public static void Change_button(){
		SB_DISCONNECT.setEnabled(true);
		SB_CONNECT.setEnabled(false);
	}
	
	public static void Update_Text_Box(String msg){
		
		ST_Area.append("\n"+msg);
	}
	
	public static void ConfigureMainWindow(){
		
		ImageIcon background = new ImageIcon(P2P_Chat_Server_main.class.getClassLoader().getResource("back3.jpg"));
		JLabel label = new JLabel(background);
		Server_MainWindow.setContentPane(label);
		
		ImageIcon img = new ImageIcon(P2P_Chat_Server_main.class.getResource("chat_icon2.png"));
		Server_MainWindow.setIconImage(img.getImage());
		Server_MainWindow.getContentPane().setLayout(null);
		SB_DISCONNECT.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
		
		SB_DISCONNECT.setBackground(new Color(51, 51, 51));
		SB_DISCONNECT.setForeground(new Color(0, 153, 255));
		SB_DISCONNECT.setText("DISCONNECT");
		Server_MainWindow.getContentPane().add(SB_DISCONNECT);
		SB_DISCONNECT.setBounds(250,50,135,25);
		SB_CONNECT.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
		
		SB_CONNECT.setBackground(new Color(51, 51, 51));
		SB_CONNECT.setForeground(new Color(0, 153, 255));
		SB_CONNECT.setText("CONNECT");
		Server_MainWindow.getContentPane().add(SB_CONNECT);
		SB_CONNECT.setBounds(120,50,120,25);
		
		B_About.setFont(new Font("Lucida Calligraphy", Font.BOLD, 14));
		B_About.setBackground(new Color(51, 51, 51));
		B_About.setForeground(new Color(0, 153, 255));
		Server_MainWindow.getContentPane().add(B_About);
		B_About.setBounds(400,50,120,25);
		
		ST_Area.setForeground(new Color(102, 0, 153));
		ST_Area.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 13));
		ST_Area.setBackground(UIManager.getColor("ScrollBar.foreground"));
		ST_Area.setLineWrap(true);
		ST_Area.setEditable(false);
		ST_Area.setEnabled(true);
		
		ST_Spane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ST_Spane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ST_Spane.setViewportView(ST_Area);
		Server_MainWindow.getContentPane().add(ST_Spane);
		ST_Spane.setBounds(50, 130, 450, 400);
		ST_Spane.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 255), 2));
		
		SL_label.setForeground(new Color(0, 153, 255));
		SL_label.setFont(new Font("Lucida Calligraphy", Font.BOLD, 16));
		SL_label.setHorizontalAlignment(SwingConstants.CENTER);
		SL_label.setBounds(50, 100, 130, 24);
		Server_MainWindow.getContentPane().add(SL_label);
	}
	
	public static void SetActionListener(){
		
		SB_CONNECT.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
				
				try {
					 P2P_Chat_Server_Handler myserver = new P2P_Chat_Server_Handler();
					 Thread client_thread = new Thread(myserver);
					 client_thread.start();
					   
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
			}
		});
		
		SB_DISCONNECT.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				System.exit(0);
			}
		});
		
		B_About.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				AboutWindow.setVisible(true);
			}
		});
		
		Server_MainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	
            	System.exit(0);
            }
        });
		
		AboutWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	
            	AboutWindow.setVisible(false);
            }
        });
		
	}

}
