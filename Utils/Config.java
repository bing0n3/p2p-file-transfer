package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Config {

  public static int receiveTimeDelay = 5000;
  public static int keepAliveDelay = 300;
  public static String SharedfilePath = "shared/";
  public static String transPath = "./";
  private static String peer_config = "config_peer.txt";
  private static String host_config = "host.txt";
  private static String sharing_config = "config_sharing.txt";

  private static int udpPort = 0;
  private static int controlPort = 0;
  private static int filePort = 0;
  private static InetAddress localAddress = null;
  private static List<String> fileCollector = new ArrayList<>();

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

    // get file from config_sharing txt
    File sharingConfig = new File(sharing_config);
    try {
      BufferedReader in = new BufferedReader(new FileReader(sharingConfig));
      String line;
      while ((line = in.readLine()) != null) {
        fileCollector.add(line.trim());
      }
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

  public static boolean isContainFile(String filename) {
    return fileCollector.contains(filename);
  }

}