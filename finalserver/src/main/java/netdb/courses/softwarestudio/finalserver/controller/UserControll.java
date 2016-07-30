package netdb.courses.softwarestudio.finalserver.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSON;

import netdb.courses.softwarestudio.finalserver.model.dao.GroupDao;
import netdb.courses.softwarestudio.finalserver.model.dao.UserDao;
import netdb.courses.softwarestudio.finalserver.model.domain.Group;
import netdb.courses.softwarestudio.finalserver.model.domain.User;

@Path("/user")
public class UserControll {
	
	@PUT
	@Consumes("application/json")
	public Response putUser(User u)
	{
		System.out.println("put user");
		System.out.println(JSON.toJSON(u).toString());
		try{
			System.out.println("name="+u.getName());
			UserDao.addUser(u);
			return Response.status(201).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(500).build();
		}
	}	
	
	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public Response getUser(@QueryParam("userId") String id) {
		
		System.out.println("get user");
		// if some query parameters are missing
		if (id == null ) {
			System.out.println("parameter passing fail, ID = " + id );
			return Response.status(400).build();
		}
		
		try {
			User user=UserDao.getUser(id);
			if(user!=null)
				return Response.ok(JSON.toJSONString(user)).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		return Response.status(204).build();
		
	}
}
