import java.io.*;

public class UserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1044175562781010790L;
	static final int LOGIN = 0, REGISTER = 1;
	private int type;
	private String name, password;

	//constructor
	public UserInfo(int type, String name, String password) {
		this.type = type;
		this.name = name;
		this.password = password;
	}

	//getters
	public int getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}
}