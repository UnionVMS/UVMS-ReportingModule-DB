package eu.europa.ec.fisheries.uvms.reporting.message;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.uvms.reporting.message.mapper.ExtMovementMessageMapper;
import lombok.SneakyThrows;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.Test;

import java.net.URL;

import static junit.framework.Assert.assertTrue;

public class ExtMovementMessageMapperTest {

    @Test
    @SneakyThrows
    public void testMapToGetMovementMapByQueryRequest() {

        URL url = Resources.getResource("ExtendedMovementMessageMapperTests.getMovementMapByQueryRequest.xml");
        String expected = Resources.toString(url, Charsets.UTF_8);

        Diff diff = new Diff(expected, ExtMovementMessageMapper.mapToGetMovementMapByQueryRequest(new MovementQuery()));
        DetailedDiff detailedDiff = new DetailedDiff(diff);
        System.out.println(detailedDiff.getAllDifferences());
        assertTrue(diff.identical());

    }

    @Test(expected = IllegalArgumentException.class)
    @SneakyThrows
    public void testMapToGetMovementMapByQueryRequestException() {
        ExtMovementMessageMapper.mapToGetMovementMapByQueryRequest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    @SneakyThrows
    public void testMapToMovementMapResponseException() {
        ExtMovementMessageMapper.mapToMovementMapResponse(null);
    }
}
