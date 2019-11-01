package model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Peer {

  private InetAddress ip;
  private int udpPort;
  private int controlPort;
  private int filePort;
  private int id;

  public Peer(InetAddress ip, int udpPort) {
    this.ip = ip;
    this.udpPort = udpPort;
  }

  public Peer(InetAddress ip, int udpPort, int controlPort) {
    this(ip, udpPort);
    this.controlPort = controlPort;
  }

  public Peer(String ip, int udpPort) throws UnknownHostException {
    this.ip = InetAddress.getByName(ip);
    this.udpPort = udpPort;
  }

  public static Peer buildTCPPeer(InetAddress ip, int controlPort) {
    return new Peer(ip, 0, controlPort);
  }

  public InetAddress getIp() {
    return ip;
  }

  public void setIp(InetAddress ip) {
    this.ip = ip;
  }

  public void setIp(String ip) throws UnknownHostException {
    this.ip = InetAddress.getByName(ip);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUdpPort() {
    return udpPort;
  }

  public void setUdpPort(int udpPort) {
    this.udpPort = udpPort;
  }

  public int getControlPort() {
    return controlPort;
  }

  public void setControlPort(int controlPort) {
    this.controlPort = controlPort;
  }

  public int getFilePort() {
    return filePort;
  }

  public void setFilePort(int filePort) {
    this.filePort = filePort;
  }
}
