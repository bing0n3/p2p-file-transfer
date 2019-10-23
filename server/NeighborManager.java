package server;

import java.net.InetAddress;
import java.util.ArrayList;
import packet.Peer;

public class NeighborManager {

  private ArrayList<Peer> neighbors;

  public ArrayList<Peer> getNeighbors() {
    return neighbors;
  }

  public void setNeighbors(ArrayList<Peer> neighbors) {
    this.neighbors = neighbors;
  }
}
