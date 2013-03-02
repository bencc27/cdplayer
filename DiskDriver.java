import javax.realtime.*;

public class DiskDriver {

    static final int BUFFER_SIZE = 2352;
    static private final long BUFFER_ADDR_1 = 0x00385000L;
    static private final long BUFFER_ADDR_2 = 0x00386000L;

    RawMemoryAccess [] buffer = {
        new RawMemoryAccess(BUFFER_ADDR_1, BUFFER_SIZE),
        new RawMemoryAccess(BUFFER_ADDR_2, BUFFER_SIZE)
    };

    static private final long C_REGISTER_ADDR = 0x00387000L;
    static private final byte MIN_SPEED = 0;
    static private final byte MAX_SPEED = 0x0f;
    static private final byte INCREM=0x01;

    RawMemoryAccess speedRegister =
        new RawMemoryAccess(C_REGISTER_ADDR, 1);
    private byte speed;
    
    FullBuffer buffer_manager=new FullBuffer(); //object to comunicate hardware and software
                                                // when a buffer is full
    public short readSample(int selector, int index){
      return buffer[selector].getShort((long)index);
    }
    
    public void speedInit(byte sp){
      speed=sp;
      speedRegister.setByte((long)0,speed);
    }
    
    public void faster(){
      if(speed>=MAX_SPEED){
        speed=MAX_SPEED;
      }else{
        speed+=INCREM;
      }
      speedRegister.setByte(0,speed);
      //System.out.println(Integer.toString((int)speedRegister.getByte(0)));
    }
    
    public void slower(){
      if(speed==MIN_SPEED){
        speed=MIN_SPEED;
      }else{
        speed-=INCREM;
      }
      speedRegister.setByte(0,speed);
      //System.out.println(Integer.toString((int)speedRegister.getByte(0)));
    }
}

