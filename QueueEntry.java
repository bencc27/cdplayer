
/*
 * 
 */
public class QueueEntry{
  private short left;
  private short right; // left and right components of a sample
  
  /*
   * Constructor of class QueueEntry
   * @param left  A lest sample
   * @param right A right sample
   */
  public QueueEntry(short left, short right){
    this.left=left;
    this.right=right;
  }
  
  public short left(){
    return left;
  }
  
  public short right(){
    return right;
  }
  
  /*
   * Method used to verify if two QueueEntries have the same values in their samples.
   * @return equals if they have the same value
   */
  public boolean equals(QueueEntry copy){
    if((this.left==copy.left())&&(this.right==copy.right())){
      return true;
    } else{
      return false;
    }
  }
  
  public void print(){
    System.out.println("Left :"+ this.left +" Right :" +this.right);
  }
}