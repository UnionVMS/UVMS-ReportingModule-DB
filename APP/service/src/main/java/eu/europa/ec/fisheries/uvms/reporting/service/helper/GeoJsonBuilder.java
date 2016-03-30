package eu.europa.ec.fisheries.uvms.reporting.service.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.rest.FeatureToGeoJsonJacksonMapper;
import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.Map;

/**
 * Created by padhyad on 3/29/2016.
 */
@Slf4j
public class GeoJsonBuilder {

    private static final String LONGITUDE = "longitude";

    private static final String LATITUDE = "latitude";

    public static final String GEOMETRY = "geometry";

    private SimpleFeatureType simpleFeatureType;

    private DefaultFeatureCollection alarms;

    public GeoJsonBuilder() {
        this.simpleFeatureType = buildSimpleFeature();
        this.alarms = new DefaultFeatureCollection(null, buildSimpleFeature());
    }

    public ObjectNode toJson() throws ReportingServiceException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.set("alarms", new FeatureToGeoJsonJacksonMapper().convert(alarms));
            return rootNode;
        } catch (IOException e) {
            log.error("Exception in building geo Json for alarm properties", e);
            throw new ReportingServiceException(e);
        }
    }

    public void addFeatureToCollection(Map<String, Object> properties, Map<String, Double> coordinates) {
        Point point = getPointByCoordinates(coordinates);
        SimpleFeatureBuilder simpleFeatureBuilder = new SimpleFeatureBuilder(simpleFeatureType);
        simpleFeatureBuilder.set(GEOMETRY, point);
        for (Map.Entry<String, Object> entrySet : properties.entrySet()) {
            simpleFeatureBuilder.set(entrySet.getKey(), entrySet.getValue());
        }
        SimpleFeature simpleFeature = simpleFeatureBuilder.buildFeature(null);
        alarms.add(simpleFeature);
    }

    private Point getPointByCoordinates(Map<String, Double> coordinates) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        return geometryFactory.createPoint(new Coordinate(coordinates.get(LONGITUDE), coordinates.get(LATITUDE)));
    }

    private SimpleFeatureType buildSimpleFeature() {
        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        featureTypeBuilder.setName("Point");
        featureTypeBuilder.add(GEOMETRY, Point.class);
        featureTypeBuilder.add(AlarmHelper.NAME, String.class);
        featureTypeBuilder.add(AlarmHelper.EXTMARK, String.class);
        featureTypeBuilder.add(AlarmHelper.IRCS, String.class);
        featureTypeBuilder.add(AlarmHelper.CFR, String.class);
        featureTypeBuilder.add(AlarmHelper.FS, String.class);
        featureTypeBuilder.add(AlarmHelper.TICKET_STATUS, String.class);
        featureTypeBuilder.add(AlarmHelper.TICKET_OPENDATE, String.class);
        featureTypeBuilder.add(AlarmHelper.TICKET_UPDATEDATE, String.class);
        featureTypeBuilder.add(AlarmHelper.TICKET_UPDATEDBY, String.class);
        featureTypeBuilder.add(AlarmHelper.RULE_NAME, String.class);
        featureTypeBuilder.add(AlarmHelper.RULE_DESC, String.class);
        featureTypeBuilder.add(AlarmHelper.RULE_DEFINITION, String.class);

        return featureTypeBuilder.buildFeatureType();
    }
}