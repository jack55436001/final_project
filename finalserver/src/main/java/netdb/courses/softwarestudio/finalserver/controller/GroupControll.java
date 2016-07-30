package netdb.courses.softwarestudio.finalserver.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSON;

import netdb.courses.softwarestudio.finalserver.model.dao.GroupDao;
import netdb.courses.softwarestudio.finalserver.model.domain.Group;

@Path("/group")
public class GroupControll {

	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	@Path("/friend")
	public Response getFriendGroup(@QueryParam("userId") String id) {
		System.out.println("get friend's group");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail" );
			return Response.status(400).build();
		}
		
		List<Group> groups = null;
		
		try {
			groups = GroupDao.getGroupsFromFriends(id);
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		//System.out.println("groups:"+JSON.toJSONString(groups));
		return Response.ok(JSON.toJSONString(groups)).build();
	}
	
	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	@Path("/me")
	public Response getMyGroup(@QueryParam("userId") String id) {
		System.out.println("get my group");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail" );
			return Response.status(400).build();
		}
		
		List<Group> groups = null;
		
		try {
			groups = GroupDao.getGroupsFromMe(id);
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		return Response.ok(JSON.toJSONString(groups)).build();
	}
	
	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	@Path("/all")
	public Response getAllGroup(@QueryParam("userId") String id) {
		System.out.println("get all group");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail" );
			return Response.status(400).build();
		}
		
		List<Group> groups = null;
		
		try {
			groups = GroupDao.getGroupsFromAll(id);
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		System.out.println("all groups:"+JSON.toJSONString(GroupDao.expiredFilter(groups)));
		return Response.ok(JSON.toJSONString(GroupDao.expiredFilter(groups))).build();
	}
	
	@PUT
	@Consumes("application/json")
	public Response putGroup(Group g)
	{
		System.out.println("put group");
		try{
			GroupDao.addGroup(g);
			return Response.status(201).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/join")
	public Response putJoin(@QueryParam("groupId")int groupId,@QueryParam("userId")String userId)
	{
		System.out.println("put join");
		System.out.println("groupId="+groupId);
		System.out.println("userId="+userId);
		try{
			GroupDao.addJoin(groupId,userId);
			return Response.status(201).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@POST
	@Consumes("application/json")
	public Response postGroup(Group g)
	{
		System.out.println("post group");
		System.out.println(JSON.toJSONString(g));
		try{
			GroupDao.updateGroup(g);
			return Response.status(201).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@DELETE
	@Consumes("application/x-www-form-urlencoded")
	public Response deleteGroup(@QueryParam("groupId") String id) {
		System.out.println("delete group");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail" );
			return Response.status(400).build();
		}
		
		try {
			GroupDao.deleteGroup(Integer.valueOf(id));
			return Response.status(200).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@DELETE
	@Consumes("application/x-www-form-urlencoded")
	@Path("/join")
	public Response deleteJoin(@QueryParam("userId") String id) {
		System.out.println("delete join");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail" );
			return Response.status(400).build();
		}
		
		try {
			GroupDao.deleteJoinByUser(id);
			return Response.status(200).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
}
