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
package Graphing;

import GenericLog.GenericDataElement;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class GraphLayer extends JPanel {

    private GenericDataElement GDE;
    private LinkedList<Double> drawnData;
    private LayeredGraph.Zoom zoom;

    public GraphLayer() {
        this.setOpaque(false);
        this.setLayout(null);
        this.
        GDE = null;
        drawnData = new LinkedList<Double>();
        
    }

    public GraphLayer(GenericDataElement GDE) {
        this();
        this.GDE = GDE;
        initGraph();
    }

    public void paint(Graphics g) { // overridden paint because there will be no other painting other than this
      
        if(!this.getParent().getSize().equals(this.getSize())) {
            this.setSize(this.getParent().getSize());
            initGraph();
        }
        Dimension d = this.getSize();
        Graphics2D g2d = (Graphics2D) g;
        if (drawnData != null && drawnData.size() > 0) {
            g2d.setColor(GDE.getColor());
            Iterator dat = drawnData.iterator();// first number
            Iterator dat2 = drawnData.iterator();
            dat2.next();
            int i = 0;
              while(dat2.hasNext())  {
                  int a = chartNumber((Double)dat.next(), d.height, GDE.getMinValue(), GDE.getMaxValue());
                  int b = chartNumber((Double)dat2.next(), d.height, GDE.getMinValue(), GDE.getMaxValue());
                  if(zoom.getZoom() > 5) {
                      g2d.fillOval(i-2, a-2, 4, 4);
                  }
                g2d.drawLine(i, a, i+zoom.getZoom(), b);
                i+=zoom.getZoom();
              }
        }
    }

    private int chartNumber(Double elemData, int height, double minValue, double maxValue) {
        int point = 0;
        if (maxValue != minValue) {
            point = (int)(height - (height * ((elemData - minValue) / (maxValue - minValue))));
        }
        return point;
    }

    public void setData(GenericDataElement GDE) {
        this.GDE = GDE;
        initGraph();
    }

    public GenericDataElement getData() {
        return GDE;
    }

    public Double getMouseInfo(int i ) {
        LayeredGraph lg = (LayeredGraph)this.getParent();
        int getIt = (i/zoom.getZoom())+lg.getCurrent()-((this.getSize().width/2)/zoom.getZoom());
        if(getIt < GDE.size() && getIt > 0) return GDE.get(getIt);
        else return 0.0;
    }
    public Color getColor() {
        return GDE.getColor();
    }
    public void setColor(Color c) {
        GDE.setColor(c);
    }

    public void initGraph() {
        if (GDE != null) {
            LayeredGraph lg = (LayeredGraph) this.getParent();
            Dimension d = this.getSize();
            drawnData = new LinkedList<Double>();
            int zoomFactor = (d.width/2)/zoom.getZoom(); // add two datapoints to be drawn due to zoom clipping at the ends
            
            if (lg.getCurrent() < zoomFactor) {
                int x = 0;
                while (x <= (zoomFactor) - lg.getCurrent()) {
                    drawnData.add(0.0);
                    x++;
                }
                int to = 0;
                if(GDE.size()-1 < d.width-x) to = GDE.size()-1;
                else to = d.width-x;
                drawnData.addAll(GDE.subList(0, to));
            } else if ((zoomFactor + lg.getCurrent()+1) < GDE.size()) {
                drawnData.addAll(GDE.subList(lg.getCurrent() - zoomFactor-1, zoomFactor + lg.getCurrent()+1));
            } else {
                drawnData.addAll(GDE.subList(lg.getCurrent() - zoomFactor-1, GDE.size()));
            }
        }
    }

    public void advanceGraph() {

        if (GDE != null) {
            LayeredGraph lg = (LayeredGraph) this.getParent();
            Dimension d = this.getSize();
            int zoomFactor = (d.width/2)/zoom.getZoom();

            if ((lg.getCurrent() + zoomFactor) < GDE.size()) {
                drawnData.add(GDE.get(lg.getCurrent() + zoomFactor));
            }
            if (drawnData.size() > zoomFactor + 1) {
                drawnData.remove(0);
            } else {
                lg.stop();
            }
            //this.paintComponents(this.getGraphics());
        }
    }

    public int graphSize() {
        return GDE.size();
    }

    public void setZoom(LayeredGraph.Zoom z) {
        zoom = z;
    }

   


}
