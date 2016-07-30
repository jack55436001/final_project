package netdb.courses.softwarestudio.finalserver.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Group {
	
	private int id;
	private String title;
	private String article;
	private String masterId;
	private String location;
	private String lat;
	private String lng;
	private String date;
	private String time;
	private int people_min;
	private int people_max;
	private int cur_people_num;
	private String tag;
	private boolean success;
	private boolean expired;
	private List<String> join=new ArrayList<String>();
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public int getPeople_max() {
		return people_max;
	}
	public void setPeople_max(int people_max) {
		this.people_max = people_max;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public String getMasterId() {
		return masterId;
	}
	public void setMasterId(String master) {
		this.masterId = master;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getPeople_min() {
		return people_min;
	}
	public void setPeople_min(int people_min) {
		this.people_min = people_min;
	}
	public int getCur_people_num() {
		return cur_people_num;
	}
	public void setCur_people_num(int cur_people_num) {
		this.cur_people_num = cur_people_num;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isExpired() {
		return expired;
	}
	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	public List<String> getJoin() {
		return join;
	}
	public void setJoin(List<String> join) {
		this.join = join;
	}

}
