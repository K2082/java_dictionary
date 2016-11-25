import java.sql.*;

public class DataSave {
	private String userName= "root";
	private String password = "123456";
	private String driver = "com.mysql.jdbc.Driver";
	private String url="jdbc:mysql://localhost:3306/dict?useUnicode=true&characterEncoding=utf-8&useSSL=false";
	
	Connection conn;
	Statement sta;
	
	DataSave()
	{
	}
	void baiduClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM words WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("bgood");
            	temp++;
            	sql = "UPDATE words SET bgood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO words(word,bgood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	void jinshanClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM words WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("jgood");
            	temp++;
            	sql = "UPDATE words SET jgood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO words(word,jgood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	void youdaoClick(String word)
	{
		try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            sta = conn.createStatement();
            String sql = "SELECT * FROM words WHERE word='" + word + "';";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
            {
            	int temp = rs.getInt("ygood");
            	temp++;
            	sql = "UPDATE words SET ygood=" + temp + " WHERE word='" + word + "';";
            	sta.execute(sql);
            }
            else
            {
            	sql = "INSERT INTO words(word,ygood) VALUES('" + word + "',1);";
            	sta.execute(sql);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
