import java.awt.Image;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Projectile {

    // --- CẤU HÌNH (Sửa nếu cần) ---
    private static final String FIREBALL_PREFIX = "src/res/fire";
    private static final String FIREBALL_SUFFIX = ".png";
    private static final int NUM_FRAMES = 10; // Số frame animation của quả cầu lửa
    // --- KẾT THÚC CẤU HÌNH ---

    private int x, y;
    private int targetX;
    private int speed;
    private boolean isActive = true;

    private Image[] frames;
    private int currentFrame = 0;

    private Stickman attacker;
    private Stickman target;
    private Runnable onHitAction; // Hành động sẽ xảy ra khi va chạm

    public Projectile(Stickman attacker, Stickman target, int startX, int startY, int targetX, Runnable onHitAction) {
        this.attacker = attacker;
        this.target = target;
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.onHitAction = onHitAction;

        // Xác định hướng và tốc độ
        this.speed = (attacker.name.equals(target.name)) ? 0 : (targetX > startX ? 35 : -35);

        loadAnimationFrames();
    }

    private void loadAnimationFrames() {
        this.frames = new Image[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            String imagePath = FIREBALL_PREFIX + (i + 1) + FIREBALL_SUFFIX;
            try {
                Image tempImage = ImageIO.read(new File(imagePath));
                if (tempImage != null) {
                    BufferedImage bufferedSprite = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB); // Resize 30x30
                    Graphics2D g2d = bufferedSprite.createGraphics();
                    g2d.drawImage(tempImage, 0, 0, 30, 30, null);
                    g2d.dispose();
                    frames[i] = bufferedSprite;
                } else {
                    System.err.println("Không tìm thấy ảnh projectile: " + imagePath);
                }
            } catch (IOException e) {
                System.err.println("Lỗi tải ảnh projectile: " + imagePath);
            }
        }
    }

    // Cập nhật vị trí và animation
    public void update() {
        if (!isActive) return;

        // Cập nhật vị trí
        x += speed;

        // Cập nhật animation
        currentFrame = (currentFrame + 1) % NUM_FRAMES;

        // Kiểm tra va chạm
        if (speed > 0 && x >= targetX) { // Di chuyển sang phải
            triggerHit();
        } else if (speed < 0 && x <= targetX) { // Di chuyển sang trái
            triggerHit();
        }
    }

    // Kích hoạt va chạm
    private void triggerHit() {
        this.isActive = false;
        if (onHitAction != null) {
            onHitAction.run(); // Gây sát thương
        }
    }

    // Vẽ quả cầu lửa
    public void draw(Graphics2D g2d) {
        if (!isActive || frames[currentFrame] == null) return;
        g2d.drawImage(frames[currentFrame], x, y, null);
    }

    public boolean isActive() {
        return isActive;
    }
    public Stickman getAttacker() {
        return this.attacker;
    }
}