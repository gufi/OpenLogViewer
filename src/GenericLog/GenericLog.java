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
package GenericLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Bryan
 */
public class GenericLog extends HashMap<String, ArrayList>  {

    private String metaData;
    /**
     * provide a <code>String</code> array of headers<br>
     * each header will be used as a HashMap key, the data related to each header will be added to an <code>ArrayList</code>.
     * @param headers - of the data to be converted
     */
    public GenericLog(String[] headers) {
        super();
        for (int x = 0; x < headers.length; x++) {
            this.put(headers[x], new ArrayList<Double>(100));
        }
        metaData = "";

    }
    /**
     * Add a piece of data to the <code>ArrayList</code> associated with the <code>key</code>
     * @param key - header
     * @param value - data to be added
     * @return true or false if it was successfully added
     */
    public boolean addValue(String key, double value) {
        ArrayList logElement = (ArrayList) this.get(key);
        return logElement.add(value);
    }

    /**
     * Add metadata This is information about the log being converted such as the location it was from or the date<br>
     * This method does not add to its self so in order to add more info you must VAR.addMetaData(VAR.getMetaData() + NEWINFO)
     * @param md
     */
    public void setMetaData(String md) {
        metaData = md;
    }
    /**
     *
     * @return String containing the current meta data
     */
    public String getMetadata() {
        return metaData;
    }
    /**
     * Test the log, this will output data to the console only
     */
    public void testLog() {
        Iterator i = this.keySet().iterator();
        ArrayList al;
        String head = "";
        while (i.hasNext()) {
            head = (String) i.next();
            al = (ArrayList) this.get(head);
            System.out.printf("%10s",head);
            for (int x = 0; x < al.size() - 1; x++) {
                System.out.printf("%10.3f ", al.get(x));
            }
            System.out.println();
        }
        System.out.print(this.metaData);
    }



}
