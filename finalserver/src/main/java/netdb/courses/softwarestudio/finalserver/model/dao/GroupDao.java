package netdb.courses.softwarestudio.finalserver.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


import netdb.courses.softwarestudio.finalserver.model.domain.Group;
import netdb.courses.softwarestudio.finalserver.service.MysqlService;

public class GroupDao {
	
	public static void addGroup(Group g)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Insert into groupEvent (title,article,people_min,date,time,location,master_id,current_people_number,success,expired,tag,people_max,lat,lon) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setString(1, g.getTitle());
			stmt.setString(2, g.getArticle());
			stmt.setInt(3, g.getPeople_min());
			stmt.setString(4, g.getDate());
			stmt.setString(5, g.getTime());
			stmt.setString(6, g.getLocation());
			stmt.setString(7, g.getMasterId());
			stmt.setInt(8, g.getCur_people_num());
			stmt.setBoolean(9, g.isSuccess());
			stmt.setBoolean(10, g.isExpired());
			stmt.setString(11, g.getTag());
			stmt.setInt(12, g.getPeople_max());
			stmt.setString(13, g.getLat());
			stmt.setString(14, g.getLng());
			MysqlService.executeUpdate(stmt);
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static void updateGroup(Group g)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Update groupEvent set title=? , article=? , people_min=? , date=? , time=? ,location=? , current_people_number=? , success=? , expired=? ,tag=? ,people_max=? ,lat=? ,lon=?,master_id=? WHERE id=?";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setString(1, g.getTitle());
			stmt.setString(2, g.getArticle());
			stmt.setInt(3, g.getPeople_min());
			stmt.setString(4, g.getDate());
			stmt.setString(5, g.getTime());
			stmt.setString(6, g.getLocation());
			stmt.setInt(7, g.getCur_people_num());
			stmt.setBoolean(8, g.isSuccess());
			stmt.setBoolean(9, g.isExpired());
			stmt.setString(10, g.getTag());
			stmt.setInt(11, g.getPeople_max());
			stmt.setString(12, g.getLat());
			stmt.setString(13, g.getLng());
			stmt.setString(14, g.getMasterId());
			stmt.setInt(15, g.getId());
			MysqlService.executeUpdate(stmt);
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static List<Group> getGroupsFromFriends(String id)
	{
		List<Group> groups = new ArrayList<Group>();
		Connection conn = MysqlService.connect();
		
		try {
			String sql = "SELECT id,title,article,date,time,location,people_min,people_max,current_people_number,master_id,success,lat,lon,tag,IF(CURDATE()>date OR CURDATE()=date AND CURTIME()>time ,1,0 ) timeout FROM groupEvent g ,friendship f WHERE f.user_id=? AND g.master_id=f.friend_id ORDER BY g.id desc, date , time ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setString(1, id);
			ResultSet rs = MysqlService.executeQuery(stmt);
			while (rs.next() ) {
				Group group = createGroup(rs);
				groups.add(group);
			}
			return groups;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}

		return null;
	}
	
	public static List<Group> getGroupsFromMe(String id)
	{
		List<Group> groups = new ArrayList<Group>();
		Connection conn = MysqlService.connect();
		
		try {
			String sql = "SELECT id,title,article,date,time,location,people_min,people_max,current_people_number,master_id,success,lat,lon,tag,IF(CURDATE()>date OR ( CURDATE()=date AND CURTIME()>time),1,0)timeout FROM groupEvent g WHERE master_id=?  ORDER BY  g.id desc, date , time ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setString(1, id);
			ResultSet rs = MysqlService.executeQuery(stmt);
			while (rs.next() ) {
				Group group = createGroup(rs);
				groups.add(group);
			}
			return groups;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}

		return null;
	}
	
	public static List<Group> getGroupsFromAll(String id)
	{
		List<Group> groups = new ArrayList<Group>();
		groups.addAll(getGroupsFromMe(id));
		groups.addAll(getGroupsFromFriends(id));
		return groups;
	}
	
	public static Group getGroupsById(int id)
	{
		Group group = new Group();
		Connection conn = MysqlService.connect();
		
		try {
			String sql = "SELECT * ,IF(CURDATE()>date OR ( CURDATE()=date AND CURTIME()>time),1,0)timeout FROM groupEvent WHERE id=? ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setInt(1, id);
			ResultSet rs = MysqlService.executeQuery(stmt);
			if (rs.next() ) {
				group = createGroup(rs);
				return group;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
		return null;
	}
	
	private static Group createGroup(ResultSet rs)throws SQLException
	{
		Group group =new Group();
		group.setId(rs.getInt("id"));
		group.setTag(rs.getString("tag"));
		group.setTitle(rs.getString("title"));
		group.setArticle(rs.getString("article"));
		group.setDate(rs.getString("date"));
		group.setTime(rs.getString("time"));
		group.setLocation(rs.getString("location"));
		group.setPeople_min(rs.getInt("people_min"));
		group.setPeople_max(rs.getInt("people_max"));
		group.setSuccess(rs.getBoolean("success"));
		group.setExpired(rs.getBoolean("timeout"));
		group.setCur_people_num(rs.getInt("current_people_number"));
		group.setMasterId(rs.getString("master_id"));
		group.setLat(rs.getString("lat"));
		group.setLng(rs.getString("lon"));
		group.setJoin(getJoins(rs.getInt("id")));
		
		return group;
	}
	
	public static List<String> getJoins(int id)
	{
		
		List<String> joins = new ArrayList<String>();
		Connection conn = MysqlService.connect();
		
		try {
			String sql = "SELECT * from group_join WHERE groupid = ? ";
			PreparedStatement stmt = MysqlService.createPreparedStatementReturnKey(conn, sql);
			stmt.setInt(1, id);
			ResultSet rs = MysqlService.executeQuery( stmt);
			while (rs.next() ) {
				String friend = rs.getString("user_id");
				joins.add(friend);
			}
			return joins;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
		return null;
	}
	
	public static void deleteGroup(int id)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Delete From groupEvent WHERE id=?";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setInt(1, id);
			MysqlService.executeUpdate(stmt);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
		
	}
	
	public static void deleteJoinByGroup(int groupId)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Delete From group_join WHERE groupid=?";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setInt(1, groupId);
			MysqlService.executeUpdate(stmt);
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static void deleteJoinByUser(String userId)
	{
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Delete From group_join WHERE user_id=?";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setString(1, userId);
			MysqlService.executeUpdate(stmt);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static void addJoin(int groupId,String userId)
	{
		Group group=getGroupsById(groupId);
		if(group==null)
			return ;
		if(group.getCur_people_num()>=group.getPeople_max() || group.isExpired())
			return ;
		System.out.println("insert join");
		String sql;
		PreparedStatement stmt;
		Connection conn = MysqlService.connect();
		// transaction
		try {
			conn.setAutoCommit(false);
			sql = "Insert into group_join  (groupid,user_id)Values(?,?)";
			stmt = MysqlService.createPreparedStatement(conn, sql);
			stmt.setInt(1, groupId);
			stmt.setString(2, userId);
			MysqlService.executeUpdate(stmt);
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MysqlService.disconnect(conn);
		}
	}
	
	public static List<Group> expiredFilter(List<Group> groups)
	{
		List<Group> result=new ArrayList<Group>();
		for(int i=0;i<groups.size();i++){
			if(!groups.get(i).isExpired())
				result.add(groups.get(i));
		}
		return result;
	}
}
