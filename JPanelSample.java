import java.awt.*;
import javax.swing.JPanel;


/*
 * This class draws one frame for the display; it is imposible to draw all the samples,
 * so this class drawa 1 sample out of a number of samples depending of the period to refresh the display.
 * The constant SAMPLES refers to this number, so it's calculated with the period chosen in Display. 
 * As the average of sectors read in a second is 75, this number is used to calculate SAMPLES.
 * 
 */
public class JPanelSample extends JPanel
{
  private static final int SAMPLES=(int)((Display.PERIOD*0.075*588)/(221));
  
  private Queue queue;
  
  public JPanelSample(Queue queue){
    this.queue=queue;
  }
  
  
public void paintComponent( Graphics g )
{
  int amplitude=0;
  QueueEntry sample_one;
  QueueEntry sample_two;
  int zero=(int)(Display.AMPLITUDE/2);
super.paintComponent( g ); // llama el método paintComponent de la superclase

this.setOpaque(true);
this.setBackground( Color.WHITE );
this.setPreferredSize(new Dimension(Display.SAMPLE_FRAME, Display.AMPLITUDE));

g.setColor( Color.RED );
sample_one=queue.getEntry(SAMPLES);
if(sample_one==null){
    return;
  }
//System.out.println("Muestra: "+ sample_one.right());
for(int i=1; i<221;i++){
  sample_two=queue.getEntry(SAMPLES);
  if(sample_two==null){
    return;
  }
g.drawLine( i+0, (5*(int)sample_one.right())+ zero, i+1, (5*(int)sample_two.right())+zero);
sample_one=sample_two;
}

} // fin del método paintComponent
} // fin de la clase JPanelColor