package Handler;

import static Utils.NetworkUtils.readFromStream;

import Utils.Config;
import Utils.ControlSocketCollection;
import Utils.NetworkUtils;
import Utils.ObjectAction;
import Utils.TransferAction;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import model.KeepAlive;
import model.Peers;

public class TCPSocketHandler implements Runnable {

  private final Object object = new Object();
  private long lastHeartTime = System.currentTimeMillis();
  private Socket socket;
  private Thread process, listening;
  private boolean running;
  private InetAddress localIP, remoteIP;
  private int localPort, remotePort;
  private ObjectAction action;
  private String queried_file;

  public TCPSocketHandler(Socket socket, ObjectAction action) {
    this.socket = socket;
    this.localIP = socket.getLocalAddress();
    this.localPort = socket.getLocalPort();
    this.remoteIP = socket.getInetAddress();
    this.remotePort = socket.getPort();
    this.action = action;
    process = new Thread(this);
    process.start();
  }

  @Override
  public void run() {
    this.running = true;
    this.listening();
  }

  public void startHeartBeat() {
    new Thread(new KeepAliveWatchDog()).start();
  }

  public void listening() {
    this.listening = new Thread("listening_socket") {
      private InputStream in;

      @Override
      public void run() {
        while (running) {
          if ((System.currentTimeMillis() - lastHeartTime) > Config.receiveTimeDelay) {
            running = false;
            timeout();
          } else {
            try {
              in = socket.getInputStream();
              if (in.available() > 0) {
                String msg = readFromStream(in);
                if (KeepAlive.serialVersionUID.equals(msg)) {
                  lastHeartTime = System.currentTimeMillis();
                  continue;
                }
                if (msg != null) {
                  action.doAction(msg, TCPSocketHandler.this);
                }
              }
            } catch (IOException ignore) {
//              e.printStackTrace();
            }
          }
        }
      }
    };
    this.listening.start();
  }

  public void send(final String obj) {

    new Thread("send_socket") {
      @Override
      public void run() {
        synchronized (object) {
          try {
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffData = obj.getBytes();
            //write header
            writer.write(NetworkUtils.int2ByteArrays(buffData.length));
            // write content
            writer.write(obj.getBytes());
            writer.flush();

            if (action.getClass().equals(TransferAction.class)) {
              close();
              System.out.println("Sucessful Transfer file, and Close File Transfer Socket!");
            }
          } catch (IOException e) {
            // to distinguish  Control Server and File Server
            if (action.getClass().equals(TransferAction.class)) {
              close();
              System.out.println("File Transfer failed");
            } else {
              close();
              e.printStackTrace();
            }
          }
        }
      }
    }.start();
  }


  public InetAddress getRemoteAddress() {
    return remoteIP;
  }

  public int getRemotePort() {
    return remotePort;
  }

  public void close() {
    this.running = false;
    this.listening.interrupt();
    try {
      this.socket.close();
      System.out
          .println("Closed TCP Socket Connect with " + this.getRemoteAddress().getHostAddress());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void timeout() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(
        "Time Out, the remote server disconnected:" + this.getRemoteAddress().getHostAddress());
    Peers.RemovePeer(this.getRemoteAddress().toString());
    ControlSocketCollection.remove(this.getRemoteAddress().toString());

  }

  public void closeInput() throws IOException {
    this.socket.shutdownInput();
  }

  public void clostOutput() throws IOException {
    synchronized (object) {
      this.socket.shutdownOutput();
    }
  }

  public String getQueried_file() {
    return queried_file;
  }

  public void setQueried_file(String queried_file) {
    this.queried_file = queried_file;
  }


  class KeepAliveWatchDog implements Runnable {

    long checkDelay = 10;

    @Override
    public void run() {
      System.out.println("Start Heartbeat to check remote");
      while (running) {
        if (System.currentTimeMillis() - lastHeartTime > Config.keepAliveDelay) {
          String obj = KeepAlive.serialVersionUID;
          synchronized (object) {
            try {
              BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
              byte[] buffData = obj.getBytes();
              //write header
              writer.write(NetworkUtils.int2ByteArrays(buffData.length));
              // write content
              writer.write(obj.getBytes());
              writer.flush();
            } catch (IOException e) {
//              e.printStackTrace();
              TCPSocketHandler.this.running = false;
              TCPSocketHandler.this.close();
              ControlSocketCollection
                  .remove(TCPSocketHandler.this.getRemoteAddress().getHostAddress());
              Peers.RemovePeer(TCPSocketHandler.this.getRemoteAddress().getHostAddress());
              System.out.println(
                  "Timeout, The remote socket disappear " + TCPSocketHandler.this.getRemoteAddress()
                      .getHostAddress() + ", close socket with it");
            }
          }
          lastHeartTime = System.currentTimeMillis();
        } else {
          try {
            Thread.sleep(checkDelay);
          } catch (InterruptedException ignored) {

          }
        }
      }
    }
  }

}
