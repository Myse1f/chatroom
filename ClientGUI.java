import javax.swing.*;
import java.awt.*;
import java.awt.event;

/*
 * Client GUI
 */
public class ClientGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort, tfName, tfPassword;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;
	
    private LoginGUI lg = new LoginGUI();
    
    // Constructor connection receiving a socket number
	ClientGUI(String host, int port) {
        
        super("Chat Client");
        
        defaultPort = port;
        defaultHost = host;
        
        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3,1));
        // the server name anmd the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1,4, 1, 3));
        // the two JTextField with default value for server address and port number
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);


        serverAndPort.add(new JLabel("Server Address:  ", SwingConstants.CENTER));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  ", SwingConstants.CENTER));
        serverAndPort.add(tfPort);
        //serverAndPort.add(new JLabel(""));
        // adds the Server an port field to the GUI
        northPanel.add(serverAndPort);

        JPanel nameAndPassword = new JPanel(new GridLayout(1, 4, 1, 3));
        tfName = new JTextField();
        tfPassword = new JTextField();
        nameAndPassword.add(new JLabel("User Name:  ", SwingConstants.CENTER));
        nameAndPassword.add(tfName);
        nameAndPassword.add(new JLabel("Password:  ", SwingConstants.CENTER));
        nameAndPassword.add(tfPassword);
        
        northPanel.add(nameAndPassword);
        
        tf = new JTextField();
        tf.setBackground(Color.WHITE);
        northPanel.add(tf);
        add(northPanel, BorderLayout.NORTH);

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1,1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);

        // the 3 buttons
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);		// you have to login before being able to logout
        whoIsIn = new JButton("Who is in");
        whoIsIn.addActionListener(this);
        whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(logout);
        southPanel.add(whoIsIn);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);
        tf.requestFocus();
        
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
		private JButton Login = new JButton("Login");
		private JButton Register = new JButton("Register");
		
		public LoginGUI() {
			super("Login");
			JPanel picPanel = new JPanel();
			picPanel.add(pic);
			add(picPanel, BorderLayout.NORTH);
			
			JPanel contentPanel = new JPanel(new GridLayout(3, 2, 1, 3));
			contentPanel.add(user);
			contentPanel.add(tfUser);
			contentPanel.add(password);
			contentPanel.add(tfPassword);
			contentPanel.add(Login);
			Login.addActionListener(this);
			contentPanel.add(Register);
			Register.addActionListener(this);
			add(contentPanel, BorderLayout.SOUTH);
			
		
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setSize(420, 450);
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
			
			if(o == Login) {
				client.sendInfo(new UserInfo(UserInfo.LOGIN, tfUser.getText().trim(), tfPassword.getText()));
				return;
			}
			
			if(o == Register) {
				client.sendInfo(new UserInfo(UserInfo.REGISTER, tfUser.getText().trim(), tfPassword.getText()));
				return;
			}
		}
	}
}
