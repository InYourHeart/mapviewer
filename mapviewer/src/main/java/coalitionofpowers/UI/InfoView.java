package coalitionofpowers.UI;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoView extends JPanel {

    private final JLabel claimLabel;
    private final JLabel cityLabel;
    private final JLabel regionLabel;
    private final JLabel occupationLabel;
    private final JLabel devastationLabel;
    private final JLabel taxLabel;
    private final JLabel manpowerLabel;

    public InfoView() {
        claimLabel = new JLabel("None selected");
        cityLabel = new JLabel("");
        regionLabel = new JLabel("");
        occupationLabel = new JLabel("");
        devastationLabel = new JLabel("");
        taxLabel = new JLabel("");
        manpowerLabel = new JLabel("");

        this.add(claimLabel);
        this.add(cityLabel);
        this.add(regionLabel);
        this.add(occupationLabel);
        this.add(devastationLabel);
        this.add(taxLabel);
        this.add(manpowerLabel);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void setClaimLabel(String name) {
        if (name.equals("")) {
            claimLabel.setText("None selected");
            return;
        }

        claimLabel.setText("Claim: " + name);
    }

    public void setCityLabel(String name) {
        if (name.equals("")) {
            cityLabel.setText("");
            return;
        }

        cityLabel.setText("City: " + name);
    }

    public void setRegionLabel(String name) {
        if (name.equals("")) {
            regionLabel.setText("");
            return;
        }

        regionLabel.setText("Region: " + name);
    }

    public void setOccupationLabel(String name) {
        if (name.equals("")) {
            occupationLabel.setText("");
            return;
        }

        occupationLabel.setText("Occupied by: " + name);
    }

    public void setDevastationLabel(String level) {
        if (level.equals("")) {
            devastationLabel.setText("");
            return;
        }

        devastationLabel.setText("Devastation level: " + level + "%");
    }

    public void setTaxLabel(String tax) {
        taxLabel.setText("Tax: " + tax);
    }

    public void setManpowerLabel(String manpower) {
        manpowerLabel.setText("Manpower: " + manpower);
    }

}
