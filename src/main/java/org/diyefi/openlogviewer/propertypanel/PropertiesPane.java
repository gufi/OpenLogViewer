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
package org.diyefi.openlogviewer.propertypanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.diyefi.openlogviewer.OpenLogViewerApp;

public class PropertiesPane extends JFrame {
	private static final long serialVersionUID = -66677778898998591L;
	File OLVProperties;
	ArrayList<SingleProperty> properties;
	ArrayList<SingleProperty> removeProperties;
	JPanel propertyPanel;
	JPanel propertyView;

	public PropertiesPane(String title) {
		super(title);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setPreferredSize(new Dimension(350, 500));
		this.setSize(new Dimension(550, 500));
		this.setJMenuBar(createMenuBar());

		propertyPanel = new JPanel();
		propertyPanel.setLayout(new BorderLayout());
		propertyView = new JPanel();
		propertyView.setPreferredSize(new Dimension(400, 0));
		propertyView.setLayout(new FlowLayout(FlowLayout.LEFT));

		JScrollPane jsp = new JScrollPane(propertyView);
		propertyPanel.add(jsp, BorderLayout.CENTER);
		propertyPanel.add(createAcceptPanel(), BorderLayout.SOUTH);
		this.add(propertyPanel);
	}

	public void setProperties(ArrayList<SingleProperty> p) {
		removeProperties = new ArrayList<SingleProperty>();
		properties = p;
		setupForLoad();
	}

	private void setupForLoad() {
		try {
			String systemDelim = File.separator;
			File homeDir = new File(System.getProperty("user.home"));

			if (!homeDir.exists() || !homeDir.canRead() || !homeDir.canWrite()) {
				System.out.println("Iether you dont have a home director, or it isnt read/writeable... fix it");

			} else {
				OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + ".OpenLogViewer");
			}

			if (!OLVProperties.exists()) {
				try {
					if (OLVProperties.mkdir()) {
						OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + ".OpenLogViewer" + systemDelim + "OLVProperties.olv");
						if (OLVProperties.createNewFile()) {
							loadProperties();
						}
					} else {
						//find somewhere else
					}
				} catch (IOException IOE) {
					System.out.print(IOE.getMessage());
				}
			} else {
				OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + ".OpenLogViewer" + systemDelim + "OLVProperties.olv");
				loadProperties();
			}
		} catch (Exception E) {
			System.out.print(E.getMessage());
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar propMenuBar = new JMenuBar();

		JMenu optionMenu = new JMenu("Options");
		propMenuBar.add(optionMenu);
		JMenuItem addProp = new JMenuItem("Add New Property");
		JMenuItem remProp = new JMenuItem("Remove Selected Propertys");

		addProp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				String s = (String) JOptionPane.showInputDialog(rootPane, "Enter the header for a new property");
				if (!s.equals("") || (s != null)) { // TODO Bad need of stringUtils here...
					SingleProperty newprop = new SingleProperty();
					newprop.setHeader(s);
					addProperty(newprop);
				}
			}
		});

		remProp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				removePropertyPanels();
			}
		});

		optionMenu.add(addProp);
		optionMenu.add(remProp);

		return propMenuBar;
	}

	private JPanel createAcceptPanel() {
		JPanel aPanel = new JPanel();
		aPanel.setPreferredSize(new Dimension(500, 32));
		aPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));

		JButton okButton = new JButton("OK");
		JButton cancel = new JButton("Cancel");

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OpenLogViewerApp.getInstance().getPropertyPane().save();
				OpenLogViewerApp.getInstance().getPropertyPane().setVisible(false);
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				OpenLogViewerApp.getInstance().getPropertyPane().resetProperties();
				OpenLogViewerApp.getInstance().getPropertyPane().setVisible(false);
			}
		});

		aPanel.add(cancel);
		aPanel.add(okButton);

		return aPanel;
	}

	private void loadProperties() {
		try {
			Scanner scan = new Scanner(new FileReader(OLVProperties));

			while (scan.hasNext()) {
				String[] propLine = scan.nextLine().split("=");
				SingleProperty sp = new SingleProperty();
				String[] prop = propLine[1].split(",");
				sp.setHeader(propLine[0]);
				sp.setColor(new Color(
						Integer.parseInt(prop[0]),
						Integer.parseInt(prop[1]),
						Integer.parseInt(prop[2])));
				sp.setMin(Double.parseDouble(prop[3]));
				sp.setMax(Double.parseDouble(prop[4]));
				sp.setSplit(Integer.parseInt(prop[5]));
				sp.setActive(Boolean.parseBoolean(prop[6]));
				addProperty(sp);
			}

			scan.close();
		} catch (FileNotFoundException FNF) {
			System.out.print(FNF.toString());
			throw new RuntimeException(FNF);
		}
	}

	public void save() {
		try {
			removeProperties.clear();
			updateProperties();
			FileWriter fstream = new FileWriter(OLVProperties);
			BufferedWriter out = new BufferedWriter(fstream);

			for (int i = 0; i < properties.size(); i++) {
				out.write(properties.get(i).toString());
				out.newLine();
			}

			out.close();
		} catch (Exception e) { // Catch exception if any
			System.err.println("Error: " + e.getMessage());
			throw new RuntimeException("Catchall of type Exception is evil!", e);
		}
	}

	private void updateProperties() {
		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			pp.updateSP();
		}
	}

	public void resetProperties() {
		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			pp.reset();
		}
		if (removeProperties.size() > 0) {
			for (int i = 0; i < removeProperties.size(); i++) {
				addProperty(removeProperties.get(i));
			}
			removeProperties.clear();
		}
	}

	private PropertyPanel exists(SingleProperty sp) {

		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			if (pp.getSp().getHeader().equalsIgnoreCase(sp.getHeader())) {
				return pp;
			}
		}
		return null;
	}

	public void addProperty(SingleProperty sp) {
		PropertyPanel pp = exists(sp);
		if (pp == null) {
			properties.add(sp);
			Collections.sort(properties);
			propertyView.add(new PropertyPanel(sp), properties.indexOf(sp));
			propertyView.setPreferredSize(new Dimension(propertyView.getPreferredSize().width, propertyView.getPreferredSize().height + 60));
			propertyView.revalidate();
		} else {
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i).getHeader().equalsIgnoreCase(sp.getHeader())) {
					properties.set(i, sp);
				}
			}
			pp.setSp(sp);
			pp.reset();
		}
	}

	public void addPropertyAndSave(SingleProperty sp) {
		addProperty(sp);
		save();
	}

	private void removeProperty(SingleProperty sp) {
		if (properties.contains(sp)) {
			properties.remove(sp);
		}
	}

	private void removePropertyPanels() {
		for (int i = 0; i < propertyView.getComponentCount();) {
			PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			if (pp.getCheck().isSelected()) {
				if (!removeProperties.contains(pp.getSp())) {
					removeProperties.add(pp.getSp());
				}

				removeProperty(pp.getSp()); // Move this to add to a queue of things to remove, incase of cancel
				propertyView.remove(propertyView.getComponent(i));
				propertyView.setPreferredSize(new Dimension(propertyView.getPreferredSize().width, propertyView.getPreferredSize().height - 60));
				propertyView.revalidate();
			} else {
				i++;
			}
		}
		propertyView.repaint();
	}

	private class PropertyPanel extends JPanel implements Comparable<PropertyPanel> {
		private static final long serialVersionUID = 1L;
		SingleProperty sp;
		JCheckBox check;
		JPanel colorBox;
		JTextField minBox;
		JTextField maxBox;
		JTextField splitBox;
		JComboBox activeBox;

		public PropertyPanel(SingleProperty sp) {
			super();
			this.sp = sp;
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
			this.setBorder(BorderFactory.createTitledBorder(sp.getHeader()));
			setPreferredSize(new Dimension(500, 50));
			JLabel minLabel = new JLabel("Min:");
			JLabel maxLabel = new JLabel("Max:");
			JLabel colorLabel = new JLabel("Color:");
			JLabel splitLabel = new JLabel("Split:");
			JLabel activeLabel = new JLabel("Active:");
			splitBox = new JTextField();
			splitBox.setPreferredSize(new Dimension(15, 20));
			splitBox.setText(Integer.toString(sp.getSplit()));
			minBox = new JTextField();
			minBox.setPreferredSize(new Dimension(50, 20));
			minBox.setText(Double.toString(sp.getMin()));
			maxBox = new JTextField();
			maxBox.setPreferredSize(new Dimension(50, 20));
			maxBox.setText(Double.toString(sp.getMax()));
			colorBox = new JPanel();
			colorBox.setBackground(sp.getColor());
			colorBox.setPreferredSize(new Dimension(30, 20));
			String[] tf = {"False", "True"};
			activeBox = new JComboBox(tf);

			if (sp.isActive()) {
				activeBox.setSelectedIndex(1);
			}

			activeBox.setPreferredSize(new Dimension(60, 20));
			check = new JCheckBox();

			colorBox.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					Color newColor = JColorChooser.showDialog(
							OpenLogViewerApp.getInstance().getOptionFrame(),
							"Choose New Color", colorBox.getBackground());
					if (newColor != null) {
						colorBox.setBackground(newColor);

					}
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}
			});

			add(colorLabel);
			add(colorBox);
			add(minLabel);
			add(minBox);
			add(maxLabel);
			add(maxBox);
			add(splitLabel);
			add(splitBox);
			add(activeLabel);
			add(activeBox);
			add(check);
		}

		public JCheckBox getCheck() {
			return check;
		}

		public SingleProperty getSp() {
			return sp;
		}

		public void setSp(SingleProperty sp) {
			this.sp = sp;
		}

		public void updateSP() {
			sp.setMin(Double.parseDouble(minBox.getText()));
			sp.setMax(Double.parseDouble(maxBox.getText()));
			sp.setColor(colorBox.getBackground());
			sp.setSplit(Integer.parseInt(splitBox.getText()));
			String active = (String) activeBox.getSelectedItem();
			sp.setActive(Boolean.parseBoolean(active));
		}

		public void reset() {
			minBox.setText(Double.toString(sp.getMin()));
			maxBox.setText(Double.toString(sp.getMax()));
			colorBox.setBackground(sp.getColor());
			splitBox.setText(Integer.toString(sp.getSplit()));
			activeBox.setSelectedItem(Boolean.toString(sp.isActive()));
		}

		@Override
		public int compareTo(PropertyPanel pp) {
			return this.sp.getHeader().compareToIgnoreCase(pp.getSp().getHeader());
		}
	}
}
