package com.dynacore.livemap;

import com.dynacore.livemap.traveltime.domain.TravelTimeFeatureImpl;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

import org.geojson.LngLatAlt;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.data.r2dbc.core.DatabaseClient;

import java.util.stream.Collectors;

import static org.locationtech.jts.geom.PrecisionModel.FLOATING;


public class GeoTest {

    @Autowired
    DatabaseClient client;

    @Autowired
    JpaProperties jpaProperties;

    record GeometryEntity(String id, String type, String geoType, LineString geom ) {  }






    public Geometry wktToGeometry(String wellKnownText)
            throws ParseException {
        WKTReader wktReader = new WKTReader();
        return wktReader.read(wellKnownText);
    }



//    public void insertPoint(String point) {
//        PointEntity entity = new PointEntity();
//        entity.setPoint((Point) wktToGeometry(point));
//    }

//.getMap(locations.toArray(new WorldLocation[locations.size()]))

    org.geojson.LineString lineString = new org.geojson.LineString(
            new LngLatAlt(4.776645712715283,52.338380232953895),
            new LngLatAlt(4.776853788479121,52.33827956952142),
            new LngLatAlt(4.77842037340242,52.337460757548655),
            new LngLatAlt(4.778671519814815,52.33733621949967),
            new LngLatAlt(4.780652279562285,52.336267847567214),
            new LngLatAlt(4.782159793662865,52.33546665913931));

    record LineStringEntity( org.geojson.LngLatAlt... points) { }

    @Test
    public void entityTest() {
        LineStringEntity geometryEntity = new LineStringEntity( lineString.getCoordinates().toArray(new LngLatAlt[0]) );


        System.out.println(lineString.toString());

        //LineString stringX = new LineString(GeometryFactory.toLineStringArray(lineString.getCoordinates()));
    }








    @Test
    public void geometryInsertion() {
        //     dropCreate();

//    INSERT INTO geometries VALUES
//    ('Linestring', 'LINESTRING(0 0, 1 1, 2 1, 2 2)'),
        ConnectionFactory connectionFactory =
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .host("localhost")
                                .port(5432) // optional, defaults to 5432
                                .username("postgres")
                                .password("admin")
                                .database("postgisdb")
                                .build());



        DatabaseClient client = DatabaseClient.create(connectionFactory);

        org.geojson.LineString lineString = new  org.geojson.LineString(
                new org.geojson.LngLatAlt(4.776645712715283,52.338380232953895),
                new org.geojson.LngLatAlt(4.776853788479121,52.33827956952142),
                new org.geojson.LngLatAlt(4.77842037340242,52.337460757548655),
                new org.geojson.LngLatAlt(4.778671519814815,52.33733621949967),
                new org.geojson.LngLatAlt(4.780652279562285,52.336267847567214),
                new org.geojson. LngLatAlt(4.782159793662865,52.33546665913931));


        System.out.println(lineString);

        PrecisionModel precisionModel = new PrecisionModel();
        GeometryFactory geometryFactory= new GeometryFactory(precisionModel);
        Coordinate[] lineStringArr =   lineString.getCoordinates().stream().map(lngLatAlt -> new Coordinate(lngLatAlt.getLongitude(),lngLatAlt.getLatitude(),lngLatAlt.getAltitude())).toArray(Coordinate[]::new);
        LineString x = geometryFactory.createLineString(lineStringArr);









      //  var geoEntity = new GeometryEntity("002", "TravelTime", "LineString", lineString);

//        client.execute("INSERT INTO geometries_play(name, geom) VALUES(name, ST_GeomFromText('LINESTRING(0 0, 1 1, 2 1, 2 2)'));")
//                .fetch().rowsUpdated().block();
//
//
//        var object = client.execute( "select id, type, geotype, ST_AsText(GEOM) FROM geometries")
//                .map(row -> {
//
//                    System.out.println( row.get("GEOM"));
//                    //       return new GeometryEntity( "idtest",  "typetest", "geotypetest", new LineString() );
//                    return row.get("GEOM");
//                    //return new GeometryEntity( (String ) row.get("ID"),  (String )row.get("TYPE"), (String )row.get("GEOTYPE"), new LineString() );
//
//                }).one().block();

 //       System.out.println(object);
    }

// Convertors:
//        List<Converter<?, ?>> converterList = new ArrayList<>();
//        converterList.add(new GeometryReadConvertor());
//        converterList.add(new GeometryWriteConvertor());
//        new R2dbcCustomConversions(getStoreConversions(), converterList);
//

}
