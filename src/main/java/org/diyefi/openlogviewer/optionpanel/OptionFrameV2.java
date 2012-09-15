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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.genericlog.GenericDataElement;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.propertypanel.SingleProperty;

public class OptionFrameV2 extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final int NUMBER_OF_COLS_OF_FREEEMS_FIELDS = 8; // Clearly a hack, but just to clarify and parameterise the existing math...
	private static final int HEIGHT_IN_FIELDS = 12;
	private static final int NUMBER_OF_ADD_BUTTONS = 1;

	private static final int WIDTH_OF_BOXES = NUMBER_OF_COLS_OF_FREEEMS_FIELDS;
	private static final int HEIGHT_OF_BOXES = 2;
	private static final int MAX_NUMBER_OF_BOXES = WIDTH_OF_BOXES * HEIGHT_OF_BOXES;


	private static final int WTF = 4;  // Don't ask me...
	private static final int WTF2 = 3; // No fucking idea AT ALL...

	private static final int COMP_HEIGHT = 20;     // every thing except panels are 20 px high; default 20
	private static final int COMP_WIDTH = 200;     // used for buttons and such that are in; default 200
	private static final int PANEL_WIDTH = 140;    // panels are 120 px wide buttons and labels are also; default 120
	private static final int PANEL_HEIGHT = 120;   // panels are 120 px high;default 120
	private static final int SCROLL_BAR_SIZE = 16; // Measured

	// Both are wrong for scroll bars... probably need an event to handle that?
	private static final int WIDTH_OF_WINDOW = (NUMBER_OF_COLS_OF_FREEEMS_FIELDS * PANEL_WIDTH);
	private static final int HEIGHT_OF_WINDOW = ((PANEL_HEIGHT * HEIGHT_OF_BOXES) + (COMP_HEIGHT * (HEIGHT_IN_FIELDS + NUMBER_OF_ADD_BUTTONS)));

	private static final char DS = DecimalFormatSymbols.getInstance().getDecimalSeparator();

	private final JFrame thisRef;
	private final JPanel inactiveHeaders;
	private final ModifyGraphPane infoPanel;
	private JButton addDivisionButton;

	private final JLayeredPane layeredPane;
	private final List<JPanel> activePanelList;

	public OptionFrameV2() {
		super("Graphing Option Pane");
		setSize(WIDTH_OF_WINDOW + WTF2, HEIGHT_OF_WINDOW + COMP_HEIGHT + SCROLL_BAR_SIZE + WTF); // why??? comp height, why??? just why???
		setPreferredSize(this.getSize());

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		thisRef = this;
		activePanelList = new ArrayList<JPanel>();
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(WIDTH_OF_WINDOW, HEIGHT_OF_WINDOW));
		OpenLogViewer.setupWindowKeyBindings(this);

		final JScrollPane scroll = new JScrollPane(layeredPane);

		inactiveHeaders = initHeaderPanel();
		layeredPane.add(inactiveHeaders);
		infoPanel = new ModifyGraphPane();
		add(infoPanel);

		this.add(scroll);
		addActiveHeaderPanel();
	}

	private JPanel initHeaderPanel() {
		final JPanel ih = new JPanel();
		ih.setLayout(null);
		ih.setName("Drop InactiveHeaderPanel");
		addDivisionButton = new JButton("Add Division");
		addDivisionButton.setBounds(0, 0, PANEL_WIDTH, COMP_HEIGHT);
		addDivisionButton.addActionListener(addDivisionListener);
		ih.add(addDivisionButton);
		ih.setBounds(0, 0, 1280, (COMP_HEIGHT * (HEIGHT_IN_FIELDS + NUMBER_OF_ADD_BUTTONS)));
		return ih;
	}

	private final ActionListener addDivisionListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			addActiveHeaderPanel();
		}
	};

	private final ActionListener remDivisionListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			remActiveHeaderPanel(e);
		}
	};

	private final ContainerListener addRemoveListener = new ContainerListener() {
		@Override
		public void componentAdded(final ContainerEvent e) {

			if (e.getChild() != null) {
				if (e.getChild() instanceof ActiveHeaderLabel) {
					((ActiveHeaderLabel) e.getChild()).setEnabled(true);
					((ActiveHeaderLabel) e.getChild()).setSelected(true);
					((ActiveHeaderLabel) e.getChild()).getGDE().setTrackIndex(activePanelList.indexOf(e.getChild().getParent()));
				}
			}
		}

		@Override
		public void componentRemoved(final ContainerEvent e) {
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
		if (activePanelList.size() < MAX_NUMBER_OF_BOXES) {
			final int row = activePanelList.size() / WIDTH_OF_BOXES;
			final int col = activePanelList.size() % WIDTH_OF_BOXES; // TODO this is duplicated code!!!! I found out because I got two behaviors at once...
			final JPanel activePanel = new JPanel();
			activePanelList.add(activePanel);
			if (OpenLogViewer.getInstance() != null) {
				OpenLogViewer.getInstance().getMultiGraphLayeredPane().setTrackCount(activePanelList.size());
			}
			activePanel.setLayout(null);
			activePanel.setName("Drop ActivePanel " + activePanelList.indexOf(activePanel));
			activePanel.addContainerListener(addRemoveListener);

			activePanel.setBounds((col * PANEL_WIDTH), inactiveHeaders.getHeight() + PANEL_HEIGHT * row, PANEL_WIDTH, PANEL_HEIGHT);
			activePanel.setBackground(Color.DARK_GRAY);
			final JButton removeButton = new JButton("Remove");
			removeButton.setToolTipText("Click Here to remove this division and associated Graphs");
			removeButton.setBounds(0, 0, PANEL_WIDTH, COMP_HEIGHT);
			removeButton.addActionListener(remDivisionListener);
			activePanel.add(removeButton);
			layeredPane.add(activePanel);

			if (activePanelList.size() == MAX_NUMBER_OF_BOXES) {
				addDivisionButton.setEnabled(false);
			}
		}
	}

	private void remActiveHeaderPanel(final ActionEvent e) {
		final JPanel panel = (JPanel) ((JButton) e.getSource()).getParent();
		activePanelList.remove(panel);
		OpenLogViewer.getInstance().getMultiGraphLayeredPane().setTrackCount(activePanelList.size());

		for (int i = 0; i < panel.getComponentCount();) {
			if (panel.getComponent(i) instanceof ActiveHeaderLabel) {
				final ActiveHeaderLabel GCB = (ActiveHeaderLabel) panel.getComponent(i);
				GCB.getInactivePanel().add(GCB);
				GCB.setLocation(GCB.getInactiveLocation());
				GCB.setSelected(false);
				GCB.getGDE().setDisplayColor(null);
			} else if (panel.getComponent(i) instanceof JButton) {
				panel.remove(panel.getComponent(i)); // removes the button
			} else {
				i++;
			}
		}

		panel.getParent().remove(panel);
		for (int i = 0; i < activePanelList.size(); i++) {
			final int row = i / WIDTH_OF_BOXES;
			final int col = i % WIDTH_OF_BOXES;
			activePanelList.get(i).setLocation((col * PANEL_WIDTH), inactiveHeaders.getHeight() + PANEL_HEIGHT * row);
		}

		if (!addDivisionButton.isEnabled()) {
			addDivisionButton.setEnabled(true);
		}

		// Move this to events eventually,
		for (int i = 0; i < activePanelList.size(); i++) {
			final JPanel active = activePanelList.get(i);
			if (active.getComponentCount() > 1) {
				for (int j = 0; j < active.getComponentCount(); j++) {
					if (active.getComponent(j) instanceof ActiveHeaderLabel) {
						((ActiveHeaderLabel) active.getComponent(j)).getGDE().setTrackIndex(i);
					}
				}
			}
		}

		if (activePanelList.isEmpty()) {
			addActiveHeaderPanel();
		}

		this.repaint();
	}

	private final MouseMotionAdapter labelAdapter = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(final MouseEvent e) {
			final Component c = e.getComponent();
			final ActiveHeaderLabel GCB = (ActiveHeaderLabel) c;
			GCB.setDragging(true);
			final Point pointNow = layeredPane.getMousePosition();
			final Component parent = c.getParent();
			if ((e.getModifiers() == 16) && (parent != null) && (pointNow != null)) { // 4 == right mouse button
				if (!parent.contains(pointNow.x - parent.getX(), pointNow.y - parent.getY())) {
					final Component parentsParent = parent.getParent();
					final Component cn = parentsParent.getComponentAt(pointNow);
					if (cn instanceof JPanel) {
						final JPanel j = (JPanel) cn;
						if (j.getName().contains("Drop")) { // implement a better way to do this later
							j.add(c); // components cannot share parents so it is automatically removed
							// reset the location to where the mouse is, otherwise first pixel when moving to the new jpanel
							c.setLocation(
									// will cause a location issue reflecting where the panel was in the PREVIOUS panel
									pointNow.x - parent.getX() - (c.getWidth() / 2),
									pointNow.y - parent.getY() - (c.getHeight() / 2));
						}
					}
				} else {
					c.setLocation(c.getX() + e.getX() - (c.getWidth() / 2), c.getY() + e.getY() - (c.getHeight() / 2));
				}
				thisRef.repaint();
			}
		}
	};

	private boolean place(final ActiveHeaderLabel GCB) {
		final int x = 0;
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

	public final void updateFromLog(final GenericLog datalog) {

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

		final List<ActiveHeaderLabel> tmpList = new ArrayList<ActiveHeaderLabel>();
		final Iterator<String> headers = datalog.keySet().iterator();
		String header = "";
		ActiveHeaderLabel toBeAdded = null;

		while (headers.hasNext()) {
			header = headers.next();
			final GenericDataElement GDE = datalog.get(header);
			toBeAdded = new ActiveHeaderLabel();

			toBeAdded.setName(header);
			toBeAdded.setText(header);
			toBeAdded.setRef(GDE);
			toBeAdded.setEnabled(false); // you are unable to activate a graph in the inacivelist
			toBeAdded.addMouseMotionListener(labelAdapter);
			if (checkForProperties(GDE)) {
				toBeAdded.setBackground(GDE.getDisplayColor());
			}
			tmpList.add(toBeAdded);
		}

		int j = 0;
		int leftSide = 0;
		for (int it = 0; it < tmpList.size(); it++) {
			if (COMP_HEIGHT + (COMP_HEIGHT * (j + 1)) > inactiveHeaders.getHeight()) {
				j = 0;
				leftSide += PANEL_WIDTH;
			}

			tmpList.get(it).setBounds(leftSide, (COMP_HEIGHT + (COMP_HEIGHT * j)), PANEL_WIDTH, COMP_HEIGHT);
			inactiveHeaders.add(tmpList.get(it));
			j++;
		}

		this.repaint();
		this.setDefaultCloseOperation(JFrame.ICONIFIED);
		OpenLogViewer.getInstance().exitFullScreen();
		this.setVisible(true);
	}

	private boolean checkForProperties(final GenericDataElement GDE) {
		for (int i = 0; i < OpenLogViewer.getInstance().getProperties().size(); i++) {
			if (OpenLogViewer.getInstance().getProperties().get(i).getHeader().equals(GDE.getName())) {
				GDE.setDisplayColor(OpenLogViewer.getInstance().getProperties().get(i).getColor());
				GDE.setDisplayMaxValue(OpenLogViewer.getInstance().getProperties().get(i).getMax());
				GDE.setDisplayMinValue(OpenLogViewer.getInstance().getProperties().get(i).getMin());
				GDE.setTrackIndex(OpenLogViewer.getInstance().getProperties().get(i).getTrackIndex());

				if (OpenLogViewer.getInstance().getProperties().get(i).isActive()) {
					return true;
				}
			}
		}
		return false;
	}

	private class ModifyGraphPane extends JInternalFrame {
		private static final long serialVersionUID = 1L;

		private GenericDataElement GDE;
		private ActiveHeaderLabel AHL;
		private final JLabel minLabel;
		private final JLabel maxLabel;
		private final JTextField minField;
		private final JTextField maxField;
		private final JButton resetButton;
		private final JButton applyButton;
		private final JButton saveButton;
		private final JButton colorButton;

		private final ActionListener resetButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (GDE != null) {
					GDE.reset();
					minField.setText(Double.toString(GDE.getDisplayMinValue()));
					maxField.setText(Double.toString(GDE.getDisplayMaxValue()));
				}
			}
		};

		private final ActionListener applyButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (GDE != null) {
					changeGDEValues();
				}
			}
		};

		private final ActionListener saveButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (GDE != null) {
					changeGDEValues();
					OpenLogViewer.getInstance().getPropertyPane().addPropertyAndSave(new SingleProperty(GDE));
				}
			}
		};

		private final ActionListener colorButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Color c = JColorChooser.showDialog(
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
			minLabel = new JLabel("Display Min:");
			maxLabel = new JLabel("Display Max:");
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

			minLabel.setBounds(0, 0, COMP_WIDTH / 2, COMP_HEIGHT);
			minField.setBounds(100, 0, COMP_WIDTH / 2, COMP_HEIGHT);
			maxLabel.setBounds(0, 20, COMP_WIDTH / 2, COMP_HEIGHT);
			maxField.setBounds(100, 20, COMP_WIDTH / 2, COMP_HEIGHT);
			colorButton.setBounds(0, 40, COMP_WIDTH, COMP_HEIGHT);
			applyButton.setBounds(0, 60, COMP_WIDTH / 2, COMP_HEIGHT);
			saveButton.setBounds(100, 60, COMP_WIDTH / 2, COMP_HEIGHT);
			resetButton.setBounds(0, 80, COMP_WIDTH, COMP_HEIGHT);

			this.setLayout(null);

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

		public void setGDE(final GenericDataElement gde, final ActiveHeaderLabel ahl) {
			this.GDE = gde;
			this.AHL = ahl;
			this.setTitle(GDE.getName());
			minField.setText(String.valueOf(GDE.getDisplayMinValue()));
			maxField.setText(String.valueOf(GDE.getDisplayMaxValue()));
			colorButton.setForeground(GDE.getDisplayColor());
		}

		private void changeGDEValues() {
			try {
				GDE.setDisplayMaxValue(Double.parseDouble(maxField.getText()));
			} catch (Exception ex) {
				throw new RuntimeException("TODO: do something with Auto field"); // TODO
			}

			try {
				GDE.setDisplayMinValue(Double.parseDouble(minField.getText()));
			} catch (Exception ex) {
				throw new RuntimeException("TODO: do something with Auto field"); // TODO
			}

			if (!GDE.getDisplayColor().equals(colorButton.getForeground())) {
				GDE.setDisplayColor(colorButton.getForeground());
				AHL.setForeground(colorButton.getForeground());
			}
		}
	}

	private class ActiveHeaderLabel extends JLabel implements Comparable<Object> {
		private static final long serialVersionUID = 1L;

		private GenericDataElement GDE;
		private Point inactiveLocation;
		private JPanel previousPanel;
		private final JPanel inactivePanel;
		private boolean dragging;
		private boolean selected;
		private final MouseListener selectedListener = new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getModifiers() == 16) {
					setSelected(!selected);
				} else if (e.getModifiers() == 18) {
					infoPanel.setGDE(GDE, (ActiveHeaderLabel) e.getSource());
					if (!infoPanel.isVisible()) {
						infoPanel.setVisible(true);
					}
				}
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				final ActiveHeaderLabel GCB = (ActiveHeaderLabel) e.getSource();
				GCB.setPreviousPanel((JPanel) GCB.getParent());
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				final ActiveHeaderLabel GCB = (ActiveHeaderLabel) e.getSource();
				if (GCB.isDragging()) {
					if (GCB.getParent() == inactiveHeaders) { // moving back to inactive
						GCB.setLocation(GCB.getInactiveLocation());
						GCB.setSelected(false);
						GCB.setEnabled(false);
						GCB.getGDE().setDisplayColor(null);
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

		public ActiveHeaderLabel() {
			addMouseListener(selectedListener);
			setOpaque(false);
			inactivePanel = inactiveHeaders;
			dragging = false;
			selected = false;
			setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.white));
		}

		@Override
		public void setBounds(final int x, final int y, final int width, final int height) {
			super.setBounds(x, y, width, height);
			if (inactiveLocation == null) {
				inactiveLocation = new Point(x, y);
			}
		}

		public void setRef(final GenericDataElement GDE) {
			this.GDE = GDE;
			// this line is here because if the tool tip is never set no mouse events
			// will ever be created for tool tips. TODO There HAS to be a better way to do this...
			this.setToolTipTextPreliminary();
//			this.setToolTipText("Please come again!"); // Didn't work, unsure why, this class is going bye bye anyway...
		}

		@Override
		public String getToolTipText(final MouseEvent e) {
			this.setToolTipTextFinal();
			return getToolTipText();
		}

		public void setToolTipTextFinal() {
			this.setToolTipText("<HTML> Data Stream: </b>" + GDE.getName()
					+ "</b><br>Min Value: <b>" + GDE.getMinValue()
					+ "</b><br>Min Display: <b>" + GDE.getDisplayMinValue()
					+ "</b><br>Max Value: <b>" + GDE.getMaxValue()
					+ "</b><br>Max Display: <b>" + GDE.getDisplayMaxValue()
					+ "<br>To modify Min and Max values for scaling purposes Ctrl+LeftClick</HTML>");
		}

		public void setToolTipTextPreliminary() {
			this.setToolTipText("<HTML> Data Stream: </b>" + GDE.getName()
					+ "</b><br>Min Value: <b> -" + DS + "-"
					+ "</b><br>Max Value: <b> -" + DS + "-"
					+ "</b><br>Min Display: <b> -" + DS + "-"
					+ "</b><br>Max Display: <b> -" + DS + "-"
					+ "<br>To modify Min and Max values for scaling purposes Ctrl+LeftClick</HTML>");
		}

		public GenericDataElement getGDE() {
			return GDE;
		}

		public JPanel getPreviousPanel() {
			return previousPanel;
		}

		public void setPreviousPanel(final JPanel previousPanel) {
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

		public void setDragging(final boolean dragging) {
			this.dragging = dragging;
		}

		public void setSelected(final boolean selected) {
			if (this.isEnabled()) {
				this.selected = selected;
			} else {
				this.selected = false;
			}
			addRemGraph();
		}

		private void addRemGraph() {
			if (selected) {
				OpenLogViewer.getInstance().getMultiGraphLayeredPane().addGraph(this.getName());
				this.setForeground(GDE.getDisplayColor());
				this.repaint();
			} else {
				OpenLogViewer.getInstance().getMultiGraphLayeredPane().removeGraph(this.getName());
				this.setForeground(GDE.getDisplayColor().darker().darker());
				this.repaint();
			}
		}

		@Override
		public int compareTo(final Object o) {
			if (o instanceof ActiveHeaderLabel) {
				final ActiveHeaderLabel GCB = (ActiveHeaderLabel) o;
				return this.GDE.compareTo(GCB.getGDE());
			} else {
				return -1;
			}
		}
	}
}
