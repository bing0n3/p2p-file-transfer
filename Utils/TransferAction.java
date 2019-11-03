package Utils;

import Handler.TCPSocketHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import model.TransferMsg;
import model.TransferMsg.TRANS_TYPE;

public class TransferAction implements ObjectAction {

  @Override
  public void doAction(Object obj, TCPSocketHandler handler) {
    String str = (String) obj;
    // parse message, transfer or file transfer
    TransferMsg msg = TransferMsg.ParseMsg(str);

    if (msg.getType().equals(TRANS_TYPE.T)) {
      System.out.println("Received Request message for file: " + msg.getStr() + " From " + handler
          .getRemoteAddress().getHostAddress());
      // start a socket and request
      if (Config.isContainFile(msg.getStr())) {
        byte[] fileContent;
        File file = new File(Config.filePath + msg.getStr());
        try {
          fileContent = Files.readAllBytes(file.toPath());
          handler.send(new String(fileContent));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
