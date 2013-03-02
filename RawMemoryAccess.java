import java.math.*;

public class RawMemoryAccess {

    private long base;
    private int size;
    private byte [] vals;
    private double lastOffset = 0.0;

    public RawMemoryAccess(long b, int size) {
        base = b;
        size = size;
        vals = new byte[size];
    }

    public byte getByte(long offset) {
        return vals[(int)offset];
    }
    
    public short getShort(long offset) {
        short val;
        val = (short)((vals[(int)offset] << 8) + 
                      (vals[(int)offset + 1] & 0x0ff));
        return val;
    }
    
    public void setByte(long offset, byte v) {
        vals[(int)offset] = v;
    }
    
    public void setShort(long offset, short v) {
        vals[(int)offset] = (byte)((v & 0xff00) >> 8);
        vals[(int)offset + 1] = (byte)(v & 0x00ff);
    }
}
