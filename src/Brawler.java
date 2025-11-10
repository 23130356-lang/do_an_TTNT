import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;

public class Brawler extends Stickman {

    private static final int NUM_IDLE_FRAMES = 6;
    private static final int NUM_ATTACK_FRAMES = 12;
    private static final int NUM_DEFEND_FRAMES = 2;
    private static final int NUM_SKILL1_FRAMES = 8;
    private static final int NUM_SKILL2_FRAMES = 8;

    public Brawler() {
        super("Brawler", 160, 0.10);

        this.attackIconPath = "src/res/brawler_attack.png";
        this.defendIconPath = "src/res/brawler_defend.png";

        loadAnimationFrames("src/res/rf", ".png", NUM_IDLE_FRAMES, State.IDLE);
        loadAnimationFrames("src/res/rfattack", ".png", NUM_ATTACK_FRAMES, State.ATTACKING);
        loadAnimationFrames("src/res/rfdef", ".png", NUM_DEFEND_FRAMES, State.DEFENDING);
        loadAnimationFrames("src/res/Brawler_Skill1_", ".png", NUM_SKILL1_FRAMES, State.SKILL1);
        loadAnimationFrames("src/res/Brawler_Skill2_", ".png", NUM_SKILL2_FRAMES, State.SKILL2);
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
        skills.add(new Skill("Nắm Đấm Thép", 30, 40, "src/res/dam_thep.png"));
        skills.add(new Skill("Gồng", 50, 20, "src/res/gong.png"));
    }
}