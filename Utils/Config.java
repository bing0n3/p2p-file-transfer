package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public class Config {

  private static int udpPort = 0;
  private static int controlPort = 0;
  private static int filePort = 0;
  private static InetAddress localAddress = null;

  public static void loadPortManage() {

    // parse config_peer file to get port for server
    File file = new File("config_peer.txt");
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      // set the first line in config to udp port
      udpPort = Integer.parseInt(in.readLine());
      controlPort = Integer.parseInt(in.readLine());
      filePort = Integer.parseInt(in.readLine());
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // get hostname from server
    File hostFile = new File("host.txt");
    try {
      BufferedReader in = new BufferedReader(new FileReader(hostFile));
      String hostname = in.readLine();
      localAddress = InetAddress.getByName(hostname);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int getUdpPort() {
    return udpPort;
  }

  public static int getControlPort() {
    return controlPort;
  }

  public static int getFilePort() {
    return filePort;
  }

  public static InetAddress getLocalAddress() {
    return localAddress;
  }
}