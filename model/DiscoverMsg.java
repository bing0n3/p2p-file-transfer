package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DiscoverMsg {

  public enum MSG_TYPE {
    PI(1),
    PO(2),
    FAIL(3);
    public final int value;

    MSG_TYPE(int value) {
      this.value = value;
    }
  }

  private MSG_TYPE type;
  private String IP;
  private int port;


  public DiscoverMsg(MSG_TYPE type) {
    this.type = type;
  }

  public DiscoverMsg(MSG_TYPE type, String IP, int port) {
    this.type = type;
    this.IP = IP;
    this.port = port;
  }

  static public DiscoverMsg ParseRecvMsg(String msg) {
    String pattern = "(PI|PO):(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";
    Pattern r = Pattern.compile(pattern);

    Matcher m = r.matcher(msg);

    if (m.find()) {
      if (m.group(1).equals("PI")) {
        return new DiscoverMsg(MSG_TYPE.PI, m.group(2), Integer.parseInt(m.group(3)));
      } else {
        return new DiscoverMsg(MSG_TYPE.PO, m.group(2), Integer.parseInt(m.group(3)));
      }
    } else {
      return new DiscoverMsg(MSG_TYPE.FAIL);
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.type == MSG_TYPE.PI) {
      sb.append("PI:");
    } else {
      sb.append("PO:");
    }
    sb.append(this.getIP());
    sb.append(":");
    sb.append(port);
    return sb.toString();
  }

  public String getIP() {
    return IP;
  }

  public void setIP(String IP) {
    this.IP = IP;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public MSG_TYPE getType() {
    return type;
  }

  public static void main(String[] args) {
    System.out.println(DiscoverMsg.ParseRecvMsg("PO:127.0.0.1:1234").toString());
  }

}

