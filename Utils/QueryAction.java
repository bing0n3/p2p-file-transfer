package Utils;

import Handler.P2PHandler;
import Handler.TCPSocketHandler;
import java.io.IOException;
import java.net.InetAddress;
import model.QueryMsg;
import model.QueryMsg.QUERY_MSG_TYPE;

public class QueryAction implements ObjectAction {

  @Override
  public void doAction(Object obj, TCPSocketHandler handler) {
    String str = (String) obj;
    try {
      QueryMsg msg = QueryMsg.ParseMsg(str);

      // prevent broadcast storm
      // if received before, do nothing
      if (MsgCollection.ContainsMsg(msg.toString())) {
        System.out.println("Ignore the duplicate message:\t" + msg.toString());
        return;
      } else {
        MsgCollection.putReceivedMsg(msg.toString());
      }

      System.out.println("\nReceived:" + msg.toString());

      if (msg.getType().equals(QUERY_MSG_TYPE.Q)) {
        if (Config.isContainFile(msg.getFileName())) {
          //find file response
          QueryMsg response = new QueryMsg(QUERY_MSG_TYPE.R, msg.getID(),
              Config.getLocalAddress().getHostName(),
              Config.getFilePort(),
              msg.getFileName());
          handler
              .send(response.toString());
          System.out.println("\nSend Message: " + response.toString());
        } else {
          // local don't have it, forward the message
          ForwardMap.Put(msg.getID(), handler.getRemoteAddress().getHostAddress());
          broadcast(msg.toString(), handler);
        }
      } else if (msg.getType().equals(QUERY_MSG_TYPE.R)) {
        // this server request file at first
        if (QueryFileCollection.contains(msg.getID())) {
          // create a direct p2p file transfer
          InetAddress ip = InetAddress.getByName(msg.getIP());
          // find the file, mark the filefound
          FileFound.fileFound.add(msg.getFileName());
          P2PHandler.requestForFile(ip, msg.getPort(), msg.getFileName());
        } else {
          // forward the message back to the first request peer
          String ip = ForwardMap.Get(msg.getID());
          TCPSocketHandler backSocket = ControlSocketCollection.GetSocketHandlerByIP(ip);
          // send back
          backSocket.send(msg.toString());
          System.out.println("\nSend Message: " + msg);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void broadcast(String msg, TCPSocketHandler handler) {
    for (TCPSocketHandler t : ControlSocketCollection.getSocketHandlers()) {
      if (t != handler) {
        t.send(msg);
        System.out.println("\nSend Message: " + msg);
      }
    }
  }
}
