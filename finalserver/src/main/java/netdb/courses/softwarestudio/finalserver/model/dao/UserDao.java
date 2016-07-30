package netdb.courses.softwarestudio.finalserver.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import netdb.courses.softwarestudio.finalserver.model.domain.Friend;
import netdb.courses.softwarestudio.finalserver.model.domain.User;
import netdb.courses.softwarestudio.finalserver.service.MysqlService;

public class UserDao {

	public static void addUser(User u)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			if(userExist(u.getId())){
				sql = "Update users set name=?,gender=?,email=?,birthday=? WHERE id=?";
				stmt = MysqlService.createPreparedStatement(conn, sql);
				stmt.setString(1, u.getName());
				stmt.setString(2, u.getGender());
				stmt.setString(3, u.getEmail());
				stmt.setString(4, u.getBirthday());
				stmt.setString(5, u.getId());
				MysqlService.executeUpdate(stmt);
				
				sql="Delete from friendship WHERE user_id=?";
				stmt = MysqlService.createPreparedStatement(conn, sql);
				stmt.setString(1, u.getId());
				MysqlService.executeUpdate(stmt);
			}else{
				sql = "Insert into users (id,name,gender,email,birthday) Values (?,?,?,?,?)";
				stmt = MysqlService.createPreparedStatement(conn, sql);
				stmt.setString(1, u.getId());
				stmt.setString(2, u.getName());
				stmt.setString(3, u.getGender());
				stmt.setString(4, u.getEmail());
				stmt.setString(5, u.getBirthday());
				MysqlService.executeUpdate(stmt);
			}
			conn.commit();
			addFriends(u.getId(),u.getFriends());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static User getUser(String id)
	{
		Connection conn = MysqlService.connect();
		try {
			String sql = "SELECT *  FROM users WHERE id = ?  ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setString(1, id);
			ResultSet rs = MysqlService.executeQuery(stmt);
			if (rs.next()) {
				User user = createUser(rs);
				System.out.println(user.getName());
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}

		return null;
	}
	
	private static User createUser(ResultSet rs) throws SQLException
	{
		User user=new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setGender(rs.getString("gender"));
		user.setEmail(rs.getString("email"));
		user.setBirthday(rs.getString("birthday"));
		user.setFriends(getFriends(rs.getString("id")));
		
		return user;
	}
	
	private static void addFriends(String userId,List<Friend> friends)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Insert into friendship (user_id,friend_id) Values (?,?)";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			//System.out.println("size="+friends.size());
			for(Friend u:friends){
				if(u.getName()==null)
					break;
				stmt.setString(1, userId);
				stmt.setString(2, u.getId());
				MysqlService.executeUpdate(stmt);
				//System.out.println("friend name="+u.getName());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	private static boolean userExist(String id)
	{		
		Connection conn = MysqlService.connect();
		try{
			String sql = "SELECT *  FROM users WHERE id = ? ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setString(1, id);
			ResultSet rs = MysqlService.executeQuery(stmt);
			if(rs.next()){
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			MysqlService.disconnect(conn);
		}
		return false;
	}
	
	private static List<Friend> getFriends(String userId)
	{		
		Connection conn = MysqlService.connect();
		List<Friend> friends=new ArrayList<Friend>();
		try{
			String sql = "SELECT * FROM friendship f,users u WHERE f.user_id = ? AND f.friend_id=u.id";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setString(1, userId);
			ResultSet rs = MysqlService.executeQuery(stmt);
			while(rs.next()){
				Friend friend=new Friend();
				friend=createFriend(rs);
				friends.add(friend);
				System.out.println(friend.getName());
			}
			return friends;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			MysqlService.disconnect(conn);
		}
		return null;
	}
	
	private static Friend createFriend(ResultSet rs) throws SQLException
	{
		Friend friend=new Friend();
		friend.setId(rs.getString("id"));
		friend.setName(rs.getString("name"));
		return friend;
	}
}
