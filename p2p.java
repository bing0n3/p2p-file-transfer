import Utils.HostName;
import Utils.PortManage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import packet.DiscoverMsg;
import packet.DiscoverMsg.MSG_TYPE;
import packet.UDPPacket;
import server.PeerDiscover;

public class p2p {

  public static void main(String[] args) {
    PortManage portManage = new PortManage();
    PeerDiscover udpServer = new PeerDiscover(portManage.getUdpPort());
    udpServer.recieve();
    int udpPort = portManage.getUdpPort();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Command commands = new Command(portManage, udpServer);
//    udpServer.send();


    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      System.out.print("p2p> ");
      try {
        String line = input.readLine();
        commands.Parse(line);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public static class Command {

    private PeerDiscover discover;
    private PortManage portManage;

    public Command(PortManage portManage, PeerDiscover discover) {
      this.discover = discover;
      this.portManage = portManage;
    }

    void Parse(String args) {
      String[] params = args.toLowerCase().split(" ");
      switch (params[0]) {
        case "":
          return;
        case "connect":
          break;
        case "send":
          try {
            UDPSend(params[1]);
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        case "exit":
          discover.close();
          System.exit(0);
      }
    }
    void UDPSend(String ip)  throws IOException {
      String hostname = HostName.get();
      InetAddress v = InetAddress.getByName(hostname);
      System.out.println(hostname);
      System.out.println(v.getHostAddress());
      DiscoverMsg msg = new DiscoverMsg(MSG_TYPE.PI, v.getHostAddress(), this.portManage.getUdpPort());
      UDPPacket here = new UDPPacket(msg.toString().getBytes(),InetAddress.getByName(ip), portManage.getUdpPort());
      discover.send(here);
    }
  }



}