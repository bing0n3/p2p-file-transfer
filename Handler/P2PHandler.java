package Handler;


import Utils.Config;
import java.io.IOException;
import model.DiscoverMsg;
import model.DiscoverMsg.MSG_TYPE;
import model.Peer;
import model.Peers;
import server.TCPServer;
import server.UDPServer;

// handler all peers address
public class P2PHandler {

  private Peer initialPeer;
  private UDPServer udpServer;
  private TCPServer tcpServer;

  public P2PHandler() {
    this.udpServer = new UDPServer();
    this.tcpServer = new TCPServer(Config.getControlPort());
    this.listenUDP();
  }

  public void Connect(String ip, int port) throws IOException {
    this.initialPeer = new Peer(ip, port);
    Peers.addPeer(initialPeer);
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
