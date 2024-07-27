package coalitionofpowers.Model;

public class Region {

    public final String name;

    public final double taxModifier;

    public final double manpowerModifier;

    public Region(String name, double taxModifier, double manpowerModifier) {
        this.name = name;
        this.taxModifier = taxModifier;
        this.manpowerModifier = manpowerModifier;
    }
}
