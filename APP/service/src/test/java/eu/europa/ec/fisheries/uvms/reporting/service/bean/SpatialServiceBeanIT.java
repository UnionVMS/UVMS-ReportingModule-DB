package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.MapConfigurationDTO;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class SpatialServiceBeanIT {

    @EJB
    SpatialService spatialService;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "reporting-service.war").addPackages(true, "eu.europa.ec.fisheries.uvms.reporting.service")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(new File("src/test/resources/META-INF/jboss-deployment-structure.xml"))
                .addAsResource("config.properties")
                .addAsResource("usmDeploymentDescriptor.xml")
                .addAsResource("logback.xml");

        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME, ScopeType.TEST, ScopeType.PROVIDED).resolve().withTransitivity().asFile();
        war = war.addAsLibraries(libs);

        System.out.println(war.toString(true));

        return war;
    }

    @Before
    public void setUp() throws Exception {
        assertNotNull("Spatial service not injected", spatialService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFilterAreaTest() throws ReportingServiceException {
        String filterArea = spatialService.getFilterArea(getUserAreas());
        assertNotNull(filterArea);
    }


    @Test
    public void shouldSaveOrUpdateMapConfiguration() throws Exception {
        // given
        MapConfigurationDTO mapConfiguration = new MapConfigurationDTO();

        // when
        boolean isSuccess = spatialService.saveOrUpdateMapConfiguration(1, mapConfiguration);

        // then
        assertTrue(isSuccess);
    }


    private List<AreaIdentifierType> getUserAreas() {
        AreaIdentifierType userArea = new AreaIdentifierType();
        userArea.setAreaType("EEZ");
        userArea.setId("1");

        return Arrays.asList(userArea);
    }

}