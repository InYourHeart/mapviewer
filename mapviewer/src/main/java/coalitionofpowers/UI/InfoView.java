package coalitionofpowers.UI;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoView extends JPanel {

    private final JLabel claimLabel;
    private final JLabel cityLabel;
    private final JLabel taxLabel;
    private final JLabel manpowerLabel;

    public InfoView() {
        claimLabel = new JLabel("Claim: None selected");
        cityLabel = new JLabel("");
        taxLabel = new JLabel("Tax: N/A");
        manpowerLabel = new JLabel("Manpower: N/A");

        this.add(claimLabel);
        this.add(cityLabel);
        this.add(taxLabel);
        this.add(manpowerLabel);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void setClaimLabel(String name) {
        claimLabel.setText("Claim: " + name);
    }

    public void setCityLabel(String name) {
        if (name.equals("")) {
            cityLabel.setText("");
            return;
        }

        cityLabel.setText("City: " + name);
    }

    public void setTaxLabel(String tax) {
        taxLabel.setText("Tax: " + tax);
    }

    public void setManpowerLabel(String manpower) {
        manpowerLabel.setText("Manpower: " + manpower);
    }

}
