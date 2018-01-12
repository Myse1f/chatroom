import java.net.*;
import java.io.*;
import java.util.*;


public class Client {
    //for I/O
    private ObjectInputStream loginInput;
    private ObjectOutputStream loginOutput;
    private ObjectInputStream registerInput;
    private ObjectOutputStream registerOutput;
    private Socket loginSocket;
    private Socket registerSocket;
    public static final int LOGIN = 0, REGISTER = 1;

    //client GUI
    private ClientGUI cg;

    //the server, port and user name and password
    final static int loginPort = 1500;
    final static int registerPort = 2000;
    final static String server = "localhost";
    private String username="";
    private String password="";

    //constructor
    public Client(String username, String password, ClientGUI cg) {
        this.username = username;
        this.password = SHA1.getResult(password);
        this.cg = cg;
    }

    public String getUsername() {
        return username;
    }

    public boolean login() {
        try {
            loginSocket = new Socket(server, loginPort);
        }
        catch(Exception e) {
            cg.dialog("Connect Error!");
            return false;
        }
        
        try {
        	loginInput = new ObjectInputStream(loginSocket.getInputStream());
            loginOutput = new ObjectOutputStream(loginSocket.getOutputStream());            
            //System.out.println("123");
        }
        catch(IOException eIO) {
            dialog("Exception on creating login I/O stream");
            return false;
        }

        int ret = 0;
        try {
            loginOutput.writeObject(new UserInfo(UserInfo.LOGIN, username, password));
            ret = (Integer) loginInput.readObject();           
        }
        catch(IOException eIO) {
            dialog("Exception happens when verifing!");
            return false;
        }
        catch(ClassNotFoundException e) {
        	//nothing i can do
        }
        //verify
        if(ret == 0) {
            dialog("User name or password is error!");
            return false;
        }
        if(ret == -1) {
        	dialog("User has been in chat room!");
        	return false;
        }

        new ListenFromServer().start();

        return true;
    }

    public boolean register() {
        try {
            registerSocket = new Socket(server, registerPort);
        }
        catch(Exception e) {
            cg.dialog("Connect Error!");
            return false;
        }

        try {  
            registerInput = new ObjectInputStream(registerSocket.getInputStream());
            registerOutput = new ObjectOutputStream(registerSocket.getOutputStream());
            //System.out.println("123");
        }
        catch(IOException eIO) {
            dialog("Exception on creating register I/O stream");
            return false;
        }

        boolean ret = false;
        try {
            registerOutput.writeObject(new UserInfo(UserInfo.REGISTER, username, password));
            //sOutput.writeObject(password);
            
            ret = (Boolean) registerInput.readObject();           
        }
        catch(IOException eIO) {
            dialog("Exception happens when registering!");
            return false;
        }
        catch(ClassNotFoundException e) {
        	//nothing i can do
        }
        //register
        if(!ret) {
            dialog("User name is existed!");
            return false;
        }

        dialog("Register successfully!");
        return true;
    }

    //send a messag to GUI
    private void display(String msg) {
        cg.append(msg + "\n");
    }

    private void dialog(String info) {
        cg.dialog(info + '\n');
    }
    
    /*
     * send a message to server
     */
    public void sendMessage(ChatMessage msg) {
        try{
            loginOutput.writeObject(msg);
        }
        catch(IOException e){
            display("Exception on send message to server: " + e);
        }
    }

    /*
	 * To send a User Information to the server
	 */
	public void sendInfo(UserInfo ui, int mode) {
		try {
			if(mode == LOGIN)
				loginOutput.writeObject(ui);
			else if(mode == REGISTER)
				registerOutput.writeObject(ui);
		}
		catch(IOException e) {
			dialog("Exception on send userinfo to server: " + e);
		}
	}

    /*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	public void disconnect() {
		try { 
			if(loginInput != null) loginInput.close();
			if(registerInput != null) registerInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(loginOutput != null) loginOutput.close();
			if(registerOutput != null) registerOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(loginSocket != null) loginSocket.close();
			if(registerSocket != null) registerSocket.close();
		}
		catch(Exception e) {} // not much else I can do

		cg.connectionFailed();
			
    }
    
    class ListenFromServer extends Thread {
            
        public void run() {
            while(true) {
                try {
                    String msg = (String)loginInput.readObject();
                    cg.append(msg);
                }
                catch(IOException eIO) {
                    display("Server has closed the connection " + eIO);
                    disconnect();
                    cg.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException e) {
                    //nothing I can do
                }
            }
        }
    }
}