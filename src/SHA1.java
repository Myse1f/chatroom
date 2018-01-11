import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA1 {
	public static final String KEY_SHA = "SHA";   

    public static String getResult(String inputStr)
    {
        BigInteger sha =null;
        System.out.println("Before encode:"+inputStr);
        byte[] inputData = inputStr.getBytes();
        
        try {
             MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);  
             messageDigest.update(inputData);
             sha = new BigInteger(messageDigest.digest());   
             System.out.println("After SHA-1:" + sha.toString(32));   
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return sha.toString(32);
    }
}
