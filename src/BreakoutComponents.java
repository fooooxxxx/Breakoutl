import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class BreakoutComponents extends JComponent {
    Paddle paddle;
    //Ball ball;
    ArrayList<Brick> bricks;
    CopyOnWriteArrayList<GameItem> items;
    CopyOnWriteArrayList<Ball> balls;
    EnergyAdder energyAdder;
    JLabel showLabel;//用于显示游戏信息,比如血量等
    JLabel itemLabel;//显示道具效果
    Random random;//随机数生成器
    final static int itemProbability = 90;//击碎brick后生成道具概率,100为100%,0为0%
    Font showFont;//显示游戏信息的字体
    //技能特效相关
    JLabel railGunLabel;
    Image backgroundImage;

    BreakoutComponents(Paddle paddle, CopyOnWriteArrayList<Ball> balls, ArrayList<Brick> bricks, CopyOnWriteArrayList<GameItem> items,EnergyAdder energyAdder) {
        this.paddle = paddle;
        paddle.setVisible(true);
        this.balls = balls;
        this.bricks = bricks;
        this.items = items;
        this.energyAdder = energyAdder;
        this.showLabel = new JLabel("游戏尚未开始");
        this.itemLabel = new JLabel("尚未获得道具");
        this.setLayout(null);//使用绝对布局
        //游戏信息显示模块
        showFont = new Font("黑体",Font.PLAIN,12);
        this.showLabel.setBounds(480, 870, 120, 30);
        this.itemLabel.setBounds(380, 870, 100, 30);
        this.showLabel.setFont(showFont);
        this.itemLabel.setFont(showFont);
        add(showLabel);
        add(itemLabel);
        add(energyAdder.scoreMultipleLabel);
        add(energyAdder.skillDescriptionLabel);
        for(Skill skillOne :energyAdder.skillLabelList)
            add(skillOne);
        random = new Random();

        backgroundImage = new ImageIcon("src/image/backgroundImage.png").getImage();
    }

    /**
     * 根据参数更新显示出来的游戏信息
     *
     * @param healthPoint 当前的游戏血量
     * @param score       当前游戏分数
     */
    public void updateHpAndScore(int healthPoint, int score) {
        showLabel.setText("生命:" + healthPoint + "    分数" + score);
    }

    /**
     * 当获得道具时调用该函数显示道具效果
     *
     * @param message 需要显示的道具效果
     */
    public void updateItemMessage(String message) {
        itemLabel.setText(message);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage,0,0,this);//绘制背景图片
        drawSkill(g);
        paddle.draw(g);
        for (Ball ball : balls) {
            ball.draw(g);
        }
        for (Brick brick : bricks) {
            brick.draw(g);
        }
        for (GameItem item : items) {
            item.draw(g);
        }
        energyAdder.draw(g);

    }

    /**绘制一些复杂的技能效果*/
    void drawSkill(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        if(JBreakout.skillTypeUsing!=-1 ){//判断是否有复杂技能在释放
            switch(JBreakout.skillTypeUsing){
                case 1://轨道炮技能效果绘制
                    if(JBreakout.skillTimeCounter > 210){
                        g2.setColor(Color.RED);//红色线
                        int leftX =(int)((1.0*JBreakout.skillTimeCounter - 210)/110*Paddle.PADDLE_WIDTH/2);//左侧红线X轴与paddleX轴坐标的差
                        g2.drawLine(paddle.getCenterX()-leftX,0,paddle.getCenterX()-leftX,paddle.getY()-1);
                        g2.drawLine(paddle.getCenterX()+leftX,0,paddle.getCenterX()+leftX,paddle.getY()-1);
                    }
                    else{//然后绘制轨道炮图片
                        if(JBreakout.skillTimeCounter == 210){//初始化只执行一次
                            railGunLabel = new JLabel(new ImageIcon("src/image/railGunFire.gif"));
                            railGunLabel.setBounds(paddle.getCenterX()-40,0,80,810);
                            add(railGunLabel);
                            System.out.println("轨道炮图片加载一次");
                        }
                        else {
                            if (JBreakout.skillTimeCounter <= 10) {//结束动画
                                railGunLabel.setVisible(false);
                                remove(railGunLabel);
                            } else//修正位置
                                railGunLabel.setBounds(paddle.getCenterX() - 40, 0, 80, 810);
                        }

                    }

                    break;
            }
        }
    }

    /**
     * JBreakout击碎道具后调用,判定道具是否生成,以及道具的种类
     *
     * @param dieBrick 被击碎的Brick,用于获取坐标和高度计算道具生成位置
     * @return 返回值为true表示道具生成, false表示道具没有生成
     */
    public boolean generateItem(Brick dieBrick) {
        if (random.nextInt(100) + 1 <= itemProbability) {//如果随机数小于等于itemProbability,则代表道具成功生成
            System.out.println("道具成功生成");
            //道具x轴坐标计算公式为 brickX+(brickW-itemW)/2
            items.add(new GameItem(dieBrick.getX() + (dieBrick.getBRICK_WIDTH() - GameItem.ITEM_WIDTH) / 2, dieBrick.getY()));
            return true;
        } else {
            System.out.println("道具没有生成");
            return false;
        }
    }


}
