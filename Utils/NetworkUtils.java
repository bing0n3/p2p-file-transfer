package Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NetworkUtils {

  public static int HEADER_LENGTH = 4;

  public static int byteArrayToInt(byte[] b) {
    int intValue = 0;
    for (int i = 0; i < b.length; i++) {
      intValue += (b[i] & 0xFF) << (8 * (3 - i)); //int占4个字节（0，1，2，3）
    }
    return intValue;
  }


  public static byte[] int2ByteArrays(int i) {
    byte[] result = new byte[4];
    result[0] = (byte) ((i >> 24) & 0xFF);
    result[1] = (byte) ((i >> 16) & 0xFF);
    result[2] = (byte) ((i >> 8) & 0xFF);
    result[3] = (byte) (i & 0xFF);
    return result;
  }

  public static String readFromStream(InputStream inputStream) {
    BufferedInputStream bis;
    byte[] header = new byte[NetworkUtils.HEADER_LENGTH];
    String result;

    try {
      bis = new BufferedInputStream(inputStream);
      int temp;
      int len = 0;
      while (len < header.length) {
        temp = bis.read(header, len, header.length - len);
        if (temp > 0) {
          len += temp;
        } else if (temp == -1) {
          bis.close();
          return null;
        }
      }
      len = 0;
      int length = NetworkUtils.byteArrayToInt(header);//数据的长度值
      byte[] content = new byte[length];
      while (len < length) {
        temp = bis.read(content, len, length - len);

        if (temp > 0) {
          len += temp;
        }
      }
      result = new String(content);


    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return result;
  }

  public static void DisconnectPeer(String ip) {

  }

}
