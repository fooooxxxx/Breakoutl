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

    /** 判断小球是否碰撞函数
     * @param object_x 被判断物体的x轴起始坐标
     * @param object_y 被判断物体的y轴起始坐标
     * @param object_height 被判断物体的高度
     * @param object_width 被判断物体的宽度
     * @return 返回true,则表示发生了碰撞,否则无碰撞*/
    public boolean collide(int object_x,int object_y,int object_width,int object_height){
        if(this.x+2*BALL_RADIUS>object_x && this.x<object_x+object_width
                && this.y+2*BALL_RADIUS > object_y && this.y<object_y+object_height){
            return true;
        }
        return false;
    }


}
