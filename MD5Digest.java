/*
Listing 2. One-Way Encryption The MessageDigest class 
can be used to perform one-way encryption. MessageDigests
 cannot be directly instantiated but are created by calling 
 the getInstance() factory method. Encryption is performed by passing 
 as an argument to the digest() method the raw bytes of a string.  

*/
import java.security.*;

/**
 * Prints to standard output the MD5 digest of its 
 * arguments, each on a separate line.
 */
public class MD5Digest {
   private MessageDigest __md5;
   private StringBuffer __digestBuffer;

   public MD5Digest() throws NoSuchAlgorithmException {
      __md5 = MessageDigest.getInstance("MD5");
      __digestBuffer = new StringBuffer();
   }

   public String md5crypt(String password) {
      int index;
      byte[] digest;

      __digestBuffer.setLength(0);
      digest = __md5.digest(password.getBytes());

      for(index = 0; index < digest.length; ++index)
         __digestBuffer.append(
            Integer.toHexString(digest[index] & 0xff));

      return __digestBuffer.toString();
   }

   public static final void main(String[] args) {
      MD5Digest md5;
      int argc;

      if(args.length < 1) {
         System.err.println("Usage: MD5Digest [password] ...");
         return;
      }

      try {
         md5 = new MD5Digest();
      } catch(NoSuchAlgorithmException e) {
         e.printStackTrace();
         return;
      }

      for(argc = 0; argc < args.length; ++argc)
         System.out.println(md5.md5crypt(args[argc]));
      
   }
}
 
