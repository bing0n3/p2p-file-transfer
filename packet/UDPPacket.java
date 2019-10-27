package packet;

import java.net.InetAddress;

public class UDPPacket {

  private byte[] data;
  private InetAddress ip;
  private int port;

  /**
   * Create a new packet
   *
   * @param data The packet to send
   */
  public UDPPacket(byte[] data) {
    this.data = data;
  }

  /**
   * Create a new packet with simple information about the client
   *
   * @param data
   * @param ip
   * @param port
   */
  public UDPPacket(byte[] data, InetAddress ip, int port) {
    this.data = data;
    this.ip = ip;
    this.port = port;

  }

  /**
   * Get the packet packet
   *
   * @return the packet packet
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Get the ip
   *
   * @return the address of the receiver
   */
  public InetAddress getAddr() {
    return ip;
  }

  /**
   * Get the port
   *
   * @return the receivers port
   */
  public int getPort() {
    return port;
  }


  @Override
  public String toString() {
    return "Data: " + new String(this.data) + "\n From: " + this.getAddr() + ":" + this.getPort();
  }
}
