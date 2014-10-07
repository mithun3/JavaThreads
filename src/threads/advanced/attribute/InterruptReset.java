package threads.advanced.attribute;

public class InterruptReset extends Object {
  public static void main(String[] args) {
    System.out.println("Thread.interrupted()="
        + Thread.interrupted());
    Thread.currentThread().interrupt();
    System.out.println("Thread.interrupted()="
        + Thread.interrupted());
    System.out.println("Thread.interrupted()="
        + Thread.interrupted());
  }
}