package Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MsgCollection {

  private static Set<String> receivedMsg = Collections.synchronizedSet(new HashSet<String>());

  public static boolean ContainsMsg(String msg) {
    return receivedMsg.contains(msg);
  }

  public static boolean putReceivedMsg(String msg) {
    return receivedMsg.add(msg);
  }


}
