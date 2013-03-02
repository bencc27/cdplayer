import javax.realtime.*;

/*
 * This class copies the values from buffers to the queue, it's component A.
 * It works as a thread without a period of execution. First it tries to get a buffer to read,
 * if there is one it copies the samples to the queue, and if not, it sleeps until there is a buffer filled.
 * To do this, it uses class FullBuffer, which has a lock on the variable selector, that is used  by simulator 
 * to say to diskReading which is the last buffer that have been filled. As simulator and diskReading take over 3 ms
 * to do their job, and as simulator has a period of over 13 ms, DiskReading has the same priority 
 * as simulator, not to provoke any delay on the execution of simulator and because it has 13-3=10ms to copy a buffer,
 * when it usually does it in 3 ms, and with the possibility of using other 10 ms as simulator can use two buffers.
 * 
 * The variable list is just for debugging; lines of code that are just for debugging
 * may be commented to reduce the time of execution.
 * 
 * Another way of implementing this class is with AsynchronousEvents that are fired by Simulator each time
 * it finish reading a sector. I made also this implementation, but I found that this solution has more problems.
 * First, more code is needed to make this implementation, with more changes in Simulator,
 * and also it's easier to make errors with the code:
 * there would be variables common for simulation and DiskReading, the threads should be created with
 * a specified order, etc. Also, making tests, sometimes Simulator and DiskReading took more time than their period
 * to read a would sector, while they usually take over 3 ms to do it; so diskReading could be fired before he
 * finishes reading a sector, and using a lock this can not happen. The only problem now would be if DiskReading took
 * twice the time of simulator to read a buffer,but that is over 26ms.
 */
public class DiskReading extends RealtimeThread {
  private Queue queue;
  private DiskDriver driver;
  private static final int SIZE_LOW=26460;//45*entries in a buffer
  private static final int SIZE_BIG=35280;//60*entries in a buffer
  
  
  public DiskReading(SchedulingParameters sched,
        ReleaseParameters release, DiskDriver driver,Queue queue) {
      super(sched, release);
      this.driver=driver;
      this.queue=queue;
    }

    public void run() {
      long start=0;
      long end=0;
      driver.speedInit((byte)0x9);  // INITIAL SPEED 0X09
      while(true) {
       // System.out.println("nos quedamos en diskr");
        int selector=driver.buffer_manager.nextBuffer();
        if(selector<2){
          //System.out.println("copyig one buffer");
         // start=System.currentTimeMillis();
        for(int i=0; i<driver.BUFFER_SIZE;i+=4){
          queue.push(driver.readSample(selector, i), driver.readSample(selector, (i+2)));
        }
        if(queue.size()<SIZE_LOW){
          driver.faster();
        }
        if(queue.size()>SIZE_BIG){
          driver.slower();
        }
        //end=System.currentTimeMillis();
                //System.out.println("Delay in DiskReading of "+ (end-start));
        } else {
          //System.out.println("Queue die");
          queue.die();
          return;
        }
    }
  }
  
}