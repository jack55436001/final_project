package netdb.courses.softwarestudio.finalserver.server;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("")
public class FinalServer extends ResourceConfig {
	
	public FinalServer() {
		packages("netdb.courses.softwarestudio.finalserver");
		//register(JacksonFeature.class);
	}

}