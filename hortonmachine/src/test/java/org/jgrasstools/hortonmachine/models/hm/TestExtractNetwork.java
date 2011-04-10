/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jgrasstools.hortonmachine.models.hm;

import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.jgrasstools.gears.utils.coverage.CoverageUtilities;
import org.jgrasstools.hortonmachine.modules.network.extractnetwork.ExtractNetwork;
import org.jgrasstools.hortonmachine.utils.HMTestCase;
import org.jgrasstools.hortonmachine.utils.HMTestMaps;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
/**
 * It test the {@link ExtractNetwork} module.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class TestExtractNetwork extends HMTestCase {
    /**
     * Test module with mode=0.
     */
    public void testExtractNetwork0() throws Exception {
        HashMap<String, Double> envelopeParams = HMTestMaps.envelopeParams;
        CoordinateReferenceSystem crs = HMTestMaps.crs;

        double[][] flowData = HMTestMaps.mflowDataBorder;
        GridCoverage2D flowCoverage = CoverageUtilities.buildCoverage("flow", flowData, envelopeParams, crs, true);
        double[][] tcaData = HMTestMaps.tcaData;
        GridCoverage2D tcaCoverage = CoverageUtilities.buildCoverage("tca", tcaData, envelopeParams, crs, true);

        ExtractNetwork extractNetwork = new ExtractNetwork();
        extractNetwork.pm = pm;
        extractNetwork.inFlow = flowCoverage;
        extractNetwork.inTca = tcaCoverage;
        extractNetwork.pMode = 0;
        extractNetwork.pThres = 5;
        extractNetwork.process();

        GridCoverage2D networkCoverage = extractNetwork.outNet;
        checkMatrixEqual(networkCoverage.getRenderedImage(), HMTestMaps.extractNet0Data, 0.01);
    }

    /**
     * Test module with mode=1.
     */
    public void testExtractNetwork1() throws Exception {

        HashMap<String, Double> envelopeParams = HMTestMaps.envelopeParams;
        CoordinateReferenceSystem crs = HMTestMaps.crs;

        double[][] flowData = HMTestMaps.mflowDataBorder;
        GridCoverage2D flowCoverage = CoverageUtilities.buildCoverage("flow", flowData, envelopeParams, crs, true);
        double[][] tcaData = HMTestMaps.tcaData;
        GridCoverage2D tcaCoverage = CoverageUtilities.buildCoverage("tca", tcaData, envelopeParams, crs, true);
        double[][] slopeData = HMTestMaps.slopeData;
        GridCoverage2D slopeCoverage = CoverageUtilities.buildCoverage("slope", slopeData, envelopeParams, crs, true);

        ExtractNetwork extractNetwork = new ExtractNetwork();
        extractNetwork.pm = pm;
        extractNetwork.inFlow = flowCoverage;
        extractNetwork.inTca = tcaCoverage;
        extractNetwork.inSlope = slopeCoverage;
        extractNetwork.pMode = 1;
        extractNetwork.pThres = 8;
        extractNetwork.doNetfc = true;
        extractNetwork.process();

        GridCoverage2D networkCoverage = extractNetwork.outNet;
        checkMatrixEqual(networkCoverage.getRenderedImage(), HMTestMaps.extractNet1Data, 0.01);

        SimpleFeatureCollection networkFC = extractNetwork.outNetfc;

        FeatureIterator<SimpleFeature> featureIterator = networkFC.features();
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coordinates = geometry.getCoordinates();
            System.out.println("Coords of feature: " + feature.getID());
            for( Coordinate coordinate : coordinates ) {
                System.out.println(coordinate);
            }
        }
        featureIterator.close();
    }

}