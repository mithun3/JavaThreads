package threads.advanced.lockSynchronize;

class SyncObject {
  private Object syncObject = new Object();

  public synchronized void f() {
    System.out.println("Inside f()");
    // Doesn't release lock:
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Leaving f()");
  }

  public void g() {
    synchronized (syncObject) {
      System.out.println("Inside g()");
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Leaving g()");
    }
  }
}

public class DualSynch {

  public static void main(String[] args) {
    final SyncObject ds = new SyncObject();
    new Thread() {
      public void run() {
        ds.f();
      }
    }.start();
    ds.g();
  }
} ///:~