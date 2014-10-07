package threads.advanced.utilities;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExceptionCallbackMain extends Object implements ExceptionListener {

  private int exceptionCount;

  public ExceptionCallbackMain() {
    exceptionCount = 0;
  }

  public void exceptionOccurred(Exception x, Object source) {
    exceptionCount++;
    System.err.println("EXCEPTION #" + exceptionCount + ", source="
        + source);
    x.printStackTrace();
  }

  public static void main(String[] args) {
    ExceptionListener xListener = new ExceptionCallbackMain();
    ExceptionCallback ec = new ExceptionCallback(xListener);
  }
}

class ExceptionCallback extends Object {
  private Set exceptionListeners;

  private Thread internalThread;

  private volatile boolean noStopRequested;

  public ExceptionCallback(ExceptionListener[] initialGroup) {
    init(initialGroup);
  }

  public ExceptionCallback(ExceptionListener initialListener) {
    ExceptionListener[] group = new ExceptionListener[1];
    group[0] = initialListener;
    init(group);
  }

  public ExceptionCallback() {
    init(null);
  }

  private void init(ExceptionListener[] initialGroup) {
    System.out.println("initializing...");

    exceptionListeners = Collections.synchronizedSet(new HashSet());

    if (initialGroup != null) {
      for (int i = 0; i < initialGroup.length; i++) {
        addExceptionListener(initialGroup[i]);
      }
    }

    noStopRequested = true;

    Runnable r = new Runnable() {
      public void run() {
        try {
          runWork();
        } catch (Exception x) {
          sendException(x);
        }
      }
    };

    internalThread = new Thread(r);
    internalThread.start();
  }

  private void runWork() {
    try {
      makeConnection(); 
    } catch (IOException x) {
      sendException(x);
    }

    String str = null;
    int len = determineLength(str); 
  }

  private void makeConnection() throws IOException {
    String portStr = "Java2s20";
    int port = 0;

    try {
      port = Integer.parseInt(portStr);
    } catch (NumberFormatException x) {
      sendException(x);
      port = 80;
    }

    connectToPort(port); 
  }

  private void connectToPort(int portNum) throws IOException {
    throw new IOException("connection refused");
  }

  private int determineLength(String s) {
    return s.length();
  }

  public void stopRequest() {
    noStopRequested = false;
    internalThread.interrupt();
  }

  public boolean isAlive() {
    return internalThread.isAlive();
  }

  private void sendException(Exception x) {
    if (exceptionListeners.size() == 0) {
      x.printStackTrace();
      return;
    }

    synchronized (exceptionListeners) {
      Iterator iter = exceptionListeners.iterator();
      while (iter.hasNext()) {
        ExceptionListener l = (ExceptionListener) iter.next();

        l.exceptionOccurred(x, this);
      }
    }
  }

  public void addExceptionListener(ExceptionListener l) {
    if (l != null) {
      exceptionListeners.add(l);
    }
  }

  public void removeExceptionListener(ExceptionListener l) {
    exceptionListeners.remove(l);
  }

  public String toString() {
    return getClass().getName() + "[isAlive()=" + isAlive() + "]";
  }
}

interface ExceptionListener {
  public void exceptionOccurred(Exception x, Object source);
} 
 
 
