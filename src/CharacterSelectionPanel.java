import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform; // <-- ĐÃ THÊM DÒNG NÀY ĐỂ SỬA LỖI
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CharacterSelectionPanel extends JPanel {
    private static final int CARD_WIDTH = 180;
    private static final int CARD_HEIGHT = 300;

    private Game gameWindow;
    private Image backgroundImage;

    public CharacterSelectionPanel(Game gameWindow) {
        this.gameWindow = gameWindow;
        this.setLayout(new BorderLayout());

        try {
            backgroundImage = ImageIO.read(new File("src/res/bg_menu.png"));
        } catch (IOException e) {
            this.setBackground(Color.BLACK);
        }

        JLabel titleLabel = new JLabel("HÃY CHỌN NHÂN VẬT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        titleLabel.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(((JLabel)c).getText(), 4, c.getHeight() - 4);
                super.paint(g, c);
            }
        });

        this.add(titleLabel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 30));
        cardsPanel.setOpaque(false);

        cardsPanel.add(new CharacterCard("Brawler", "src/res/brawler_idle_1.png", new Color(255, 176, 6)));
        cardsPanel.add(new CharacterCard("Tanker", "src/res/tanker_idle_1.png", new Color(100, 255, 100)));
        cardsPanel.add(new CharacterCard("Assassin", "src/res/assassin_idle_1.png", new Color(200, 96, 200)));
        cardsPanel.add(new CharacterCard("Mage", "src/res/mage_idle_1.png", new Color(255, 65, 65)));

        this.add(cardsPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private class CharacterCard extends JPanel {
        private String name;
        private BufferedImage image;
        private Color glowColor;

        private boolean isHovered = false;
        private float scale = 1.0f;
        private float targetScale = 1.0f;
        private Timer animTimer;

        public CharacterCard(String name, String imagePath, Color glowColor) {
            this.name = name;
            this.glowColor = glowColor;
            this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            this.setOpaque(false);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));

            try {
                BufferedImage rawImg = ImageIO.read(new File(imagePath));
                this.image = resizeImage(rawImg, CARD_WIDTH - 20, CARD_HEIGHT - 60);
            } catch (IOException e) {
                System.err.println("Không tải được ảnh chọn nhân vật: " + imagePath);
            }

            animTimer = new Timer(16, e -> {
                float speed = 0.2f;
                scale += (targetScale - scale) * speed;
                if (Math.abs(targetScale - scale) < 0.001) {
                    scale = targetScale;
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    targetScale = 1.15f;
                    animTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    targetScale = 1.0f;
                    animTimer.start();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    targetScale = 0.95f;
                    animTimer.start();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isHovered) {
                        gameWindow.startGame(name);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            AffineTransform oldAT = g2d.getTransform();
            g2d.translate(w / 2, h / 2);
            g2d.scale(scale, scale);
            g2d.translate(-w / 2, -h / 2);

            RoundRectangle2D bgShape = new RoundRectangle2D.Float(10, 10, w - 20, h - 20, 20, 20);

            if (isHovered) {
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fill(bgShape);
                g2d.setColor(glowColor);
                g2d.setStroke(new BasicStroke(3f));
                g2d.draw(bgShape);
            } else {
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fill(bgShape);
                g2d.setColor(new Color(100, 100, 100, 100));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(bgShape);
            }

            if (image != null) {
                int imgX = (w - image.getWidth()) / 2;
                int imgY = 30;
                g2d.drawImage(image, imgX, imgY, null);

                if (!isHovered) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(imgX, imgY, image.getWidth(), image.getHeight());
                    g2d.setComposite(AlphaComposite.SrcOver);
                }
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (w - fm.stringWidth(name)) / 2;
            int textY = h - 40;

            if (isHovered) {
                g2d.setColor(glowColor);
            } else {
                g2d.setColor(Color.GRAY);
            }
            g2d.drawString(name, textX, textY);

            g2d.setTransform(oldAT);
        }

        private BufferedImage resizeImage(BufferedImage original, int targetW, int targetH) {
            Image tmp = original.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            return dimg;
        }
    }
}