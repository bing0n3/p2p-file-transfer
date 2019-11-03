package Utils;

import Handler.TCPSocketHandler;

public interface ObjectAction {

  void doAction(Object obj, TCPSocketHandler handler);
}
