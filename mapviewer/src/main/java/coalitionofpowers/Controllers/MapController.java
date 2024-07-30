package coalitionofpowers.Controllers;

import java.io.File;
import java.io.FileReader;
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
import coalitionofpowers.Utility.Colors;

public class MapController {

    private final ApplicationView applicationView;
    private final MapView mapView;
    private final InfoView infoView;

    private final Map<Integer, Claim> claimList;
    private final Map<Integer, Terrain> terrainList;
    private final Map<Integer, City> cityList;
    private final Map<Integer, Region> regionList;

    public MapController(String title, String politicalImageFilepath, String terrainImageFilepath, String regionImageFilepath,
            String occupationsImageFilepath, String devastationImageFilepath) throws IOException, InterruptedException {

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

            claimList.put(Integer.decode("0x" + claimHex), new Claim(claimName));
        }
    }

    public void loadTerrainList(String filepath) throws IOException, CsvException {
        for (String[] terrainData : getValuesFromCSV(filepath)) {
            String terrainName = terrainData[0];
            String terrainHex = terrainData[1];
            int terrainBaseTax = Integer.parseInt(terrainData[2]);
            int terrainBaseManpower = Integer.parseInt(terrainData[3]);

            terrainList.put(Integer.decode("0x" + terrainHex), new Terrain(terrainName, terrainHex, terrainBaseTax, terrainBaseManpower));
        }
    }

    public void loadCityList(String filepath) throws IOException, CsvException {
        for (String[] cityData : getValuesFromCSV(filepath)) {
            String cityName = cityData[0];
            String cityHex = cityData[2];

            cityList.put(Integer.decode("0x" + cityHex), new City(cityName));
        }
    }

    public void loadRegionList(String filepath) throws IOException, CsvException {
        for (String[] regionData : getValuesFromCSV(filepath)) {
            String regionName = regionData[0];
            String regionHex = regionData[1];
            double regionTaxModifier = Double.parseDouble(regionData[2]);
            double regionManpowerModifier = Double.parseDouble(regionData[3]);

            regionList.put(Integer.decode("0x" + regionHex), new Region(regionName, regionTaxModifier, regionManpowerModifier));
        }
    }

    public void calculateClaimValues() throws InterruptedException, IOException {
        int[] politicalImagePixels = mapView.getClaimImagePixels();
        int[] terrainImagePixels = mapView.getTerrainImagePixels();
        int[] regionsImagePixels = mapView.getRegionsImagePixels();
        int[] occupationsImagePixels = mapView.getOccupationsImagePixels();
        int[] devastationImagePixels = mapView.getDevastationImagePixels();

        for (int i = 0; i < politicalImagePixels.length; i++) {
            int politicalPixel = politicalImagePixels[i] & 0xffffff;
            int occupationPixel = occupationsImagePixels[i] & 0xffffff;

            //If pixel is occupied and the occupier exists, do not count it.
            if (claimList.containsKey(occupationPixel)) {
                continue;
            }

            if (!claimList.containsKey(politicalPixel)) {
                continue;
            }

            Claim claim = claimList.get(politicalPixel);
            if (claim != null) {
                int terrainPixel = terrainImagePixels[i] & 0xffffff;

                Terrain terrain = terrainList.get(terrainPixel);

                if (terrain != null) {
                    double taxModifier = 1;
                    double manpowerModifier = 1;

                    Region region = regionList.get(regionsImagePixels[i] & 0xffffff);
                    if (region != null) {
                        taxModifier *= region.taxModifier;
                        manpowerModifier *= region.manpowerModifier;
                    }

                    double devastationPercentage = Colors.getDevastation(devastationImagePixels[i] & 0xffffff);

                    if (devastationPercentage != -1) {
                        taxModifier *= devastationPercentage;
                        manpowerModifier *= devastationPercentage;
                    }

                    claim.totalTax += terrain.baseTax * taxModifier;
                    claim.totalManpower += terrain.baseManpower * manpowerModifier;
                }
            }

        }
    }

    public void showInfoForPixel(int claimColor, int terrainColor, int regionColor, int occupationColor, int devastationColor) {
        Claim claim = claimList.get(claimColor);
        City city = cityList.get(terrainColor);
        Region region = regionList.get(regionColor);
        Claim occupier = claimList.get(occupationColor);
        double devastationPercentage = Colors.getDevastation(devastationColor);

        if (claim == null) {
            infoView.setClaimLabel("None selected");
            infoView.setTaxLabel("");
            infoView.setManpowerLabel("");
        } else {
            infoView.setClaimLabel(claim.name);

            String taxString = claim.totalTax + " £";
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

        if (city == null) {
            infoView.setCityLabel("");
        } else {
            infoView.setCityLabel(city.name);
        }

        if (region == null) {
            infoView.setRegionLabel("");
        } else {
            infoView.setRegionLabel(region.name);
        }

        if (occupier == null) {
            infoView.setOccupationLabel("");
        } else {
            infoView.setOccupationLabel(occupier.name);
        }

        if (devastationPercentage == -1) {
            infoView.setDevastationLabel("");
        } else {
            infoView.setDevastationLabel(String.format("%.2f", devastationPercentage));
        }
    }
}
