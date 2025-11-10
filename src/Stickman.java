import java.awt.Image;
import java.util.ArrayList;

public abstract class Stickman {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int energy;
    protected int maxEnergy = 100;
    protected double defense;
    protected boolean isDefending = false;

    protected ArrayList<Skill> skills;

    protected String attackIconPath;
    protected String defendIconPath;

    public enum State { IDLE, ATTACKING, DEFENDING, SKILL1, SKILL2 }
    protected State currentState = State.IDLE;

    protected Image[] idleFrames;
    protected int currentIdleFrame = 0, numIdleFrames = 0;

    protected Image[] attackFrames;
    protected int currentAttackFrame = 0, numAttackFrames = 0;

    protected Image[] defendFrames;
    protected int currentDefendFrame = 0, numDefendFrames = 0;

    protected Image[] skill1Frames;
    protected int currentSkill1Frame = 0, numSkill1Frames = 0;

    protected Image[] skill2Frames;
    protected int currentSkill2Frame = 0, numSkill2Frames = 0;

    public Stickman(String name, int maxHp, double defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.defense = defense;
        this.energy = 0;
        this.skills = new ArrayList<>();

        initializeSkills();
    }

    public void attack(Stickman target) {
        int damage = 10;
        target.takeDamage(damage);
        this.gainEnergy(30);
        this.isDefending = false;
    }

    public void defend() {
        System.out.println(this.name + " đang phòng thủ!");
        this.isDefending = true;
    }

    public void useSkill(int skillIndex, Stickman target) {
        Skill skill = skills.get(skillIndex);

        if (this.energy >= skill.getEnergyCost()) {
            this.energy -= skill.getEnergyCost();
            target.takeDamage(skill.getBaseDamage());
            this.isDefending = false;
        } else {
            System.out.println(this.name + " không đủ năng lượng!");
        }
    }

    public void takeDamage(int amount) {
        int finalDamage = amount;

        if (this.isDefending) {
            finalDamage *= 0.5;
            System.out.println(this.name + " đã đỡ! Sát thương giảm còn " + finalDamage);
            this.isDefending = false;
        }

        finalDamage = (int)(finalDamage * (1.0 - this.defense));

        this.hp -= finalDamage;
        if (this.hp < 0) {
            this.hp = 0;
        }
        System.out.println(this.name + " nhận " + finalDamage + " sát thương. Còn lại " + this.hp + " HP.");
    }

    public void gainEnergy(int amount) {
        this.energy += amount;
        if (this.energy > maxEnergy) {
            this.energy = maxEnergy;
        }
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public double getHpPercent() {
        return (double)hp / maxHp;
    }

    public double getEnergyPercent() {
        return (double)energy / maxEnergy;
    }

    public void setState(State newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            this.currentIdleFrame = 0;
            this.currentAttackFrame = 0;
            this.currentDefendFrame = 0;
            this.currentSkill1Frame = 0;
            this.currentSkill2Frame = 0;
        }
    }

    public Image getCurrentFrameImage() {
        switch (currentState) {
            case ATTACKING:
                if (attackFrames != null && numAttackFrames > 0)
                    return attackFrames[currentAttackFrame];
                break;
            case DEFENDING:
                if (defendFrames != null && numDefendFrames > 0)
                    return defendFrames[currentDefendFrame];
                break;
            case SKILL1:
                if (skill1Frames != null && numSkill1Frames > 0)
                    return skill1Frames[currentSkill1Frame];
                break;
            case SKILL2:
                if (skill2Frames != null && numSkill2Frames > 0)
                    return skill2Frames[currentSkill2Frame];
                break;
            case IDLE:
            default:
                if (idleFrames != null && numIdleFrames > 0)
                    return idleFrames[currentIdleFrame];
                break;
        }
        return null;
    }

    public void updateAnimation() {
        switch (currentState) {
            case ATTACKING:
                if (numAttackFrames > 0) {
                    currentAttackFrame = (currentAttackFrame + 1) % numAttackFrames;
                }
                break;
            case DEFENDING:
                if (numDefendFrames > 0) {
                    currentDefendFrame = (currentDefendFrame + 1) % numDefendFrames;
                }
                break;
            case SKILL1:
                if (numSkill1Frames > 0) {
                    currentSkill1Frame = (currentSkill1Frame + 1) % numSkill1Frames;
                }
                break;
            case SKILL2:
                if (numSkill2Frames > 0) {
                    currentSkill2Frame = (currentSkill2Frame + 1) % numSkill2Frames;
                }
                break;
            case IDLE:
            default:
                if (numIdleFrames > 0) {
                    currentIdleFrame = (currentIdleFrame + 1) % numIdleFrames;
                }
                break;
        }
    }

    public int getFrameCount(State state) {
        switch (state) {
            case ATTACKING: return numAttackFrames;
            case SKILL1:    return numSkill1Frames;
            case SKILL2:    return numSkill2Frames;
            case DEFENDING: return numDefendFrames;
            default:        return numIdleFrames;
        }
    }

    public String getAttackIconPath() {
        return attackIconPath;
    }

    public String getDefendIconPath() {
        return defendIconPath;
    }

    protected abstract void initializeSkills();
}