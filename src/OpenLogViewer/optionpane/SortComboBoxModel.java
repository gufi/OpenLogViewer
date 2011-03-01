/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenLogViewer.optionpane;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Owner
 */
public class SortComboBoxModel extends DefaultComboBoxModel{
	
	public SortComboBoxModel()
	{
		super();
	}


	@Override
	public void addElement(Object element)
	{
		insertElementAt(element, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertElementAt(Object element, int index)
	{
		int size = getSize();
		for (index = 0; index < size; index++)
		{
				Comparable c = (Comparable)getElementAt( index );
				if (c.compareTo(element) > 0)
					break;
		}
		super.insertElementAt(element, index);
	}


}
