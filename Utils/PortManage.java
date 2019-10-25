package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.Port;

public class PortManage {

  private int udpPort;
  // provide all avaliable port in the local machine
  private ArrayList<Integer> ports;


  public PortManage() {
    ports = new ArrayList<>();

    File file = new File("config_peer.txt");
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));

      // set the first line in config to udp port
      udpPort = Integer.parseInt(in.readLine());

      String line;
      while ((line = in.readLine()) != null) {
        ports.add(Integer.parseInt(line));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public ArrayList<Integer> getAvaliablePorts() {
    return ports;
  }

  // pop one avaliable port and remove it
  public Integer pop() {
    try {
      return ports.remove(0);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public Integer peek() {
    return ports.get(0);
  }

  // Push port into arraylist
  public void push(Integer port) {
    ports.add(port);
  }

  public int getUdpPort() {
    return udpPort;
  }

  public void setUdpPort(int udpPort) {
    this.udpPort = udpPort;
  }
}