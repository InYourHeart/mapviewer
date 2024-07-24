package coalitionofpowers.Controllers;

import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import coalitionofpowers.Model.City;
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
    private final Map<Integer, City> cityList;

    public MapController(String title, String baseImageFilepath, String terrainImageFilepath) throws IOException {
        mapView = new MapView(ImageIO.read(new File(baseImageFilepath)), ImageIO.read(new File(terrainImageFilepath)), this);
        infoView = new InfoView();
        applicationView = new ApplicationView("Coalition of Powers Map Viewer", mapView, infoView);

        claimList = new HashMap<>();
        terrainList = new HashMap<>();
        cityList = new HashMap<>();
    }

    public void loadClaimList(String filepath) throws IOException, CsvException {
        FileReader fr = new FileReader(filepath);

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader csvReader = new CSVReaderBuilder(fr).withCSVParser(parser).build();

        List<String[]> claimDataList = csvReader.readAll();

        for (int i = 0; i < claimDataList.size(); i++) {
            String[] claimData = claimDataList.get(i);

            String claimName = claimData[0];
            String claimHex = claimData[1];

            claimList.put(Integer.parseInt(claimHex, 16) & 0xffffff, new Claim(claimName));
        }
    }

    public void loadCityList(String filepath) throws IOException, CsvException {
        FileReader fr = new FileReader(filepath);

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader csvReader = new CSVReaderBuilder(fr).withCSVParser(parser).build();

        List<String[]> cityDataList = csvReader.readAll();

        for (int i = 1; i < cityDataList.size(); i++) {
            String[] cityData = cityDataList.get(i);

            String cityName = cityData[0];
            String cityHex = cityData[2];

            cityList.put(Integer.parseInt(cityHex, 16) & 0xffffff, new City(cityName));
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

    public void calculateClaimValues() throws InterruptedException, IOException {
        int[] baseImagePixels = mapView.getBaseImagePixels();
        int[] terrainImagePixels = mapView.getTerrainImagePixels();

        int height = mapView.getMapHeight();
        int width = mapView.getMapWidth();

        String whitePixelsInPoliticalMap = "";
        String whitePixelsInTerrainMap = "";

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int baseImagePixel = baseImagePixels[j * width + i] & 0x0000000000ffffff;

                if (baseImagePixel == 16777215) {
                    whitePixelsInPoliticalMap += "(" + j + "," + i + ")\n";
                    System.out.println(j + "," + i);
                    continue;
                }

                if (baseImagePixel == -1 || baseImagePixel == 0) {
                    continue;
                }

                if (claimList.get(baseImagePixel) != null) {
                    int terrainPixel = terrainImagePixels[j * width + i] & 0x0000000000ffffff;

                    if (terrainPixel == 16777215) {
                        whitePixelsInTerrainMap += "(" + j + "," + i + ")\n";
                        System.out.println(j + "," + i);
                        continue;
                    }

                    Terrain pixelTerrain = terrainList.get(terrainPixel);

                    if (pixelTerrain != null) {
                        claimList.get(baseImagePixel).totalTax += pixelTerrain.baseTax;
                        claimList.get(baseImagePixel).totalManpower += pixelTerrain.baseManpower;
                    }
                }
            }
        }

        if (!whitePixelsInPoliticalMap.isEmpty()) {
            FileWriter w1 = new FileWriter("politicalMapErrors.txt");
            w1.write(whitePixelsInPoliticalMap);
            w1.close();
        }

        if (!whitePixelsInTerrainMap.isEmpty()) {
            FileWriter w1 = new FileWriter("terrainMapErrors.txt");
            w1.write(whitePixelsInTerrainMap);
            w1.close();
        }
    }

    public void showInfoForPixel(int claimColor, int terrainColor, Point clickPoint) {
        Claim claim = claimList.get(claimColor);
        City city = cityList.get(terrainColor);

        if (city == null) {
            infoView.setCityLabel("");
        } else {
            infoView.setCityLabel(city.name);
        }

        if (claim == null) {
            infoView.setClaimLabel("None selected");
            infoView.setTaxLabel("N/A");
            infoView.setManpowerLabel("N/A");
        } else {
            infoView.setClaimLabel(claim.name);

            String taxString = claim.totalTax + " $";
            String manpowerString = claim.totalManpower + " men";
            if (claim.totalTax > 1000000) {
                taxString = String.format("%.2fM", claim.totalTax / 1000000.0) + " $";
            }

            if (claim.totalManpower > 1000000) {
                manpowerString = String.format("%.2fM", claim.totalManpower / 1000000.0) + " men";
            }

            infoView.setTaxLabel(taxString);
            infoView.setManpowerLabel(manpowerString);
        }
    }
}
