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
    private static final int NUM_SKILL2_FRAMES = 5;

    public Tanker() {
        super("Tanker", 300, 0.20);

        this.attackIconPath = "src/res/tanker_attack.png";
        this.defendIconPath = "src/res/tanker_defend.png";

        loadAnimationFrames("src/res/dk", ".png", NUM_IDLE_FRAMES, State.IDLE);
        loadAnimationFrames("src/res/dkrun", ".png", NUM_ATTACK_FRAMES, State.ATTACKING);
        loadAnimationFrames("src/res/dkdef", ".png", NUM_DEFEND_FRAMES, State.DEFENDING);
        loadAnimationFrames("src/res/dkrun", ".png", NUM_SKILL1_FRAMES, State.SKILL1);
        loadAnimationFrames("src/res/dkdef", ".png", NUM_SKILL2_FRAMES, State.SKILL2);
    }


    private void loadAnimationFrames(String prefix, String suffix, int numFrames, State state) {
        Image[] frames = new Image[numFrames];
        for (int i = 0; i < numFrames; i++) {
            String imagePath = prefix + (i + 1) + suffix;
            try {
                Image tempImage = ImageIO.read(new File(imagePath));
                if (tempImage != null) {
                    BufferedImage bufferedSprite = new BufferedImage(tempImage.getWidth(null), tempImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bufferedSprite.createGraphics();
                    g2d.drawImage(tempImage, 0, 0, null);
                    g2d.dispose();
                    frames[i] = bufferedSprite;
                } else {
                    frames[i] = null;
                }
            } catch (IOException e) {
                frames[i] = null;
            }
        }

        switch(state) {
            case IDLE: this.idleFrames = frames; this.numIdleFrames = numFrames; break;
            case ATTACKING: this.attackFrames = frames; this.numAttackFrames = numFrames; break;
            case DEFENDING: this.defendFrames = frames; this.numDefendFrames = numFrames; break;
            case SKILL1: this.skill1Frames = frames; this.numSkill1Frames = numFrames; break;
            case SKILL2: this.skill2Frames = frames; this.numSkill2Frames = numFrames; break;
        }
    }

    @Override
    protected void initializeSkills() {
        skills.add(new Skill("Húc Khiên", 40, 30, "src/res/huc_khien.png"));

        // --- SỬA Ở ĐÂY: Thạch Hoá (Damage 0, Mana 60) ---
        skills.add(new Skill("Thạch Hoá", 60, 0, "src/res/thach_hoa.png"));}

    @Override
    public void defend() {
        super.defend();
        this.hp += 5; // Defend thường hồi nhẹ 5 máu
        if (this.hp > this.maxHp) this.hp = this.maxHp;
    }

    // --- LOGIC HỒI MÁU CHO SKILL ---
    @Override
    public void useSkill(int skillIndex, Stickman target) {
        Skill skill = skills.get(skillIndex);

        if (skill.getName().equals("Thạch Hoá")) {
            if (this.energy >= skill.getEnergyCost()) {
                this.energy -= skill.getEnergyCost();

                // Hồi 40 Máu
                int healAmount = 40;
                this.hp += healAmount;
                if (this.hp > this.maxHp) this.hp = this.maxHp;

                System.out.println(this.name + " dùng Thạch Hoá hồi " + healAmount + " HP!");
                this.isDefending = false;
            } else {
                System.out.println(this.name + " không đủ năng lượng!");
            }
        } else {
            super.useSkill(skillIndex, target);
        }
    }
}