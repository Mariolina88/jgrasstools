package org.jgrasstools.hortonmachine.models.hm;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.jgrasstools.gears.io.shapefile.ShapefileFeatureReader;
import org.jgrasstools.gears.io.timedependent.TimeseriesByStepReaderId2Value;
import org.jgrasstools.gears.io.timedependent.TimeseriesByStepWriterId2Value;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.jgrasstools.hortonmachine.modules.statistics.kriging.Kriging;
import org.jgrasstools.hortonmachine.utils.HMTestCase;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
/**
 * Test the kriging model.
 * 
 * @author daniele andreis
 *
 */
public class TestKriging extends HMTestCase {

    public void testKriging() throws Exception {
        PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.err);

        URL stazioniUrl = this.getClass().getClassLoader().getResource("rainstations.shp");
        File stazioniFile = new File(stazioniUrl.toURI());
        URL puntiUrl = this.getClass().getClassLoader().getResource("basins_passirio_width0.shp");
        File puntiFile = new File(puntiUrl.toURI());
        URL krigingRainUrl = this.getClass().getClassLoader().getResource("rain_test.csv");
        File krigingRainFile = new File(krigingRainUrl.toURI());

        ShapefileFeatureReader stationsReader = new ShapefileFeatureReader();
        stationsReader.file = stazioniFile.getAbsolutePath();
        stationsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> stationsFC = stationsReader.geodata;

        ShapefileFeatureReader interpolatedPointsReader = new ShapefileFeatureReader();
        interpolatedPointsReader.file = puntiFile.getAbsolutePath();
        interpolatedPointsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> interpolatedPointsFC = interpolatedPointsReader.geodata;

        TimeseriesByStepReaderId2Value reader = new TimeseriesByStepReaderId2Value();
        reader.file = krigingRainFile.getAbsolutePath();
        reader.idfield = "ID";
        reader.tStart = "2000-01-01 00:00";
        reader.tTimestep = 60;
        // reader.tEnd = "2000-01-01 00:00";
        reader.fileNovalue = "-9999";

        reader.initProcess();

        Kriging kriging = new Kriging();
        kriging.pm = pm;

        kriging.inStations = stationsFC;
        kriging.fStationsid = "ID_PUNTI_M";

        kriging.inInterpolate = interpolatedPointsFC;
        kriging.fInterpolateid = "netnum";

        // it doesn't execute the model with log value.
        kriging.doLogarithmic = false;
        /*
         * Set up the model in order to use the variogram with an explicit integral scale and variance.
         */
        kriging.pVariance = 0.5;
        kriging.pIntegralscale = new double[]{10000, 10000, 100};
        /*
         * Set up the model in order to run with a FeatureCollection as point to interpolated. In this case only 2D.
         */
        kriging.pMode = 0;

        File interpolatedRainFile = new File(krigingRainFile.getParentFile(), "kriging_interpolated.csv");
        interpolatedRainFile = classesTestFile2srcTestResourcesFile(interpolatedRainFile);
        TimeseriesByStepWriterId2Value writer = new TimeseriesByStepWriterId2Value();
        writer.file = interpolatedRainFile.getAbsolutePath();

        writer.tStart = reader.tStart;
        writer.tTimestep = reader.tTimestep;

        while( reader.doProcess ) {
            reader.nextRecord();
            HashMap<Integer, double[]> id2ValueMap = reader.data;
            kriging.inData = id2ValueMap;
            kriging.executeKriging();
            /*
             * Extract the result.
             */
            HashMap<Integer, double[]> result = kriging.outData;
            writer.data = result;
            writer.writeNextLine();
        }

        reader.close();
        writer.close();
    }

    /**
     * Run the kriging models.
     * 
     * <p>
     * This is the case which all the station have the same value.
     * </p>
     * @throws Exception 
     * @throws Exception
     */
    public void testKriging2() throws Exception {
        PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.err);

        URL stazioniUrl = this.getClass().getClassLoader().getResource("rainstations.shp");
        File stazioniFile = new File(stazioniUrl.toURI());
        URL puntiUrl = this.getClass().getClassLoader().getResource("basins_passirio_width0.shp");
        File puntiFile = new File(puntiUrl.toURI());
        URL krigingRainUrl = this.getClass().getClassLoader().getResource("rain_test2A.csv");
        File krigingRainFile = new File(krigingRainUrl.toURI());

        ShapefileFeatureReader stationsReader = new ShapefileFeatureReader();
        stationsReader.file = stazioniFile.getAbsolutePath();
        stationsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> stationsFC = stationsReader.geodata;

        ShapefileFeatureReader interpolatedPointsReader = new ShapefileFeatureReader();
        interpolatedPointsReader.file = puntiFile.getAbsolutePath();
        interpolatedPointsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> interpolatedPointsFC = interpolatedPointsReader.geodata;

        TimeseriesByStepReaderId2Value reader = new TimeseriesByStepReaderId2Value();
        reader.file = krigingRainFile.getAbsolutePath();
        reader.idfield = "ID";
        reader.tStart = "2000-01-01 00:00";
        reader.tTimestep = 60;
        // reader.tEnd = "2000-01-01 00:00";
        reader.fileNovalue = "-9999";

        reader.initProcess();

        Kriging kriging = new Kriging();
        kriging.pm = pm;

        kriging.inStations = stationsFC;
        kriging.fStationsid = "ID_PUNTI_M";

        kriging.inInterpolate = interpolatedPointsFC;
        kriging.fInterpolateid = "netnum";

        // it doesn't execute the model with log value.
        kriging.doLogarithmic = false;
        /*
         * Set up the model in order to use the variogram with an explicit integral scale and variance.
         */
        kriging.pVariance = 0.5;
        kriging.pIntegralscale = new double[]{10000, 10000, 100};
        /*
         * Set up the model in order to run with a FeatureCollection as point to interpolated. In this case only 2D.
         */
        kriging.pMode = 0;

        File interpolatedRainFile = new File(krigingRainFile.getParentFile(), "kriging_interpolated.csv");
        interpolatedRainFile = classesTestFile2srcTestResourcesFile(interpolatedRainFile);
        TimeseriesByStepWriterId2Value writer = new TimeseriesByStepWriterId2Value();
        writer.file = interpolatedRainFile.getAbsolutePath();

        writer.tStart = reader.tStart;
        writer.tTimestep = reader.tTimestep;

        while( reader.doProcess ) {
            reader.nextRecord();
            HashMap<Integer, double[]> id2ValueMap = reader.data;
            kriging.inData = id2ValueMap;
            kriging.executeKriging();
            /*
             * Extract the result.
             */
            HashMap<Integer, double[]> result = kriging.outData;
            Set<Integer> pointsToInterpolateResult = result.keySet();
            Iterator<Integer> iterator = pointsToInterpolateResult.iterator();
            while( iterator.hasNext() ) {
                int id = iterator.next();
                double[] actual = result.get(id);
                assertEquals(1.0, actual[0], 0);
            }
            writer.data = result;
            writer.writeNextLine();
        }

        reader.close();
        writer.close();
    }
    /**
     * Run the kriging models.
     * 
     * <p>
     * This is the case which all the station have the same value.
     * </p>
     * @throws Exception 
     * @throws Exception
     */
    public void testKriging3() throws Exception {
        PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.err);

        URL stazioniUrl = this.getClass().getClassLoader().getResource("rainstations.shp");
        File stazioniFile = new File(stazioniUrl.toURI());
        URL puntiUrl = this.getClass().getClassLoader().getResource("basins_passirio_width0.shp");
        File puntiFile = new File(puntiUrl.toURI());
        URL krigingRainUrl = this.getClass().getClassLoader().getResource("rain_test2A.csv");
        File krigingRainFile = new File(krigingRainUrl.toURI());

        ShapefileFeatureReader stationsReader = new ShapefileFeatureReader();
        stationsReader.file = stazioniFile.getAbsolutePath();
        stationsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> stationsFCTmP = stationsReader.geodata;
        FeatureIterator<SimpleFeature> iterator = stationsFCTmP.features();
        FeatureCollection<SimpleFeatureType, SimpleFeature> stationsFC = FeatureCollections
        .newCollection();
        stationsFC.add(iterator.next());
        stationsFCTmP.close(iterator);
        ShapefileFeatureReader interpolatedPointsReader = new ShapefileFeatureReader();
        interpolatedPointsReader.file = puntiFile.getAbsolutePath();
        interpolatedPointsReader.readFeatureCollection();
        FeatureCollection<SimpleFeatureType, SimpleFeature> interpolatedPointsFC = interpolatedPointsReader.geodata;

        TimeseriesByStepReaderId2Value reader = new TimeseriesByStepReaderId2Value();
        reader.file = krigingRainFile.getAbsolutePath();
        reader.idfield = "ID";
        reader.tStart = "2000-01-01 00:00";
        reader.tTimestep = 60;
        // reader.tEnd = "2000-01-01 00:00";
        reader.fileNovalue = "-9999";

        reader.initProcess();

        Kriging kriging = new Kriging();
        kriging.pm = pm;

        kriging.inStations = stationsFC;
        kriging.fStationsid = "ID_PUNTI_M";

        kriging.inInterpolate = interpolatedPointsFC;
        kriging.fInterpolateid = "netnum";

        // it doesn't execute the model with log value.
        kriging.doLogarithmic = false;
        /*
         * Set up the model in order to use the variogram with an explicit integral scale and variance.
         */
        kriging.pVariance = 0.5;
        kriging.pIntegralscale = new double[]{10000, 10000, 100};
        /*
         * Set up the model in order to run with a FeatureCollection as point to interpolated. In this case only 2D.
         */
        kriging.pMode = 0;

        File interpolatedRainFile = new File(krigingRainFile.getParentFile(), "kriging_interpolated.csv");
        interpolatedRainFile = classesTestFile2srcTestResourcesFile(interpolatedRainFile);
        TimeseriesByStepWriterId2Value writer = new TimeseriesByStepWriterId2Value();
        writer.file = interpolatedRainFile.getAbsolutePath();

        writer.tStart = reader.tStart;
        writer.tTimestep = reader.tTimestep;

        while( reader.doProcess ) {
            reader.nextRecord();
            HashMap<Integer, double[]> id2ValueMap = reader.data;
            kriging.inData = id2ValueMap;
            kriging.executeKriging();
            /*
             * Extract the result.
             */
            HashMap<Integer, double[]> result = kriging.outData;
            Set<Integer> pointsToInterpolateResult = result.keySet();
            Iterator<Integer> iteratorTest = pointsToInterpolateResult.iterator();
            while( iterator.hasNext() ) {
                int id = iteratorTest.next();
                double[] actual = result.get(id);
                assertEquals(1.0, actual[0], 0);
            }
            writer.data = result;
            writer.writeNextLine();
        }

        reader.close();
        writer.close();
    }
}
