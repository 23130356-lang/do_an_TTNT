import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;

public class Mage extends Stickman {

    private static final int NUM_IDLE_FRAMES = 7;
    private static final int NUM_ATTACK_FRAMES = 7;
    private static final int NUM_DEFEND_FRAMES = 3;
    private static final int NUM_SKILL1_FRAMES = 6;
    private static final int NUM_SKILL2_FRAMES = 7;

    public Mage() {
        super("Mage", 200, 0.0);

        this.attackIconPath = "src/res/mage_attack.png";
        this.defendIconPath = "src/res/mage_defend.png";

        loadAnimationFrames("src/res/Idle", ".png", NUM_IDLE_FRAMES, State.IDLE);
        loadAnimationFrames("src/res/", ".png", NUM_ATTACK_FRAMES, State.ATTACKING);
        loadAnimationFrames("src/res/wzdef", ".png", NUM_DEFEND_FRAMES, State.DEFENDING);
        loadAnimationFrames("src/res/Mage_Skill1_", ".png", NUM_SKILL1_FRAMES, State.SKILL1);
        loadAnimationFrames("src/res/wzsk", ".png", NUM_SKILL2_FRAMES, State.SKILL2);
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
        skills.add(new Skill("Tích Tụ", 0, 0, "src/res/tich_tu.png"));
        skills.add(new Skill("Cầu Lửa", 60, 80, "src/res/cau_lua.png"));
    }
    @Override
    public void defend() {
        System.out.println(this.name + " đang phòng thủ (Mage)!");
        this.isDefending = true;
        this.gainEnergy(5);;
    }
    @Override
    public void attack(Stickman target) {
        int damage = 5;
        target.takeDamage(damage);
        this.gainEnergy(40);
        this.isDefending = false;
    }

    @Override
    public void useSkill(int skillIndex, Stickman target) {
        Skill skill = skills.get(skillIndex);

        if (skill.getName().equals("Tích Tụ")) {
            if (this.energy >= skill.getEnergyCost()) {
                this.energy -= skill.getEnergyCost();
                this.gainEnergy(50);
                this.isDefending = false;
            } else {
                System.out.println(this.name + " không đủ năng lượng!");
            }
        } else {
            super.useSkill(skillIndex, target);
        }
    }
}