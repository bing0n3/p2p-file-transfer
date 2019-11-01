package data;

import java.net.InetAddress;

public class TCPPacket {

  private byte[] data;
  private InetAddress ip;
  private int sourcePort;
  private int destPort;

  public TCPPacket(byte[] data, InetAddress ip, int sourcePort, int destPort) {
    this.data = data;
    this.ip = ip;
    this.sourcePort = sourcePort;
    this.destPort = destPort;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public InetAddress getIp() {
    return ip;
  }

  public void setIp(InetAddress ip) {
    this.ip = ip;
  }

  public int getSourcePort() {
    return sourcePort;
  }

  public void setSourcePort(int sourcePort) {
    this.sourcePort = sourcePort;
  }

  public int getDestPort() {
    return destPort;
  }

  public void setDestPort(int destPort) {
    this.destPort = destPort;
  }
}
