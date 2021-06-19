package example;
public class CloseToZero {

  /**
   * 
   * @param n
   * @return
   */
  public int close_to_zero(int n) {
    if (n == 0) {
      return 0;
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    n++; // bug here
    return n;
  }

}
