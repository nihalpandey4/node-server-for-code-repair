package example;

public class CloseToZero {

  public int close_to_zero(int n) {
    if (n == 0) {
      return 1;
    } else if (n > 0) {
      n--;
    } else {
      n++;
    }
    n++; // bug here
    return n;
  }

}
