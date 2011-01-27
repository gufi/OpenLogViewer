/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphing;

import GenericLog.GenericLog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

import javax.swing.JPanel;

/**
 *
 * @author Bryan
 */
public class PlayableLog extends JPanel implements ActionListener {



    Timer timer;
    GenericLog genLog;
    int current;
    boolean play;

    public PlayableLog() {
        super();
        genLog = null;
        timer = new Timer(10, this);
        timer.setInitialDelay(1000);
        timer.start();
        play = false;
        current = 0;
    }

    public void actionPerformed(ActionEvent e) {
        if(play) {
            current++;
            //if(current == 200) current = 0;
        }

        repaint();
    }

     public void paintComponent(Graphics g) {
         //super.paintComponents(g);


         Dimension d = this.getSize();
         Graphics2D g2d = (Graphics2D)g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.clearRect(0, 0, d.width, d.height);
         g2d.setBackground(Color.YELLOW);
         g2d.setColor(Color.BLACK);
         g2d.fillRect(0, 0, d.width,d.height );
         g2d.setColor(Color.BLUE);
         //g2d.drawOval(0, 0, 200+current, 150+current);
         if(genLog != null) {
             int[] xPoints = xAxis(d.width);
             int[] yPoints = yAxis("SP5", d);
             g2d.drawPolyline(xPoints, yPoints, d.width);
         }

     }

     private int[] xAxis(int dataNum) {
        int[] xAxis = new int[dataNum];
        int x = 0;
        while (x < xAxis.length) {
            xAxis[x] = x;
            x++;
        }
        return xAxis;
    }

     private int[] yAxis(String key, Dimension d) {
        int x = 0;
        ArrayList<Double> data = genLog.get(key);
        int[] yAxis = new int[d.width];
        while ((x < d.width) && (current + x < data.size() - 1)) {
            yAxis[x] = chartNumber(data.get(current + x), d.height);
            x++;
        }
        return yAxis;
    }

    private int chartNumber(Double elemData, int height) {
        int point = 0;
        if(elemData != 0) point = (int)(height / (elemData / height));
        return point;
    }

    public void setLog(GenericLog genLog) {
        this.genLog = genLog;
    }

    public void play() {
        play = true;
    }

    public void stop() {
        play = false;
    }


}






/*
    int current;
    GenericLog logData;
    boolean play;
    String header;
    Timer animate;
    public PlayableLog(GenericLog logData, String header) {
       // this.setMinimumSize(new Dimension(500,500));
       // this.setPreferredSize(new Dimension(500,500));
        this.header = header;
        this.logData = logData;
        this.play = false;
        this.current = 0;
        this.animate = new Timer(20,this);
        this.animate.setInitialDelay(1);
        this.animate.start();
        this.setMinimumSize(new Dimension(500,500));
        this.setPreferredSize(new Dimension(500,500));
        this.setSize(new Dimension(500,500));
        this.setOpaque(false);
        this.setVisible(true);
    }



    public void Play() {
        play = true;
    }

    public void Pause() {
        play = false;
    }

    public void Stop() {
        Pause();
        Reset();
    }

    public void Reset() {
        current = 0;
    }

    @Override
    public void update(Graphics g) {

    paint(g);


    }
    // draw a single graph




    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int gWindowX = (int) this.getSize().getWidth(); // get current Width of the jpanel, this may change as the screen is resized
        int gWindowY = (int) this.getSize().getHeight(); // same here but Height
        System.out.println(this.getGraphics() == null);



        Graphics2D g2d = (Graphics2D) this.getGraphics();
        //g2d.setXORMode(g2d.getBackground());



        //Graphics graph = g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLUE);
        int[] xAxisArray = yAxis(header, gWindowX);
        int[] yAxisArray = xAxis();
        g2d.drawPolyline(xAxisArray, yAxisArray, 30);
        g2d.drawLine(0, 0, 600, 600);
        //g2d.dispose();
        //g2d.
        //  for(int x =0; x < 30; x++){
        //   System.out.println(current + "\n" + xAxisArray[x] + " " + yAxisArray[x]);
        //  }


    }

    private int[] xAxis() {
        int[] xAxis = new int[30];
        int x = 0;
        while (x < xAxis.length) {
            xAxis[x] = x;
            x++;
        }
        return xAxis;
    }

    

    

    public void actionPerformed(ActionEvent e) {
       
        if (logData != null && play)  current++;
    }

    public void run() {
        System.out.println("Thread Running");
        try {
            while (true) {
                //  System.out.println("still running");
                
                if (logData != null && play) {
                    System.out.println(animate.isRunning());
                    if(!animate.isRunning()) animate.start();
                     this.getParent().repaint();
                    System.out.println(current);

                }
            }
        } catch (NullPointerException NPE) {
            System.out.println("NPE: " + NPE.toString());
        } catch (Exception e) {
            System.out.println("Thread Stopped Running :" + e.toString());
        }
    }

}
*/