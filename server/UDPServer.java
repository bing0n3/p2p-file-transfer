package server;

import Utils.Config;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import model.DiscoverMsg;
import model.DiscoverMsg.MSG_TYPE;
import model.Peer;
import model.Peers;
import model.UDPPacket;

public class UDPServer implements Runnable {

  private DatagramSocket socket;
  private boolean running;

  private Thread send, receive, process;

  public final Set<InetAddress> visited = new HashSet<>();

  public UDPServer() {
    try {
      this.init();
    } catch (SocketException e) {
      e.printStackTrace();
      System.out.println("unable to initial udp server");
    }
  }


  public void init() throws SocketException {
    this.socket = new DatagramSocket(Config.getUdpPort());
    process = new Thread(this, "udp_server_process");
    process.start();
  }

  public void send(final UDPPacket pkt) {
    send = new Thread("udp_send_thread") {
      public void run() {
        DatagramPacket dgpacket = new DatagramPacket(pkt.getData(), pkt.getData().length,
            pkt.getAddr(), pkt.getPort());
        try {
          socket.send(dgpacket);
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
        System.out.println(
            "SEND " + new String(pkt.getData()) + " To " + pkt.getAddr().getHostAddress() + ":"
                + pkt
                .getPort());
      }
    };
    send.start();
  }


  // broadcast model to other connected
  public void broadcast(DiscoverMsg msg) throws UnknownHostException {
    for (Peer peer : Peers.getUDPPeers()) {
      this.broadcast(peer, msg);
    }
  }

  public void broadcast(Peer peer, DiscoverMsg msg) throws UnknownHostException {

    this.send(
        new UDPPacket(msg.toString().getBytes(), peer.getIp(), peer.getUdpPort()));
  }

  public void listen() {
    receive = new Thread(new RecieveHandler(this), "tcp_receive_thread");
    receive.start();
    System.out.println("Start to Listen incoming UDP message");
  }

  @Override
  public void run() {
    this.running = true;
    System.out.println("UDP Server stated at port " + Config.getUdpPort());
  }

  public void close() {
    this.running = false;
    this.socket.close();
    System.out.println("\nTerminate Neighbor Discover Server!");
  }


  static class RecieveHandler implements Runnable {

    public UDPServer server;

    RecieveHandler(UDPServer server) {
      this.server = server;
    }

    @Override
    public void run() {
      while (server.running) {
        byte[] buffer = new byte[1024];
        DatagramPacket dgpkt = new DatagramPacket(buffer, buffer.length);
        try {
          server.socket.receive(dgpkt);
          process(dgpkt);
        } catch (IOException e) {
//          e.printStackTrace();
        }

      }
    }

    public void process(DatagramPacket pkt) throws UnknownHostException {

      // parse Received msg
      DiscoverMsg recvMsg = DiscoverMsg.ParseRecvMsg(new String(pkt.getData()));

      System.out.println("received " + recvMsg.toString());

      if (Config.getLocalAddress().getHostAddress().equals(recvMsg.getIP())) {
        return;
      }

      // if the message type is po
      if (recvMsg.getType().equals(MSG_TYPE.PO)) {
        // the message type is po, add it to the peers list

        // if already in, set controlPort
        if (Peers.Contains(recvMsg.getIP())) {
          Peers.getPeer(recvMsg.getIP()).setControlPort(recvMsg.getPort());
        } else {
          // if not, add to peers
          // peer without udp port
          InetAddress recvIP = InetAddress.getByName(recvMsg.getIP());
          Peers
              .addPeer(recvIP, pkt.getPort(), recvMsg.getPort());
        }
      } else if (recvMsg.getType().equals(MSG_TYPE.PI)) {
        // if not in the visited and not from itself, return ack and broadcast to neighbor
        if (!Peers.Contains(recvMsg.getIP())) {
          server.broadcast(recvMsg);
          // add peer into
          Peers.addPeer(InetAddress.getByName(recvMsg.getIP()), recvMsg.getPort());
          // create pong message return back to server
          DiscoverMsg pong = new DiscoverMsg(MSG_TYPE.PO, Config.getLocalAddress().getHostAddress(),
              Config.getControlPort());
          // send response message back
          server.send(
              new UDPPacket(pong.toString().getBytes(), InetAddress.getByName(recvMsg.getIP()),
                  recvMsg.getPort()));
        } else {
          System.out.println("Ignore Duplicate Discover message: " + recvMsg.toString());
        }
      } else {
        System.out.println("Received: illegal msg");
      }
    }
  }

}


