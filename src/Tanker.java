import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;

public class Tanker extends Stickman {

    private static final int NUM_IDLE_FRAMES = 4;
    private static final int NUM_ATTACK_FRAMES = 13;
    private static final int NUM_DEFEND_FRAMES = 5;
    private static final int NUM_SKILL1_FRAMES = 8;
    private static final int NUM_SKILL2_FRAMES = 8;

    public Tanker() {
        super("Tanker", 200, 0.20);

        this.attackIconPath = "src/res/tanker_attack.png";
        this.defendIconPath = "src/res/tanker_defend.png";

        loadAnimationFrames("src/res/dk", ".png", NUM_IDLE_FRAMES, State.IDLE);
        loadAnimationFrames("src/res/dkrun", ".png", NUM_ATTACK_FRAMES, State.ATTACKING);
        loadAnimationFrames("src/res/dkdef", ".png", NUM_DEFEND_FRAMES, State.DEFENDING);
        loadAnimationFrames("src/res/Tanker_Skill1_", ".png", NUM_SKILL1_FRAMES, State.SKILL1);
        loadAnimationFrames("src/res/Tanker_Skill2_", ".png", NUM_SKILL2_FRAMES, State.SKILL2);
    }

    private void loadAnimationFrames(String prefix, String suffix, int numFrames, State state) {
        Image[] frames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++) {
            String imagePath = prefix + (i + 1) + suffix;
            try {
                Image tempImage = ImageIO.read(new File(imagePath));
                BufferedImage bufferedSprite = new BufferedImage(
                        tempImage.getWidth(null),
                        tempImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = bufferedSprite.createGraphics();
                g2d.drawImage(tempImage, 0, 0, null);
                g2d.dispose();
                frames[i] = bufferedSprite;
            } catch (IOException e) {
                System.err.println("Không tải được frame: " + imagePath);
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
        skills.add(new Skill("Húc Khiên", 40, 30, "src/res/huc_khien.png"));
        skills.add(new Skill("Thạch Hoá", 50, 0, "src/res/thach_hoa.png"));
    }

    @Override
    public void defend() {
        super.defend();
        this.hp += 5;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }
}