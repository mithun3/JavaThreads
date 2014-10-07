package threads.advanced.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class InvokeLaterFrame extends JFrame {
  private JTextField statusField = new JTextField("Initial Value");

  public InvokeLaterFrame() {
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
    final InvokeLaterFrame ilf = new InvokeLaterFrame();
    run(ilf, 150, 60);
    // Use invokeAndWait() to synchronize output to prompt:
    // SwingUtilities.invokeAndWait(new Runnable() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        ilf.statusField.setText("Application ready");
      }
    });
    System.out.println("Done");
  }

  public static void run(JFrame frame, int width, int height) {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(width, height);
    frame.setVisible(true);
  }
} ///:~