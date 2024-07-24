package coalitionofpowers.Model;

import java.util.Dictionary;

public class Claim {

    public final String name;

    public String flag;

    public double totalTax;
    public Dictionary tradeIncomes;
    public Dictionary miscIncomes;
    public double taxEfficiency;

    public double totalManpower;
    public double manpowerEfficiency;

    public Claim(String name) {
        this.name = name;
        this.totalTax = 0;
        this.totalManpower = 0;
    }
}
