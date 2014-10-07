package threads.advanced.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class EventThreadFrame extends JFrame {
  private JTextField statusField = new JTextField("Initial Value");

  public EventThreadFrame() {
    Container cp = getContentPane();
    cp.add(statusField, BorderLayout.NORTH);
    addWindowListener(new WindowAdapter() {
      public void windowOpened(WindowEvent e) {
        try { // Simulate initialization overhead
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
        statusField.setText("Initialization complete");
      }
    });
  }

  public static void main(String[] args) {
    EventThreadFrame etf = new EventThreadFrame();
    run(etf, 150, 60);
    etf.statusField.setText("Application ready");
    System.out.println("Done");
  }

  public static void run(JFrame frame, int width, int height) {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(width, height);
    frame.setVisible(true);
  }
} ///:~