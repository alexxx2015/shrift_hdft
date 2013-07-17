package de.tum.in.i22.pdp.cm.in;


/**
 * Used in {@link FastServiceHandler} to distinguish between methods.
 * See {@link FastServiceHandler} implementation.
 * @author Stoimenov
 *
 */
public enum EPmp2PdpMethod {
	DEPLOY_MECHANISM((byte)1),
	EXPORT_MECHANISM((byte)2),
	REVOKE_MECHANISM((byte)3);
	
    private final byte value;

    private EPmp2PdpMethod(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static EPmp2PdpMethod fromByte(byte b) {

        for(EPmp2PdpMethod t : values())
        {
            if(t.getValue() == b)
                return t;
        } 
        
        throw new RuntimeException("Byte value " + b + " not valid for EPmp2PdpMethod.");
    }
}