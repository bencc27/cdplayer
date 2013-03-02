/*
 * Class with methods synchronized to communicate 
 * the disk hardware reader with the software.
 * Method bufferFull is called only by Simulator;
 * when it has filled a buffer, it changes the value of selector
 * with the number of the las buffer filled, and notifies the DiskReading thread.
 * The method nextBuffer is called by DiskReading to get the number of the last filled buffer;
 * if there is no buffer filled yet, the thread is blocked until Simulator fills one. 
 */
public class FullBuffer {
  private static final int ALL_EMPTY=2;
  private int selector=ALL_EMPTY;
  
  public synchronized void bufferFull(int select){
    selector=select;
    notifyAll();
  }
  
  public synchronized int nextBuffer(){
    int select=ALL_EMPTY;
    while(selector==ALL_EMPTY){
      try{
      wait();
      }catch(InterruptedException ie){
      }
    }
    select=selector;
    selector=ALL_EMPTY;
    return select;
}
  
}