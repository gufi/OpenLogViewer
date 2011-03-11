/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenLogViewer.optionpane;

import GenericLog.GenericDataElement;
import GenericLog.GenericLog;
import OpenLogViewer.OpenLogViewerApp;
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
import java.util.Iterator;
import javax.swing.*;

/**
 *
 * @author Bryan
 */
public class OptionFrameV2 extends JFrame {

    JFrame thisRef;
    JPanel inactiveHeaders;
    JPanel infoPanel;
    JLabel headerLabel;
    JLabel minLabel;
    JLabel maxLabel;
    JButton addDivisionButton;
    JButton remDivisionButton;
    JTextField minField;
    JTextField maxField;
    JLayeredPane layeredPane;
    ArrayList<JPanel> activePanelList;

    public OptionFrameV2() {

        super("Graphing Option Pane");
        this.setSize(800, 400);
        this.setPreferredSize(this.getSize());
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        thisRef = this;
        activePanelList = new ArrayList<JPanel>();
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(799, 600));


        JScrollPane scroll = new JScrollPane(layeredPane);

        infoPanel = initInfoPanel();
        layeredPane.add(infoPanel);

        inactiveHeaders = initHeaderPanel();
        layeredPane.add(inactiveHeaders);

        this.add(scroll);

    }

    private JPanel initInfoPanel() {
        JPanel ip = new JPanel();
        ip.setName("InfoPanel");
        headerLabel = new JLabel("No Active Header");
        minLabel = new JLabel("Min:");
        maxLabel = new JLabel("Max:");
        minField = new JTextField(10);
        maxField = new JTextField(10);

        headerLabel.setBounds(0, 0, 200, 20);
        minLabel.setBounds(0, 20, 100, 20);
        minField.setBounds(100, 20, 100, 20);
        maxLabel.setBounds(0, 40, 100, 20);
        maxField.setBounds(100, 40, 100, 20);

        ip.setLayout(null);

        ip.add(headerLabel);
        ip.add(minLabel);
        ip.add(minField);
        ip.add(maxLabel);
        ip.add(maxField);
        ip.setBounds(0, 160, 200, 400);
        return ip;
    }

    private JPanel initHeaderPanel() {
        JPanel ih = new JPanel();
        ih.setLayout(null);
        ih.setName("Drop InactiveHeaderPanel");
        this.addDivisionButton = new JButton("Add Division");
        addDivisionButton.setBounds(0, 0, 120, 20);
        addDivisionButton.addActionListener(addDivisionListener);
        ih.add(addDivisionButton);
        ih.setBounds(0, 0, 800, 160);
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
                if (e.getChild() instanceof GCheckBox) {
                    ((GCheckBox) e.getChild()).setEnabled(true);
                    ((GCheckBox) e.getChild()).setSelected(true);
                    ((GCheckBox) e.getChild()).getGDE().setSplitNumber(
                            activePanelList.indexOf(
                            e.getChild().getParent()
                            )+1);
                }
                //System.out.println("Added " + e.getComponent().getName());
            }
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            if (e.getChild() != null) {
                if (e.getChild() instanceof GCheckBox) {
                    ((GCheckBox) e.getChild()).setEnabled(false);
                    ((GCheckBox) e.getChild()).setSelected(false);
                }
                for (int i = 0; i < e.getContainer().getComponentCount(); i++) {
                    if (e.getContainer().getComponent(i) instanceof GCheckBox) {
                        e.getContainer().getComponent(i).setLocation(0, i * 20);
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
            OpenLogViewerApp.getInstance().getLayeredGraph().setTotalSplits(activePanelList.size());
            activePanel.setLayout(null);
            activePanel.setName("Drop ActivePanel " + (activePanelList.indexOf(activePanel) + 1));
            activePanel.addContainerListener(addRemoveListener);

            activePanel.setBounds(220 + (col * 120), 160 + 120 * row, 120, 120);
            activePanel.setBackground(Color.DARK_GRAY);
            JButton removeButton = new JButton("Remove");
            removeButton.setToolTipText("Click Here to remove this division and associated Graphs");
            removeButton.setBounds(0, 0, 120, 20);
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
            if (panel.getComponent(i) instanceof GCheckBox) {
                GCheckBox GCB = (GCheckBox) panel.getComponent(i);
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
            activePanelList.get(i).setLocation(220 + (col * 120), 160 + 120 * row);
        }
        if (!addDivisionButton.isEnabled()) {
            addDivisionButton.setEnabled(true);
        }
        this.repaint();
    }
    MouseListener acceptRejectListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
        }//not used

        @Override
        public void mouseEntered(MouseEvent e) {
        }//not used

        @Override
        public void mouseExited(MouseEvent e) {
        }//not used

        @Override
        public void mousePressed(MouseEvent e) {
            GCheckBox GCB = (GCheckBox) e.getSource();
            GCB.setPreviousLocation(GCB.getLocation());
            GCB.setPreviousPanel((JPanel) GCB.getParent());
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            int x = 0;
            int y = 20;
            GCheckBox GCB = (GCheckBox) e.getSource();
            if (GCB.isDragging()) {
                if (GCB.getParent() == inactiveHeaders) { // moving back to inactive
                    GCB.setLocation(GCB.getInactiveLocation());
                    GCB.setSelected(false);
                } else { // moving to
                    boolean placed = false;
                    while (y < GCB.getParent().getHeight() && !placed) {
                        if (GCB.getParent().getComponentAt(x, y) == GCB.getParent() || GCB.getParent().getComponentAt(x, y) == GCB) {
                            GCB.setLocation(x, y);
                            placed = true;
                        }
                        y = y + 20;
                    }
                    if (!placed) {
                        if (GCB.getPreviousPanel() != GCB.getParent()) {
                            GCB.getPreviousPanel().add(GCB);
                        }
                        if (GCB.getPreviousPanel() == GCB.getInactivePanel()) {
                            GCB.setLocation(GCB.getInactiveLocation());
                            GCB.setEnabled(false);
                            GCB.setSelected(false);
                        } else {
                            GCB.setLocation(GCB.getPreviousLocation());
                        }
                        thisRef.repaint();
                    }
                }
                GCB.setDragging(false);
            }
        }
    };
    MouseMotionAdapter labelAdapter = new MouseMotionAdapter() {

        @Override
        public void mouseDragged(MouseEvent e) {
            //System.out.println(e.getModifiers() + " " + e.getButton());
            Component c = e.getComponent();
            GCheckBox GCB = (GCheckBox) c;
            GCB.setDragging(true);
            if (c.getParent() != null && layeredPane.getMousePosition() != null && e.getModifiers() == 4) {// 4 == right mouse button
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

    public void updateFromLog(GenericLog gl) {

        Iterator i = gl.keySet().iterator();
        String head = "";
        GCheckBox toBeAdded = null;
        int j = 0;
        int leftSide = 0;
        while (i.hasNext()) {
            head = (String) i.next();
            GenericDataElement GDE = gl.get(head);
            toBeAdded = new GCheckBox();

            toBeAdded.setName(head);
            toBeAdded.setText(head);
            toBeAdded.setRef(GDE);

            toBeAdded.setBounds(leftSide, (20 + (20 * j)),
                    (((20 + (head.length() * 8)) < 100) ? (32 + (head.length() * 7)) : 100)// this keeps the select boxes at a max of 120
                    , 20);
            toBeAdded.setEnabled(false);//you are unable to activate a graph in the inacivelist
            toBeAdded.addMouseMotionListener(labelAdapter);
            toBeAdded.addMouseListener(acceptRejectListener);
            if (checkForProperties(toBeAdded, GDE)) {
                toBeAdded.setBackground(GDE.getColor());
            }
            inactiveHeaders.add(toBeAdded);
            j++;
            if (20 + (20 * j) > inactiveHeaders.getHeight()) {
                j = 0;
                leftSide = toBeAdded.getX() + 100;
            }
        }

        this.repaint();
        this.setDefaultCloseOperation(JFrame.ICONIFIED);
        this.setVisible(true);
    }

    private boolean checkForProperties(GCheckBox GCB, GenericDataElement GDE) {
        for (int i = 0; i < OpenLogViewerApp.getInstance().getProperties().size(); i++) {
            if (OpenLogViewerApp.getInstance().getProperties().get(i).equals(GDE.getName())) {
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

    private class GCheckBox extends JCheckBox implements Comparable {

        private GenericDataElement GDE;
        private Point previousLocation;
        private Point inactiveLocation;
        private JPanel previousPanel;
        private JPanel inactivePanel;
        private boolean dragging;
        private ItemListener selectedListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                GCheckBox i = (GCheckBox) e.getSource();
                OptionFrameV2 of = OpenLogViewerApp.getInstance().getOptionFrame();
                if (state == ItemEvent.SELECTED) {
                    i.setForeground(GDE.getColor());
                    i.repaint();
                    OpenLogViewerApp.getInstance().getLayeredGraph().addGraph(i.getName());
                } else if (state == ItemEvent.DESELECTED) {
                    i.setForeground(null);
                    if (OpenLogViewerApp.getInstance().getLayeredGraph().removeGraph(i.getName())) {
                        OpenLogViewerApp.getInstance().getLayeredGraph().repaint();
                    }
                }
            }
        };

        public GCheckBox() {
            super();
            addItemListener(selectedListener);
            super.setOpaque(false);
            inactivePanel = inactiveHeaders;
            dragging = false;
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

        @Override
        public int compareTo(Object o) {
            if (o instanceof GCheckBox) {
                GCheckBox GCB = (GCheckBox) o;
                return this.GDE.compareTo(GCB.getGDE());
            } else {
                return -1;
            }
        }
    }

    
}
