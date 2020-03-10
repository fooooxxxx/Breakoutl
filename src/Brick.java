import javax.swing.*;
import java.awt.*;

/**
 * 砖块类
 */
public class Brick extends JComponent {

    /** Width of a brick */
    private int BRICK_WIDTH;
    /** Height of a brick */
    public static final int BRICK_HEIGHT = 12;
    /** 是否存在 */
    private boolean isAlive = true;

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
