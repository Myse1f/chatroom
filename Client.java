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

    //send a messag to GUI
    private void display(String msg) {
        cg.append(msg + "\n");
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
}