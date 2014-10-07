package threads.advanced.utilities;

public class TransitionDetectorMain extends Object {
  private static Thread startTrueWaiter(final TransitionDetector td,
      String name) {

    Runnable r = new Runnable() {
      public void run() {
        try {
          while (true) {
            print("about to wait for false-to-"
                + "true transition, td=" + td);

            td.waitForFalseToTrueTransition();

            print("just noticed for false-to-"
                + "true transition, td=" + td);
          }
        } catch (InterruptedException ix) {
          return;
        }
      }
    };

    Thread t = new Thread(r, name);
    t.start();

    return t;
  }

  private static Thread startFalseWaiter(final TransitionDetector td,
      String name) {

    Runnable r = new Runnable() {
      public void run() {
        try {
          while (true) {
            print("about to wait for true-to-"
                + "false transition, td=" + td);

            td.waitForTrueToFalseTransition();

            print("just noticed for true-to-"
                + "false transition, td=" + td);
          }
        } catch (InterruptedException ix) {
          return;
        }
      }
    };

    Thread t = new Thread(r, name);
    t.start();

    return t;
  }

  private static void print(String msg) {
    String name = Thread.currentThread().getName();
    System.err.println(name + ": " + msg);
  }

  public static void main(String[] args) {
    try {
      TransitionDetector td = new TransitionDetector(false);

      Thread threadA = startTrueWaiter(td, "threadA");
      Thread threadB = startFalseWaiter(td, "threadB");

      Thread.sleep(200);
      print("td=" + td + ", about to set to 'false'");
      td.setValue(false);

      Thread.sleep(200);
      print("td=" + td + ", about to set to 'true'");
      td.setValue(true);

      Thread.sleep(200);
      print("td=" + td + ", about to pulse value");
      td.pulseValue();

      Thread.sleep(200);
      threadA.interrupt();
      threadB.interrupt();
    } catch (InterruptedException x) {
      x.printStackTrace();
    }
  }
}

class TransitionDetector extends Object {
  private boolean value;

  private Object valueLock;

  private Object falseToTrueLock;

  private Object trueToFalseLock;

  public TransitionDetector(boolean initialValue) {
    value = initialValue;
    valueLock = new Object();
    falseToTrueLock = new Object();
    trueToFalseLock = new Object();
  }

  public void setValue(boolean newValue) {
    synchronized (valueLock) {
      if (newValue != value) {
        value = newValue;

        if (value) {
          notifyFalseToTrueWaiters();
        } else {
          notifyTrueToFalseWaiters();
        }
      }
    }
  }

  public void pulseValue() {
    // Sync on valueLock to be sure that no other threads
    // get into setValue() between these two setValue()
    // calls.
    synchronized (valueLock) {
      setValue(!value);
      setValue(!value);
    }
  }

  public boolean isTrue() {
    synchronized (valueLock) {
      return value;
    }
  }

  public void waitForFalseToTrueTransition() throws InterruptedException {

    synchronized (falseToTrueLock) {
      falseToTrueLock.wait();
    }
  }

  private void notifyFalseToTrueWaiters() {
    synchronized (falseToTrueLock) {
      falseToTrueLock.notifyAll();
    }
  }

  public void waitForTrueToFalseTransition() throws InterruptedException {

    synchronized (trueToFalseLock) {
      trueToFalseLock.wait();
    }
  }

  private void notifyTrueToFalseWaiters() {
    synchronized (trueToFalseLock) {
      trueToFalseLock.notifyAll();
    }
  }

  public String toString() {
    return String.valueOf(isTrue());
  }
} 
 
 
