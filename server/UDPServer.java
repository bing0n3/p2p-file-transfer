package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import packet.DiscoverMsg;
import packet.DiscoverMsg.MSG_TYPE;
import packet.UDPPacket;

public class UDPServer implements Runnable {

  private int port;
  private DatagramSocket socket;
  private boolean running;

  // thread safe
  private final Vector<String> filter;

  private Thread send, receive, process;

  public final Set<InetAddress> visited = new HashSet<>();


  public UDPServer(int port) {
    this.filter = new Vector<>();
    this.port = port;
    try {
      this.init();
    } catch (SocketException e) {
      e.printStackTrace();
      System.out.println("unable to initial udp server");
    }
  }

  public void init() throws SocketException {
    this.socket = new DatagramSocket(port);
    process = new Thread(this, "server_process");
    process.start();
  }

  public void send(final UDPPacket pkt) {
    send = new Thread("send_thread") {
      public void run() {
        DatagramPacket dgpacket = new DatagramPacket(pkt.getData(), pkt.getData().length,
            pkt.getAddr(), pkt.getPort());
        System.out.println("send" + new String(pkt.getData()));
        try {
          socket.send(dgpacket);
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    };
    send.start();
  }

  // broadcast packet to other connected
  public void broadcast(byte[] data) {
//    for(Connection c : CLIENTS) {
//      this.send(new UDPPacket(packet, c.getAddress(), c.getPort()));
//    }
  }

  public void recieve() {
    receive = new Thread(new RecieveHandler(this), "receive_thread");
    receive.start();
  }

  @Override
  public void run() {
    this.running = true;
    System.out.println("UDP Server stated at port " + this.port);
  }

  public void close() {
    this.running = false;
    this.socket.close();
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
          e.printStackTrace();
        }

      }
    }

    public void process(DatagramPacket pkt) throws UnknownHostException {

      // parse Received msg
      DiscoverMsg recvMsg = DiscoverMsg.ParseRecvMsg(new String(pkt.getData()));

      if (recvMsg.getType() == MSG_TYPE.PO) {
        // it is neighbor
        System.out.println("received " + recvMsg.toString());
      } else if (recvMsg.getType() == MSG_TYPE.PI) {
        System.out.println("received " + recvMsg.toString());
        UDPPacket readSend = new UDPPacket(recvMsg.toString().getBytes());
        // if not in the visited, return ack and broadcast to neighbor
        if (!server.visited.contains(pkt.getAddress())) {
          DiscoverMsg pong = new DiscoverMsg(MSG_TYPE.PO, pkt.getAddress().getHostAddress(),
              pkt.getPort());
          server.send(
              new UDPPacket(pong.toString().getBytes(), InetAddress.getByName(recvMsg.getIP()),
                  recvMsg.getPort()));
        }

        // broadcast to all neighbor
        //TODO
      }

    }
  }

}


