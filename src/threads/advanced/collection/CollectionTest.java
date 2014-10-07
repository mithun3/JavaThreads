package threads.advanced.collection;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class CollectionTest {
  static int nLoops;

  public static void main(String[] args) {

    nLoops = 10000;
    doTest(new Vector());
    doTest(new ArrayList());
    doTest(Collections.synchronizedList(new ArrayList()));
    nLoops = Integer.parseInt(args[0]);

    System.out.println("Starting synchronized vector test");
    cleanGC();
    Timestamp syncTS = new Timestamp();
    doTest(new Vector());
    syncTS.stop();
    System.out.println("Synchronized vector took " + syncTS);

    System.out.println("Starting unsynchronized vector test");
    cleanGC();
    Timestamp unsyncTS = new Timestamp();
    unsyncTS.stop();
    System.out.println("Unsynchronized vector took " + unsyncTS);

    double d = ((double) (syncTS.elapsedTime() - unsyncTS.elapsedTime()))
        / nLoops;
    System.out.println("Unsynchronized operation saves " + d + " "
        + syncTS.units() + " per call");

    System.out.println("Starting synchronized array list test");
    cleanGC();
    syncTS = new Timestamp();
    doTest(Collections.synchronizedList(new ArrayList()));
    syncTS.stop();
    System.out.println("Synchronized array list took " + syncTS);

    System.out.println("Starting unsynchronized array list test");
    cleanGC();
    unsyncTS = new Timestamp();
    doTest(new ArrayList());
    unsyncTS.stop();
    System.out.println("Unsynchronized aray list took " + unsyncTS);

    d = ((double) (syncTS.elapsedTime() - unsyncTS.elapsedTime())) / nLoops;
    System.out.println("Unsynchronized operation saves " + d + " "
        + syncTS.units() + " per call");
  }

  static void cleanGC() {
    System.gc();
    System.runFinalization();
    System.gc();
  }

  static void doTest(List l) {
    Integer n = new Integer(0);
    for (int i = 0; i < nLoops; i++)
      l.add(n);
  }
}
class Timestamp {

  private long startTime;

  private long stopTime;

  private boolean stopped = false;

  private TimeUnit ts;

  public Timestamp() {
    this(TimeUnit.NANOSECONDS);
  }

  public Timestamp(TimeUnit ts) {
    this.ts = ts;
    start();
  }

  public void start() {
    startTime = System.nanoTime();
    stopped = false;
  }

  public void stop() {
    stopTime = System.nanoTime();
    stopped = true;
  }

  public long elapsedTime() {
    if (!stopped)
      throw new IllegalStateException("Timestamp not stopped");
    return ts.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
  }

  public String toString() {
    try {
      return elapsedTime() + " " + ts;
    } catch (IllegalStateException ise) {
      return "Timestamp (not stopped)";
    }
  }

  public String units() {
    return ts.toString();
  }
}