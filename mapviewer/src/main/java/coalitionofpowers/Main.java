package coalitionofpowers;

import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import com.opencsv.exceptions.CsvException;

import coalitionofpowers.Controllers.MapController;

public class Main {

    public static JSlider zoomSlider;
    public static JScrollPane scrollPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (IOException | CsvException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException ex) {
            }
        });
    }

    private static void createAndShowGUI() throws IOException, CsvException, InterruptedException {
        MapController mapController = new MapController("Coalition of Powers Map Viewer",
                "mapviewer\\maps\\color.png",
                "mapviewer\\\\maps\\\\terrain.png");

        mapController.loadClaimList("mapviewer\\data\\claims.csv");
        mapController.loadTerrainList("mapviewer\\data\\terrains.csv");

        mapController.calculateClaimValues();
    }
}
