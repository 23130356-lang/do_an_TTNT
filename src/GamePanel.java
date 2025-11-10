import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {

    private static final int ANIMATION_FRAME_DELAY = 100;

    Stickman player;
    Stickman enemy;

    boolean playerTurn = true;
    String gameMessage = "Lượt của bạn!";

    JButton attackButton;
    JButton skill1Button;
    JButton skill2Button;
    JButton defendButton;

    private Random aiRandom;
    private Game mainGameWindow;
    private String playerClassChoice;
    private Image backgroundImage;

    private Timer movementTimer;
    private int playerX, playerBaseX;
    private int enemyX, enemyBaseX;

    private boolean isPlayerReturning = false;

    private ArrayList<Projectile> projectiles;

    public GamePanel(Game gameWindow, String playerChoice) {
        this.mainGameWindow = gameWindow;
        this.playerClassChoice = playerChoice;

        this.setLayout(null);
        this.aiRandom = new Random();
        this.projectiles = new ArrayList<>();

        try {
            int bgIndex = aiRandom.nextInt(4) + 1;
            String bgPath = "src/res/bg" + bgIndex + ".png";
            backgroundImage = ImageIO.read(new File(bgPath));
        } catch (IOException e) {
            System.err.println("Không tải được background! " + e.getMessage());
            this.setBackground(Color.DARK_GRAY);
        }

        switch (playerChoice) {
            case "Tanker": player = new Tanker(); break;
            case "Brawler": player = new Brawler(); break;
            case "Assassin": player = new Assassin(); break;
            case "Mage": player = new Mage(); break;
            default: player = new Brawler(); break;
        }

        int enemyChoice = aiRandom.nextInt(4);
        switch (enemyChoice) {
            case 0: enemy = new Tanker(); break;
            case 1: enemy = new Brawler(); break;
            case 2: enemy = new Assassin(); break;
            case 3: enemy = new Mage(); break;
        }

        System.out.println("Trận đấu: " + player.name + " vs " + enemy.name);

        this.playerBaseX = 250;
        this.playerX = this.playerBaseX;
        this.enemyBaseX = 550;
        this.enemyX = this.enemyBaseX;

        try {
            Image img = loadAndFixImage(player.getAttackIconPath(), 60, 60);
            attackButton = new JButton(new ImageIcon(img));
            attackButton.setToolTipText("Tấn Công");
        } catch (Exception e) {
            attackButton = new JButton("Tấn Công");
        }
        attackButton.setBounds(245, 450, 70, 70);
        attackButton.addActionListener(this);
        this.add(attackButton);

        try {
            Image img = loadAndFixImage(player.getDefendIconPath(), 60, 60);
            defendButton = new JButton(new ImageIcon(img));
            defendButton.setToolTipText("Phòng Thủ");
        } catch (Exception e) {
            defendButton = new JButton("Phòng Thủ");
        }
        defendButton.setBounds(325, 450, 70, 70);
        defendButton.addActionListener(this);
        this.add(defendButton);

        Skill skill1 = player.skills.get(0);
        try {
            Image img1 = loadAndFixImage(skill1.getIconPath(), 60, 60);
            skill1Button = new JButton(new ImageIcon(img1));
            skill1Button.setToolTipText(skill1.getName() + " (Tốn: " + skill1.getEnergyCost() + ")");
        } catch (Exception e) {
            skill1Button = new JButton(skill1.getName());
        }

        skill1Button.setBounds(405, 450, 70, 70);
        skill1Button.addActionListener(this);
        this.add(skill1Button);

        Skill skill2 = player.skills.get(1);
        try {
            Image img2 = loadAndFixImage(skill2.getIconPath(), 60, 60);
            skill2Button = new JButton(new ImageIcon(img2));
            skill2Button.setToolTipText(skill2.getName() + " (Tốn: " + skill2.getEnergyCost() + ")");
        } catch (Exception e) {
            skill2Button = new JButton(skill2.getName());
        }

        skill2Button.setBounds(485, 450, 70, 70);
        skill2Button.addActionListener(this);
        this.add(skill2Button);

        updateButtonState();

        Timer gameLoopTimer = new Timer(ANIMATION_FRAME_DELAY, this);
        gameLoopTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof Timer && e.getSource() != movementTimer) {
            updateAnimations();
            updateProjectiles();
            repaint();
            return;
        }

        if (e.getSource() == movementTimer) {
            return;
        }

        if (!playerTurn) return;

        Object source = e.getSource();
        playerTurn = false;
        setPlayerButtonsEnabled(false);

        if (source == attackButton) {
            player.setState(Stickman.State.ATTACKING);
            int delay = player.getFrameCount(Stickman.State.ATTACKING) * ANIMATION_FRAME_DELAY;

            if (player instanceof Brawler || player instanceof Tanker) {
                gameMessage = player.name + " đang lao tới!";
                startCharacterLunge(player, enemy, false, () -> player.attack(enemy), delay);
            } else if (player instanceof Assassin) {
                gameMessage = "Sát thủ di chuyển!";
                startCharacterLunge(player, enemy, true, () -> player.attack(enemy), delay);
            } else if (player instanceof Mage) {
                gameMessage = "Pháp sư vận sức!";
                startCharacterInPlaceAction(player, () -> player.attack(enemy), delay);
            }
        }
        else if (source == defendButton) {
            player.defend();
            player.setState(Stickman.State.DEFENDING);
            gameMessage = "Bạn phòng thủ.";

            Timer defendPauseTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    endPlayerTurn();
                }
            });
            defendPauseTimer.setRepeats(false);
            defendPauseTimer.start();
        }
        else if (source == skill1Button) {
            player.setState(Stickman.State.SKILL1);
            int delay = player.getFrameCount(Stickman.State.SKILL1) * ANIMATION_FRAME_DELAY;
            String skillName = player.skills.get(0).getName();
            gameMessage = "Bạn dùng " + skillName + "!";

            if (player instanceof Assassin) {
                startCharacterLunge(player, enemy, true, () -> player.useSkill(0, enemy), delay);
            } else {
                startCharacterInPlaceAction(player, () -> player.useSkill(0, enemy), delay);
            }
        }
        else if (source == skill2Button) {
            player.setState(Stickman.State.SKILL2);
            String skillName = player.skills.get(1).getName();
            gameMessage = "Bạn dùng " + skillName + "!";

            if (player instanceof Mage && skillName.equals("Cầu Lửa")) {
                spawnProjectile(player, enemy, () -> player.useSkill(1, enemy));
                Timer delayTimer = new Timer(1000, ev -> {
                    if (player.isAlive()) {
                        player.setState(Stickman.State.IDLE);
                    }
                });
                delayTimer.setRepeats(false);
                delayTimer.start();

            } else if (player instanceof Assassin) {
                int delay = player.getFrameCount(Stickman.State.SKILL2) * ANIMATION_FRAME_DELAY;
                startCharacterLunge(player, enemy, true, () -> player.useSkill(1, enemy), delay);
            } else {
                int delay = player.getFrameCount(Stickman.State.SKILL2) * ANIMATION_FRAME_DELAY;
                startCharacterInPlaceAction(player, () -> player.useSkill(1, enemy), delay);
            }
        }
    }

    private void endPlayerTurn() {
        if (checkGameOver()) {
            repaint();
            return;
        }
        gameMessage = "Máy đang suy nghĩ...";
        repaint();
        startEnemyTurn();
    }

    private void endEnemyTurn() {
        if (checkGameOver()) {
            repaint();
            return;
        }

        if (player.currentState == Stickman.State.DEFENDING) {
            player.setState(Stickman.State.IDLE);
        }

        playerTurn = true;
        gameMessage = "Lượt của bạn!";
        setPlayerButtonsEnabled(true);
        updateButtonState();
        repaint();
    }

    private void startCharacterInPlaceAction(Stickman attacker, Runnable action, int delayMs) {
        if (delayMs <= 0) delayMs = 500;

        Timer attackTimer = new Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                if (attacker.isAlive()) {
                    attacker.setState(Stickman.State.IDLE);
                }

                if (attacker == player) {
                    endPlayerTurn();
                } else {
                    endEnemyTurn();
                }
            }
        });
        attackTimer.setRepeats(false);
        attackTimer.start();
    }

    private void startCharacterLunge(Stickman attacker, Stickman target, boolean jumpBehind, Runnable action, int animationDelayMs) {
        final int targetX;
        final int speed;
        final boolean isPlayer = (attacker == player);
        final int targetBaseX = isPlayer ? enemyBaseX : playerBaseX;
        if (animationDelayMs <= 0) animationDelayMs = 500;

        if (jumpBehind) {
            targetX = isPlayer ? (targetBaseX + 80) : (targetBaseX - 80);
            speed = 30;
        } else {
            targetX = isPlayer ? (targetBaseX - 80) : (targetBaseX + 80);
            speed = 20;
        }
        if (isPlayer) { isPlayerReturning = false; }
        if (movementTimer != null && movementTimer.isRunning()) {
            movementTimer.stop();
        }
        final int finalAnimationDelayMs = animationDelayMs;

        movementTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentX = isPlayer ? playerX : enemyX;
                if (currentX < targetX) {
                    currentX += speed;
                    if (currentX >= targetX) currentX = targetX;
                } else {
                    currentX -= speed;
                    if (currentX <= targetX) currentX = targetX;
                }
                if (isPlayer) playerX = currentX;
                else enemyX = currentX;

                if (currentX == targetX) {
                    movementTimer.stop();
                    action.run();
                    gameMessage = attacker.name + " tấn công!";
                    Timer pauseTimer = new Timer(finalAnimationDelayMs, ev -> startCharacterReturn(attacker));
                    pauseTimer.setRepeats(false);
                    pauseTimer.start();
                }
            }
        });
        movementTimer.start();
    }

    private void startCharacterReturn(Stickman attacker) {
        final int speed = 20;
        final boolean isPlayer = (attacker == player);
        final int basePos = isPlayer ? playerBaseX : enemyBaseX;
        if (isPlayer) { isPlayerReturning = true; }
        if (movementTimer != null && movementTimer.isRunning()) {
            movementTimer.stop();
        }
        movementTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentX = isPlayer ? playerX : enemyX;
                if (currentX < basePos) {
                    currentX += speed;
                    if (currentX >= basePos) currentX = basePos;
                } else {
                    currentX -= speed;
                    if (currentX <= basePos) currentX = basePos;
                }
                if (isPlayer) playerX = currentX;
                else enemyX = currentX;

                if (currentX == basePos) {
                    movementTimer.stop();
                    if (attacker.isAlive()) {
                        attacker.setState(Stickman.State.IDLE);
                    }
                    if (isPlayer) {
                        isPlayerReturning = false;
                        endPlayerTurn();
                    } else {
                        endEnemyTurn();
                    }
                }
            }
        });
        movementTimer.start();
    }

    private void updateAnimations() {
        if (player != null) {
            player.updateAnimation();
        }
        if (enemy != null) {
            enemy.updateAnimation();
        }
    }

    // --- HÀM BỊ THIẾU CỦA BẠN (1) ---
    private void spawnProjectile(Stickman attacker, Stickman target, Runnable onHitAction) {
        boolean isPlayer = (attacker == player);
        int startX = isPlayer ? (playerX + 40) : (enemyX - 40);
        int startY = 350 - 90;
        int targetX = isPlayer ? (enemyX - 40) : (playerX + 40);

        Projectile p = new Projectile(attacker, target, startX, startY, targetX, onHitAction);
        projectiles.add(p);
    }

    // --- HÀM BỊ THIẾU CỦA BẠN (2) ---
    private void updateProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update();

            if (!p.isActive()) {
                projectiles.remove(i);

                if (p.getAttacker() == player) { // Đã sửa lỗi p.attacker
                    endPlayerTurn();
                } else {
                    endEnemyTurn();
                }
            }
        }
    }

    private void startEnemyTurn() {
        Timer enemyTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performEnemyAI();
            }
        });
        enemyTimer.setRepeats(false);
        enemyTimer.start();
    }

    private void performEnemyAI() {
        int choice = aiRandom.nextInt(100);

        int skill1Cost = enemy.skills.get(0).getEnergyCost();
        int skill2Cost = enemy.skills.get(1).getEnergyCost();

        if (enemy.energy >= skill2Cost && choice < 30) {
            enemy.setState(Stickman.State.SKILL2);
            String skillName = enemy.skills.get(1).getName();
            gameMessage = "Máy dùng " + skillName + "!";

            if (enemy instanceof Mage && skillName.equals("Cầu Lửa")) {
                spawnProjectile(enemy, player, () -> enemy.useSkill(1, player));
                Timer delayTimer = new Timer(1000, ev -> {
                    if (enemy.isAlive()) {
                        enemy.setState(Stickman.State.IDLE);
                    }
                });
                delayTimer.setRepeats(false);
                delayTimer.start();

            } else if (enemy instanceof Assassin) {
                int delay = enemy.getFrameCount(Stickman.State.SKILL2) * ANIMATION_FRAME_DELAY;
                startCharacterLunge(enemy, player, true, () -> enemy.useSkill(1, player), delay);
            } else {
                int delay = enemy.getFrameCount(Stickman.State.SKILL2) * ANIMATION_FRAME_DELAY;
                startCharacterInPlaceAction(enemy, () -> enemy.useSkill(1, player), delay);
            }
        }
        else if (enemy.energy >= skill1Cost && choice < 60) {
            enemy.setState(Stickman.State.SKILL1);
            int delay = enemy.getFrameCount(Stickman.State.SKILL1) * ANIMATION_FRAME_DELAY;
            gameMessage = "Máy dùng " + enemy.skills.get(0).getName() + "!";

            if (enemy instanceof Assassin) {
                startCharacterLunge(enemy, player, true, () -> enemy.useSkill(0, player), delay);
            } else {
                startCharacterInPlaceAction(enemy, () -> enemy.useSkill(0, player), delay);
            }
        }
        else if (enemy.getHpPercent() < 0.4 && choice < 85) {
            enemy.defend();
            enemy.setState(Stickman.State.DEFENDING);
            gameMessage = "Máy phòng thủ!";
            endEnemyTurn();
        }
        else {
            enemy.setState(Stickman.State.ATTACKING);
            int delay = enemy.getFrameCount(Stickman.State.ATTACKING) * ANIMATION_FRAME_DELAY;

            if (enemy instanceof Brawler || enemy instanceof Tanker) {
                gameMessage = enemy.name + " đang lao tới!";
                startCharacterLunge(enemy, player, false, () -> enemy.attack(player), delay);
            } else if (enemy instanceof Assassin) {
                gameMessage = "Sát thủ (máy) di chuyển!";
                startCharacterLunge(enemy, player, true, () -> enemy.attack(player), delay);
            } else if (enemy instanceof Mage) {
                gameMessage = "Pháp sư (máy) vận sức!";
                startCharacterInPlaceAction(enemy, () -> enemy.attack(player), delay);
            }
        }
    }

    private void setPlayerButtonsEnabled(boolean enabled) {
        attackButton.setEnabled(enabled);
        defendButton.setEnabled(enabled);
        skill1Button.setEnabled(enabled);
        skill2Button.setEnabled(enabled);
    }

    private void updateButtonState() {
        boolean canUseSkill1 = playerTurn && (player.energy >= player.skills.get(0).getEnergyCost());
        boolean canUseSkill2 = playerTurn && (player.energy >= player.skills.get(1).getEnergyCost());

        skill1Button.setEnabled(canUseSkill1);
        skill2Button.setEnabled(canUseSkill2);
    }

    private boolean checkGameOver() {
        String message = "";

        if (!player.isAlive()) {
            message = "BẠN ĐÃ THUA!";
        } else if (!enemy.isAlive()) {
            message = "BẠN ĐÃ THẮNG!";
        } else {
            return false;
        }

        gameMessage = message;
        setPlayerButtonsEnabled(false);
        repaint();

        Object[] options = {"Chơi Lại", "Về Menu"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message + " Bạn muốn làm gì?",
                "Trận Đấu Kết Thúc",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            mainGameWindow.startGame(this.playerClassChoice);
        } else {
            mainGameWindow.showMainMenu();
        }

        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawStickman(g2d, playerX, 350, Color.BLUE, player, isPlayerReturning);
        drawUI(g2d, player, 100, 50, "PLAYER: " + player.name);

        drawStickman(g2d, enemyX, 350, Color.RED, enemy, true);
        drawUI(g2d, enemy, 500, 50, "ENEMY: " + enemy.name);

        for (Projectile p : projectiles) {
            p.draw(g2d);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(gameMessage, 300, 420);
    }

    private void drawStickman(Graphics2D g2d, int x_base, int y_base, Color color, Stickman s, boolean isFlipped) {

        AffineTransform oldTransform = g2d.getTransform();

        if (isFlipped) {
            g2d.translate(x_base, 0);
            g2d.scale(-1, 1);
            g2d.translate(-x_base, 0);
        }

        Image spriteToDraw = s.getCurrentFrameImage();

        if (spriteToDraw != null) {
            int spriteWidth = 150;
            int spriteHeight = 150;
            int drawX = x_base - (spriteWidth / 2);
            int drawY = y_base - spriteHeight;

            g2d.drawImage(spriteToDraw, drawX, drawY, spriteWidth, spriteHeight, null);

        } else {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3));
            int x = x_base;
            int y = y_base;
            g2d.drawOval(x - 15, y - 100, 30, 30);
            g2d.drawLine(x, y - 70, x, y - 20);
            g2d.drawLine(x, y - 60, x - 20, y - 40);
            g2d.drawLine(x, y - 60, x + 20, y - 40);
            g2d.drawLine(x, y - 20, x - 15, y);
            g2d.drawLine(x, y - 20, x + 15, y);
        }

        g2d.setTransform(oldTransform);
    }

    private void drawUI(Graphics2D g2d, Stickman s, int x, int y, String label) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(label, x, y - 5);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y + 10, 200, 20);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y + 10, (int)(200 * s.getHpPercent()), 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(s.hp + " / " + s.maxHp, x + 5, y + 25);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y + 35, 150, 15);
        g2d.setColor(Color.CYAN);
        g2d.fillRect(x, y + 35, (int)(150 * s.getEnergyPercent()), 15);
        g2d.setColor(Color.WHITE);
        g2d.drawString(s.energy + " / " + s.maxEnergy, x + 5, y + 48);
    }

    private Image loadAndFixImage(String path, int width, int height) {
        try {
            Image originalImage = ImageIO.read(new File(path));

            BufferedImage fixedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = fixedImage.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            return fixedImage;

        } catch (IOException e) {
            System.err.println("LỖI NẶNG khi tải ảnh: " + path);
            e.printStackTrace();
            return null;
        }
    }
}