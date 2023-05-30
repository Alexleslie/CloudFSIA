import DataStructure.MetaTagD;

import java.security.MessageDigest;

public class HashLib {
   public static byte[] h1(MetaTagD metaTagD) {
      MessageDigest md = null;

      int index = metaTagD.getIndex();
      byte[] filename = metaTagD.getFilename();
      int state = metaTagD.getState();

      try {
         md = MessageDigest.getInstance("SHA-256");
      }catch (Exception e) {
         System.out.println(e);
      }

      md.update((byte) index);
      md.update(filename);
      md.update((byte) state);

      return md.digest();
   }


}
