package coalitionofpowers.Controllers;

import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import coalitionofpowers.Model.Claim;
import coalitionofpowers.Model.Terrain;
import coalitionofpowers.UI.ApplicationView;
import coalitionofpowers.UI.InfoView;
import coalitionofpowers.UI.MapView;

public class MapController {

    private final ApplicationView applicationView;
    private final MapView mapView;
    private final InfoView infoView;

    private final Map<Integer, Claim> claimList;
    private final Map<Integer, Terrain> terrainList;

    public MapController(String title, String baseImageFilepath, String terrainImageFilepath) throws IOException {
        mapView = new MapView(ImageIO.read(new File(baseImageFilepath)), ImageIO.read(new File(terrainImageFilepath)), this);
        infoView = new InfoView();
        applicationView = new ApplicationView("Coalition of Powers Map Viewer", mapView, infoView);

        claimList = new HashMap<>();
        terrainList = new HashMap<>();
    }

    public void loadClaimList(String filepath) throws IOException, CsvException {
        FileReader fr = new FileReader(filepath);

        try (CSVReader csvr = new CSVReader(fr)) {
            List<String[]> claimDataList = csvr.readAll();

            for (String[] claimData : claimDataList) {
                String claimName = claimData[0];
                String claimHex = claimData[1];

                claimList.put(Integer.parseInt(claimHex, 16) & 0xffffff, new Claim(claimName, claimHex));
            }
        }
    }

    public void loadTerrainList(String filepath) throws IOException, CsvException {
        FileReader fr = new FileReader(filepath);

        try (CSVReader csvr = new CSVReader(fr)) {
            List<String[]> terrainDataList = csvr.readAll();

            for (String[] terrainData : terrainDataList) {
                String terrainName = terrainData[0];
                String terrainHex = terrainData[1];
                int terrainBaseTax = Integer.parseInt(terrainData[2]);
                int terrainBaseManpower = Integer.parseInt(terrainData[3]);

                terrainList.put(Integer.parseInt(terrainHex, 16) & 0xffffff, new Terrain(terrainName, terrainHex, terrainBaseTax, terrainBaseManpower));
            }
        }
    }

    public void calculateClaimValues() throws InterruptedException {
        int[] baseImagePixels = mapView.getBaseImagePixels();
        int[] terrainImagePixels = mapView.getTerrainImagePixels();

        int height = mapView.getMapHeight();
        int width = mapView.getMapWidth();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int baseImagePixel = baseImagePixels[j * width + i] & 0x0000000000ffffff;

                if (baseImagePixel == -1 || baseImagePixel == 0) {
                    continue;
                }

                if (claimList.get(baseImagePixel) != null) {
                    int terrainPixel = terrainImagePixels[j * width + i] & 0x0000000000ffffff;

                    Terrain pixelTerrain = terrainList.get(terrainPixel);

                    if (pixelTerrain != null) {
                        claimList.get(baseImagePixel).totalTax += pixelTerrain.baseTax;
                        claimList.get(baseImagePixel).totalManpower += pixelTerrain.baseManpower;
                    }
                }
            }
        }

    }

    public void showInfoForClaim(int claimColor, Point clickPoint) {
        Claim claim = claimList.get(claimColor);

        if (claim == null) {
            infoView.setNameLabel("None selected");
            infoView.setTaxLabel("N/A");
            infoView.setManpowerLabel("N/A");
            return;
        }

        infoView.setNameLabel(claim.name);
        infoView.setTaxLabel(String.valueOf(claim.totalTax));
        infoView.setManpowerLabel(String.valueOf(claim.totalManpower));
    }
}
