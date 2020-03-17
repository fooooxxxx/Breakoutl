import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class BreakoutComponents extends JComponent {
    Paddle paddle;
    Ball ball;
    ArrayList<Brick> bricks;
    JLabel showLabel;//用于显示游戏信息,比如血量等
    BreakoutComponents(Paddle paddle, Ball ball,ArrayList<Brick> bricks) {
        this.paddle = paddle;
        paddle.setVisible(true);
        this.ball = ball;
        this.bricks = bricks;
        this.showLabel = new JLabel("游戏尚未开始");
        this.setLayout(null);//使用绝对布局
        this.showLabel.setBounds(20,0,100,30);
        add(showLabel);
    }

    /** 根据参数更新显示出来的游戏信息
     * @param healthPoint 当前的游戏血量
     * @param score 当前游戏分数*/
    public void updateHpAndScore(int healthPoint,int score){
        showLabel.setText("生命:"+healthPoint +"    分数" + score);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
        for(Brick brick : bricks){
            brick.draw(g);
        }
    }

}
