package threads.advanced.utilities;
public class EarlyReturn extends Object {
  private volatile int value;

  public EarlyReturn(int v) {
    value = v;
  }

  public synchronized void setValue(int v) {
    if (value != v) {
      value = v;
      notifyAll();
    }
  }

  public synchronized boolean waitUntilAtLeast(int minValue, long msTimeout)
      throws InterruptedException {

    System.out.println("value=" + value + ",minValue=" + minValue);

    long endTime = System.currentTimeMillis() + msTimeout;
    long msRemaining = msTimeout;

    while ((value < minValue) && (msRemaining > 0L)) {
      System.out.println("about to: wait(" + msRemaining + ")");
      wait(msRemaining);
      msRemaining = endTime - System.currentTimeMillis();
      System.out.println("back from wait(), new msRemaining="
          + msRemaining);
    }

    System.out.println("leaving waitUntilAtLeast() - " + "value=" + value
        + ",minValue=" + minValue);

    // May have timed out, or may have met value,
    return (value >= minValue);
  }

  public static void main(String[] args) {
    try {
      final EarlyReturn er = new EarlyReturn(0);

      Runnable r = new Runnable() {
        public void run() {
          try {
            Thread.sleep(1500);
            er.setValue(2);
            Thread.sleep(500);
            er.setValue(3);
            Thread.sleep(500);
            er.setValue(4);
          } catch (Exception x) {
            x.printStackTrace();
          }
        }
      };

      Thread t = new Thread(r);
      t.start();

      System.out.println("waitUntilAtLeast(5, 3000)");
      long startTime = System.currentTimeMillis();
      boolean retVal = er.waitUntilAtLeast(5, 3000);
      long elapsedTime = System.currentTimeMillis() - startTime;

      System.out.println(elapsedTime + " ms, retVal=" + retVal);
    } catch (InterruptedException ix) {
      ix.printStackTrace();
    }
  }
} 
 
 
