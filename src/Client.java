import java.net.*;
import java.io.*;
import java.util.*;


public class Client {
    //for I/O
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    //client GUI
    private ClientGUI cg;

    //the server, port and user name and password
    final static int port = 1500;
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
            socket = new Socket(server, port);
        }
        catch(Exception e) {
            cg.dialog("Connect Error!");
            return false;
        }
        
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            //System.out.println("123");
        }
        catch(IOException eIO) {
            dialog("Exception on creating Ouput stream");
            disconnect();
            return false;
        }

        boolean ret = false;
        try {
            sOutput.writeObject(new UserInfo(UserInfo.LOGIN, username, password));
            //sOutput.writeObject(password);
            sInput = new ObjectInputStream(socket.getInputStream());
            System.out.println("213");
            ret = (Boolean) sInput.readObject();           
        }
        catch(IOException eIO) {
            dialog("Exception happens when verifing!");
            disconnect();
            return false;
        }
        catch(ClassNotFoundException e) {
        	//nothing i can do
        }
        //verify
        if(!ret) {
            dialog("User name or password is error!");
            disconnect();
            return false;
        }

        new ListenFromServer().start();

        return true;
    }

    public boolean register() {
        try {
            socket = new Socket(server, port);
        }
        catch(Exception e) {
            cg.dialog("Connect Error!");
            return false;
        }

        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("123");
        }
        catch(IOException eIO) {
            dialog("Exception on creating Ouput stream");
            disconnect();
            return false;
        }

        boolean ret = false;
        try {
            sOutput.writeObject(new UserInfo(UserInfo.REGISTER, username, password));
            //sOutput.writeObject(password);
            sInput = new ObjectInputStream(socket.getInputStream());
            ret = (Boolean) sInput.readObject();           
        }
        catch(IOException eIO) {
            dialog("Exception happens when registering!");
            disconnect();
            return false;
        }
        catch(ClassNotFoundException e) {
        	//nothing i can do
        }
        //register
        if(!ret) {
            dialog("User name is existed!");
            disconnect();
            return false;
        }

        dialog("Register successfully!");
        disconnect();
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
            sOutput.writeObject(msg);
        }
        catch(IOException e){
            display("Exception on send message to server: " + e);
        }
    }

    /*
	 * To send a User Information to the server
	 */
	public void sendInfo(UserInfo ui) {
		try {
			sOutput.writeObject(ui);
		}
		catch(IOException e) {
			display("Exception on send userinfo to server: " + e);
		}
	}

    /*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	public void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do

		cg.connectionFailed();
			
    }
    
    class ListenFromServer extends Thread {
            

        public void run() {
            while(true) {
                try {
                    String msg = (String)sInput.readObject();
                    cg.append(msg);
                }
                catch(IOException eIO) {
                    display("Server has closed the connection " + eIO);
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