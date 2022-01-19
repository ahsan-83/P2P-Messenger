import java.awt.Color;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import java.awt.Font;
import java.io.File;

import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class Client_Chat_GUI extends JFrame {
	
	public JButton CB_SEND ;
	public JButton CB_FILE ;
	private JLabel CL_Message;
	private JLabel CL_Conversation;
	public JLabel lbl1;
	public JLabel lbl2;
	public JButton lbl3;
	public JButton lbl4;
	private JScrollPane CSP_Conversation;
	public JTextArea CTF_Message;
	public JTextArea CTA_Conversation ;
	public JProgressBar CPR_Bar;
	public File Selected_File;
	public File File_Directory;
	
	public Client_Chat_GUI(String Client_Name){
		
		super("Chat with " + Client_Name );
		ConfigureWindow();
	}
	
	public void ConfigureWindow(){
		
		ImageIcon img = new ImageIcon(getClass().getResource("chat_icon.png"));
		ImageIcon background = new ImageIcon(getClass().getClassLoader().getResource("back1.jpg"));
		
		this.setIconImage(img.getImage());
		this.setBounds(0, 0, 600, 650);
		this.setLocationRelativeTo(P2P_Chat_Client_MainWindow.MainWindow);
		this.setContentPane(new JLabel(background));
		this.getContentPane().setLayout(null);
		this.setResizable(false);
		
		CTA_Conversation = new JTextArea();
		CTA_Conversation.setForeground(new Color(102, 0, 153));
		CTA_Conversation.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 13));
		CTA_Conversation.setBackground(UIManager.getColor("ScrollBar.foreground"));
		CTA_Conversation.setLineWrap(true);
		CTA_Conversation.setEditable(false);
		
		CL_Conversation = new JLabel("Conversation");
		CL_Conversation.setForeground(new Color(0, 153, 255));
		CL_Conversation.setFont(new Font("Lucida Calligraphy", Font.BOLD, 16));
		CL_Conversation.setHorizontalAlignment(SwingConstants.LEFT);
		CL_Conversation.setBounds(20, 11, 150, 30);
		this.getContentPane().add(CL_Conversation);
		
		CTF_Message = new JTextArea();
		CTF_Message.setForeground(new Color(0, 0, 0));
		CTF_Message.setFont(new Font("Lucida Console", Font.BOLD | Font.ITALIC, 13));
		CTF_Message.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 255), 2));
		CTF_Message.setLineWrap(true);
		CTF_Message.setEditable(true);
		CTF_Message.setBounds(21, 482, 410, 108);
		this.getContentPane().add(CTF_Message);
		
		CB_SEND = new JButton("SEND");
		CB_SEND.setBackground(new Color(51, 51, 51));
		CB_SEND.setFont(new Font("Lucida Calligraphy", Font.BOLD, 15));
		CB_SEND.setForeground(new Color(0, 153, 255));
		CB_SEND.setBounds(455, 514, 100, 40);
		this.getContentPane().add(CB_SEND);
		
		CB_FILE = new JButton("TRANSFER FILE ");
		CB_FILE.setBackground(new Color(51, 51, 51));
		CB_FILE.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
		CB_FILE.setForeground(new Color(0, 153, 255));
		CB_FILE.setBounds(20, 415, 180, 30);
		this.getContentPane().add(CB_FILE);
		
		CL_Message = new JLabel("MESSAGE");
		CL_Message.setForeground(new Color(0, 153, 255));
		CL_Message.setFont(new Font("Lucida Calligraphy", Font.BOLD, 15));
		CL_Message.setHorizontalAlignment(SwingConstants.CENTER);
		CL_Message.setBounds(20, 457, 120, 24);
		this.getContentPane().add(CL_Message);
		
		CPR_Bar = new JProgressBar();
		CPR_Bar.setFont(new Font("Lucida Calligraphy", Font.BOLD, 12));
		CPR_Bar.setBackground(new Color(51, 51, 51));
		CPR_Bar.setForeground(new Color(0, 153, 255));
		CPR_Bar.setBounds(300, 415, 200, 30);
		CPR_Bar.setStringPainted(true);
		CPR_Bar.setVisible(false);
		this.getContentPane().add(CPR_Bar);
		
		CSP_Conversation = new JScrollPane();
		CSP_Conversation.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		CSP_Conversation.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		CSP_Conversation.setViewportView(CTA_Conversation);
		this.getContentPane().add(CSP_Conversation);
		CSP_Conversation.setBounds(20, 41, 542, 350);
		CSP_Conversation.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 255), 2));	
		
		lbl1 = new JLabel();
		lbl1.setVerticalAlignment(SwingConstants.TOP);
		lbl1.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
		lbl1.setForeground(new Color(51, 204, 255));
		lbl1.setHorizontalAlignment(SwingConstants.LEFT);
		lbl1.setBounds(220, 405, 370, 20);
		this.getContentPane().add(lbl1);
		lbl1.setEnabled(false);
		lbl1.setVisible(false);
		
		lbl2 = new JLabel();
		lbl2.setVerticalAlignment(SwingConstants.TOP);
		lbl2.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
		lbl2.setForeground(new Color(51, 204, 255));
		lbl2.setHorizontalAlignment(SwingConstants.LEFT);
		lbl2.setBounds(220, 435, 370, 20);
		this.getContentPane().add(lbl2);
		lbl2.setEnabled(false);
		lbl2.setVisible(false);
		
		
		lbl3 = new JButton("<HTML><u>Accept</u></HTML>");
		lbl3.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
		lbl3.setForeground(new Color(51, 204, 255));
		lbl3.setBackground(new Color(51, 51, 51));
		lbl3.setBounds(220, 460, 90, 20);
		this.getContentPane().add(lbl3);
		lbl3.setEnabled(false);
		lbl3.setVisible(false);
		
		
		lbl4 = new JButton("<HTML><u>Deny</u></HTML>");
		lbl4.setFont(new Font("Lucida Calligraphy", Font.BOLD, 13));
		lbl4.setForeground(new Color(51, 204, 255));
		lbl4.setBackground(new Color(51, 51, 51));
		lbl4.setBounds(320, 460, 90, 20);
		this.getContentPane().add(lbl4);
		lbl4.setEnabled(false);
		lbl4.setVisible(false);
		
		this.setVisible(false);
		
	}
	
	public void Open_File(){
		
		JFileChooser fileChooser = new JFileChooser();

	      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY );
	      int result = fileChooser.showOpenDialog( this );
	      
	      if ( result == JFileChooser.CANCEL_OPTION )
	    	  Selected_File = null;
	      else
	    	  Selected_File = fileChooser.getSelectedFile();    	  
	
	}

}
