package threads.advanced.lockSynchronize;

public class Deadlock extends Object {
  private String objID;

  public Deadlock(String id) {
    objID = id;
  }

  public synchronized void checkOther(Deadlock other) {
    print("entering checkOther()");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException x) {
    }

    print("invoke 'other.action()'");
    other.action();

    print("leaving checkOther()");
  }

  public synchronized void action() {
    print("entering action()");

    // simulate some work here
    try {
      Thread.sleep(500);
    } catch (InterruptedException x) {
    }

    print("leaving action()");
  }

  public void print(String msg) {
    threadPrint("objID=" + objID + " - " + msg);
  }

  public static void threadPrint(String msg) {
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + ": " + msg);
  }

  public static void main(String[] args) {
    final Deadlock obj1 = new Deadlock("Thread 1");
    final Deadlock obj2 = new Deadlock("Thread 2");

    Runnable runA = new Runnable() {
      public void run() {
        obj1.checkOther(obj2);
      }
    };

    Thread thread = new Thread(runA, "A");
    thread.start();

    try {
      Thread.sleep(200);
    } catch (InterruptedException x) {
    }

    Runnable runB = new Runnable() {
      public void run() {
        obj2.checkOther(obj1);
      }
    };

    Thread threadB = new Thread(runB, "B");
    threadB.start();

    try {
      Thread.sleep(5000);
    } catch (InterruptedException x) {
    }

    threadPrint("finished sleeping");

    threadPrint("about to interrupt() threadA");
    thread.interrupt();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException x) {
    }

    threadPrint("about to interrupt() threadB");
    threadB.interrupt();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException x) {
    }

    threadPrint("did that break the deadlock?");
  }
}