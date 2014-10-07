package threads.advanced.attribute;

public class InterruptCheck extends Object {
  public static void main(String[] args) {
    Thread t = Thread.currentThread();
    System.out.println("A: t.isInterrupted()=" + t.isInterrupted());
    t.interrupt();
    System.out.println("B: t.isInterrupted()=" + t.isInterrupted());
    System.out.println("C: t.isInterrupted()=" + t.isInterrupted());

    try {
      Thread.sleep(2000);
      System.out.println("NOT interrupted");
    } catch (InterruptedException x) {
      System.out.println("Interrupted");
    }

    System.out.println("D: t.isInterrupted()=" + t.isInterrupted());
  }
}