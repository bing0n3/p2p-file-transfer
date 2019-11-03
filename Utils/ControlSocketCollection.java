package Utils;

import Handler.TCPSocketHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ControlSocketCollection {

  private static Map<String, TCPSocketHandler> sockets = Collections
      .synchronizedMap(new HashMap<String, TCPSocketHandler>());


  public static void remove(String ip) {
    sockets.remove(ip);
  }

  public static void removeAll() {
    for (Entry<String, TCPSocketHandler> entry : sockets.entrySet()) {
      entry.getValue().close();
    }
    sockets = Collections.synchronizedMap(new HashMap<String, TCPSocketHandler>());
  }

  public static void put(String ip, TCPSocketHandler handler) {
    sockets.put(ip, handler);
  }

  public static TCPSocketHandler GetSocketHandlerByIP(String ip) {
    return sockets.get(ip);
  }

  public static boolean ContainsSocketHandler(String ip) {
    return sockets.containsKey(ip);
  }

  public static List<TCPSocketHandler> getSocketHandlers() {
    return new ArrayList<>(sockets.values());
  }
}
