package coalitionofpowers.Model;

public class Terrain {

    public final String name;

    public final String hex;

    public final int baseTax;

    public final int baseManpower;

    public Terrain(String name, String hex, int baseTax, int baseManpower) {
        this.name = name;
        this.hex = hex;
        this.baseTax = baseTax;
        this.baseManpower = baseManpower;
    }
}
