package threads.advanced.simpleThreads;

public class ThreeThreadsTest {
  public static void main(String[] args) {
    new SimpleThread6("Jamaica").start();
    new SimpleThread6("Fiji").start();
    new SimpleThread6("Bora Bora").start();
  }
}

class SimpleThread6 extends Thread {
  public SimpleThread6(String str) {
    super(str);
  }

  public void run() {
    for (int i = 0; i < 10; i++) {
      System.out.println(i + " " + getName());
      try {
        sleep((long) (Math.random() * 1000));
      } catch (InterruptedException e) {
      }
    }
    System.out.println("DONE! " + getName());
  }
}