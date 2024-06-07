package coalitionofpowers.UI;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoView extends JPanel {

    private final JLabel nameLabel;
    private final JLabel taxLabel;
    private final JLabel manpowerLabel;

    public InfoView() {
        nameLabel = new JLabel("Claim: None selected");
        taxLabel = new JLabel("Tax: N/A");
        manpowerLabel = new JLabel("Manpower: N/A");

        this.add(nameLabel);
        this.add(taxLabel);
        this.add(manpowerLabel);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void setNameLabel(String name) {
        nameLabel.setText("Claim: " + name);
    }

    public void setTaxLabel(String tax) {
        taxLabel.setText("Tax: " + tax);
    }

    public void setManpowerLabel(String manpower) {
        manpowerLabel.setText("Manpower: " + manpower);
    }

}
