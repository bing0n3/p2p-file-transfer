package model;

import Utils.ControlSocketCollection;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Peers {

  private static Peer initialPeer = null;
  private static Map<String, model.Peer> peers = Collections
      .synchronizedMap(new HashMap<String, Peer>());


  public static Peer getInitialPeer() {
    return initialPeer;
  }

  public static void setPeers(Map<String, Peer> peers) {
    Peers.peers = peers;
  }

  public static void setInitialPeer(Peer initialPeer) {
    Peers.initialPeer = initialPeer;
  }

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

  public static void RemovePeer(String ip) {
    peers.remove(ip);
  }

  public static void RemoveAll() {
    initialPeer = null;
    peers = Collections
        .synchronizedMap(new HashMap<String, Peer>());
  }

  public static ArrayList<Peer> getPeers() {
    return new ArrayList<>(peers.values());
  }

  // return neighbor have udp discover port
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

  // return neighbor have tcp control port
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


  public static List<Peer> getTwoPeers() {
    // connect the given ip at first to ensure be a network
    List<Peer> res = new ArrayList<>();
    Peer first = Peers.getInitialPeer();
    res.add(first);
    for (Peer p : Peers.getTCPPeers()) {
      if (!ControlSocketCollection.ContainsSocketHandler(p.getIp().getHostAddress()) && !first
          .getIp()
          .equals(p.getIp())) {
        res.add(p);
        break;
      }
    }
    return res;
  }

}
