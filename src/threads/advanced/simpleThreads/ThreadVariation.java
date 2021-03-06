package threads.advanced.simpleThreads;

class ThreadVariations {
  private int countDown = 5;

  private Inner inner;

  private class Inner extends Thread {
    Inner(String name) {
      super(name);
      start();
    }

    public void run() {
      while (true) {
        System.out.println(this);
        if (--countDown == 0)
          return;
        try {
          sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }

    public String toString() {
      return getName() + ": " + countDown;
    }
  }

  public ThreadVariations(String name) {
    inner = new Inner(name);
  }
}

// Using an anonymous inner class:

class InnerThread2 {
  private int countDown = 5;

  private Thread t;

  public InnerThread2(String name) {
    t = new Thread(name) {
      public void run() {
        while (true) {
          System.out.println(this);
          if (--countDown == 0)
            return;
          try {
            sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }

      public String toString() {
        return getName() + ": " + countDown;
      }
    };
    t.start();
  }
}

// Using a named Runnable implementation:

class InnerRunnable1 {
  private int countDown = 5;

  private Inner inner;

  private class Inner implements Runnable {
    Thread t;

    Inner(String name) {
      t = new Thread(this, name);
      t.start();
    }

    public void run() {
      while (true) {
        System.out.println(this);
        if (--countDown == 0)
          return;
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }

    public String toString() {
      return t.getName() + ": " + countDown;
    }
  }

  public InnerRunnable1(String name) {
    inner = new Inner(name);
  }
}

// Using an anonymous Runnable implementation:

class InnerRunnable2 {
  private int countDown = 5;

  private Thread t;

  public InnerRunnable2(String name) {
    t = new Thread(new Runnable() {
      public void run() {
        while (true) {
          System.out.println(this);
          if (--countDown == 0)
            return;
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }

      public String toString() {
        return Thread.currentThread().getName() + ": " + countDown;
      }
    }, name);
    t.start();
  }
}

// A separate method to run some code as a thread:

class ThreadMethod {
  private int countDown = 5;

  private Thread t;

  private String name;

  public ThreadMethod(String name) {
    this.name = name;
  }

  public void runThread() {
    if (t == null) {
      t = new Thread(name) {
        public void run() {
          while (true) {
            System.out.println(this);
            if (--countDown == 0)
              return;
            try {
              sleep(10);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }

        public String toString() {
          return getName() + ": " + countDown;
        }
      };
      t.start();
    }
  }
}

public class ThreadVariation {

  public static void main(String[] args) {
    new ThreadVariations("InnerThread1");
    new InnerThread2("InnerThread2");
    new InnerRunnable1("InnerRunnable1");
    new InnerRunnable2("InnerRunnable2");
    new ThreadMethod("ThreadMethod").runThread();
  }
} ///:~