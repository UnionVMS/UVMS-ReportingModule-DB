package eu.europa.ec.fisheries.uvms.reporting.rest;

import eu.europa.ec.fisheries.uvms.reporting.rest.constants.RestConstants;
import eu.europa.ec.fisheries.uvms.reporting.rest.resources.ReportingResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath(RestConstants.REST_URL)
public class RestActivator extends Application {

    final static Logger LOG = LoggerFactory.getLogger(RestActivator.class);

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(ReportingResource.class);
        LOG.info(RestConstants.MODULE_NAME + " module starting up");
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    @GET
    @Produces("text/html")
    @Path("test")
    public String getHtml() {
        return "<html lang=\"en\"><body><h1>Hello, World!!</body></h1></html>";
    }

}
