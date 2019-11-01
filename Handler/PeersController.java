package Handler;

import data.Peer;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PeersController {

  private static Map<String, Peer> peers = Collections
      .synchronizedMap(new HashMap<String, Peer>());

  public static void addPeer(InetAddress ip, int port) {
    if (peers.containsKey(ip.getHostAddress())) {
      System.out.println("This IP address existed");
    } else {
      peers.put(ip.getHostAddress(), new Peer(ip, port));
    }
  }

  public static void addPeer(Peer peer) {
    if (peers.containsKey(peer.getIp().getHostAddress())) {
      System.out.println("This IP address existed");
    } else {
      peers.put(peer.getIp().getHostAddress(), peer);
    }
  }

  public static void addTCPPeer(InetAddress ip, int port) {
    if (peers.containsKey(ip.getHostAddress())) {
      System.out.println("This IP address existed");
    } else {
      peers.put(ip.getHostAddress(), Peer.buildTCPPeer(ip, port));
    }
  }

  public static void addPeer(InetAddress ip, int port, int controlPort) {
    if (peers.containsKey(ip.getHostAddress())) {
      System.out.println("This IP address existed");
    } else {
      peers.put(ip.getHostAddress(), new Peer(ip, port, controlPort));
    }
  }


  public static Peer getPeer(InetAddress ip) {
    return peers.get(ip.getHostAddress());
  }

  public static Peer getPeer(String ip) {
    return peers.get(ip);
  }

  public static ArrayList<Peer> getPeers() {
    return new ArrayList<>(peers.values());
  }

  public static ArrayList<Peer> getUDPPeers() {
    ArrayList<Peer> res = new ArrayList<>();
    for (Map.Entry entry : peers.entrySet()) {
      String key = (String) entry.getKey();
      Peer value = (Peer) entry.getValue();
      if (value.getUdpPort() != 0) {
        res.add(value);
      }
    }
    return res;
  }

  public static ArrayList<Peer> getTCPPeers() {
    ArrayList<Peer> res = new ArrayList<>();
    for (Map.Entry entry : peers.entrySet()) {
      String key = (String) entry.getKey();
      Peer value = (Peer) entry.getValue();
      if (value.getControlPort() != 0) {
        res.add(value);
      }
    }
    return res;
  }

  public static boolean Contains(InetAddress ip) {
    return peers.containsKey(ip.getHostAddress());
  }

  public static boolean Contains(String ip) {
    return peers.containsKey(ip);
  }

}
