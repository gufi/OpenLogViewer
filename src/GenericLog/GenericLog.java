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
import java.util.Set;

/**
 *
 * @author Bryan
 */
public class GenericLog extends HashMap {

    private String metaData;

    public GenericLog(String[] headers) {
        super();
        for (int x = 0; x < headers.length; x++) {
            this.put(headers[x], new ArrayList(100));
        }
        metaData = "";

    }

    public boolean addValue(String key, double value) {
        ArrayList logElement = (ArrayList) this.get(key);
        return logElement.add(value);
    }

    public void addMetaData(String md) {
        metaData = md;
    }

    public String getMetadata() {
        return metaData;
    }

    public void testLog() {
        Iterator i = this.keySet().iterator();
        ArrayList al;
        String head = "";
        while (i.hasNext()) {
            head = (String) i.next();
            al = (ArrayList) this.get(head);
            System.out.printf("%s10",head);
            for (int x = 0; x < al.size() - 1; x++) {
                System.out.printf("%10.3f ", al.get(x));
            }
            System.out.println();
        }
        System.out.print(this.metaData);
    }
}
