import javax.swing.*;
import java.awt.*;

/**
 * 砖块类
 */
public class Brick extends JComponent {

    /** Width of a brick */
    private int BRICK_WIDTH;
    /** Height of a brick */
    public static final int BRICK_HEIGHT = 20;
    /** 是否存在,true为存在,false为消失 */
    private boolean isAlive = true;
    /** brick的生命值 */
    private int brickHP = 1;

    private int x, y;
    private Color color;

    /** 设定的tan值 */
    double brickTan=0;

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (isAlive) {
            g2.setColor(color);
            g2.fillRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }
    }

    /** 碰撞时调用,进行一次血量判定
     * @param damage 小球碰撞一次的伤害值
     * @return 血量为空,返回true,反之有剩余血量返回false*/
    public boolean hpCheck(int damage){//如果返回false,则说明该brick还有剩余血量
        this.brickHP -= damage;
        if(this.brickHP<=0){//血量为空
            isAlive=false;//该砖块消失
            return true;
        }
        return false;
    }

    public void setBrickHP(int brickHP) {
        this.brickHP = brickHP;
    }

    public Brick() {

    }

    /** 计算并更新brickTan中心到各顶点的tan值 */
    public void countBrickTan(){
        brickTan = (double)BRICK_HEIGHT/BRICK_WIDTH;
        System.out.println("brickTan" + brickTan);
    }

    public int getBRICK_WIDTH() {
        return BRICK_WIDTH;
    }

    /** 根据生命值自动设置brick样式*/
    public void setAutoColor(){
        switch(brickHP){//根据生命值设定颜色

        }
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
}
