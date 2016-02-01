package eu.europa.ec.fisheries.uvms.reporting.rest.dto;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VelocityType;
import org.junit.Test;
import java.io.IOException;

public class VelocityTypeTest {

    @Test
    public void whenSerializingUsingJsonValue_thenCorrect() throws IOException {

        String enumAsString = new ObjectMapper().writeValueAsString(VelocityType.KPH);

        assertThat(enumAsString, is("\"kph\""));
    }

    @Test
    public void whenDeserializerUsingJsonValue_thenCorrect() throws IOException {

        String json = "\"kph\"";

        VelocityType velocityType = new ObjectMapper().reader().withType(VelocityType.class)
                .readValue(json);

        assertEquals("kph", velocityType.getDisplayName());
    }

}
