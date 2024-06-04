package coalitionofpowers.UI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ApplicationView extends JFrame {

    public ApplicationView(String title, JPanel mapScrollView) throws IOException {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        setTitle(title);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        setSize(width - 100, height - 100);

        mapScrollView.setBounds(50, 50, this.getWidth() - 400, this.getHeight() - 150);
        mapScrollView.setVisible(true);
        add(mapScrollView);

        setVisible(true);
    }
}
