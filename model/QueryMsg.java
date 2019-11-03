package model;

import Utils.QueryFileCollection;
import java.net.UnknownHostException;
import java.util.UUID;

public class QueryMsg {

  public enum QUERY_MSG_TYPE {
    Q(1),
    R(2),
    FAIL(4);

    private int value;

    QUERY_MSG_TYPE(int value) {
      this.value = value;
    }
  }

  private QUERY_MSG_TYPE type;
  private String id;
  private String ip;
  private int port;
  private String fileName;

  public QueryMsg(QUERY_MSG_TYPE type, String fileName) {
    this.type = type;
    this.fileName = fileName;
    this.id = UUID.randomUUID().toString();
  }

  public QueryMsg(QUERY_MSG_TYPE type) {
    this.type = type;
  }

  public QueryMsg(QUERY_MSG_TYPE type, String uid, String fileName) {
    this.type = type;
    this.fileName = fileName;
    this.id = uid;
  }

  public QueryMsg(QUERY_MSG_TYPE type, String uid, String ip, int port, String fileName)
      throws UnknownHostException {
    this.type = type;
    this.fileName = fileName;
    this.id = uid;
    this.ip = ip;
    this.port = port;
  }

  public static QueryMsg buildQueryMsg(String fileName) {

    QueryMsg msg = new QueryMsg(QUERY_MSG_TYPE.Q, fileName);
    QueryFileCollection.put(msg.getID(), msg.fileName);
    return msg;
  }

  public static QueryMsg ParseMsg(String str) throws UnknownHostException {
    String dummy = str.substring(2);

    if (str.startsWith("Q:")) {
      String[] fields = dummy.split(";");
      return new QueryMsg(QUERY_MSG_TYPE.Q, fields[0], fields[1]);
    } else if (str.startsWith("R:")) {
      // 0 is uuid, 1 is ip address =, 2 is filename
      String[] fields = dummy.split(";");
      String[] address = fields[1].split(":");
      return new QueryMsg(QUERY_MSG_TYPE.R, fields[0], address[0],
          Integer.parseInt(address[1]), fields[2]);
    } else {
      return new QueryMsg(QUERY_MSG_TYPE.FAIL);
    }
  }

  public QUERY_MSG_TYPE getType() {
    return type;
  }

  public void setType(QUERY_MSG_TYPE type) {
    this.type = type;
  }

  public String getID() {
    return id;
  }

  public void setID(String id) {
    this.id = id;
  }

  public String getIP() {
    return ip;
  }

  public void setIP(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String toString() {
    if (type.equals(QUERY_MSG_TYPE.Q)) {
      return this.type + ":" + this.id.toString() + ";" + this.fileName;
    } else if (type.equals(QUERY_MSG_TYPE.R)) {
      return this.type + ":" + this.id.toString() + ";" + this.ip + ":" + this.port
          + ";" + this.fileName;
    } else {
      return "FAIL";
    }
  }

  // test
  public static void main(String[] args) {
    try {
      System.out
          .println(QueryMsg.ParseMsg("Q:58e0a7d7-eebc-11d8-9669-0800200c9a66;asdf").toString());
      System.out
          .println(QueryMsg.ParseMsg("R:58e0a7d7-eebc-11d8-9669-0800200c9a66;127.0.0.1:5000;asdf")
              .toString());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
}
