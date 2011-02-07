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
package datareader;

import GenericLog.GenericDataElement;
import GenericLog.GenericLog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Bryan
 */
public class GraphMenu extends JMenu {

    private JCheckBoxMenuItem antiAliasing;
    private JMenuItem optionPaneItem;
    private OptionFrame optionFrame;
    private GenericLog gLogRef;


    public GraphMenu(String s, boolean b) {
        super(s, b);
    }

    public GraphMenu(String s) {
        super(s);
    }

    public GraphMenu() {
        super();
        initMenu();
    }

    private void initMenu() {
        this.setText("Graphing");
        this.setName("Graphing Menu");

        optionFrame = null;
        optionPaneItem = new JMenuItem();
        optionPaneItem.setText("Option Pane");
        optionPaneItem.setName("Option Pane");
        optionPaneItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                optionFrame.setVisible(true);
            }
        });
        antiAliasing = new JCheckBoxMenuItem();
        antiAliasing.setText("AntiAliasing");
        antiAliasing.setName("antialiasing");
        antiAliasing.setSelected(false);
        antiAliasing.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                DataLogReaderApp.getInstance().getDrawnGraph().setAntiAlising(j.isSelected());
            }
        });
        this.add(antiAliasing);

    }

    public void updateFromLog(GenericLog gl) {
        gLogRef = gl;
        if (optionFrame == null) {
            this.add(optionPaneItem);
            optionFrame = new OptionFrame("Option Pane");
            optionFrame.setPreferredSize(DataLogReaderApp.getInstance().getPreferredSize());
            optionFrame.setSize(optionFrame.getPreferredSize());
        } else {
            optionFrame.getHeaderPanel().removeAll();
            optionFrame.getActiveList().removeAllItems();
        }
        Iterator i = gl.keySet().iterator();
        String head = "";
        GCheckBox toBeAdded = null;
        while (i.hasNext()) {
            head = (String) i.next();
            GenericDataElement GDE = gl.get(head);
            toBeAdded = new GCheckBox();
            toBeAdded.setName(head);
            toBeAdded.setText(head);
            toBeAdded.setSelected(false);
            optionFrame.getHeaderPanel().add(toBeAdded);
        }
        optionFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        optionFrame.setVisible(true);
    }

    private class OptionFrame extends JFrame {
        private JPanel headerPanel;
        private JPanel optionPanel;
        private JComboBox activeList;
        public OptionFrame(String title) throws HeadlessException {
            super(title);
            headerPanel = new JPanel();
            optionPanel = new JPanel();
            activeList = new JComboBox();
            activeList.setEditable(false);
            activeList.setModel(new DefaultComboBoxModel());
            activeList.setSize(200, 30);
            headerPanel.setLayout(new GridLayout(10, 5));
            this.add(headerPanel, BorderLayout.CENTER);
            initOptionPanel();
            this.add(optionPanel, BorderLayout.EAST);
        }

        public OptionFrame() throws HeadlessException {
        }

        JLabel minLabel = new JLabel("Min: ");
        JLabel maxLabel = new JLabel("Max: ");
        JTextField maxField = new JTextField(10);
        JTextField minField = new JTextField(10);
        JButton setButton = new JButton("Commit");
        JButton changeColor = new JButton("Change Color");
        private void initOptionPanel() {
            optionPanel.setLayout(null);
            optionPanel.setSize(200, 500);
            optionPanel.setMinimumSize(new Dimension(200, 500));
            optionPanel.setPreferredSize(optionPanel.getMinimumSize());
            optionPanel.add(activeList);
            setButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if(!maxField.getText().equals("")) {
                        gLogRef.get((String)activeList.getSelectedItem()).setMaxValue(Double.parseDouble(maxField.getText()));
                    }
                    if(!minField.getText().equals("")) {
                        gLogRef.get((String)activeList.getSelectedItem()).setMinValue(Double.parseDouble(minField.getText()));
                    }
                }
            });
            changeColor.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color newColor = JColorChooser.showDialog(
                     optionFrame,
                     "Choose Background Color", Color.WHITE);
                    DataLogReaderApp.getInstance().getDrawnGraph().setColor(optionFrame.getActiveList().getSelectedIndex(), newColor);
                }
            });


            maxLabel.setBounds(0,30,100,30);
            optionPanel.add(maxLabel);
            maxField.setBounds(100,30,100,30);
            optionPanel.add(maxField);
            minLabel.setBounds(0,60,100,30);
            optionPanel.add(minLabel);
            minField.setBounds(100,60,100,30);
            optionPanel.add(minField);
            changeColor.setBounds(0,90,200,30);
            optionPanel.add(changeColor);
            setButton.setBounds(0,120, 200, 30);
            optionPanel.add(setButton);


        }

        public JPanel getHeaderPanel() {
            return headerPanel;
        }

        public void setHeaderPanel(JPanel headerPanel) {
            this.headerPanel = headerPanel;
        }

        public JPanel getOptionPanel() {
            return optionPanel;
        }

        public void setOptionPanel(JPanel optionPanel) {
            this.optionPanel = optionPanel;
        }

        public JComboBox getActiveList() {
            return activeList;
        }

        public void setActiveList(JComboBox activeList) {
            this.activeList = activeList;
        }
    }

    private class GCheckBox extends JCheckBox implements ActionListener {

        public GCheckBox() {
            super();
            addActionListener(this);

        }

        public void actionPerformed(ActionEvent e) {
            GCheckBox i = (GCheckBox) e.getSource();
            if (i.isSelected()) {
                optionFrame.getActiveList().addItem(i.getName());
                DataLogReaderApp.getInstance().getDrawnGraph().addActiveHeader(i.getName());
                DataLogReaderApp.getInstance().getDrawnGraph().reInitGraph();
            } else {
                optionFrame.getActiveList().removeItem(i.getName());
                if (DataLogReaderApp.getInstance().getDrawnGraph().removeActiveHeader(i.getName())) {
                    DataLogReaderApp.getInstance().getDrawnGraph().reInitGraph();
                }
            }
        }
    }
}
/**
 * 2/5/2011 meal for the night DO NOT EDIT MENU!
 * Sesame chicken alacarte
 * chicken lomein alacarte
 * orange chicken x2
 * 
 */
