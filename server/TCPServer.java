package server;

import Handler.TCPSocketHandler;
import Utils.ControlSocketCollection;
import Utils.ObjectAction;
import Utils.QueryAction;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer implements Runnable {

  private int port;
  private ServerSocket welcomeServer;
  private boolean running;

  private Thread recieve, process;
  private ObjectAction action;


  public TCPServer(int port, ObjectAction action) {
    this.port = port;
    this.action = action;

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
    this.process = new Thread(this, "tcp_server_process");
    process.start();
  }

  /*
  listen incoming model
   */
  public void Listen() {
    recieve = new Thread("tcp_listen") {
      @Override
      public void run() {
        while (running) {
          try {
            Socket incomeSocket = welcomeServer.accept();
            TCPSocketHandler socketHandler = new TCPSocketHandler(incomeSocket, action);
            System.out
                .println("Connected by\t" + socketHandler.getRemoteAddress().getHostAddress() + ":"
                    + socketHandler.getRemotePort());

            if (action.getClass().equals(QueryAction.class)) {
              ControlSocketCollection
                  .put(socketHandler.getRemoteAddress().getHostAddress(), socketHandler);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };
    recieve.start();
  }


  public void close() {
    try {
      this.running = false;
      this.welcomeServer.close();
      if (action.getClass().equals(QueryAction.class)) {
        System.out.println("Stop File Control Server!");
      } else {
        System.out.println("Stop File Transfer Server!");
      }
    } catch (IOException e) {
//      e.printStackTrace();
    }
  }

  public TCPSocketHandler connect(InetAddress host, int port) throws IOException {
    Socket socket = new Socket();
    System.out.println("Connect with " + host.getHostAddress() + ":" + port);
    socket.connect(new InetSocketAddress(host, port));
    TCPSocketHandler handler = new TCPSocketHandler(socket, action);
    handler.startHeartBeat();
    if (action.getClass().equals(QueryAction.class)) {
      ControlSocketCollection.put(socket.getInetAddress().getHostAddress(), handler);
    }
    return handler;
  }


  @Override
  public void run() {
    this.running = true;
    System.out.println("TCP Server started at port " + this.port);
  }

}


