/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DataLogReaderApp.java
 *
 * Created on Jan 26, 2011, 2:55:31 PM
 */

package datareader;

import Decoder.FreeEMSBin;
import GenericLog.GenericLog;
import Graphing.PlayableLog;
import java.awt.Dimension;

/**
 *
 * @author Bryan
 */
public class DataLogReaderApp extends javax.swing.JFrame {

    /** Creates new form DataLogReaderApp */
    public DataLogReaderApp() {
        initComponents();
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pl = new PlayableLog();
        playBar = new PlayBarPanel();
        
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        setLayout(new java.awt.BorderLayout());
        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        pl.setName("pl"); // NOI18N
        pl.setPreferredSize(new Dimension(600,400));
        pl.setLayout(new java.awt.FlowLayout());
        

        jPanel1.add(pl, java.awt.BorderLayout.CENTER);

        

        jPanel1.add(playBar, java.awt.BorderLayout.SOUTH);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText("File");
        jMenu1.setName("jMenu1"); // NOI18N

        openFileMenuItem.setText("Open Log");
        openFileMenuItem.setName("openlog"); // NOI18N
        openFileMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                openFileMenuItemMouseReleased(evt);
            }
        });
        jMenu1.add(openFileMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setName("jMenu2"); // NOI18N
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);
        pack();
    }
    private void openFileMenuItemMouseReleased( java.awt.event.MouseEvent evt) {

    }

    public void setLog(GenericLog genericLog) {
        pl.setLog(genericLog);
    }
    
    /**
     * Returns the reference to this instance, it is meant to be a method to make getting the main frame simpler
     * @return <code>this</code> instance
     */
    public static DataLogReaderApp getInstance() {
        return mainAppRef;
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               mainAppRef = new DataLogReaderApp();
                       mainAppRef.setVisible(true);
            }
        });
    }

    public PlayableLog getPlayableLog() {
        return pl;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static DataLogReaderApp mainAppRef;

    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JPanel jPanel1;
    private PlayableLog pl;
    private PlayBarPanel playBar;
    
    // End of variables declaration//GEN-END:variables
    private GenericLog genLog;
    private FreeEMSBin fems;
    //PlayableLog pl;
}
