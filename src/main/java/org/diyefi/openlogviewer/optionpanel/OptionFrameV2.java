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
package org.diyefi.openlogviewer.optionpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.propertypanel.SingleProperty;

/**
 *
 * @author Bryan Harris
 */
public class OptionFrameV2 extends JFrame {

    private JFrame thisRef;
    private JPanel inactiveHeaders;
    private ModifyGraphPane infoPanel;
    private JButton addDivisionButton;
    
    private JLayeredPane layeredPane;
    private ArrayList<JPanel> activePanelList;
    private static final int COMP_HEIGHT = 20;// every thing except panels are 20 px high; default 20
    private static final int COMP_WIDTH = 200; // used for buttons and such that are in; default 200
    private static final int PANEL_WIDTH = 140;// panels are 120 px wide buttons and labels are also; default 120
    private static final int PANEL_HEIGHT = 120;// panels are 120 px high;default 120

    @SuppressWarnings("LeakingThisInConstructor")
    public OptionFrameV2() {

        super("Graphing Option Pane");
        this.setSize(1160, 460);
        this.setPreferredSize(this.getSize());

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        thisRef = this;
        activePanelList = new ArrayList<JPanel>();
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(990, 400));


        JScrollPane scroll = new JScrollPane(layeredPane);

        inactiveHeaders = initHeaderPanel();
        layeredPane.add(inactiveHeaders);
        infoPanel = new ModifyGraphPane();
        this.add(infoPanel);

        this.add(scroll);
        addActiveHeaderPanel();

    }

    private JInternalFrame initInfoPanel() {
        JInternalFrame ip = new JInternalFrame();


        return ip;
    }

    private JPanel initHeaderPanel() {
        JPanel ih = new JPanel();
        ih.setLayout(null);
        ih.setName("Drop InactiveHeaderPanel");
        this.addDivisionButton = new JButton("Add Division");
        addDivisionButton.setBounds(0, 0, PANEL_WIDTH, COMP_HEIGHT);
        addDivisionButton.addActionListener(addDivisionListener);
        ih.add(addDivisionButton);
        ih.setBounds(0, 0, 1280, 180);
        return ih;
    }
    private ActionListener addDivisionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            addActiveHeaderPanel();
        }
    };
    private ActionListener remDivisionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            remActiveHeaderPanel(e);
        }
    };
    private ContainerListener addRemoveListener = new ContainerListener() {

        @Override
        public void componentAdded(ContainerEvent e) {

            if (e.getChild() != null) {
                if (e.getChild() instanceof ActiveHeaderLabel) {
                    ((ActiveHeaderLabel) e.getChild()).setEnabled(true);
                    ((ActiveHeaderLabel) e.getChild()).setSelected(true);
                    ((ActiveHeaderLabel) e.getChild()).getGDE().setSplitNumber(
                            activePanelList.indexOf(
                            e.getChild().getParent()) + 1);
                }
            }
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            if (e.getChild() != null) {
                if (e.getChild() instanceof ActiveHeaderLabel) {
                    ((ActiveHeaderLabel) e.getChild()).setEnabled(false);
                    ((ActiveHeaderLabel) e.getChild()).setSelected(false);
                }
                for (int i = 0; i < e.getContainer().getComponentCount(); i++) {
                    if (e.getContainer().getComponent(i) instanceof ActiveHeaderLabel) {
                        e.getContainer().getComponent(i).setLocation(0, i * COMP_HEIGHT);
                    }
                }
            }
        }
    };

    private void addActiveHeaderPanel() {

        if (activePanelList.size() < 8) {

            int row = activePanelList.size() / 4;
            int col = activePanelList.size() % 4;
            JPanel activePanel = new JPanel();
            activePanelList.add(activePanel);
            if (OpenLogViewerApp.getInstance() != null) {
                OpenLogViewerApp.getInstance().getLayeredGraph().setTotalSplits(activePanelList.size());
            }
            activePanel.setLayout(null);
            activePanel.setName("Drop ActivePanel " + (activePanelList.indexOf(activePanel) + 1));
            activePanel.addContainerListener(addRemoveListener);

            activePanel.setBounds((col * PANEL_WIDTH), inactiveHeaders.getHeight() + PANEL_HEIGHT * row, PANEL_WIDTH, PANEL_HEIGHT);
            activePanel.setBackground(Color.DARK_GRAY);
            JButton removeButton = new JButton("Remove");
            removeButton.setToolTipText("Click Here to remove this division and associated Graphs");
            removeButton.setBounds(0, 0, PANEL_WIDTH, COMP_HEIGHT);
            removeButton.addActionListener(remDivisionListener);
            activePanel.add(removeButton);
            layeredPane.add(activePanel);
            if (activePanelList.size() == 8) {
                addDivisionButton.setEnabled(false);
            } 
        }
    }

    private void remActiveHeaderPanel(ActionEvent e) {
        JPanel panel = (JPanel) ((JButton) e.getSource()).getParent();
        activePanelList.remove(panel);
        OpenLogViewerApp.getInstance().getLayeredGraph().setTotalSplits(activePanelList.size());
        for (int i = 0; i < panel.getComponentCount();) {
            if (panel.getComponent(i) instanceof ActiveHeaderLabel) {
                ActiveHeaderLabel GCB = (ActiveHeaderLabel) panel.getComponent(i);
                GCB.getInactivePanel().add(GCB);
                GCB.setLocation(GCB.getInactiveLocation());
                GCB.setSelected(false);
            } else if (panel.getComponent(i) instanceof JButton) {
                panel.remove(panel.getComponent(i));//removes the button
            } else {
                i++;
            }
        }

        panel.getParent().remove(panel);
        for (int i = 0; i < activePanelList.size(); i++) {
            int row = i / 4;
            int col = i % 4;
            activePanelList.get(i).setLocation((col * PANEL_WIDTH), inactiveHeaders.getHeight() + PANEL_HEIGHT * row);
        }
        if (!addDivisionButton.isEnabled()) {
            addDivisionButton.setEnabled(true);
        }
        ////////////////////////////////////////////////////////////////////Move this to events eventually,
        if (activePanelList.size() > 1) {
            for (int i = 0; i < activePanelList.size(); i++) {
                JPanel active = activePanelList.get(i);
                if (active.getComponentCount() > 1) {
                    for (int j = 0; j < active.getComponentCount(); j++) {
                        if (active.getComponent(j) instanceof ActiveHeaderLabel) {
                            ((ActiveHeaderLabel) active.getComponent(j)).getGDE().setSplitNumber(i + 1);
                        }
                    }
                }
            }
        }
        if(activePanelList.isEmpty()) addActiveHeaderPanel();
        this.repaint();
    }
    private MouseMotionAdapter labelAdapter = new MouseMotionAdapter() {

        @Override
        public void mouseDragged(MouseEvent e) {
            Component c = e.getComponent();
            ActiveHeaderLabel GCB = (ActiveHeaderLabel) c;
            GCB.setDragging(true);
            if (c.getParent() != null && layeredPane.getMousePosition() != null && (e.getModifiers() == 16)) {// 4 == right mouse button
                if (!c.getParent().contains(layeredPane.getMousePosition().x - c.getParent().getX(), layeredPane.getMousePosition().y - c.getParent().getY())) {
                    Component cn = c.getParent().getParent().getComponentAt(layeredPane.getMousePosition());
                    if (cn instanceof JPanel) {
                        JPanel j = (JPanel) cn;
                        if (j.getName().contains("Drop")) { // implement a better way to do this later
                            j.add(c);// components cannot share parents so it is automatically removed
                            c.setLocation( // reset the location to where the mouse is, otherwise first pixel when moving to the new jpanel
                                    // will cause a location issue reflecting where the panel was in the PREVIOUS panel
                                    layeredPane.getMousePosition().x - c.getParent().getX() - (c.getWidth() / 2),
                                    layeredPane.getMousePosition().y - c.getParent().getY() - (c.getHeight() / 2));
                        }
                    }
                } else {
                    c.setLocation(c.getX() + e.getX() - (c.getWidth() / 2), c.getY() + e.getY() - (c.getHeight() / 2));
                }
                thisRef.repaint();
            }
        }
    };

    private boolean place(ActiveHeaderLabel GCB) {
        int x = 0;
        int y = COMP_HEIGHT;
        while (y < GCB.getParent().getHeight()) {
            if (GCB.getParent().getComponentAt(x, y) == GCB.getParent() || GCB.getParent().getComponentAt(x, y) == GCB) {
                GCB.setLocation(x, y);
                return true;
            }
            y = y + COMP_HEIGHT;
        }
        return false;
    }

    public void updateFromLog(GenericLog gl) {

        while (activePanelList.size() > 0) {
            activePanelList.get(0).removeAll();
            layeredPane.remove(activePanelList.get(0));
            activePanelList.remove(activePanelList.get(0)); // only did it this way incase things are out of order at any point
        }

        addDivisionButton.setEnabled(true);

        if (inactiveHeaders.getComponentCount() > 1) {
            inactiveHeaders.removeAll();
            inactiveHeaders.add(this.addDivisionButton);
        }
        this.addActiveHeaderPanel(); // will be based on highest number of divisions found when properties are applied


        ArrayList<ActiveHeaderLabel> tmpList = new ArrayList<ActiveHeaderLabel>();
        Iterator i = gl.keySet().iterator();
        String head = "";
        ActiveHeaderLabel toBeAdded = null;

        while (i.hasNext()) {
            head = (String) i.next();
            GenericDataElement GDE = gl.get(head);
            toBeAdded = new ActiveHeaderLabel();

            toBeAdded.setName(head);
            toBeAdded.setText(head);
            toBeAdded.setRef(GDE);
            toBeAdded.setEnabled(false);//you are unable to activate a graph in the inacivelist
            toBeAdded.addMouseMotionListener(labelAdapter);
            if (checkForProperties(toBeAdded, GDE)) {
                toBeAdded.setBackground(GDE.getColor());
            }
            tmpList.add(toBeAdded);

        }
        Collections.sort(tmpList);
        int j = 0;
        int leftSide = 0;
        for (int it = 0; it < tmpList.size(); it++) {
            if (COMP_HEIGHT + (COMP_HEIGHT * (j + 1)) > inactiveHeaders.getHeight()) {
                j = 0;
                leftSide += PANEL_WIDTH;
            }
            tmpList.get(it).setBounds(leftSide, (COMP_HEIGHT + (COMP_HEIGHT * j)),
                    PANEL_WIDTH//(((COMP_HEIGHT + (head.length() * 8)) < 120) ? (32 + (head.length() * 7)) : 120)// this keeps the select boxes at a max of 120
                    , COMP_HEIGHT);
            inactiveHeaders.add(tmpList.get(it));

            j++;

        }

        this.repaint();
        this.setDefaultCloseOperation(JFrame.ICONIFIED);
        this.setVisible(true);
    }

    private boolean checkForProperties(ActiveHeaderLabel GCB, GenericDataElement GDE) {
        for (int i = 0; i < OpenLogViewerApp.getInstance().getProperties().size(); i++) {
            if (OpenLogViewerApp.getInstance().getProperties().get(i).getHeader().equals(GDE.getName())) {
                GDE.setColor(OpenLogViewerApp.getInstance().getProperties().get(i).getColor());
                GDE.setMaxValue(OpenLogViewerApp.getInstance().getProperties().get(i).getMax());
                GDE.setMinValue(OpenLogViewerApp.getInstance().getProperties().get(i).getMin());
                GDE.setSplitNumber(OpenLogViewerApp.getInstance().getProperties().get(i).getSplit());
                if (OpenLogViewerApp.getInstance().getProperties().get(i).isActive()) {
                    //GCB.setSelected(true);
                    return true;
                }
            }
        }
        return false;
    }

    private class ModifyGraphPane extends JInternalFrame {

        GenericDataElement GDE;
        ActiveHeaderLabel AHL;
        private JLabel minLabel;
        private JLabel maxLabel;
        private JTextField minField;
        private JTextField maxField;
        private JButton resetButton;
        private JButton applyButton;
        private JButton saveButton;
        private JButton colorButton;
        private ActionListener resetButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (GDE != null) {
                    GDE.reset();
                    minField.setText(Double.toString(GDE.getMinValue()));
                    maxField.setText(Double.toString(GDE.getMaxValue()));
                }
            }
        };
        private ActionListener applyButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (GDE != null) {
                    changeGDEValues();
                }
            }
        };
        private ActionListener saveButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (GDE != null) {
                    changeGDEValues();
                    OpenLogViewerApp.getInstance().getPropertyPane().addPropertyAndSave(new SingleProperty(GDE));
                }
            }
        };
        private ActionListener colorButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                        new JFrame(),
                        "Choose Background Color",
                        colorButton.getForeground());
                if (c != null) {
                    colorButton.setForeground(c);
                }
            }
        };

        public ModifyGraphPane() {
            this.setName("InfoPanel");
            minLabel = new JLabel("Min:");
            maxLabel = new JLabel("Max:");
            minField = new JTextField(10);
            maxField = new JTextField(10);
            resetButton = new JButton("Reset Min/Max");
            resetButton.addActionListener(resetButtonListener);

            applyButton = new JButton("Apply");
            applyButton.addActionListener(applyButtonListener);

            saveButton = new JButton("Save");
            saveButton.addActionListener(saveButtonListener);

            colorButton = new JButton("Color");
            colorButton.addActionListener(colorButtonListener);


            //X       Y       width   height
            minLabel.setBounds(0, 0, COMP_WIDTH / 2, COMP_HEIGHT);
            minField.setBounds(100, 0, COMP_WIDTH / 2, COMP_HEIGHT);
            maxLabel.setBounds(0, 20, COMP_WIDTH / 2, COMP_HEIGHT);
            maxField.setBounds(100, 20, COMP_WIDTH / 2, COMP_HEIGHT);
            colorButton.setBounds(0, 40, COMP_WIDTH, COMP_HEIGHT);
            applyButton.setBounds(0, 60, COMP_WIDTH / 2, COMP_HEIGHT);
            saveButton.setBounds(100, 60, COMP_WIDTH / 2, COMP_HEIGHT);
            resetButton.setBounds(0, 80, COMP_WIDTH, COMP_HEIGHT);

            this.setLayout(null);

            ///ip.add(headerLabel);
            this.add(minLabel);
            this.add(minField);
            this.add(maxLabel);
            this.add(maxField);
            this.add(colorButton);
            this.add(applyButton);
            this.add(saveButton);
            this.add(resetButton);
            this.setBounds(500, 180, 210, 133);
            this.setMaximizable(false);
            this.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            this.setClosable(true);
        }

        public void setGDE(GenericDataElement gde, ActiveHeaderLabel ahl) {
            this.GDE = gde;
            this.AHL = ahl;
            this.setTitle(GDE.getName());
            minField.setText(GDE.getMinValue().toString());
            maxField.setText(GDE.getMaxValue().toString());
            colorButton.setForeground(GDE.getColor());
        }

        private void changeGDEValues() {
            try {
                GDE.setMaxValue(Double.parseDouble(maxField.getText()));
            } catch (Exception ex) {
                //TO-DO: do something with Auto field
            }
            try {
                GDE.setMinValue(Double.parseDouble(minField.getText()));
            } catch (Exception ex) {
                //TO-DO: do something with Auto field
            }
            if (!GDE.getColor().equals(colorButton.getForeground())) {
                GDE.setColor(colorButton.getForeground());
                AHL.setForeground(colorButton.getForeground());
            }
        }
    }

    private class ActiveHeaderLabel extends JLabel implements Comparable {

        private GenericDataElement GDE;
        private Point previousLocation;
        private Point inactiveLocation;
        private JPanel previousPanel;
        private JPanel inactivePanel;
        private boolean dragging;
        private boolean selected;
        private MouseListener selectedListener = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getModifiers() == 16) {
                    setSelected(!selected);
                } else if (e.getModifiers() == 18) {
                    infoPanel.setGDE(GDE, (ActiveHeaderLabel) e.getSource());
                    if (!infoPanel.isVisible()) {
                        infoPanel.setVisible(true);

                    }
                }
                // System.out.println(e.getModifiers());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ActiveHeaderLabel GCB = (ActiveHeaderLabel) e.getSource();
                GCB.setPreviousLocation(GCB.getLocation());
                GCB.setPreviousPanel((JPanel) GCB.getParent());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ActiveHeaderLabel GCB = (ActiveHeaderLabel) e.getSource();
                if (GCB.isDragging()) {
                    if (GCB.getParent() == inactiveHeaders) { // moving back to inactive
                        GCB.setLocation(GCB.getInactiveLocation());
                        GCB.setSelected(false);
                        GCB.setEnabled(false);
                    } else { // moving to

                        if (!place(GCB)) {
                            if (GCB.getPreviousPanel() != GCB.getParent()) { // if it moved
                                GCB.getPreviousPanel().add(GCB);
                                place(GCB);
                            }
                            if (GCB.getPreviousPanel() == GCB.getInactivePanel()) {
                                GCB.setLocation(GCB.getInactiveLocation());
                                GCB.setEnabled(false);
                                GCB.setSelected(false);
                            } else {
                                place(GCB);
                            }
                            thisRef.repaint();
                        }
                    }
                    GCB.setDragging(false);
                }
            }
        };
        private ItemListener enabledListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        public ActiveHeaderLabel() {
            super();
            addMouseListener(selectedListener);
            super.setOpaque(false);
            inactivePanel = inactiveHeaders;
            dragging = false;
            selected = false;
            super.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.white));


        }

        @Override
        public void setBounds(int x, int y, int widht, int height) {
            super.setBounds(x, y, widht, height);
            if (inactiveLocation == null) {
                inactiveLocation = new Point(x, y);
            }
        }

        public void setRef(GenericDataElement GDE) {
            this.GDE = GDE;
            // this line is here because if the tool tip is never set no mouse events
            // will ever be created for tool tips
            this.setToolTipText();
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            this.setToolTipText();
            return getToolTipText();
        }

        public void setToolTipText() {
            this.setToolTipText("<HTML> Data Stream: </b>" + GDE.getName() 
		    + "</b><br>Min Value: <b>" + GDE.getMinValue()
                    + "</b><br>Max Value: <b>" + GDE.getMaxValue()
                    + "</b><br>Total Length: <b>" + GDE.size() + "</b> data points"
                    + "<br>To modify Min and Max values for scaling purposes Ctrl+LeftClick</HTML>");
        }

        public GenericDataElement getGDE() {
            return GDE;
        }

        public Point getPreviousLocation() {
            return previousLocation;
        }

        public void setPreviousLocation(Point previousLocation) {
            this.previousLocation = previousLocation;
        }

        public JPanel getPreviousPanel() {
            return previousPanel;
        }

        public void setPreviousPanel(JPanel previousPanel) {
            this.previousPanel = previousPanel;
        }

        public Point getInactiveLocation() {
            return inactiveLocation;
        }

        public JPanel getInactivePanel() {
            return inactivePanel;
        }

        public boolean isDragging() {
            return dragging;
        }

        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            if (this.isEnabled()) {
                this.selected = selected;
            } else {
                this.selected = false;
            }
            addRemGraph();
        }

        private void addRemGraph() {
            if (selected) {
                this.setForeground(GDE.getColor());
                this.repaint();
                OpenLogViewerApp.getInstance().getLayeredGraph().addGraph(this.getName());
            } else {
                this.setForeground(GDE.getColor().darker().darker());
                if (OpenLogViewerApp.getInstance().getLayeredGraph().removeGraph(this.getName())) {
                    OpenLogViewerApp.getInstance().getLayeredGraph().repaint();
                }
            }
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof ActiveHeaderLabel) {
                ActiveHeaderLabel GCB = (ActiveHeaderLabel) o;
                return this.GDE.compareTo(GCB.getGDE());
            } else {
                return -1;
            }
        }
    }
}
