package model;

public class TransferMsg {

  public enum TRANS_TYPE {
    T(0), // request
    F(1); // response
    private int value;

    TRANS_TYPE(int value) {
      this.value = value;
    }
  }

  private TRANS_TYPE type;
  private String str;

  public TransferMsg(TRANS_TYPE type, String filename) {
    this.type = type;
    this.str = filename;
  }

  public TRANS_TYPE getType() {
    return type;
  }

  public String getStr() {
    return str;
  }

  public static TransferMsg ParseMsg(String str) {
    if (str.startsWith("T:")) {
      String[] params = str.split(":");
      return new TransferMsg(TRANS_TYPE.T, params[1]);
    } else {
      return new TransferMsg(TRANS_TYPE.F, str);
    }
  }

  public String toString() {
    if (this.type.equals(TRANS_TYPE.T)) {
      return "T:" + this.getStr();
    } else {
      return this.getStr();
    }
  }

}
