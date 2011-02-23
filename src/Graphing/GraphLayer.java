/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
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
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class GraphLayer extends JPanel {

    private GenericDataElement GDE;
    private Color graphColor;
    private LinkedList<Double> drawnData;

    public GraphLayer() {
        this.setOpaque(false);
        this.setLayout(null);
        this.
        GDE = null;
        drawnData = new LinkedList<Double>();
        //Random r = new Random();
        //graphColor = Color.getHSBColor(r.nextFloat(), 1.0F, 1.0F);
    }

    public GraphLayer(GenericDataElement GDE) {
        this();
        this.GDE = GDE;
        initGraph();
    }

    public void paint(Graphics g) { // overridden paint because there will be no other painting other than this
      
        if(!this.getParent().getSize().equals(this.getSize())) {
            initGraph();
            this.setSize(this.getParent().getSize());
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
                g2d.drawLine(i, chartNumber((Double)dat.next(), d.height, GDE.getMinValue(), GDE.getMaxValue()), i+1, chartNumber((Double)dat2.next(), d.height, GDE.getMinValue(), GDE.getMaxValue()));
                i++;
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
        if(i < drawnData.size())return drawnData.get(i);
        else return 0.0;
    }

    public void setColor(Color c) {
        graphColor = c;
        repaint();
    }

    public Color getColor() {
        return GDE.getColor();
    }

    public void initGraph() {
        if (GDE != null) {
            LayeredGraph lg = (LayeredGraph) this.getParent();
            Dimension d = this.getSize();
            drawnData = new LinkedList<Double>();
            
            if (lg.getCurrent() < d.width / 2) {
                int x = 0;
                while (x < (d.width / 2) - lg.getCurrent()) {
                    drawnData.add(0.0);
                    x++;
                }
                int to = 0;
                if(GDE.size()-1 < d.width-x) to = GDE.size()-1;
                else to = d.width-x;
                drawnData.addAll(GDE.subList(0, to));
            } else if ((d.width / 2 + lg.getCurrent()) < GDE.size()) {
                drawnData.addAll(GDE.subList(lg.getCurrent() - d.width/2, d.width / 2 + lg.getCurrent()));
            } else {
                drawnData.addAll(GDE.subList(lg.getCurrent() - d.width / 2, GDE.size() - 1));
            }
        }
    }

    public void advanceGraph() {
        if (GDE != null) {
            LayeredGraph lg = (LayeredGraph) this.getParent();
            Dimension d = this.getSize();
            if ((lg.getCurrent() + d.width / 2) < GDE.size()) {
                drawnData.add(GDE.get(lg.getCurrent() + d.width / 2));
            }
            if (drawnData.size() > d.width / 2 + 1) {
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
}
