package coalitionofpowers.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

public class ApplicationView extends JFrame {

    public ApplicationView(String title, JPanel mapView, JPanel infoView) throws IOException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth() - 100;
        int height = (int) screenSize.getHeight() - 100;

        JPanel mapViewPadding = new JPanel();
        mapViewPadding.setSize((int) (width * 0.75), height);
        CompoundBorder outerBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 60, 20),
                BorderFactory.createLineBorder(Color.black, 3));
        mapViewPadding.setBorder(outerBorder);
        mapViewPadding.setLayout(new GridLayout());
        mapViewPadding.add(mapView);
        add(mapViewPadding);

        JPanel infoViewPadding = new JPanel();
        infoViewPadding.setSize((int) (width * 0.25), height);
        infoViewPadding.setLocation((int) (width * 0.75), 0);
        infoViewPadding.setBorder(BorderFactory.createEmptyBorder(20, 20, 60, 35));
        infoViewPadding.setLayout(new GridLayout());
        infoViewPadding.add(infoView);
        infoView.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        add(infoViewPadding);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(title);
        setSize(width, height);
        setLayout(null);

        setVisible(true);
    }
}
