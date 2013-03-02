import javax.realtime.*;

/*
 * Class used to simulate the conversion of the samples once they have been written in the registers
 * of the DAC. This class just copies the samples to another queue to be used after by Display.
 * It uses DacDriver's lock to read samples from its data registers, waiting dacWritting to be executed.
 * @version 2
 */
public class Converter extends RealtimeThread {
  private Queue queue;
  private DacDriver dac;
  public Converter(SchedulingParameters sched, ReleaseParameters rel,DacDriver driver,Queue queue) {
      super(sched, rel);
      this.dac=driver;
      this.queue=queue;
    }

    public void run() {
      QueueEntry sample;
      while(true) {
        queue.push(dac.transformSample());
    }
  }
  
}