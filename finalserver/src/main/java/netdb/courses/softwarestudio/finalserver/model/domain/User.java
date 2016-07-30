package netdb.courses.softwarestudio.finalserver.model.domain;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String id;
	private String name;
	private String email;
	private String gender;
	private String birthday;
	private List<Friend> friends=new ArrayList<Friend>();
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		/*String msg="WTFFFFFFF";
		try{
			msg = new String(name.getBytes("ISO-8859-1"), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Friend> getFriends() {
		return friends;
	}
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	
	
	
}
