import javax.swing.*;
import java.awt.*;

/**
 * 砖块类
 */
public class Brick extends JComponent {

    /** Width of a brick */
    private int BRICK_WIDTH;
    /** Height of a brick */
    public static final int BRICK_HEIGHT = 26;
    /** 是否存在,true为存在,false为消失 */
    private boolean isAlive = true;
    /** brick的生命值 */
    private int brickHP = 1;
    /** 消除该brick获得的分数 */
    private int brickScore = 1;



    private boolean isDestroyable;//是否可以被破坏,不可破坏的砖块不计入胜利条件内
    private int x, y;
    private Color color;

    /** 设定的tan值 */
    double brickTan = 0;

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (isAlive) {//brick是否存活
            g2.setColor(color);
            g2.fillRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }
        ;
    }

    /**
     * 碰撞时调用,进行一次血量判定
     *
     * @param damage 小球碰撞一次的伤害值
     * @return 小球血量为空, 被击碎, 返回true, 反之有剩余血量返回false
     */
    public boolean hpCheck(int damage) {//如果返回false,则说明该brick还有剩余血量
        this.brickHP -= damage;
        if (this.brickHP <= 0) {//血量为空
            isAlive = false;//该砖块消失
            return true;
        }
        return false;
    }

    public void setBrickHP(int brickHP) {
        this.brickHP = brickHP;
        this.brickScore = brickHP * 2;
    }

    public Brick(boolean isDestroy) {
        this.isDestroyable = isDestroy;
    }

    /**默认构造函数将生成可破坏的砖块*/
    public Brick() { this.isDestroyable = true;}

    /** 计算并更新brickTan中心到各顶点的tan值 */
    public void countBrickTan() {
        brickTan = (double) BRICK_HEIGHT / BRICK_WIDTH;
        System.out.println("brickTan" + brickTan);
    }

    public int getBRICK_WIDTH() {
        return BRICK_WIDTH;
    }

    /** 根据生命值自动设置brick样式 */
    public void setAutoColor() {
        if (brickHP != 0 && isDestroyable) {
            switch (brickHP) {//根据生命值设定颜色
                case 3:
                    setColor(Color.BLUE);
                    break;
                case 2:
                    setColor(Color.GREEN);
                    break;
                case 1:
                    setColor(Color.RED);
                    break;
                default:
                    setColor(Color.BLACK);//
            }
        }
        else
            setColor(Color.GRAY);//不可破坏砖块为灰色
    }

    public int getBrickScore() {
        return brickScore;
    }

    public void setBRICK_WIDTH(int BRICK_WIDTH) {
        this.BRICK_WIDTH = BRICK_WIDTH;
    }

    public static int getBrickHeight() {
        return BRICK_HEIGHT;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getCenterX(){ return x+BRICK_WIDTH/2; }

    public int getCenterY(){ return y+BRICK_HEIGHT/2; }

    public boolean getDestroyable(){
        return isDestroyable;
    }

    public void setDestroyable(boolean destroyable) {
        isDestroyable = destroyable;
    }
}
