import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class BreakoutComponents extends JComponent {
    Paddle paddle;
    Ball ball;
    ArrayList<Brick> bricks;
    ArrayList<GameItem> items;
    JLabel showLabel;//用于显示游戏信息,比如血量等
    Random random;//随机数生成器
    final static int itemProbability = 90;//击碎brick后生成道具概率,100为100%,0为0%


    BreakoutComponents(Paddle paddle, Ball ball,ArrayList<Brick> bricks,ArrayList<GameItem> items) {
        this.paddle = paddle;
        paddle.setVisible(true);
        this.ball = ball;
        this.bricks = bricks;
        this.items = items;
        this.showLabel = new JLabel("游戏尚未开始");
        this.setLayout(null);//使用绝对布局
        this.showLabel.setBounds(20,0,100,30);
        add(showLabel);
        random = new Random();
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
        for(GameItem item : items){
            item.draw(g);
        }
    }

    /** JBreakout击碎道具后调用,判定道具是否生成,以及道具的种类
     * @param dieBrick 被击碎的Brick,用于获取坐标和高度计算道具生成位置
     *  @return 返回值为true表示道具生成,false表示道具没有生成*/
    public boolean generateItem(Brick dieBrick){
        if(random.nextInt(100)+1 <= itemProbability){//如果随机数小于等于itemProbability,则代表道具成功生成
            System.out.println("道具成功生成");
            //道具x轴坐标计算公式为 brickX+(brickW-itemW)/2
            items.add(new GameItem(dieBrick.getX()+(dieBrick.getBRICK_WIDTH()-GameItem.ITEM_WIDTH)/2,dieBrick.getY()));
            return true;
        }
        else{
            System.out.println("道具没有生成");
            return false;
        }
    }



}
