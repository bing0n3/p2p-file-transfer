package Handler;


import static Utils.NetworkUtils.readFromStream;

import Utils.Config;
import Utils.ControlSocketCollection;
import Utils.FileFound;
import Utils.MsgCollection;
import Utils.NetworkUtils;
import Utils.QueryAction;
import Utils.QueryFileCollection;
import Utils.TransferAction;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import model.DiscoverMsg;
import model.DiscoverMsg.MSG_TYPE;
import model.Peer;
import model.Peers;
import model.QueryMsg;
import model.TransferMsg;
import model.TransferMsg.TRANS_TYPE;
import server.TCPServer;
import server.UDPServer;

// handler all peers address
public class P2PHandler {

  private static TCPServer fileServer = null;
  private static UDPServer udpServer = null;
  private static TCPServer tcpServer = null;

  public P2PHandler() {
    udpServer = new UDPServer();
    tcpServer = new TCPServer(Config.getControlPort(), new QueryAction());
    fileServer = new TCPServer(Config.getFilePort(), new TransferAction());
    this.listenUDP();
    tcpServer.Listen();
    fileServer.Listen();
  }

  public void Connect(String ip, int port) throws IOException, InterruptedException {
    Peers.setInitialPeer(new Peer(ip, port));
    Peers.addPeer(Peers.getInitialPeer());

    if (ip.equals(Config.getLocalAddress().getHostAddress())) {
      System.out.println("The IP address your input is the local server");
      Peers.RemoveAll();
    }

    this.Broadcast();
    System.out.println("\nWait Build Connection------------------------------");
    Thread.sleep(3000);
    this.ConnectToTwoNeighboor();
  }

  public void Leave() {
    // disconnect with all neighbor
    System.out.println("Leave All Neighbors");
    Peers.RemoveAll();
    ControlSocketCollection.removeAll();
    MsgCollection.empty();
    FileFound.Empy();
    QueryFileCollection.empty();
    return;
  }

  public void Exit() {
    Leave();
    udpServer.close();
    tcpServer.close();
    fileServer.close();
    System.exit(0);
  }

  public void ConnectTCP(String ip, int port) throws IOException {
    Peer peer = new Peer(ip, port);
    tcpServer.connect(peer.getIp(), peer.getUdpPort());
  }

  // broadcast udp information to initialed connected
  public void Broadcast() throws IOException {
    DiscoverMsg msg = new DiscoverMsg(MSG_TYPE.PI,
        Config.getLocalAddress().getHostAddress(),
        Config.getUdpPort());
    udpServer.broadcast(Peers.getInitialPeer(), msg);
  }

  public void ConnectToTwoNeighboor() throws IOException {
    List<Peer> peers = Peers.getTwoPeers();
    for (Peer p : peers) {
      tcpServer.connect(p.getIp(), p.getControlPort());
    }
  }

  public void QueryFile(final String filename) {
    if (Config.isContainFile(filename)) {
      System.out.println("The file " + filename + " is in the local");
      return;
    }

    String msg = QueryMsg.buildQueryMsg(filename).toString();
    System.out.println("Broadcast Query Message to Neighbors for " + filename);
    for (TCPSocketHandler handler : ControlSocketCollection.getSocketHandlers()) {
      handler.send(msg);
    }
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(5000);
          if (!FileFound.fileFound.contains(filename)) {
            System.out.println("The file " + filename + " doesn't exist in the network");
          }
        } catch (InterruptedException ignored) {

        }
      }
    }.start();
  }

  public static TCPServer getFileServer() {
    return fileServer;
  }

  // start to listen udp message
  private void listenUDP() {
    udpServer.listen();
  }

  public void Close() {
    udpServer.close();
  }


  public static void requestForFile(final InetAddress ip, final int port, final String filename) {
    new Thread("file_request") {
      @Override
      public void run() {
        try {
          Socket socket = new Socket(ip, port);
          BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
          TransferMsg msg = new TransferMsg(TRANS_TYPE.T, filename);
          byte[] buffData = msg.toString().getBytes();
          bos.write(NetworkUtils.int2ByteArrays(buffData.length));
          bos.write(buffData);
          bos.flush();

          String content = null;
          InputStream in = socket.getInputStream();
          if (in != null) {
            content = readFromStream(in);
          }

          File target = new File(Config.filePath + filename);
          if (target.exists()) {
            System.out.println("File " + filename + " existed");
            return;
          } else {
            target.createNewFile();
          }
          OutputStream os = new FileOutputStream(Config.filePath + filename);
          if (content != null) {
            os.write(content.getBytes());
          }
          os.flush();

          os.close();
          socket.close();
          // close socket
          System.out.println("File Saved successful! And Socket Canceled");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

}
