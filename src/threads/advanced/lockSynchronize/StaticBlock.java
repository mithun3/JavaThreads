package threads.advanced.lockSynchronize;

public class StaticBlock extends Object {
  public static synchronized void staticA() {
    System.out.println("entering staticA()");

    try {
      Thread.sleep(5000);
    } catch (InterruptedException x) {
    }

    System.out.println("leaving staticA()");
  }

  public static void staticB() {
    synchronized (StaticBlock.class) {
      System.out.println("in staticB() : inside sync block");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException x) {
      }
    }
  }

  public static void main(String[] args) {
    Runnable runA = new Runnable() {
      public void run() {
        StaticBlock.staticA();
      }
    };

    Thread threadA = new Thread(runA, "A");
    threadA.start();

    try {
      Thread.sleep(200);
    } catch (InterruptedException x) {
    }

    Runnable runB = new Runnable() {
      public void run() {
        StaticBlock.staticB();
      }
    };

    Thread threadB = new Thread(runB, "B");
    threadB.start();
  }
}