package threads.advanced.fileIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipedBytes extends Object {
  public static void writeStuff(OutputStream rawOut) {
    try {
      DataOutputStream out = new DataOutputStream(
          new BufferedOutputStream(rawOut));

      int[] data = { 82, 105, 99, 104, 97, 114, 100, 32, 72, 121, 100,
          101 };

      for (int i = 0; i < data.length; i++) {
        out.writeInt(data[i]);
      }

      out.flush();
      out.close();
    } catch (IOException x) {
      x.printStackTrace();
    }
  }

  public static void readStuff(InputStream rawIn) {
    try {
      DataInputStream in = new DataInputStream(new BufferedInputStream(
          rawIn));

      boolean eof = false;
      while (!eof) {
        try {
          int i = in.readInt();
          System.out.println("just read: " + i);
        } catch (EOFException eofx) {
          eof = true;
        }
      }

      System.out.println("Read all data from the pipe");
    } catch (IOException x) {
      x.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      final PipedOutputStream out = new PipedOutputStream();

      final PipedInputStream in = new PipedInputStream(out);

      Runnable runA = new Runnable() {
        public void run() {
          writeStuff(out);
        }
      };

      Thread threadA = new Thread(runA, "threadA");
      threadA.start();

      Runnable runB = new Runnable() {
        public void run() {
          readStuff(in);
        }
      };

      Thread threadB = new Thread(runB, "threadB");
      threadB.start();
    } catch (IOException x) {
      x.printStackTrace();
    }
  }
}