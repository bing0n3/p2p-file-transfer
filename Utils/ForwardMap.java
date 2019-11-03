package Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ForwardMap {

  private static Map<String, String> forwardMap = Collections
      .synchronizedMap(new HashMap<String, String>());

  // return ip address
  public static String Get(String UUID) {
    return forwardMap.get(UUID);
  }


  public static boolean Contains(String UUID) {
    return forwardMap.containsKey(UUID);
  }

  public static void Put(String UUID, String ip) {
    forwardMap.put(UUID, ip);
  }
}
