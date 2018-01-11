import javax.naming.event.ObjectChangeListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * Client GUI
 */
public class ClientGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold on the messages
	private JTextArea tm;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton send, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// the Client object
	private Client client;
	
    private LoginGUI lg;
    
    // Constructor connection receiving a socket number
	ClientGUI() {
        
        super("Chat Client");
        lg = new LoginGUI();

        int port = Client.port;
        String host = Client.server;
        
        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(2,1));
        // the server name anmd the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1,4, 1, 3));
        // the two JTextField with default value for server address and port number
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfServer.setEditable(false);
        tfPort.setEditable(false);

        serverAndPort.add(new JLabel("Server Address:  ", SwingConstants.CENTER));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  ", SwingConstants.CENTER));
        serverAndPort.add(tfPort);
        //serverAndPort.add(new JLabel(""));
        // adds the Server an port field to the GUI
        northPanel.add(serverAndPort);

        //name label
        label = new JLabel("Your Name: " + client.getUsername(), SwingConstants.CENTER);
        northPanel.add(label);
        add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2,1));
        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room!\n", 60, 80);
        ta.setLineWrap(true);
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        tm = new JTextArea(20, 80);
        tm.setLineWrap(true);
        tm.setEditable(true);
        tm.addActionListener(this);
        centerPanel.add(new JScrollPane(tm));
        add(centerPanel, BorderLayout.CENTER);

        // the logout, send and whoIsIn buttons
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(true);		// you have to login before being able to logout
        whoIsIn = new JButton("Who is in");
        whoIsIn.addActionListener(this);
        whoIsIn.setEnabled(true);		// you have to login before being able to Who is in
        send = new JButton("Send");
        send.addActionListener(this);
        send.setEnabled(true);

        JPanel southPanel = new JPanel();
        southPanel.add(logout);
        southPanel.add(whoIsIn);
        southPanel.add(send);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(false);        
    }

    // called by the Client to append text in the TextArea 
    void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
    }

    // called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
        lg.setVisible(true);
        this.setVisible(false);
	}

    public void dialog(String info) {
        //String infoLF = info + '\n';
        JOptionPane.showMessageDialog(this, info, "Warning!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        //logout
        if(o == logout) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            connectionFailed();
            return;
        }
    }

    class LoginGUI extends JFrame implements ActionListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImageIcon bg = new ImageIcon("./chatroom.jpg");
		private JLabel pic = new JLabel(bg);
		private JLabel user = new JLabel("User Name:  ", SwingConstants.CENTER);
		private JLabel password = new JLabel("Password:  ", SwingConstants.CENTER);
		private JTextField tfUser = new JTextField(0);
		private JTextField tfPassword = new JTextField(15);	
		private JButton login = new JButton("Login");
		private JButton register = new JButton("Register");
		
		public LoginGUI() {
			super("Login Client");
			JPanel picPanel = new JPanel();
			picPanel.add(pic);
			add(picPanel, BorderLayout.NORTH);
			
			JPanel contentPanel = new JPanel(new GridLayout(3, 2, 1, 3));
			contentPanel.add(user);
			contentPanel.add(tfUser);
			contentPanel.add(password);
			contentPanel.add(tfPassword);
			contentPanel.add(login);
			login.addActionListener(this);
			contentPanel.add(register);
			Register.addActionListener(this);
			add(contentPanel, BorderLayout.SOUTH);
			
		
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setSize(420, 450);
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
            String username = tfUser.getText().trim();
            String password = tfPassword.getText();
            if(username.length() == 0) {
                dialog("Username should not be empty!");
                return;
            }
            if(password.length() == 0) {
                dialog("Password should not be empty!");
                return;
            }
			if(o == login) {
                client = new Client(username, password, ClientGUI.this);
                if(client.login()) {
                    this.setVisible(false); 
                    ClientGUI.this.setVisible(true);
                    ta.setText("Welcome to the Chat room!\n");
                    tm.setText("");
                    tm.requestFocus();
                }
                
                tfUser.setText("");
                tfPassword.setText("");
                
                return;
			}
			
			if(o == register) {
                client = new Client(username, password, ClientGUI.this);
                if(client.register()) {
                    tfPassword.setText("");
                }
                else {
                    tfUser.setText("");
                    tfPassword.setText("");
                }
                return;
			}
		}
	}
}
