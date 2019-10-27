package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class TCPSocketHandler implements Runnable {

  private int id;
  private Socket socket;
  private Thread process, send, listening;
  private boolean running;
  private Writer writer;
  private InetAddress localIP, remoteIP;
  private int localPort, remotePort;

  public TCPSocketHandler(Socket socket) {
    this.socket = socket;
    this.localIP = socket.getLocalAddress();
    this.localPort = socket.getLocalPort();
    this.remoteIP = socket.getInetAddress();
    this.remotePort = socket.getPort();
    process = new Thread(this);
    process.start();
    this.listening();
  }

  @Override
  public void run() {
    this.running = true;
  }

  public void listening() {
    this.listening = new Thread("listening_socket") {
      @Override
      public void run() {
        while (running) {
          try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();
            int index;
            String temp;
            while ((temp = br.readLine()) != null) {
              if ((index = temp.indexOf("eof")) != -1) {
                sb.append(temp.substring(0, index));
              }
              sb.append(temp);
              listenProcess(sb.toString());
            }
            br.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };

    this.listening.start();
  }

  public void send(final String str) {
    this.send = new Thread("send_socket") {
      @Override
      public void run() {
        try {
          if (writer == null) {
            writer = new PrintWriter(socket.getOutputStream());
          }
          writer.write(str);
          writer.write("eof");
          writer.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
  }

  public void close() throws IOException {
    this.running = false;
    this.writer.close();
    this.socket.close();
  }

  private void listenProcess(String string) {
    this.send("received" + string);
  }

}
