package coalitionofpowers.Model;

public class Claim {

    public final String name;

    public double totalTax;
    public double totalManpower;

    public Claim(String name) {
        this.name = name;
        this.totalTax = 0;
        this.totalManpower = 0;
    }
}
