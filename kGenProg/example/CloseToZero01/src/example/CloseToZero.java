package example;

public class CloseToZero {

  /**
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n == 0) {
      n++; // bug here
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    return n;
  }
}
