import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterSelectionPanel extends JPanel implements ActionListener {

    private JButton tankerButton;
    private JButton brawlerButton;
    private JButton assassinButton;
    private JButton mageButton;

    private Game mainGameWindow;

    public CharacterSelectionPanel(Game gameWindow) {
        this.mainGameWindow = gameWindow;

        this.setBackground(Color.DARK_GRAY);
        this.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel title = new JLabel("CHỌN HỆ CỦA BẠN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        add(title);

        tankerButton = new JButton("Tanker (Trâu Bò, Phản Đòn)");
        brawlerButton = new JButton("Brawler (Cân Bằng, Đa Dụng)");
        assassinButton = new JButton("Assassin (Sát Thương Lớn, Máu Giấy)");
        mageButton = new JButton("Mage (Pháo Đài Kính, Skill Khủng)");

        tankerButton.addActionListener(this);
        brawlerButton.addActionListener(this);
        assassinButton.addActionListener(this);
        mageButton.addActionListener(this);

        add(tankerButton);
        add(brawlerButton);
        add(assassinButton);
        add(mageButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String chosenCharacter = "";

        if (e.getSource() == tankerButton) {
            chosenCharacter = "Tanker";
        } else if (e.getSource() == brawlerButton) {
            chosenCharacter = "Brawler";
        } else if (e.getSource() == assassinButton) {
            chosenCharacter = "Assassin";
        } else if (e.getSource() == mageButton) {
            chosenCharacter = "Mage";
        }

        mainGameWindow.startGame(chosenCharacter);
    }
}