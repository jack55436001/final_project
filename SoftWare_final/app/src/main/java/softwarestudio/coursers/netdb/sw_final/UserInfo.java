package softwarestudio.coursers.netdb.sw_final;

import org.json.JSONObject;

/**
 * Created by shanglun on 2016/6/16.
 */
public class UserInfo {
    private String name="NULL";
    private String email="NULL";
    private String birthday="NULL";
    private String id="NULL";
    private String gender="NULL";
    private JSONObject friends;

    public void setFriends(JSONObject friends)
    {
        this.friends=friends;
    }


    public void setName(String name)
    {
        this.name=name;
    }

    public void setEmail(String email)
    {
        this.email=email;
    }

    public void setBirthday(String birthday)
    {
        this.birthday=birthday;
    }

    public  void setId(String id)
    {
        this.id=id;
    }

    public void setGender(String gender)
    {
        this.gender=gender;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public String getId()
    {
        return id;
    }

    public String getGender()
    {
        return gender;
    }

    public JSONObject getFriends()
    {
        return friends;
    }
}
