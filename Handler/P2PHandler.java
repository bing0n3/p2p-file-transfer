package Handler;


import Utils.Config;
import data.DiscoverMsg;
import data.DiscoverMsg.MSG_TYPE;
import data.Peer;
import java.io.IOException;
import server.UDPServer;

// handler all peers address
public class P2PHandler {

  private Peer initialPeer;
  private UDPServer udpServer;

  public P2PHandler() {
    this.udpServer = new UDPServer();
    this.listenUDP();
  }

  public void Connect(String ip, int port) throws IOException {
    this.initialPeer = new Peer(ip, port);
    PeersController.addPeer(initialPeer);
//    this.Broadcast();
  }

  // broadcast udp information to initialed connected
  public void Broadcast() throws IOException {
    DiscoverMsg msg = new DiscoverMsg(MSG_TYPE.PI,
        Config.getLocalAddress().getHostAddress(),
        Config.getUdpPort());
    this.udpServer.broadcast(initialPeer, msg);
  }

  // start to listen udp message
  private void listenUDP() {
    this.udpServer.listen();
  }

  public void Close() {
    this.udpServer.close();
  }
}
