import javax.realtime.*;

/*
 * Class used to control the acces to DAC registers
 */
public class DacDriver {

    private final long LEFT_DATA_REGISTER = 0L;
    private final long RIGHT_DATA_REGISTER = 0x0010L;
    private final long LEFT_CONTROL_REGISTER =  0x028L;
    private final long RIGHT_CONTROL_REGISTER = 0x038L;
    private final long BASE = 0x00384000L;
    private final byte LATCH = 0x01;
    private final int SIZE = 58;
    private RawMemoryAccess dac = 
        new RawMemoryAccess(BASE, SIZE);
    
    /*
     * Method to write the next sample to the data registers
     */
    public synchronized void write(short left, short right){
      dac.setShort(LEFT_DATA_REGISTER, left);
      dac.setByte(LEFT_CONTROL_REGISTER, LATCH);
      dac.setShort(RIGHT_DATA_REGISTER, right);
      dac.setByte(RIGHT_CONTROL_REGISTER, LATCH);
      notifyAll();
    }
    
    /*
     * Method used to read samples from de data registers
     * @return queue entry with the salue of the next sample
     */
    public synchronized QueueEntry transformSample(){
      while(((dac.getByte(RIGHT_CONTROL_REGISTER))!=LATCH)||((dac.getByte(LEFT_CONTROL_REGISTER))!=LATCH)){
         try{
      wait();
      }catch(InterruptedException ie){
      }
      }            
      dac.setByte(RIGHT_CONTROL_REGISTER, (byte)0); //Changes the value of control register to
      dac.setByte(LEFT_CONTROL_REGISTER, (byte)0);   // read the samples only once
            return new QueueEntry(dac.getShort(LEFT_DATA_REGISTER), dac.getShort(RIGHT_DATA_REGISTER));
    }
}
