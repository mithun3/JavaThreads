package threads.advanced.simpleThreads;

public class Terminate1
{
   public static void main (String [] args)
   {
      ThreadBA tb = new ThreadBA ();
      ThreadAB ta = new ThreadAB (tb);

      tb.start ();
      ta.start ();
   }
}

class ThreadAB extends Thread
{
   private ThreadBA tb;

   ThreadAB (ThreadBA tb)
   {
      this.tb = tb;
   }

   public void run ()
   {
      try
      {
          Thread.sleep (10);
      }
      catch (InterruptedException e)
      {
      }
      tb.finished = true;
  }
}

class ThreadBA extends Thread
{
   public volatile boolean finished = false;

   private int sum = 0;

   public void run ()
   {
      while (!finished)
      {
         System.out.println ("sum = " + sum++);
         Thread.yield ();
      }
   }
}