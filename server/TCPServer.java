package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TCPServer implements Runnable {

  private int port;
  private ServerSocket welcomeServer;
  private boolean running;

  private Set<String> filter;
  private Thread send, recieve, process;

  private ArrayList<TCPSocketHandler> servers;
  private ArrayList<TCPSocketHandler> clients;
  // sockets manage
//  private ArrayList<Socket> sockets;

  public TCPServer(int port) {

    filter = new HashSet<>();
    this.port = port;
    this.servers = new ArrayList<>();
    this.clients = new ArrayList<>();

    try {
      this.init();
    } catch (SocketException e) {
      e.printStackTrace();
      System.out.println("Fail to establish TCP connection");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void init() throws IOException {
    this.welcomeServer = new ServerSocket(this.port);
    this.process = new Thread(this, "server_process");
    process.start();
  }

  /*
  listen incoming data
   */
  private void listen() {
    recieve = new Thread() {
      @Override
      public void run() {
        while (running) {
          try {
            Socket socket = welcomeServer.accept();
            servers.add(new TCPSocketHandler(socket));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };

    recieve.start();
  }


  public void close() {

  }

  public void connect(InetAddress host, int port) throws IOException {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(host, port));
    TCPSocketHandler handler = new TCPSocketHandler(socket);
    clients.add(handler);
    handler.send("test");
    System.out.println("Connect with" + host.getHostName() + port);
  }

  @Override
  public void run() {
    this.running = true;
    System.out.println("TCP Server started at port " + this.port);
  }


}


