import javax.realtime.*;


/*
 * Class that represents the queue to store samples before they are converted.
 * It's a round byte array, with two pointers, one to the position where the next sample should be written,
 * and other  with the position of the next sample to be pushed out.
 * @version 1
 * @author batman
 */
public class Queue {
  private final int QUEUE_SIZE=89376;
  private QueueEntry []queue= new QueueEntry[QUEUE_SIZE];
  private int index_ini=0;
  private int index_fin=0;
  private boolean alive=true;
  
  /*
   * Method to put a pair of samples in the queue.
   * It creates a new QueueEntry from the samples.
   * @param left  A lest sample
   * @param right A right sample
   */
  public void push(short left, short right){
    if(index_fin==(QUEUE_SIZE-1)){
      index_fin=0;
    }else{
      index_fin++;
    }
    queue[index_fin]=new QueueEntry(left,right);
  }

  /*
   * Method to put a given QueueEntry in the Queue
   * @param entry A QueueEntry to put in the queue.
   */
  public void push(QueueEntry entry){
    if(index_fin==(QUEUE_SIZE-1)){
      index_fin=0;
    }else{
      index_fin++;
    }
     queue[index_fin]=entry;
  }
  
  /*
   * A getter to get an entry of the queue.
   * Each entry can be got only once.
   * @return an entry
   */
  public QueueEntry getEntry(){
    if(size()==0){
      return null;
    }else{
    if(index_ini==(QUEUE_SIZE-1)){
      index_ini=0;
    }else{
      index_ini++;
    }
    return queue[index_ini];
   }
  }
  
  /*
   * A getter that returns an entry of the queue.
   * Instead of returning the next entry, it returns the 15th next entry,
   * and those that are before are just "erased". This method is used
   * in the class Display, as it is impossible to show all the samples
   * it just shows one sample in a number depending of how full is the queue.
   * @param num position of the next entry to be returned.
   * @return entry in the position given
   */
  public QueueEntry getEntry(int num){
      if(num==0){
        return null;
      }else  if(size()==0){
      return null;
    }else{
    if(index_ini==(QUEUE_SIZE-num)){
      index_ini=0;
    }else{
      index_ini+=num;
    }
    return queue[index_ini];
   }
  }
  
  /*
   * To get the current size of the queue, the number of the samples that are still to be read.
   * @return size of the queue.
   */
  public int size(){
    int size=index_fin-index_ini;
    if(size<0){
      size+=QUEUE_SIZE;
    }
    return size;
  }
  
  /*
   * Method to anonunce that the disk reading has stopped
   */
  public void die(){
    alive=false;
  }
  
  /*
   * Method to see if the disk reading cotinues
   * @return alive if the queue is still to be filled
   */
  public boolean isAlive(){
    return alive;
  }
}