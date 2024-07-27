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
import coalitionofpowers.Model.Region;
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
    private final Map<Integer, Region> regionList;

    public MapController(String title, String politicalImageFilepath, String terrainImageFilepath, String regionImageFilepath,
            String occupationsImageFilepath, String devastationImageFilepath) throws IOException {

        mapView = new MapView(ImageIO.read(new File(politicalImageFilepath)),
                ImageIO.read(new File(terrainImageFilepath)),
                ImageIO.read(new File(regionImageFilepath)),
                ImageIO.read(new File(occupationsImageFilepath)),
                ImageIO.read(new File(devastationImageFilepath)),
                this);

        infoView = new InfoView();
        applicationView = new ApplicationView("Coalition of Powers Map Viewer", mapView, infoView);

        claimList = new HashMap<>();
        terrainList = new HashMap<>();
        cityList = new HashMap<>();
        regionList = new HashMap<>();
    }

    private List<String[]> getValuesFromCSV(String filepath) throws IOException, CsvException {
        FileReader fr = new FileReader(filepath);

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader csvReader = new CSVReaderBuilder(fr).withCSVParser(parser).build();

        return csvReader.readAll();
    }

    public void loadClaimList(String filepath) throws IOException, CsvException {
        for (String[] claimData : getValuesFromCSV(filepath)) {
            String claimName = claimData[0];
            String claimHex = claimData[1];

            claimList.put(Integer.parseInt(claimHex, 16) & 0xffffff, new Claim(claimName));
        }
    }

    public void loadTerrainList(String filepath) throws IOException, CsvException {
        for (String[] terrainData : getValuesFromCSV(filepath)) {
            String terrainName = terrainData[0];
            String terrainHex = terrainData[1];
            int terrainBaseTax = Integer.parseInt(terrainData[2]);
            int terrainBaseManpower = Integer.parseInt(terrainData[3]);

            terrainList.put(Integer.parseInt(terrainHex, 16) & 0xffffff, new Terrain(terrainName, terrainHex, terrainBaseTax, terrainBaseManpower));
        }
    }

    public void loadCityList(String filepath) throws IOException, CsvException {
        for (String[] cityData : getValuesFromCSV(filepath)) {
            String cityName = cityData[0];
            String cityHex = cityData[2];

            cityList.put(Integer.parseInt(cityHex, 16) & 0xffffff, new City(cityName));
        }
    }

    public void loadRegionList(String filepath) throws IOException, CsvException {
        for (String[] regionData : getValuesFromCSV(filepath)) {
            String regionName = regionData[0];
            String regionHex = regionData[1];
            double regionTaxModifier = Double.parseDouble(regionData[2]);
            double regionManpowerModifier = Double.parseDouble(regionData[3]);

            regionList.put(Integer.parseInt(regionHex, 16) & 0xffffff, new Region(regionName, regionTaxModifier, regionManpowerModifier));
        }
    }

    public void calculateClaimValues() throws InterruptedException, IOException {
        int[] politicalImagePixels = mapView.getClaimImagePixels();
        int[] terrainImagePixels = mapView.getTerrainImagePixels();
        int[] regionsImagePixels = mapView.getRegionsImagePixels();
        int[] occupationsImagePixels = mapView.getOccupationsImagePixels();
        int[] devastationImagePixels = mapView.getDevastationImagePixels();

        int height = mapView.getMapHeight();
        int width = mapView.getMapWidth();

        String whitePixelsInPoliticalMap = "";
        String whitePixelsInTerrainMap = "";

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixelIndex = j * width + i;

                int politicalPixel = politicalImagePixels[pixelIndex] & 0x0000000000ffffff;
                int occupationPixel = occupationsImagePixels[pixelIndex] & 0x000000000ffffff;

                //If pixel is occupied and the occupier exists, do not count it.
                if (claimList.containsKey(occupationPixel)) {
                    continue;
                }

                if (politicalPixel == 16777215) {
                    whitePixelsInPoliticalMap += "(" + j + "," + i + ")\n";
                    System.out.println(j + "," + i);
                    continue;
                }

                if (politicalPixel == -1 || politicalPixel == 0) {
                    continue;
                }

                Claim claim = claimList.get(politicalPixel);
                if (claim != null) {
                    int terrainPixel = terrainImagePixels[pixelIndex] & 0x0000000000ffffff;

                    if (terrainPixel == 16777215) {
                        whitePixelsInTerrainMap += "(" + j + "," + i + ")\n";
                        System.out.println(j + "," + i);
                        continue;
                    }

                    Terrain terrain = terrainList.get(terrainPixel);

                    if (terrain != null) {
                        double taxModifier = 1;
                        double manpowerModifier = 1;

                        Region region = regionList.get(regionsImagePixels[pixelIndex] & 0x0000000000ffffff);
                        if (region != null) {
                            taxModifier *= region.taxModifier;
                            manpowerModifier *= region.manpowerModifier;
                        }

                        claim.totalTax += terrain.baseTax * taxModifier;
                        claim.totalManpower += terrain.baseManpower * manpowerModifier;
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
