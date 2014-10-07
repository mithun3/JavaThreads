package threads.advanced.collection;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class AtomicTest {
  static int nLoops;

  static int nThreads;

  public static void main(String[] args) {

    nLoops = 10000;
    nThreads = 1;
    doTest(new AtomicRunnable());
    doTest(new SyncRunnable());
    nLoops = Integer.parseInt(args[0]);
    nThreads = Integer.parseInt(args[1]);

    System.out.println("Starting atomic test");
    cleanGC();
    Timestamp atomicTS = new Timestamp();
    doTest(new AtomicRunnable());
    atomicTS.stop();
    System.out.println("Atomic took " + atomicTS);

    System.out.println("Starting sync test");
    cleanGC();
    Timestamp syncTS = new Timestamp();
    doTest(new SyncRunnable());
    syncTS.stop();
    System.out.println("Local sync took " + syncTS);

    double d = ((double) (syncTS.elapsedTime() - atomicTS.elapsedTime()))
        / (nLoops * nThreads);
    System.out.println("Atomic operation saves " + d + " " + syncTS.units()
        + " per call");
  }

  static void cleanGC() {
    System.gc();
    System.runFinalization();
    System.gc();
  }

  static class AtomicRunnable implements Runnable {
    AtomicInteger ai = new AtomicInteger(1);

    public void run() {
      for (int i = 0; i < nLoops; i++)
        ai.incrementAndGet();
    }
  }

  static class SyncRunnable implements Runnable {
    int testVar;

    synchronized void incrVar() {
      testVar++;
    }

    public void run() {
      for (int i = 0; i < nLoops; i++)
        incrVar();
    }
  }

  static void doTest(Runnable r) {
    Thread threads[] = new Thread[nThreads];
    for (int i = 0; i < nThreads; i++) {
      threads[i] = new Thread(r);
    }
    for (int i = 0; i < nThreads; i++) {
      threads[i].start();
    }
    for (int i = 0; i < nThreads; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException ie) {
      }
    }
  }
}

class Timestamp1 {

  private long startTime;

  private long stopTime;

  private boolean stopped = false;

  private TimeUnit ts;

  public Timestamp1() {
    this(TimeUnit.NANOSECONDS);
  }

  public Timestamp1(TimeUnit ts) {
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
 
 
