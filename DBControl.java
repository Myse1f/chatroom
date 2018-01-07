import java.sql.*;

public class DBControl {
	private Connection con;
	private PreparedStatement sqlQuery;
	private PreparedStatement sqlInsert;
	private ResultSet res;
	
	public DBControl() {
		getConnection();
		getStatement();
	}
	
	public void DBClose() {
		try {
			con.close();
			System.out.println("DB CLOSED!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void getConnection() {
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("DRIVER LOAD SUCCESSFULLY");
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		try{
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "");
			System.out.println("CONNECT SUCCESSFULLY!");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getStatement() {
		try{
			sqlQuery = con.prepareStatement("SELECT password FROM user WHERE name = ?");
			sqlInsert = con.prepareStatement("Insert INTO user values(?, ?)");
			System.out.println("STATEMENT CREATE SUCCESSFULLY!");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPassword(String name) {
		String pswd = "-1";
		try {
			sqlQuery.setString(1, name);
			res = sqlQuery.executeQuery();
			if(res.next())
				pswd = res.getString(1);
			else
				System.out.println("User name unexists!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pswd;
	}
	
	public boolean addUser(String name, String pswd) {
		try {
			sqlInsert.setString(1, name);
			sqlInsert.setString(2, pswd);
			//insert data into table
			sqlInsert.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			//primary key duplicated, return false
			System.out.println(e.getMessage());
			
			return false;
		}
	}
	
	public static void main(String [] args) {
		DBControl db = new DBControl();
		db.addUser("Mike", "123");
		//System.out.println(db.getPassword("Mike"));
		db.DBClose();
	}
}
