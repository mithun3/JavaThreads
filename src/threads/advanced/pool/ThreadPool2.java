package threads.advanced.pool;

import java.util.*;

import threads.advanced.utilities.BusyFlag;
import threads.advanced.utilities.CondVar;

public class ThreadPool2 {

  class ThreadPool2Request {
    Runnable target;
    Object lock;

    ThreadPool2Request(Runnable t, Object l) {
      target = t;
      lock = l;
    }
  }

  class ThreadPool2Thread extends Thread {
    ThreadPool2 parent;
    boolean shouldRun = true;

    ThreadPool2Thread(ThreadPool2 parent, int i) {
      super("ThreadPool2Thread " + i);
      this.parent = parent;
    }

    public void run() {
      ThreadPool2Request obj = null;
      while (shouldRun) {
        try {
          parent.cvFlag.getBusyFlag();
          while (obj == null && shouldRun) {
            try {
              obj = (ThreadPool2Request)
                  parent.objects.elementAt(0);
              parent.objects.removeElementAt(0);
            } catch (ArrayIndexOutOfBoundsException aiobe) {
              obj = null;
            } catch (ClassCastException cce) {
              System.err.println("Unexpected data");
              obj = null;
            }
            if (obj == null) {
              try {
                parent.cvAvailable.cvWait();
              } catch (InterruptedException ie) {
                return;
              }
            }
          }
        } finally {
          parent.cvFlag.freeBusyFlag();
        }
        if (!shouldRun)
          return;
        obj.target.run();
        try {
          parent.cvFlag.getBusyFlag();
          nObjects--;
          if (nObjects == 0)
            parent.cvEmpty.cvSignal();
        } finally {
          parent.cvFlag.freeBusyFlag();
        }
        if (obj.lock != null) {
          synchronized(obj.lock) {
            obj.lock.notify();
          }
        }
        obj = null;
      }
    }
  }

  Vector objects;
  int  nObjects = 0;
  CondVar cvAvailable, cvEmpty;
  BusyFlag cvFlag;
  ThreadPool2Thread poolThreads[];
  boolean terminated = false;

  public ThreadPool2(int n) {
    cvFlag = new BusyFlag();
    cvAvailable = new CondVar(cvFlag);
    cvEmpty = new CondVar(cvFlag);
    objects = new Vector();
    poolThreads = new ThreadPool2Thread[n];
    for (int i = 0; i < n; i++) {
      poolThreads[i] = new ThreadPool2Thread(this, i);
      poolThreads[i].start();
    }
  }

  private void add(Runnable target, Object lock) {
    try {
      cvFlag.getBusyFlag();
      if (terminated)
        throw new IllegalStateException("Thread pool has shutdown");
      objects.addElement(new ThreadPool2Request(target, lock));
      nObjects++;
      cvAvailable.cvSignal();
    } finally {
      cvFlag.freeBusyFlag();
    }
  }

  public void addRequest(Runnable target) {
    add(target, null);
  }

  public void addRequestAndWait(Runnable target)
              throws InterruptedException {
    Object lock = new Object();
    synchronized(lock) {
      add(target, lock);
      lock.wait();
    }
  }

  public void waitForAll(boolean terminate) throws InterruptedException {
    try {
      cvFlag.getBusyFlag();
      while (nObjects != 0)
        cvEmpty.cvWait();
      if (terminate) {
        for (int i = 0; i < poolThreads.length; i++)
          poolThreads[i].shouldRun = false;
        cvAvailable.cvBroadcast();
        terminated = true;
      }
    } finally {
      cvFlag.freeBusyFlag();
    }
  }

  public void waitForAll() throws InterruptedException {
    waitForAll(false);
  }
}