/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datareader.optionpane;

import GenericLog.GenericDataElement;
import GenericLog.GenericLog;
import datareader.DataLogReaderApp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Bryan
 */
public class OptionFrame extends JFrame {

    private JPanel headerPanel;
    private JPanel optionPanel;
    private JComboBox activeList;
    JLabel minLabel = new JLabel("Min: ");
    JLabel maxLabel = new JLabel("Max: ");
    JTextField maxField = new JTextField(10);
    JTextField minField = new JTextField(10);
    JButton setButton = new JButton("Commit");
    JButton changeColor = new JButton("Change Color");
    ActionListener commitButtonActionListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            GenericDataElement GDE = (GenericDataElement) activeList.getSelectedItem();
            boolean somethingChanged = false;
            if (!maxField.getText().equals(minField.getText())) {
                if (!maxField.getText().equals("")) {
                    if (GDE != null) {
                        GDE.setMaxValue(Double.parseDouble(maxField.getText()));
                        somethingChanged = true;
                    }
                }
                if (!minField.getText().equals("")) {
                    if (GDE != null) {
                        GDE.setMinValue(Double.parseDouble(minField.getText()));
                        somethingChanged = true;
                    }
                }
                
            }
            if(!changeColor.getForeground().equals(GDE.getColor())) {
                Color newColor = Color.decode(changeColor.getForeground().toString());
                GDE.setColor(newColor);
                somethingChanged = true;
            }
            if(somethingChanged) DataLogReaderApp.getInstance().getLayeredGraph().repaint();
        }
    };
    ActionListener colorChangeListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            GenericDataElement GDE = (GenericDataElement) activeList.getSelectedItem();
            if (GDE != null) {
                Color newColor = JColorChooser.showDialog(
                        DataLogReaderApp.getInstance().getOptionFrame(),
                        "Choose Background Color", GDE.getColor());
                GDE.setColor(newColor);
                changeColor.setForeground(newColor);
                changeColor.repaint();
                //DataLogReaderApp.getInstance().getLayeredGraph().repaint();
            }
        }
    };
    ActionListener updateMinMax = new ActionListener() {
      public void actionPerformed(ActionEvent e){
          GenericDataElement GDE = (GenericDataElement) activeList.getSelectedItem();
          maxField.setText(GDE.getMaxValue().toString());
          minField.setText(GDE.getMinValue().toString());
          changeColor.setForeground(GDE.getColor());
      }
    };

    public OptionFrame(String title) throws HeadlessException {
        super(title);
        headerPanel = new JPanel();
        optionPanel = new JPanel();
        activeList = new JComboBox();
        activeList.setEditable(false);
        activeList.setModel(new DefaultComboBoxModel());
        activeList.setSize(200, 30);
        activeList.addActionListener(updateMinMax);
        headerPanel.setLayout(new GridLayout(10, 5));
        this.add(headerPanel, BorderLayout.CENTER);
        initOptionPanel();
        this.add(optionPanel, BorderLayout.EAST);
        this.setSize(new Dimension(800, 200));
    }

    private void initOptionPanel() {
        optionPanel.setLayout(null);
        optionPanel.setSize(200, 500);
        optionPanel.setMinimumSize(new Dimension(200, 500));
        optionPanel.setPreferredSize(optionPanel.getMinimumSize());
        optionPanel.add(activeList);
        setButton.addActionListener(commitButtonActionListener);
        changeColor.addActionListener(colorChangeListener);


        maxLabel.setBounds(0, 30, 100, 20);
        optionPanel.add(maxLabel);
        maxField.setBounds(100, 30, 100, 20);
        optionPanel.add(maxField);

        minLabel.setBounds(0, 50, 100, 20);
        optionPanel.add(minLabel);
        minField.setBounds(100, 50, 100, 20);
        optionPanel.add(minField);

        changeColor.setBounds(0, 70, 200, 20);
        optionPanel.add(changeColor);
        setButton.setBounds(0, 90, 200, 20);
        optionPanel.add(setButton);


    }

    public void updateFromLog(GenericLog gl) {


        this.getHeaderPanel().removeAll();
        this.getActiveList().removeAllItems();

        Iterator i = gl.keySet().iterator();
        String head = "";
        GCheckBox toBeAdded = null;
        while (i.hasNext()) {
            head = (String) i.next();
            GenericDataElement GDE = gl.get(head);
            toBeAdded = new GCheckBox();
            toBeAdded.setName(head);
            toBeAdded.setText(head);
            toBeAdded.setRef(GDE);
            toBeAdded.setSelected(false);
            this.getHeaderPanel().add(toBeAdded);
        }
        this.setDefaultCloseOperation(this.ICONIFIED);
        this.setVisible(true);
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

    private class GCheckBox extends JCheckBox implements ActionListener {

        GenericDataElement GDE;

        public GCheckBox() {
            super();
            addActionListener(this);
        }

        public void setRef(GenericDataElement GDE) {
            this.GDE = GDE;
        }

        public GenericDataElement getGDE() {
            return GDE;
        }

        public void actionPerformed(ActionEvent e) {
            GCheckBox i = (GCheckBox) e.getSource();
            OptionFrame of = DataLogReaderApp.getInstance().getOptionFrame();
            if (i.isSelected()) {
                of.getActiveList().addItem(GDE);
                of.getActiveList().setSelectedItem(GDE);

                DataLogReaderApp.getInstance().getLayeredGraph().addGraph(i.getName());
            } else {
                of.getActiveList().removeItem(GDE);
                if (DataLogReaderApp.getInstance().getLayeredGraph().removeGraph(i.getName())) {
                    DataLogReaderApp.getInstance().getLayeredGraph().repaint();
                }
            }
        }
    }
}
