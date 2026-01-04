import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;

public class Assassin extends Stickman {

    // (Hãy chắc chắn các con số này đúng với số lượng file bạn có)
    private static final int NUM_IDLE_FRAMES = 6;
    private static final int NUM_ATTACK_FRAMES = 17;
    private static final int NUM_DEFEND_FRAMES = 2;
    private static final int NUM_SKILL1_FRAMES = 8;
    private static final int NUM_SKILL2_FRAMES = 8;

    public Assassin() {
        super("Assassin", 170, 0.05);

        this.attackIconPath = "src/res/assassin_attack.png";
        this.defendIconPath = "src/res/assassin_defend.png";

        loadAnimationFrames("src/res/st", ".png", NUM_IDLE_FRAMES, State.IDLE);
        loadAnimationFrames("src/res/strun", ".png", NUM_ATTACK_FRAMES, State.ATTACKING);
        loadAnimationFrames("src/res/stdef", ".png", NUM_DEFEND_FRAMES, State.DEFENDING);
        loadAnimationFrames("src/res/strun", ".png", NUM_SKILL1_FRAMES, State.SKILL1);
        loadAnimationFrames("src/res/strun", ".png", NUM_SKILL2_FRAMES, State.SKILL2);
    }
    private void loadAnimationFrames(String prefix, String suffix, int numFrames, State state) {
        Image[] frames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++) {
            String imagePath = prefix + (i + 1) + suffix;
            try {
                Image tempImage = ImageIO.read(new File(imagePath));

                if (tempImage != null) {
                    BufferedImage bufferedSprite = new BufferedImage(
                            tempImage.getWidth(null),
                            tempImage.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    Graphics2D g2d = bufferedSprite.createGraphics();
                    g2d.drawImage(tempImage, 0, 0, null);
                    g2d.dispose();
                    frames[i] = bufferedSprite;
                } else {
                    System.err.println("LỖI: Không tìm thấy file ảnh: " + imagePath);
                    frames[i] = null;
                }

            } catch (IOException e) {
                System.err.println("LỖI: File ảnh bị hỏng: " + imagePath);
                frames[i] = null;
            }
        }

        switch(state) {
            case IDLE:
                this.idleFrames = frames;
                this.numIdleFrames = numFrames;
                break;
            case ATTACKING:
                this.attackFrames = frames;
                this.numAttackFrames = numFrames;
                break;
            case DEFENDING:
                this.defendFrames = frames;
                this.numDefendFrames = numFrames;
                break;
            case SKILL1:
                this.skill1Frames = frames;
                this.numSkill1Frames = numFrames;
                break;
            case SKILL2:
                this.skill2Frames = frames;
                this.numSkill2Frames = numFrames;
                break;
        }
    }

    @Override
    protected void initializeSkills() {
        skills.add(new Skill("Đâm Lén", 40, 60, "src/res/dam_len.png"));
        skills.add(new Skill("Kết Liễu", 60, 80, "src/res/ket_lieu.png"));
    }

    @Override
    public void attack(Stickman target) {
        int damage = 15;
        target.takeDamage(damage);
        this.gainEnergy(25);
        this.isDefending = false;
    }
}