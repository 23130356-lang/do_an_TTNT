import javax.swing.JFrame;

public class Game extends JFrame {

    public Game() {
        setTitle("Stickman Battle Arena");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);

        showMainMenu();

        setVisible(true);
    }

    public void showMainMenu() {
        getContentPane().removeAll();
        CharacterSelectionPanel selectionPanel = new CharacterSelectionPanel(this);
        add(selectionPanel);
        revalidate();
        repaint();
    }

    public void startGame(String playerChoice) {
        System.out.println("Người chơi đã chọn: " + playerChoice);
        getContentPane().removeAll();

        GamePanel gamePanel = new GamePanel(this, playerChoice);
        add(gamePanel);

        revalidate();
        repaint();
    }


    public static void main(String[] args) {
        new Game();
    }

}