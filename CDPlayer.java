import javax.realtime.*;
import java.util.*;

/*
 * Class used to start all the threads necesary to read the samples.
 * @version 2
 * @author Juan Santos Martín
 */
public class  CDPlayer {
  public static void main(String [] args) {
    int time_simulation=10;
    int paint=0;
    if(args.length>0){
    time_simulation=Integer.parseInt(args[0]);
    if(args.length>1){
      time_simulation=2;
      paint=Integer.parseInt(args[1]);
    }
    }
    ArrayList list_disk=new ArrayList();//ArrayList used to verify all the samples are well copied
    ArrayList list_dac=new ArrayList();
    
    Queue queue=new Queue();  //Components to do the tasks of the specifications of the program
    Queue conv_queue=new Queue();
    DiskDriver disk=new DiskDriver();
    disk.speedInit((byte)0xa);
    DacDriver dac=new DacDriver();
    Simulator sim=new Simulator(disk,list_disk, time_simulation);
    
    // Build parameters for construction of DacWriting thread
    ReleaseParameters release =
      new PeriodicParameters(null,
          new RelativeTime(0, 22700),null,         // 22,7 us period
          new RelativeTime(0, 12700), null,null); 
    SchedulingParameters scheduling = new PriorityParameters(
        PriorityScheduler.getNormPriority(null)+1);

    DacWriting dac_writer= new DacWriting(scheduling, release, dac, queue,list_dac);
   
    ReleaseParameters release_disk =
      new SporadicParameters(
          new RelativeTime(10, 0)); // minimum time between two arrivals
    SchedulingParameters scheduling_disk = new PriorityParameters(
        PriorityScheduler.getNormPriority(null));
    DiskReading disk_reader=new DiskReading(scheduling_disk, release_disk, disk, queue);
    
    /////////////////DISPLAY THREADS ///////////////////////////////////////////////
     ReleaseParameters releasec =
      new PeriodicParameters(null,
          new RelativeTime(0, 22700),null,        
          new RelativeTime(0, 12700), null,null); 
    SchedulingParameters schedulingc = new PriorityParameters(
        PriorityScheduler.getNormPriority(null)+1);

    Converter converter= new Converter(schedulingc, releasec, dac, conv_queue);
    
    ReleaseParameters released =
      new PeriodicParameters(null,
          new RelativeTime(80, 0),null,
          new RelativeTime(80, 0), null,null); 
    SchedulingParameters schedulingd = new PriorityParameters(
        PriorityScheduler.getNormPriority(null)-2);

    Display display= new Display(schedulingd, released,conv_queue);
    ///////////////////////////////////////////////////////////////////////////////////
    
    //Starts the execution of the thread
    long start = System.currentTimeMillis();//Variable used to store the start of the program
    
    disk_reader.start();    // Start the threads
    sim.start();
    if(paint==2){
      converter.start();
      display.start();
    }
    dac_writer.start();
    try {
      dac_writer.join();  // Wait for the thread to end
    } catch (InterruptedException e) {
      
    //System.out.println(Integer.toString(dac_writer.num));
    }
    long end = System.currentTimeMillis();   //Calculate and print the time of the execution
    System.out.println("Execution time was "+(end-start)+" ms.");   
     System.out.println("Verifying values copied ...");
    Iterator disk_it = list_disk.iterator();
    Iterator audio_it = list_dac.iterator();
    int ok=0;
    int fail=0;
      while(disk_it.hasNext() && audio_it.hasNext()) {  //loop to compare all the samples stored in the lists
         QueueEntry disk_sample= (QueueEntry)disk_it.next();// and afterwards print the number of errors
         QueueEntry audio_sample= (QueueEntry)audio_it.next();//and the number of good copies
         //disk_sample.print();
         //audio_sample.print();
         if(disk_sample.equals(audio_sample)){
           ok++;
         } else{
         fail++;
         }
      }
    System.out.println("Samples well copied: "+ok);
    System.out.println("Samples badly copied: "+fail);
    System.out.println("Program finished");
    System.exit(0); //finish the program and all the threads
    //System.out.println("Final number of dac writing: "+Integer.toString(dac_writer.num));
  }
}