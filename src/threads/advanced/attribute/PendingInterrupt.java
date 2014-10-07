package threads.advanced.attribute;

public class PendingInterrupt extends Object {
  public static void main(String[] args) {
    Thread.currentThread().interrupt();

    long startTime = System.currentTimeMillis();
    try {
      Thread.sleep(2000);
      System.out.println("NOT interrupted");
    } catch (InterruptedException x) {
      System.out.println("Interrupted");
    }

    System.out.println("elapsedTime="
        + (System.currentTimeMillis() - startTime));
  }
}