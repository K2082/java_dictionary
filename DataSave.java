import java.sql.*;

public class DataSave{
	private String userName= "sql9149763";
	private String password = "GgvKIMsKyP";
	private String driver = "com.mysql.jdbc.Driver";
	private String url="jdbc:mysql://sql9.freemysqlhosting.net/sql9149763?useUnicode=true&characterEncoding=utf-8&useSSL=false";
	private String uname = null;
	Connection conn;
	Statement sta;
	
	private static DataSave d;
	
	private DataSave()
	{

	}
	
	public static synchronized DataSave getInstance() {  
	    if (d == null) 
	        d = new DataSave();  
	    
	    return d;  
	}  
	
	
	/*
	 * check if user is logged in or not
	 */
	public boolean isLoggedIn()
	{
		if(uname == null)
			return false;
		else
			return true;
	}
	
	
	/*
	 * show all the word card for the current user
	 */
	public String[][] showcards()
	{
		String[][] card = null;
		
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "SELECT * FROM card WHERE rec='" + uname + "';";
            ResultSet rs = sta.executeQuery(sql);
            rs.last();
            int n = rs.getRow();
            rs.first();
            card = new String[2][n];
            for(int i=0; i < n; i++)
            {
            	
            	card[0][i] = rs.getString(2);
            	card[1][i] = rs.getString(3);
            	rs.next();
            	
            }
            conn.close();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return card;
	}
	
	/*
	 * send a card to a user
	 */
	public void sendCard(String reciever, String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "INSERT INTO card(word, send, rec) VALUES ('" + word + "', '" + uname + "', '" + reciever + "');";
            sta.execute(sql);
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * send a card to a group of user
	 */
	public void sendCard(String[] reciever, String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql;
            for(int i = 0; i < reciever.length; i++)
            {
	            sql = "INSERT INTO card(word, send, rec) VALUES ('" + word + "', '" + uname + "', '" + reciever[i] + "');";
	            sta.execute(sql);
            }
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * return all online users in a String array
	 */
	public String[] onlineuser()
	{
		String[] online = null;
		
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "SELECT username FROM user WHERE state='y';";
            ResultSet rs = sta.executeQuery(sql);
            rs.last();
            int n = rs.getRow();
            rs.first();
            online = new String[n];
            for(int i=0; i < n; i++)
            {
            	online[i] = rs.getString(1);
            	rs.next();
            }
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return online;
	}
	
	
	/*
	 * return all offline users in a String array
	 */
	public String[] offlineuser()
	{
		String[] offline = null;
		
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "SELECT username FROM user WHERE state='n';";
            ResultSet rs = sta.executeQuery(sql);
            rs.last();
            int n = rs.getRow();
            rs.first();
            offline = new String[n];
            for(int i=0; i < n; i++)
            {
            	offline[i] = rs.getString(1);
            	rs.next();
            }
            conn.close();;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return offline;
	}
	
	
	/*Register a new user
	 * return true when register success
	 * return false when user name already exists
	 */
	public boolean register(String username, String pw){
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "SELECT * FROM user WHERE username='" + username + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	conn.close();
            	return false;
            }
            else
            {
            	sql = "INSERT INTO user(username,password,state) VALUES('" + username + "', '" + pw + "','n');";
            	sta.execute(sql);
            	conn.close();
            	return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return false;
	}
	
	
	/*
	 * logout: change the user state to 'n'; which means not online
	 */
	public void logout()
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            if(uname != null)
            {
	            String sql =  "UPDATE user set state='n' WHERE username='" + uname + "';";
	            sta.execute(sql);
	            conn.close();
	            uname = null;
            }
            else
            {
            	return;
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * login
	 * when login success, return true and change state to online,
	 * if user name and password cannot be verified, return false
	 */
	public boolean login(String username, String pw){
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            
            String sql = "SELECT * FROM user WHERE username='" + username + "' and password='" + pw + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	sql = "UPDATE user set state='y' WHERE username='" + username + "' and password='" + pw + "';";
            	sta.execute(sql);
            	uname = username;
            	conn.close();
            	return true;
            }
            else
            {
            	conn.close();
            	return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return false;
	}
	
	
	/* 
	 * add one up for baidu
	 */
	public void baiduClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("bgood");
            	temp++;
            	sql = "UPDATE word SET bgood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO word(word,bgood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	/* 
	 * add one up for jinshan
	 */
	public void jinshanClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("jgood");
            	temp++;
            	sql = "UPDATE word SET jgood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO word(word,jgood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	/* 
	 * add one up for youdao
	 */
	public void youdaoClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("ygood");
            	temp++;
            	sql = "UPDATE word SET ygood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO word(word,ygood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * return the number of ygood for the word
	 */
	public int youdaoCount(String word)
	{
		int yc = 0;
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	yc = rs.getInt("ygood");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return yc;
	}
	
	/*
	 * return the number of bgood for the word
	 */
	public int baiduCount(String word)
	{
		int bc = 0;
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	bc = rs.getInt("bgood");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return bc;
	}
	

	/*
	 * return the number of jgood for the word
	 */
	public int jinshanCount(String word)
	{
		int jc = 0;
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM word WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	jc = rs.getInt("jgood");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return jc;
	}
}
