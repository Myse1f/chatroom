import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * Server GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener{
    
    private static final long serialVersionUID = 1L;
    //stop or start button
    private JButton stopStart;
    //Text Area for chat room and event
    private JTextArea chat, event;
    // The port number
	private JTextField tPortNumber;
    //server
    private Server server;
    
    //constructor 
    public ServerGUI(int port) {
        super("Chatroom Server");
        server = null;
        // in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
        tPortNumber = new JTextField("  " + port);
        tPortNumber.setEditable(false);
		north.add(tPortNumber);
		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(500, 600);
		setVisible(true);
    }

    // append message to the two JTextArea
	// position at the end
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
		
    }
    
    // start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			stopStart.setText("Start");
			server.display("Server stop!");
			server.stop();
			server = null;
			return;
		}

		// ceate a new Server
		server = new Server(this);
		// and start it as a thread
		server.start();
		stopStart.setText("Stop");
    }

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
	
	public static void main(String []args) {
		new ServerGUI(1500);
	}

}