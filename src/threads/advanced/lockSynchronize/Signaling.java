package threads.advanced.lockSynchronize;

public class Signaling extends Object {
  private BooleanLock readyLock;

  public Signaling(BooleanLock readyLock) {
    this.readyLock = readyLock;

    Runnable r = new Runnable() {
      public void run() {
        try {
          runWork();
        } catch (Exception x) {
          // in case ANY exception slips through
          x.printStackTrace();
        }
      }
    };

    Thread internalThread = new Thread(r, "internal");
    internalThread.start();
  }

  private void runWork() {
    try {
      print("about to wait for readyLock to be true");
      readyLock.waitUntilTrue(0); // 0 - wait forever
      print("readyLock is now true");
    } catch (InterruptedException x) {
      print("interrupted while waiting for readyLock " + "to become true");
    }
  }

  private static void print(String msg) {
    String name = Thread.currentThread().getName();
    System.err.println(name + ": " + msg);
  }

  public static void main(String[] args) {
    try {
      print("creating BooleanLock instance");
      BooleanLock ready = new BooleanLock(false);

      print("creating Signaling instance");
      new Signaling(ready);

      print("about to sleep for 3 seconds");
      Thread.sleep(3000);

      print("about to setValue to true");
      ready.setValue(true);
      print("ready.isTrue()=" + ready.isTrue());
    } catch (InterruptedException x) {
      x.printStackTrace();
    }
  }
}

class BooleanLock extends Object {
  private boolean value;

  public BooleanLock(boolean initialValue) {
    value = initialValue;
  }

  public BooleanLock() {
    this(false);
  }

  public synchronized void setValue(boolean newValue) {
    if (newValue != value) {
      value = newValue;
      notifyAll();
    }
  }

  public synchronized boolean waitToSetTrue(long msTimeout)
      throws InterruptedException {

    boolean success = waitUntilFalse(msTimeout);
    if (success) {
      setValue(true);
    }

    return success;
  }

  public synchronized boolean waitToSetFalse(long msTimeout)
      throws InterruptedException {

    boolean success = waitUntilTrue(msTimeout);
    if (success) {
      setValue(false);
    }

    return success;
  }

  public synchronized boolean isTrue() {
    return value;
  }

  public synchronized boolean isFalse() {
    return !value;
  }

  public synchronized boolean waitUntilTrue(long msTimeout)
      throws InterruptedException {

    return waitUntilStateIs(true, msTimeout);
  }

  public synchronized boolean waitUntilFalse(long msTimeout)
      throws InterruptedException {

    return waitUntilStateIs(false, msTimeout);
  }

  public synchronized boolean waitUntilStateIs(boolean state, long msTimeout)
      throws InterruptedException {

    if (msTimeout == 0L) {
      while (value != state) {
        wait(); // wait indefinitely until notified
      }

      // condition has finally been met
      return true;
    }

    // only wait for the specified amount of time
    long endTime = System.currentTimeMillis() + msTimeout;
    long msRemaining = msTimeout;

    while ((value != state) && (msRemaining > 0L)) {
      wait(msRemaining);
      msRemaining = endTime - System.currentTimeMillis();
    }

    // May have timed out, or may have met value,
    // calculate return value.
    return (value == state);
  }
} 
 
 
