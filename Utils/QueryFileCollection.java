package Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryFileCollection {

  private static Map<String, String> fileMapper = Collections
      .synchronizedMap(new HashMap<String, String>());

  public static void put(String uuid, String filename) {
    fileMapper.put(uuid, filename);
  }

  public static boolean contains(String uuid) {
    return fileMapper.containsKey(uuid);
  }

  public static String get(String uuid) {
    return fileMapper.get(uuid);
  }

  public static void empty() {
    fileMapper = Collections
        .synchronizedMap(new HashMap<String, String>());
  }

}

