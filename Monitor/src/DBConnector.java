import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

public class DBConnector {
	String db_url = "jdbc:mysql://localhost:3306/monitor";
	String db_user = "root";
	String db_pwd = "hello";
	
	public DBConnector() throws ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
	}
	
	public Connection getConnection() throws SQLException{
		return (Connection) java.sql.DriverManager.getConnection(db_url, db_user, db_pwd);
	}
	
	public void db_close(Connection con, PreparedStatement prepStmt, ResultSet rs) throws Exception{
		if(rs != null) rs.close();
		if(prepStmt != null) prepStmt.close();
		if(con != null) con.close();
	}
	
	public String rsToJson(ResultSet rs) throws Exception{
		JSONArray array = new JSONArray();
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int colCount = metaData.getColumnCount(); //��ȡ����
		
		while(rs.next()){//����result Set�е�ÿ������
			JSONObject jsObj = new JSONObject();
			for(int i = 1; i <= colCount; ++i){//����ÿһ��
				String colName = metaData.getColumnLabel(i);
				String val = rs.getString(colName);
				jsObj.put(colName, val);
			}
			array.put(jsObj);
		}		
		//db_close(con, prepStmt, rs); //�ر�����
		return array.toString();
	}
	
	public String getPassword(String name) throws Exception{
		Connection con = getConnection();
		String sql = "select * from monitor_user where user_Name=?";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		prepStmt.setString(1, name);//��name���������һ����
		ResultSet rs = prepStmt.executeQuery(); //ִ��sql���
		String pwd = null;
		if(rs.next()) pwd = rs.getString(2); //��ȡ�ڶ����ֶΣ�������
		db_close(con, prepStmt, rs); //�ر�����
		return pwd;
	}
	
	public String getDevice() throws Exception{
		Connection con = getConnection();
		String sql = "select * from monitor_device";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery(); //ִ��sql���
		
		//ת��Ϊjson���
		JSONArray array = new JSONArray();
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int colCount = metaData.getColumnCount(); //��ȡ����
		
		while(rs.next()){//����result Set�е�ÿ������
			JSONObject jsObj = new JSONObject();
			for(int i = 1; i <= colCount; ++i){//����ÿһ��
				String colName = metaData.getColumnLabel(i);
				String val = rs.getString(colName);
				jsObj.put(colName, val);
			}
			array.put(jsObj);
		}
		
		db_close(con, prepStmt, rs); //�ر�����
		return array.toString();
	}
	
	public String register(String name, String pwd) throws Exception{
		Connection con = getConnection();
		String sql = "insert into monitor_user(user_Name,user_Password) values(?,?)";
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		prepStmt.setString(1, name);//��name���������һ����
		prepStmt.setString(2, pwd);//��pwd��������ڶ�����
		
		int rs = prepStmt.executeUpdate(); //ִ��sql���
		String rt = "fail";
		if(rs > 0) rt = "success";
		con.close(); //�ر�����
		prepStmt.close();
		//db_close(con, prepStmt, rs); //�ر�����
		return rt;
	}
	
	public String insertDevice(String deviceId, String deviceState, String state, String groupId) throws Exception{
		//System.out.println(deviceId +","+ deviceState +","+  state +","+  groupId);
		
		Connection con = getConnection();
		String sql = "select * from monitor_device where device_Id="+deviceId;
		PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(sql);
		ResultSet rs = prepStmt.executeQuery(); //ִ��sql���
		if(rs.next()){ //˵�����ڸ��豸������и��²���
			sql = "UPDATE monitor_device set device_State="+deviceState+", state="+state+" where device_Id="+deviceId;
			prepStmt = (PreparedStatement) con.prepareStatement(sql);
		}else{
			sql = "insert into monitor_device(device_Id,device_State,state ,group_Id) values(?,?,?,?)";
			prepStmt = (PreparedStatement) con.prepareStatement(sql);
			prepStmt.setString(1, deviceId);//��name���������һ����
			prepStmt.setString(2, deviceState);//��pwd���������һ����
			prepStmt.setString(3, state);//��pwd���������һ����
			prepStmt.setString(4, groupId);//��pwd���������һ����
		}
		
		String rt = "fail";
		if(prepStmt.executeUpdate() > 0) rt = "success"; //ִ�в���sql��� ����һ��int���͵�ֵ
		con.close(); //�ر�����
		prepStmt.close();
		//db_close(con, prepStmt, rs); //�ر�����
		return rt;
	}
}
