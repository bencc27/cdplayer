import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.realtime.*;

/* This class creates a window with swing to show the samples.
 * It uses the same queue as converter to print the samples that have already been in data registers.
 * The window is divided in 6 JPanels not to print all the samples each time, and changes the position
 * of the frames when it prints new samples.
 * The displays shows just right samples.
 */
public class Display extends RealtimeThread {
  
  public static final int SAMPLE_FRAME=221;
  public static final int FRAMES=6;
  public static final int AMPLITUDE=400;
  public static final int WIDTH=FRAMES*SAMPLE_FRAME+5;
  public static int PERIOD=80;
  private static JPanelSample []samples= new JPanelSample[6];
  private static JFrame frame;
  private static Queue queue;
  
  
  public Display(SchedulingParameters sched, ReleaseParameters rel,Queue queue){
    super(sched, rel);
    this.queue=queue;
  }
                   
    private static void initDisplay() {
        //Create and set up the window.
        frame= new JFrame("Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    
        frame.setLayout(new GridLayout());
        //Create panels and put them in the content pane.
        for(int i=0; i<6;i++){
        samples[i] = new JPanelSample(queue);
        }
        for(int i=0; i<6;i++){
        frame.getContentPane().add(samples[i]);
        }
        

        
        frame.setPreferredSize(new Dimension(WIDTH, AMPLITUDE));
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void run() {
        initDisplay();
        int i=0;
        while(true){
        try{
        waitForNextPeriod();//sleep for 1000 ms
        }catch(Exception e){};
        frame.remove(samples[i]);
        samples[i]=new JPanelSample(queue);
        for(int j=(i+1); j<=(i+6);j++){ 
        frame.getContentPane().add(samples[j%6]);
        }
        
        frame.pack();
        frame.setVisible(true);
        }
    }
}
