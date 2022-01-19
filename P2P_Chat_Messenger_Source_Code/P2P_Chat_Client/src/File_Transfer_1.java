import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class File_Transfer_1 implements Runnable{
	
	private Client_Chat_Handler new_Client_handler;
	private Client_Chat_GUI new_Client_GUI;
	public ServerSocket FS_SOCK = null;
	public Socket F_SOCK;
	public ObjectOutputStream f_out;
	public ObjectInputStream f_in;
	public boolean Connection_flag = false;
	private Object f_obj;
	private File sending_file;
	private String filename;
	private long filesize;
	
	public File_Transfer_1(String ip, int port , Object gui, Object chat_obj) throws IOException{
		
		F_SOCK = new Socket(ip,port);
		
		this.new_Client_GUI = (Client_Chat_GUI) gui;
		this.new_Client_handler = (Client_Chat_Handler)chat_obj;
		this.Connection_flag = true;
		set_button_Action();
	}
	
	
	public File_Transfer_1(int port , Object gui, Object chat_obj) throws IOException{
		
		FS_SOCK = new ServerSocket(port);
		F_SOCK = FS_SOCK.accept();
		
		this.new_Client_GUI = (Client_Chat_GUI) gui;
		this.new_Client_handler = (Client_Chat_Handler)chat_obj;
		this.Connection_flag = true;
		set_button_Action();
	}
	
	
	public void set_button_Action(){
		
		new_Client_GUI.lbl3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){	
				
				file_recv ff = new file_recv();
				ff.rec_OK = true;
				try {
					
					f_out.writeObject(ff);
					f_out.flush();
					
					new_Client_GUI.lbl1.setEnabled(false);
				    new_Client_GUI.lbl2.setEnabled(false);
				    new_Client_GUI.lbl3.setEnabled(false);
				    new_Client_GUI.lbl4.setEnabled(false);
				    
				    new_Client_GUI.lbl1.setVisible(false);
				    new_Client_GUI.lbl2.setVisible(false);
				    new_Client_GUI.lbl3.setVisible(false);
				    new_Client_GUI.lbl4.setVisible(false);
					
					new_Client_GUI.CPR_Bar.setVisible(true);
					new_Client_GUI.CB_FILE.setEnabled(false);
					
				} catch (IOException e) {
					return;
				}	
			}
		});
		
		
		
		new_Client_GUI.lbl4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){	
				
				file_recv ff = new file_recv();
				ff.rec_OK = false;
				try {
					f_out.writeObject(ff);
					f_out.flush();
					
					new_Client_GUI.lbl1.setEnabled(false);
				    new_Client_GUI.lbl2.setEnabled(false);
				    new_Client_GUI.lbl3.setEnabled(false);
				    new_Client_GUI.lbl4.setEnabled(false);
				    
				    new_Client_GUI.lbl1.setVisible(false);
				    new_Client_GUI.lbl2.setVisible(false);
				    new_Client_GUI.lbl3.setVisible(false);
				    new_Client_GUI.lbl4.setVisible(false);
					
				} catch (IOException e) {
					return;
				}	
			}
		});
	}
	
	
	public void setStreams() throws IOException{
		
		f_out = new ObjectOutputStream(F_SOCK.getOutputStream());
		f_out.flush();
		
		f_in = new ObjectInputStream(F_SOCK.getInputStream());	
	}
	

	public void run(){
		
		try {
			setStreams();
			
			getInput();
			
		} catch (ClassNotFoundException | IOException e) {
			Close_All_Components();
		}finally{
			
			try {
				f_in.close();
				f_out.close();	
			}catch(IOException e){
				Close_All_Components();
			}try{
				F_SOCK.close();
				
				if(FS_SOCK!=null){
					FS_SOCK.close();
				}
				
				Close_All_Components();
				System.err.println("file scoket connection closed");
			}catch (IOException e) {
				Close_All_Components();
				  return;
			}	
		}
	}
	
	public synchronized void File_disconnect(){
		
		File_Disconnect f_dis = new File_Disconnect();
		f_dis.setDis_flag();
		
		try {
			f_out.writeObject(f_dis);
			f_out.flush();
		}
		catch (IOException e) {
			return;
		}
	}
	
	
	public void getInput() throws ClassNotFoundException, IOException{
		
		while(Connection_flag){
			try{
				f_obj = f_in.readObject();
			}
			catch (EOFException eofException) {
				return;
			}catch(IOException ioexception){
				Close_All_Components();
				return;
			}
			
			if(f_obj instanceof file_promp){
				
				TransferConfirmation(f_obj);
			}
			
			else if(f_obj instanceof file_recv){
				
				File_Send(f_obj);	
			}
			
			else if(f_obj instanceof File_Send){
					
				File_Send send = (File_Send)f_obj;
				if(send.Send_File){
				File_Receive();
				}else{
					filesize = 0;
					filename = null;
				}		
			}
			
			else if(f_obj instanceof Tansfer_Complete){
				
				//Just Confirmation for FileTransfer
			}
			else if(f_obj instanceof File_Disconnect){
				
				Close_All_Components();
				break;
			}			
		}
	}
	
	public void Send_File_Notification(File file) throws IOException{
			
		this.sending_file = file;
		
		file_promp pp = new file_promp();
		pp.filename = sending_file.getName();
		pp.filesize = sending_file.length();

		f_out.writeObject(pp);
		f_out.flush();
	
	}
	
	public void Close_All_Components(){
		
		if(P2P_Chat_Client_MainWindow.L_Pin_Label.isEnabled()){
			
			if(new_Client_handler.Client_name.equals(P2P_Chat_Client_MainWindow.L_Pin_Label.getText())){
				
				P2P_Chat_Client_MainWindow.L_Pin_Label.setText(null);
				P2P_Chat_Client_MainWindow.L_Pin_Label.setEnabled(false);
				P2P_Chat_Client_MainWindow.L_Pin_Label.setVisible(false);
			}
		}

		Connection_flag = false;
		new_Client_GUI.CPR_Bar.setVisible(false);
		new_Client_GUI.CB_FILE.setEnabled(false);
		
		new_Client_GUI.lbl1.setEnabled(false);
	    new_Client_GUI.lbl2.setEnabled(false);
	    new_Client_GUI.lbl3.setEnabled(false);
	    new_Client_GUI.lbl4.setEnabled(false);
	    
	    new_Client_GUI.lbl1.setVisible(false);
	    new_Client_GUI.lbl2.setVisible(false);
	    new_Client_GUI.lbl3.setVisible(false);
	    new_Client_GUI.lbl4.setVisible(false);
	}
	
	private void TransferConfirmation(Object O) throws IOException{
		
		if(new_Client_handler.Chat_Window_State==false){
	
			new_Client_handler.Play_Notification_Sound();
				
				if(new_Client_handler.Knock_State==1){
					P2P_Chat_Client_MainWindow.L_Pin_Label.setText(new_Client_handler.Client_name);
					P2P_Chat_Client_MainWindow.L_Pin_Label.setEnabled(true);
					P2P_Chat_Client_MainWindow.L_Pin_Label.setVisible(true);
				}
				
				new_Client_handler.Knock_State++;
			
		}
		file_promp pp = (file_promp)O;
		filename = pp.filename;
        filesize = pp.filesize;
	    
        
        if((filesize/(1024*1024))>=1024){
	    	
	    	double size = (double)filesize/(1024*1024*1024);
	    	String for_size = String.format("%.3f", size);
	    	new_Client_GUI.lbl2.setText("File Size : " + for_size + " GB");
	    }
        
        else if ((filesize/1024)>=1024 && (filesize/(1024*1024))<1024){
	    	
	    	double size = (double)filesize/(1024*1024);
	    	String for_size = String.format("%.3f", size);
	    	new_Client_GUI.lbl2.setText("File Size : " + for_size + " MB");
	    }
        
        else if((filesize/1024)<1024){
	    	
	    	double size = (double)filesize/1024;
	    	String for_size = String.format("%.3f", size);
	    	new_Client_GUI.lbl2.setText("File Size : " + for_size + " KB");
	    }
	     
	    new_Client_GUI.lbl1.setEnabled(true);
	    new_Client_GUI.lbl2.setEnabled(true);
	    new_Client_GUI.lbl3.setEnabled(true);
	    new_Client_GUI.lbl4.setEnabled(true);
	    
	    new_Client_GUI.lbl1.setVisible(true);
	    new_Client_GUI.lbl2.setVisible(true);
	    new_Client_GUI.lbl3.setVisible(true);
	    new_Client_GUI.lbl4.setVisible(true);
	    
	    new_Client_GUI.lbl1.setText("File : " + filename);  	    
	}
	
	
	private void File_Send(Object O) throws IOException{
		
		if(Connection_flag){
			
			file_recv ob = (file_recv)O;
			
			if(ob.rec_OK){
				
				File_Send f_send = new File_Send();
				f_send.Send_File = true;
				f_out.writeObject(f_send);
				f_out.flush();
				
				new_Client_GUI.CPR_Bar.setVisible(true);
				new_Client_GUI.CB_FILE.setEnabled(false);
				FileInputStream file_reader = new FileInputStream(sending_file);
				
				byte[] buffer = new byte[F_SOCK.getSendBufferSize()];
				
                int bytesRead = 0;
                float count=0;
                float totalsize = (float)sending_file.length();
                
                while((bytesRead = file_reader.read(buffer))>0 && Connection_flag)
                {
                    count=count+bytesRead;
                    int percent=(int)(count/totalsize*100);
                    f_out.write(buffer,0,bytesRead);
                    new_Client_GUI.CPR_Bar.setValue(percent);
                }
                
                if(Connection_flag){
        			
        			if(count==totalsize){
        				
        				if(new_Client_handler.Chat_Window_State==false){
        					
        					new_Client_handler.Play_Notification_Sound();
        						
        						if(new_Client_handler.Knock_State==1){
        							P2P_Chat_Client_MainWindow.L_Pin_Label.setText(new_Client_handler.Client_name);
        							P2P_Chat_Client_MainWindow.L_Pin_Label.setEnabled(true);
        							P2P_Chat_Client_MainWindow.L_Pin_Label.setVisible(true);
        						}
        						
        						new_Client_handler.Knock_State++;
        					
        				}
        				
        				Tansfer_Complete T_comp = new Tansfer_Complete();
        				T_comp.complete = true;
        				f_out.writeObject(T_comp);
        				f_out.flush();
        				
        				JOptionPane.showMessageDialog(new_Client_GUI,"Transfer Completed");
        				new_Client_GUI.CPR_Bar.setVisible(false);
        				new_Client_GUI.CB_FILE.setEnabled(true);
        				new_Client_GUI.CPR_Bar.setValue(0);
        				file_reader.close();
        				
        				
        			}
        			
        		}else{
        			
        			//JOptionPane.showMessageDialog(null, "Connection Error!!!");
        			new_Client_GUI.CPR_Bar.setVisible(false);
        			new_Client_GUI.CB_FILE.setEnabled(false);
        			file_reader.close();
        			return;		
        		}                	  
				  
			}	
			else {
				sending_file = null;
				return;
			}
		}
	}
	
	
	private void File_Receive() throws IOException{
		
		File f = new File(System.getProperty("java.class.path"));
		File dir = f.getAbsoluteFile().getParentFile();
		String path = dir.toString();
		/*System.out.println(path);
		String path_new = "";
		for(int i=0;i<path.length();i++){
			if(path.charAt(i)==';')break;
			path_new+=path.charAt(i);
		}
		path="";
		for(int i=0;i<path_new.length()-4;i++){
			path+=path_new.charAt(i);
		}
		System.out.println(path);*/
		FileOutputStream file_write = new FileOutputStream(new File("src/Downloads/"+filename));
        byte[] buffer = new byte[F_SOCK.getReceiveBufferSize()];
        int bytesReceived = 0;
        float count=0;
        float totalsize = (float)filesize;
		
        while(((bytesReceived = f_in.read(buffer))>0) && Connection_flag)
        {
        	file_write.write(buffer,0,bytesReceived);
            count=count+bytesReceived;
            int percent=(int)(count/totalsize*100);
            new_Client_GUI.CPR_Bar.setValue(percent);
        }
        
		if(Connection_flag){
			
			if(count==totalsize){
				
				if(new_Client_handler.Chat_Window_State==false){
					
					new_Client_handler.Play_Notification_Sound();
						
						if(new_Client_handler.Knock_State==1){
							P2P_Chat_Client_MainWindow.L_Pin_Label.setText(new_Client_handler.Client_name);
							P2P_Chat_Client_MainWindow.L_Pin_Label.setEnabled(true);
							P2P_Chat_Client_MainWindow.L_Pin_Label.setVisible(true);
						}
						
						new_Client_handler.Knock_State++;
					
				}
				JOptionPane.showMessageDialog(new_Client_GUI,"Transfer Completed");
				new_Client_GUI.CPR_Bar.setVisible(false);
				new_Client_GUI.CPR_Bar.setValue(0);
				new_Client_GUI.CB_FILE.setEnabled(true);
				file_write.close();
			}
			
		}else{
			
			//JOptionPane.showMessageDialog(null, "Connection Error!!!");
			new_Client_GUI.CPR_Bar.setVisible(false);
			new_Client_GUI.CPR_Bar.setValue(0);
			new_Client_GUI.CB_FILE.setEnabled(false);
			file_write.close();
			return;		
		}	
	}

	
}

