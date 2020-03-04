import javax.swing.*;
import java.awt.*;

/**
 * Ball类
 */
public class Ball extends JComponent {
    //小球半径
    private static final int BALL_RADIUS = 10;
    //小球初始位置
    private int x = 100;
    private int y = 100;
    //小球在x和y轴上的初始速度
    private int vx = 1;
    private int vy = 1;

    /** 设置小球初始位置在Paddle的顶部*/
    public void setStartBallPosition(){

    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        //绘制小球
        g2.drawOval(x,y,BALL_RADIUS*2,BALL_RADIUS*2);
    }

    /** 小球移动函数*/
    public void moveAndBounce() {
        x += vx;
        y += vy;
        if (x < 0) {
            x = 0;
            rebounceX();
        }
        if (x + 2 * BALL_RADIUS > JBreakout.realWidth) {
            x = JBreakout.realWidth - 2 * BALL_RADIUS;
            rebounceX();
        }
        if (y < 0) { y = 0;rebounceY(); }
        if(y+2*BALL_RADIUS > JBreakout.realHeight){
            y = JBreakout.realHeight - 2*BALL_RADIUS;
            rebounceY();
        }
    }

    /** 遇到左右墙的反弹*/
    public void rebounceX(){
        vx = -vx;
    }

    /** 遇到上下墙的反弹*/
    public void rebounceY(){
        vy = -vy;
    }



}
