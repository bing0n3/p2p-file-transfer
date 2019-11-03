package Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileFound {

  public static Set<String> fileFound = Collections.synchronizedSet(new HashSet<String>());

  public static void Empy() {
    fileFound = Collections.synchronizedSet(new HashSet<String>());
  }

}
