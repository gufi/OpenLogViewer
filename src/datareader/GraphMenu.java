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

import datareader.optionpane.OptionFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Bryan
 */
public class GraphMenu extends JMenu {

    private JCheckBoxMenuItem antiAliasing;
    private JMenuItem optionPaneItem;
    private OptionFrame optionFrame;

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


        optionPaneItem = new JMenuItem();
        optionPaneItem.setText("Option Pane");
        optionPaneItem.setName("Option Pane");
        optionPaneItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!DataLogReaderApp.getInstance().getOptionFrame().isVisible()) {
                    DataLogReaderApp.getInstance().getOptionFrame().setVisible(true);
                }
            }
        });
        this.add(optionPaneItem);

    }
}
/**
 * 2/5/2011 meal for the night DO NOT EDIT MENU!
 * Sesame chicken alacarte
 * chicken lomein alacarte
 * orange chicken x2
 * 
 */
