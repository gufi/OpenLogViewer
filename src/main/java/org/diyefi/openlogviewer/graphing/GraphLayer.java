/* OpenLogViewer
 *
 * Copyright 2011
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package org.diyefi.openlogviewer.graphing;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JPanel;
import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;

/**
 *
 * @author Bryan
 */
public class GraphLayer extends JPanel implements HierarchyBoundsListener,PropertyChangeListener {

    private GenericDataElement GDE;
    private LinkedList<Double> drawnData;
    private LayeredGraph.Zoom zoom;
    private int nullData;

    public GraphLayer() {
        this.setOpaque(false);
        this.setLayout(null);
        this.GDE = null;
        drawnData = new LinkedList<Double>();
        this.nullData = 0;
    }

    private int getCurrent() {
        LayeredGraph lg = (LayeredGraph) this.getParent();
        return lg.getCurrent();
    }

    @Override
    public void ancestorMoved(HierarchyEvent e) {
    }

    @Override
    public void ancestorResized(HierarchyEvent e) {
        if (e.getID() == HierarchyEvent.ANCESTOR_RESIZED) {
            sizeGraph();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equalsIgnoreCase("Split")){
            sizeGraph();
        }
    }



    @Override
    public void paint(Graphics g) { // overridden paint because there will be no other painting other than this
        
        Dimension d = this.getSize();
        Graphics2D g2d = (Graphics2D) g;
        if (drawnData != null && drawnData.size() > 0) {
            g2d.setColor(GDE.getColor());
            Iterator dat = drawnData.iterator();
            int i = 0;
            Double chartNum = 0.0;
            try {
                chartNum = (Double) dat.next();
                int a = chartNumber(chartNum, (int)(d.height*0.95), GDE.getMinValue(), GDE.getMaxValue());
                while (dat.hasNext()) {


                    chartNum = (Double) dat.next();

                    int b = chartNumber(chartNum, (int)(d.height*0.95), GDE.getMinValue(), GDE.getMaxValue());
                    if (i >= nullData * zoom.getZoom()) {
                        if (zoom.getZoom() > 5) {
                            if(a != b){
                                g2d.fillOval(i -2, a-2, 4, 4);
                                g2d.fillOval(i +zoom.getZoom()-2, b-2, 4, 4);
                            }
                        }
                        g2d.drawLine(i, a, i + zoom.getZoom(), b);
                    }
                    a = b;
                    i += zoom.getZoom();
                }
            } catch (ConcurrentModificationException CME) {
                System.out.println(this.getClass().toString() + " " + CME.getMessage());
            }
        }
    }

    private int chartNumber(Double elemData, int height, double minValue, double maxValue) {

        int point = 0;
        if (maxValue != minValue) {
            point = (int) (height - (height * ((elemData - minValue) / (maxValue - minValue))));
        }
        return point;
    }

    public void setData(GenericDataElement GDE) {
        this.GDE = GDE;
        sizeGraph();
        initGraph();
    }

    public GenericDataElement getData() {
        return GDE;
    }

    public Double getMouseInfo(int i) {
        LayeredGraph lg = (LayeredGraph) this.getParent();
        int getIt = (i / zoom.getZoom()) + lg.getCurrent() - ((this.getSize().width / 2) / zoom.getZoom());
        if (getIt < GDE.size() && getIt >= 0) {
            return GDE.get(getIt);
        } else {
            return 0.0;
        }
    }

    public Color getColor() {
        return GDE.getColor();
    }

    public void setColor(Color c) {
        GDE.setColor(c);
    }

    public void initGraph() {
        if (GDE != null) {
            LayeredGraph lg = OpenLogViewerApp.getInstance().getLayeredGraph();
            Dimension d = this.getSize();
            drawnData = new LinkedList<Double>();
            int zoomFactor = ((d.width + zoom.getZoom()) / zoom.getZoom()) / 2; // add two datapoints to be drawn due to zoom clipping at the ends
            if ((double) d.width / 2 > zoom.getZoom() * (double) (zoomFactor)) {
                zoomFactor++;// without this certain zoom factors will cause data to be misdrawn on screen
            }
            if (lg.getCurrent() <= zoomFactor) {
                int x = 0;
                int fill = (zoomFactor) - lg.getCurrent();
                nullData = fill;
                while (x < fill) {
                    drawnData.add(0.0);
                    x++;
                }

                // this code looks hacky as fuck
                int to = 0;
                if (GDE.size() - 1 < (d.width / zoom.getZoom()) - x + 2) {// get the whole array because its stupid short
                    to = (GDE.size() - 1);
                } else { // get the first width-zoom-x
                    to = (d.width / zoom.getZoom()) - x + 2;
                }
                if(to >= 0 && to < GDE.size()){
                	drawnData.addAll(GDE.subList(0, to));
                }
            } else if ((zoomFactor + lg.getCurrent() + 2) < GDE.size()) {
                nullData = 0;
                drawnData.addAll(GDE.subList(lg.getCurrent() - zoomFactor, zoomFactor + 2 + lg.getCurrent()));
            } else {
                nullData = 0;
                drawnData.addAll(GDE.subList(lg.getCurrent() - zoomFactor, GDE.size()));
            }
        }
    }

    public void sizeGraph() {
        LayeredGraph lg = OpenLogViewerApp.getInstance().getLayeredGraph();
//        Dimension d = lg.getSize();
        int wherePixel = 0 ;
        if (lg.getTotalSplits() > 1) {
            if (GDE.getSplitNumber() <= lg.getTotalSplits()) {
                wherePixel += lg.getHeight() / lg.getTotalSplits() * GDE.getSplitNumber() - (lg.getHeight() / lg.getTotalSplits());
            } else {
                wherePixel += lg.getHeight() / lg.getTotalSplits() * lg.getTotalSplits() - (lg.getHeight() / lg.getTotalSplits());
            }
        }

        this.setBounds(0, wherePixel, lg.getWidth(), lg.getHeight() / (lg.getTotalSplits()));
        initGraph();
    }

    public void advanceGraph() {

        if (GDE != null) {
            LayeredGraph lg = (LayeredGraph) this.getParent();
            Dimension d = this.getSize();
            int zoomFactor = (d.width / 2) / zoom.getZoom();

            if ((lg.getCurrent() + zoomFactor) < GDE.size()) {
                drawnData.add(GDE.get(lg.getCurrent() + zoomFactor));
                if (nullData > 0) {
                    nullData--;
                }
            }
            if (drawnData.size() > zoomFactor + 1) {
                drawnData.remove(0);
            } else {
                lg.stop();
            }
        }
    }

    public int graphSize() {
        return GDE.size();
    }

    public void setZoom(LayeredGraph.Zoom z) {
        zoom = z;
    }
}
