import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    //for I/O
    private ObjectInputStream sInput;
    private ObjectInputStream sOutput;
    private Socket socket;

    //client GUI
    private ClientGUI cg;

    //the server, port and user name and password
    final static int port = 2000;
    final static String server = "localhost";
    private String username, password;

    //constructor
    public Client(String username, String password, ClientGUI cg) {
        this.username = username;
        this.password = password;
        this.cg = cg;
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
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectInputStream(socket.getOutputStream());
        }
        catch(IOException eIO) {
            dialog("Exception on creating I/O stream");
            return false;
        }

        boolean ret;
        try {
            sOutput.writeObject(usename);
            sOutput.writeObject(password);
            ret = sInput.readObject();
            
        }
        catch(IOException eIO) {
            dialog("Exception happens when verifing!");
            return false;
        }
        //verify
        if(!ret) {
            dialog("user name or password is error!")
            return false;
        }

        new ListenFromServer.start();

        return true;
    }

    public boolean register() {
        
    }

    //send a messag to GUI
    private void display(String msg) {
        cg.append(msg + "\n");
    }

    private void dialog(String info) {
        cg.dialog(info);
    }
    
    /*
     * send a message to server
     */
    private void sendMessage(ChatMessage msg) {
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
	void sendInfo(UserInfo ui) {
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
	private void disconnect() {
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
                    display("Server has closed the connection " + e);
                    cg.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException e2) {
                    //nothing I can do
                }
            }
        }
    }
}