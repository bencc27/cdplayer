import java.math.*;
import javax.realtime.*;
import java.util.*;

class Simulator extends RealtimeThread {

    private RawMemoryAccess buf[] = new RawMemoryAccess[2];
    private RawMemoryAccess ctl;
    private RelativeTime delay = new RelativeTime();
    static final int SAMPLES_PER_SECOND = 4;
    private static final int BYTES_PER_SAMPLE = 4;
    private static final int MS_PER_SECTOR =
        DiskDriver.BUFFER_SIZE * 1000 / 
        (SAMPLES_PER_SECOND * BYTES_PER_SAMPLE);
    private static String blanks = "                                                                                          ";
    
    /*
     * Variable added to control the disk speed
     * and the comunication with the software:
     */
    private FullBuffer buffer_manager;
    
    private static final int SECTORS_AVERAGE = 75; //Average of the sector that should be read in a second.
    private int SECONDS_OF_SIMULATION=10;    //TIME USED FOR THE SIMULATION
    
    
    private ArrayList list;                                                     
    
    Simulator(DiskDriver driver,ArrayList list, int simtime){
        super();
        buf[0] = driver.buffer[0];
        buf[1] = driver.buffer[1];
        ctl = driver.speedRegister;
        buffer_manager=driver.buffer_manager;//simulator takes de buffer_manager
        SECONDS_OF_SIMULATION=simtime;
        this.list=list;
    }
    
    /*
     * I hava tried to implement simulator as a periodic thread, using waitForNextPeriod instead of sleep,
     * but each time the period is changed, it's necesary to execute waitForNextPeriod right after, and the time 
     * the thread took for reading a sector varied very much, sometimes it took 3ms and others more than 13.
     * 
     * The period is calculated as the inverse of the number of sectors that should be read, 
     * and this is calculated with the value stored in the speed register.
     * If there is 0 in the speedRegister, the disk would stop,
     * and if its the register had 15, the disk would read 90.
     */
    
    public void run(){
        final int SECTOR_LIMIT = SECTORS_AVERAGE*SECONDS_OF_SIMULATION;
        long start=0;   // start and end are used to measure the delay when filling one buffer, to calculate afterwards
        long end=0;     // the period used to sleep without making it not bigger than it should be
        int sectors;
        int selector = 0;
        int time=13; //1000/75 ms for 75 sectors/second
        System.out.println("Seconds of simulation: " + SECONDS_OF_SIMULATION);
        for (int i = 0; i < SECTOR_LIMIT; ++i) {                    // parcours tous les secteurs  
          short left=0;
          short right =0;
          start=System.currentTimeMillis();         
          //if((int)(start-end)<time){
            //System.out.println("Time error");
            //System.exit(0);
          //}
         // System.out.println("Time calling Simulator "+(start-end));
            for (int j = 0; j < DiskDriver.BUFFER_SIZE / 4; ++j) {  // parcours valeurs dans un secteur
                long offset = j * 4;
                left= nextLeftValue();
                  right=nextRightValue();
                buf[selector].setShort(offset,left);
                buf[selector].setShort(offset + 2, right);
                list.add(new QueueEntry(left,right));
            }
            //System.out.println("Sim. Buffer " + i + " filled");
            buffer_manager.bufferFull(selector);
            try {
                end=System.currentTimeMillis();
                //System.out.println("Delay of simulator: "+ (end-start));   
                time=(int)((1000/((int)ctl.getByte((long)0)*6))-end+start);//Period calculated
                delay.set((int)time,0);
                //System.out.println(delay);
                if(time>0) {
                sleep(delay);
                }
            } catch (Exception ie  ) {
                //  ignore
            }
            selector = (selector == 0) ? 1 : 0;
        }
        buffer_manager.bufferFull(3); // make disk reader know the lecture is finished
        System.out.println("Disk stopped");
        System.out.println("Sectors read: "+ SECTOR_LIMIT);
    }

    private int lCt;
    private short nextLeftValue(){
        double v = mySin((double)lCt * Math.PI / 32);
        v += mySin((double)lCt * Math.PI / 16);
        ++lCt;

        return (short)(15 * v);
    }

    private int rCt;
    private short nextRightValue(){
        double v = myCos((double)rCt * Math.PI / 40);
        v += myCos((double)rCt * Math.PI / 10);
        ++rCt;

        return (short)(15 * v);
    }
    
    static void out(short l, short r){
        int blankCt = l + 30;
        System.out.print(Simulator.blanks.substring(0, blankCt));
        System.out.println("L");
        
        blankCt = r + 30;
        System.out.print(Simulator.blanks.substring(0, blankCt));
        System.out.println("R");
    }
    
    private double mySin(double v){
        int fullCycles = (int)(v / (2.0 * Math.PI));
        v -= fullCycles * 2.0 * Math.PI;

        return Math.sin(v);
    }

    private double myCos(double v){
        int fullCycles = (int)(v / (2.0 * Math.PI));
        v -= fullCycles * 2.0 * Math.PI;

        return Math.cos(v);
    }
}
