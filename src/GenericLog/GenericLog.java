/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
