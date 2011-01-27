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
        playBar = new javax.swing.JPanel();
        reverseButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        fastForwardButton = new javax.swing.JButton();
        ejectButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
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

        playBar.setName("playBar"); // NOI18N
        playBar.setPreferredSize(new java.awt.Dimension(857, 40));
        playBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        reverseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_01.png"))); // NOI18N
        reverseButton.setAlignmentY(0.0F);
        reverseButton.setBorder(null);
        reverseButton.setBorderPainted(false);
        reverseButton.setContentAreaFilled(false);
        reverseButton.setName("reverseButton"); // NOI18N
        playBar.add(reverseButton);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_02.png"))); // NOI18N
        playButton.setAlignmentY(0.0F);
        playButton.setBorder(null);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setName("playButton"); // NOI18N
        playBar.add(playButton);

        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_03.png"))); // NOI18N
        pauseButton.setAlignmentY(0.0F);
        pauseButton.setBorder(null);
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setName("pauseButton"); // NOI18N
        playBar.add(pauseButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_04.png"))); // NOI18N
        stopButton.setAlignmentY(0.0F);
        stopButton.setBorder(null);
        stopButton.setBorderPainted(false);
        stopButton.setContentAreaFilled(false);
        stopButton.setName("stopButton"); // NOI18N
        playBar.add(stopButton);

        fastForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_05.png"))); // NOI18N
        fastForwardButton.setAlignmentY(0.0F);
        fastForwardButton.setBorder(null);
        fastForwardButton.setBorderPainted(false);
        fastForwardButton.setContentAreaFilled(false);
        fastForwardButton.setName("fastForwardButton"); // NOI18N
        playBar.add(fastForwardButton);

        ejectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/datareader/resources/Playbar_06.png"))); // NOI18N
        ejectButton.setAlignmentY(0.0F);
        ejectButton.setBorder(null);
        ejectButton.setBorderPainted(false);
        ejectButton.setContentAreaFilled(false);
        ejectButton.setName("ejectButton"); // NOI18N
        ejectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejectButtonMouseReleased(evt);
            }
        });
        playBar.add(ejectButton);

        jPanel1.add(playBar, java.awt.BorderLayout.SOUTH);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText("File");
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText("Woot");
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.setOpaque(true);
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setName("jMenu2"); // NOI18N
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);
        pack();
    }
    private void ejectButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ejectButtonMouseReleased
        FreeEMSBin fems = new FreeEMSBin("C:\\Users\\Bryan\\Desktop\\inputlog.bin");
        pl.setLog(fems.getGenericLog());
        System.out.println("logLoaded");
        pl.play();
    }//GEN-LAST:event_ejectButtonMouseReleased


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataLogReaderApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ejectButton;
    private javax.swing.JButton fastForwardButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton pauseButton;
    private PlayableLog pl;
    private javax.swing.JPanel playBar;
    private javax.swing.JButton playButton;
    private javax.swing.JButton reverseButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    private GenericLog genLog;
    //PlayableLog pl;
}
