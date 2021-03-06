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
package org.jgrasstools.gears.utils.chart;

import java.awt.image.BufferedImage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A simple scatter plotter.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class Scatter implements IChart {

    protected String xLabel = "X";
    protected String yLabel = "Y";
    protected XYSeriesCollection dataset;
    protected boolean xLog = false;
    protected boolean yLog = false;
    protected boolean showLines = true;
    protected boolean showShapes = true;
    protected JFreeChart chart;
    protected String title;

    public Scatter( String title ) {
        this.title = title;
        dataset = new XYSeriesCollection();
    }

    public Scatter() {
        this("Scatter");
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void addSeries( String seriesName, double[] x, double[] y ) {
        XYSeries series = new XYSeries(seriesName);
        for( int i = 0; i < x.length; i++ ) {
            series.add(x[i], y[i]);
        }
        dataset.addSeries(series);
    }

    public void setLogAxes( boolean xLog, boolean yLog ) {
        this.xLog = xLog;
        this.yLog = yLog;
    }

    public void setShowLines( boolean showLines ) {
        this.showLines = showLines;
    }

    public void setShowShapes( boolean showShapes ) {
        this.showShapes = showShapes;
    }

    public void setXLabel( String xLabel ) {
        this.xLabel = xLabel;
    }

    public void setYLabel( String yLabel ) {
        this.yLabel = yLabel;
    }

    public BufferedImage getImage( int width, int height ) {
        JFreeChart chart = getChart();
        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
        return bufferedImage;
    }

    public JFreeChart getChart() {
        if (chart == null) {
            chart = ChartFactory.createXYLineChart(title, // chart title
                    xLabel,
                    // domain axis label
                    yLabel,
                    // range axis label
                    dataset,
                    // data
                    PlotOrientation.VERTICAL,
                    // orientation
                    false,
                    // include legend
                    true,
                    // tooltips?
                    false
            // URLs?
                    );

            XYPlot plot = (XYPlot) chart.getPlot();
            if (xLog) {
                LogAxis xAxis = new LogAxis("");
                xAxis.setBase(10);
                plot.setDomainAxis(xAxis);
            }
            if (yLog) {
                LogAxis yAxis = new LogAxis("");
                yAxis.setBase(10);
                plot.setRangeAxis(yAxis);
            }

            ValueAxis rangeAxis = plot.getRangeAxis();
            if (rangeAxis instanceof NumberAxis) {
                NumberAxis axis = (NumberAxis) rangeAxis;
                axis.setAutoRangeIncludesZero(false);
            }

            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
            int seriesCount = plot.getSeriesCount();
            for( int i = 0; i < seriesCount; i++ ) {
                renderer.setSeriesShapesVisible(i, showShapes);
                renderer.setSeriesLinesVisible(i, showLines);
            }
        }
        return chart;
    }
    public void setXRange( double min, double max ) {
        XYPlot plot = (XYPlot) getChart().getPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(min, max);
    }

    public void setYRange( double min, double max ) {
        XYPlot plot = (XYPlot) getChart().getPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(min, max);
    }

    // public static void main( String[] args ) {
    // double[] asd = {1, 2};
    // double[] qwe = {1, 2};
    // Scatter scatter = new Scatter("");
    // scatter.addSeries("asd", asd, qwe);
    // scatter.setShowLines(false);
    // scatter.setXLabel("diameter");
    //
    // PlotFrame frame = new PlotFrame(scatter);
    // frame.plot();
    // }

}
