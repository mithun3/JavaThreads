package threads.advanced.collection;


import java.util.Vector;

public class SafeVectorCopy {
  public static void main(String[] args) {
    Vector vect = new Vector();
    vect.addElement("Synchronization");
    vect.addElement("is");
    vect.addElement("important");

    String[] word;

    synchronized (vect) {
      int size = vect.size();
      word = new String[size];

      for (int i = 0; i < word.length; i++) {
        word[i] = (String) vect.elementAt(i);
      }
    }

    System.out.println("word.length" + word.length);
    for (int i = 0; i < word.length; i++) {
      System.out.println("[" + i + "]=" + word[i]);
    }
  }
}