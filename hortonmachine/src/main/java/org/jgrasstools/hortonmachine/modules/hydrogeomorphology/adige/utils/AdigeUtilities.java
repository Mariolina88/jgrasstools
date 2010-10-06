/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.jgrasstools.gears.libs.monitor.IJGTProgressMonitor;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.HillSlope;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.IHillSlope;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.PfafstetterNumber;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class AdigeUtilities {

    /**
     * Generates {@link HillSlope}s from the informations gathered in the provided feature layers.
     * 
     * @param netFeatureCollection the network features
     * @param hillslopeFeatureCollection the hillslope features
     * @param netnumAttr the attribute name of the field connecting the two layers
     * @param pfafAttr the net pfafstetter field name
     * @param startelevAttr the field name of the start elevation of the net (can be null)
     * @param endelevAttr the field name of the end elevation of the net (can be null)
     * @param baricenterAttr the field holding the baricenter of the hasin elevation (can be null)
     * @param out a printstream for logging
     * @return the list of ordered hillslopes, starting from the most downstream one
     * @throws Exception
     */
    public static List<IHillSlope> generateHillSlopes( FeatureCollection<SimpleFeatureType, SimpleFeature> netFeatureCollection,
            FeatureCollection<SimpleFeatureType, SimpleFeature> hillslopeFeatureCollection, String netnumAttr, String pfafAttr,
            String startelevAttr, String endelevAttr, String baricenterAttr, IJGTProgressMonitor out ) throws Exception {

        SimpleFeatureType fT = netFeatureCollection.getSchema();
        // netnum attribute
        int lAttrIndex = fT.indexOf(netnumAttr);
        if (lAttrIndex == -1) {
            String pattern = "Attribute {0} not found in layer {1}.";
            Object[] args = new Object[]{netnumAttr, fT.getTypeName()};
            String newPattern = MessageFormat.format(pattern, args);
            throw new IllegalArgumentException(newPattern);
        }
        // pfafstetter attribute
        int pAttrIndex = fT.indexOf(pfafAttr);
        if (pAttrIndex == -1) {
            String pattern = "Attribute {0} not found in layer {1}.";
            Object[] args = new Object[]{pfafAttr, fT.getTypeName()};
            String newPattern = MessageFormat.format(pattern, args);
            throw new IllegalArgumentException(newPattern);
        }
        // net start elevation attribute
        int startNetElevAttrIndex = -1;
        if (startelevAttr != null) {
            startNetElevAttrIndex = fT.indexOf(startelevAttr);
            if (startNetElevAttrIndex == -1) {
                String pattern = "Attribute {0} not found in layer {1}.";
                Object[] args = new Object[]{startelevAttr, fT.getTypeName()};
                String newPattern = MessageFormat.format(pattern, args);
                throw new IllegalArgumentException(newPattern.getClass().getSimpleName());
            }
        }
        // net end elevation attribute
        int endNetElevAttrIndex = -1;
        if (endelevAttr != null) {
            endNetElevAttrIndex = fT.indexOf(endelevAttr);
            if (endNetElevAttrIndex == -1) {
                String pattern = "Attribute {0} not found in layer {1}.";
                Object[] args = new Object[]{endelevAttr, fT.getTypeName()};
                String newPattern = MessageFormat.format(pattern, args);
                throw new IllegalArgumentException(newPattern);
            }
        }

        out.message("Analizing the network layer...");
        List<SimpleFeature> netFeaturesList = new ArrayList<SimpleFeature>();
        List<Integer> netIdsList = new ArrayList<Integer>();
        ArrayList<PfafstetterNumber> netPfaffsList = new ArrayList<PfafstetterNumber>();
        FeatureIterator<SimpleFeature> featureIterator = netFeatureCollection.features();
        PfafstetterNumber mostDownStreamPNumber = null;
        SimpleFeature mostDownStreamNetFeature = null;
        Integer mostDownStreamLinkId = -1;
        while( featureIterator.hasNext() ) {
            SimpleFeature f = (SimpleFeature) featureIterator.next();
            String attribute = (String) f.getAttribute(pAttrIndex);
            PfafstetterNumber current = new PfafstetterNumber(attribute);
            Integer tmpId = ((Number) f.getAttribute(lAttrIndex)).intValue();
            if (mostDownStreamPNumber == null) {
                mostDownStreamPNumber = current;
            } else {
                if (current.isDownStreamOf(mostDownStreamPNumber)) {
                    mostDownStreamLinkId = tmpId;
                    mostDownStreamNetFeature = f;
                    mostDownStreamPNumber = current;
                }
            }
            netFeaturesList.add(f);
            netIdsList.add(tmpId);
            netPfaffsList.add(current);
        }
        featureIterator.close();

        /*
         * search subbasins
         */
        out.message("Analyzing the hillslopes layer...");
        SimpleFeatureType ft = hillslopeFeatureCollection.getSchema();
        // netnum attribute on basins
        int linkAttrIndexInBasinLayerIndex = ft.indexOf(netnumAttr);
        if (linkAttrIndexInBasinLayerIndex == -1) {
            String pattern = "Attribute {0} not found in layer {1}.";
            Object[] args = new Object[]{netnumAttr, ft.getTypeName()};
            pattern = MessageFormat.format(pattern, args);
            throw new IllegalArgumentException(pattern);
        }

        // baricenter attribute
        int baricenterAttributeIndex = -1;
        if (baricenterAttr != null) {
            baricenterAttributeIndex = ft.indexOf(baricenterAttr);
            if (baricenterAttributeIndex == -1) {
                String pattern = "Attribute {0} not found in layer {1}.";
                Object[] args = new Object[]{baricenterAttr, ft.getTypeName()};
                pattern = MessageFormat.format(pattern, args);
                throw new IllegalArgumentException(pattern);
            }
        }

        List<SimpleFeature> hillslopeFeaturesList = new ArrayList<SimpleFeature>();
        List<Integer> hillslopeIdsList = new ArrayList<Integer>();
        FeatureIterator<SimpleFeature> hillslopeIterator = hillslopeFeatureCollection.features();
        SimpleFeature mostDownstreamHillslopeFeature = null;
        while( hillslopeIterator.hasNext() ) {
            SimpleFeature f = hillslopeIterator.next();
            Integer linkAttribute = ((Number) f.getAttribute(linkAttrIndexInBasinLayerIndex)).intValue();
            if (mostDownStreamLinkId == linkAttribute) {
                mostDownstreamHillslopeFeature = f;
            }
            hillslopeIdsList.add(linkAttribute);
            hillslopeFeaturesList.add(f);
        }
        /*
         * create all the hillslopes and connect them with their net feature and other hillslopes
         */
        out.message("Linking together network and hillslopes layers...");
        ArrayList<IHillSlope> hillslopeElements = new ArrayList<IHillSlope>();
        IHillSlope mostDownstreamHillslope = null;
        if (mostDownStreamPNumber.isEndPiece()) {
            Integer basinId = hillslopeIdsList.get(0);
            IHillSlope tmpHslp = new HillSlope(mostDownStreamNetFeature, mostDownstreamHillslopeFeature, mostDownStreamPNumber,
                    basinId.intValue(), baricenterAttributeIndex, startNetElevAttrIndex, endNetElevAttrIndex);
            hillslopeElements.add(tmpHslp);
            mostDownstreamHillslope = tmpHslp;
        } else {
            /*
             * almost there, now get from the basins list the ones with that netNums
             */
            ArrayList<SimpleFeature> selectedNetFeatureList = new ArrayList<SimpleFeature>();
            ArrayList<Integer> selectedNetId = new ArrayList<Integer>();
            for( int i = 0; i < hillslopeFeaturesList.size(); i++ ) {
                SimpleFeature basinFeature = hillslopeFeaturesList.get(i);
                Integer link = hillslopeIdsList.get(i);
                for( int j = 0; j < netFeaturesList.size(); j++ ) {
                    Integer netNum = netIdsList.get(j);
                    if (netNum.equals(link)) {
                        SimpleFeature netFeature = netFeaturesList.get(j);
                        IHillSlope tmpHslp = new HillSlope(netFeature, basinFeature, netPfaffsList.get(j), netNum.intValue(),
                                baricenterAttributeIndex, startNetElevAttrIndex, endNetElevAttrIndex);
                        hillslopeElements.add(tmpHslp);
                        selectedNetFeatureList.add(netFeature);
                        selectedNetId.add(netNum);
                        break;
                    }
                }
            }

            mostDownStreamPNumber = null;
            Integer mostDownStreamNetId = null;
            for( SimpleFeature feature : selectedNetFeatureList ) {
                String attribute = (String) feature.getAttribute(pAttrIndex);
                PfafstetterNumber current = new PfafstetterNumber(attribute);
                Integer tmpId = ((Number) feature.getAttribute(lAttrIndex)).intValue();
                if (mostDownStreamPNumber == null) {
                    mostDownStreamPNumber = current;
                } else {
                    if (current.isDownStreamOf(mostDownStreamPNumber)) {
                        mostDownStreamNetId = tmpId;
                        mostDownStreamPNumber = current;
                    }
                }
            }

            for( int i = 0; i < hillslopeElements.size(); i++ ) {
                Integer hId = hillslopeIdsList.get(i);
                if (hId.equals(mostDownStreamNetId)) {
                    mostDownstreamHillslope = hillslopeElements.get(i);
                    break;
                }
            }

        }

        if (mostDownstreamHillslope == null)
            throw new RuntimeException();
        HillSlope.connectElements(hillslopeElements);

        List<IHillSlope> orderedHillslopes = new ArrayList<IHillSlope>();
        mostDownstreamHillslope.getAllUpstreamElements(orderedHillslopes, null);

        return orderedHillslopes;

    }
}