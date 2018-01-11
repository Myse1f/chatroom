import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    // a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	public static final int port = 1500;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;

    public Server(ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
	}

    public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				sg.appendEvent("accept!");
				// if I was asked to stop
				if(!keepGoing)
					break;

				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				sg.appendEvent("accept!");
				UserInfo ui = (UserInfo) ois.readObject();

                switch(ui.getType()) {
                    case UserInfo.REGISTER:
                        RegisterThread t1 = new RegisterThread(socket, ui);
                        t1.start();
                        break;
                    case UserInfo.LOGIN:
                        ClientThread t2 = new ClientThread(socket, ui);  // make a thread of it
                        al.add(t2);									// save it in the ArrayList
                        t2.start();
                        break;
                }
                
                ois.close();

			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
		catch(ClassNotFoundException e) {
			//nothing I can really do
		}
	}

    protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can do
		}
	}

    private void display(String msg) {
		String log = sdf.format(new Date()) + " " + msg;
		sg.appendEvent(log + "\n");
	}

    private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		sg.appendRoom(messageLf);     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.ui.getName() + " removed from list.");
			}
		}
	}

    // for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}

    class ClientThread extends Thread {
        // the socket where to listen/talk
		Socket socket;
		DBControl db;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the UserInfo of the Client
		UserInfo ui;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;    	
        
        // Constructor
		ClientThread(Socket socket, UserInfo ui) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
            this.ui = ui;
			db = new DBControl();
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username and password
				display(ui.getName() + " is trying to connect.");
			}
			catch (IOException e) {
				display("Exception creating new I/O Streams: " + e);
				return;
			}

            date = new Date().toString() + "\n";
		}

        private boolean verify() {
            String tmp = db.getPassword(ui.getName());
            if(tmp.equals("-1")) {
                //the user is not existed
				display("The username " + ui.getName() + " is not existed!");
				
                return false;
            }
            else if(!tmp.equals(ui.getPassword())) {
                display(ui.getName() + ": password is incorrect!");
                return false;
            }

            display(ui.getName() + " connected!");
            return true;
        }

        public void run() {
            //verify the username and password\
            if(!verify()) {
                try {
                    sOutput.writeObject(new Boolean(false));
                }
                catch(IOException ioE) {
                    display("Error sending result to " + ui.getName());
                }
                return;
            }
            try {
                sOutput.writeObject(new Boolean(true));
            }
            catch(IOException ioE) {
                display("Error sending result to " + ui.getName());
            }           
            
            //to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(ui.getName() + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}

                // Switch on the type of message receive
				switch(cm.getType()) {

                    case ChatMessage.MESSAGE:
                    	// the messaage part of the ChatMessage
				        String message = cm.getMessage();
                        broadcast(ui.getName() + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        display(ui.getName() + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
                        writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                        // scan al the users connected
                        for(int i = 0; i < al.size(); ++i) {
                            ClientThread ct = al.get(i);
                            writeMsg((i+1) + ") " + ct.ui.getName() + " since " + ct.date);
                        }
					    break;
				}
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        // try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
			db.DBClose();
		}

        /*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + ui.getName());
				display(e.toString());
			}
			return true;
		}
    }
	
	class RegisterThread extends Thread {

		// the socket where to listen/talk
		Socket socket;
		DBControl db;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// the UserInfo of the Client
		UserInfo ui;
		//date

		RegisterThread(Socket socket, UserInfo ui) {
			this.socket = socket;
            this.ui = ui;
			db = new DBControl();
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username and password
				display(ui.getName() + " is trying to connect.");
			}
			catch (IOException e) {
				display("Exception creating new I/O Streams: " + e);
				return;
			}

		}

		public void run() {
			if(!db.addUser(ui.getName(), ui.getPassword())) {
				//add user failed
				try {
					sOutput.writeObject(new Boolean(false));
				}
				catch(IOException ioE) {
					display("Error sending result to " + ui.getName());
				}
			}
			else {
				try {
					sOutput.writeObject(new Boolean(true));
				}
				catch(IOException ioE) {
					display("Error sending result to " + ui.getName());
				}
			}
			close();
		}

		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
			db.DBClose();
		}
	}

}