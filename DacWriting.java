import javax.realtime.*;
import java.util.*;

/*
 * This is component C, used to write the samples to the DAC converter every 22,7us.
 * It is implemented with a periodicThread of period 22,7 us, with  a priority bigger
 * than the rest of threads to ensure it makes its job ontime.
 * The variable num is just for debugging, to verify that all the samples
 * are copied.
 * The line of list.add(entry) may be commented because it introduces a big delay in the execution.
 * To verify that all the samples are well copied it should be uncommented.
 */
public class DacWriting extends RealtimeThread {
  private Queue queue;
  private DacDriver driver;
  private ArrayList list;
  //public int num=0;
  
  public DacWriting(SchedulingParameters sched,
        ReleaseParameters release, DacDriver driver,Queue queue, ArrayList list) {
      super(sched, release);
      this.driver=driver;
      this.queue=queue;
      this.list=list;
    }

    public void run() {
      QueueEntry entry;
      while(queue.isAlive() || (queue.size()>0)) {   
        entry=queue.getEntry();
        if(entry!=null){
          driver.write(entry.left(), entry.right());
          list.add(entry);
          //num++;
        }
       // System.out.println("Samples copied by dac "+num);
        //System.out.println("nos quedamos en dacw");
        waitForNextPeriod();        
    }
  }
  
}