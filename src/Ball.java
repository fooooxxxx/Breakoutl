import javax.swing.*;
import java.awt.*;

/**
 * Ball类
 */
public class Ball extends JComponent implements CollideInterface{
    //小球半径
    private static final int BALL_RADIUS = 5;
    //小球初始位置
    private int x = 200;
    private int y = 200;
    //小球在x和y轴上的初始速度
    private int vx = 3;
    private int vy = 3;

    /** ball构造函数
     * @param vx x轴小球速度
     * @param vy y轴小球速度*/
    Ball(int vx,int vy){
        this.vx = vx;
        this.vy = vy;
    }
    Ball(){

    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        //绘制小球
        g2.drawOval(x,y,BALL_RADIUS*2,BALL_RADIUS*2);
    }

    /** 小球移动函数,并且处理与四边的碰撞
     * @return 返回值代表该球是否触底,false表示场上非唯一的ball触底了,需要移除,true表示不需要或者这是最后一个小球 */
    public boolean moveAndBounce() {
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
        if(y+2*BALL_RADIUS > JBreakout.realHeight){//球碰到底部
            if(JBreakout.ballNum == 1){//如果这是场上唯一的ball,则扣血
                JBreakout.isBallLaunching = false;
                JBreakout.healthPoint-=1;
            }
            else{
                JBreakout.ballNum--;//减少JBreakout的数量
                return false;//返回一个false,以便JBreakout处理删除该球
            }
        }
        return true;
    }

    /** 遇到左右墙的反弹 ,x轴速度逆转*/
    public void rebounceX(){
        vx = -vx;
    }

    /** 遇到上下墙的反弹, y轴速度逆转*/
    public void rebounceY(){
        vy = -vy;
    }

    /** 判断小球是否碰撞函数
     * @param object_x 被判断物体的x轴起始坐标
     * @param object_y 被判断物体的y轴起始坐标
     * @param object_height 被判断物体的高度
     * @param object_width 被判断物体的宽度
     * @return 返回true,则表示发生了碰撞,否则无碰撞*/
    @Override
    public boolean collide(int object_x,int object_y,int object_width,int object_height){
        if(this.x+2*BALL_RADIUS>object_x && this.x<object_x+object_width
                && this.y+2*BALL_RADIUS > object_y && this.y<object_y+object_height){//判断是否发生碰撞
            return true;
        }
        return false;
    }

    /** 获得圆心X轴坐标 */
    int getBallCenterX(){
        return x+BALL_RADIUS/2;
    }

    /** 获得圆心Y轴坐标 */
    int getBallCenterY(){
        return y+BALL_RADIUS/2;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static int getBallRadius() {
        return BALL_RADIUS;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
