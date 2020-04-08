import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class JBreakout extends JFrame {
    //游戏参数
    public static final int APPLICATION_WIDTH = 616;
    public static final int APPLICATION_HEIGHT = 939;
    /** 血量 */
    public static int healthPoint = 0;
    /** 当期分数 */
    public static int score = 0;
    /** 小球数量 */
    public static int ballNum = 1;//当小球数量为0时,生命值减一
    //游戏面板实际宽高
    public static int realWidth = 0;
    public static int realHeight = 0;

    /** 每层砖块的数量 */
    private static final int BRICKS_PER_ROW = 10;
    /** 层数 */
    private static final int BRICK_ROWS = 10;
    /** 砖块之间的间隔 */
    private static final int BRICK_SEP = 2;
    //变量
    BreakoutComponents breakoutComponents;
    Paddle paddle;
    //Ball ball;
    ArrayList<Brick> bricks;//砖块
    MainMenu mainMenu;//主菜单JPanel
    Timer mainTimer;
    CopyOnWriteArrayList<GameItem> items;//道具列表
    Iterator<GameItem> itemIterator;//道具迭代器
    CopyOnWriteArrayList<Ball> balls;//小球列表


    static boolean isBallLaunching = false;//球是否已经发射
    static boolean isGameStart = false;//游戏是否开始

    //字体变量
    Font ggFont = new Font("黑体", Font.BOLD, 18);

    public JBreakout() {
        //设置窗体大小
        setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        //设置窗体标题
        setTitle("Breakout");


        //设置点击关闭按钮关闭程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体大小不可改变
        setResizable(false);

        //初始化组件
        mainMenu = new MainMenu(this);//主菜单JPanel

        //添加组件
        mainMenu.setVisible(true);
        add(mainMenu);

        //startGame();

    }

    /** 开始游戏,设置监听,并且启用监听 */
    public void startGame() {
        mainMenu.setVisible(false);
        healthPoint = 3;//初始血量为3
        score = 0;//清空分数
        paddle = new Paddle();
        balls = new CopyOnWriteArrayList<>();
        balls.add(new Ball());
        bricks = initBricks();//生成砖块
        items = new CopyOnWriteArrayList<>();

        preSound();//音频预加载
        breakoutComponents = new BreakoutComponents(paddle, balls, bricks, items);
        add(breakoutComponents);
        breakoutComponents.setVisible(true);
        breakoutComponents.requestFocus();//强制获取焦点
        breakoutComponents.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        paddle.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        paddle.moveRight();
                        break;
                    case KeyEvent.VK_SPACE:
                        launchBall();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        paddle.moveLeftCancel();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        paddle.moveRightCancel();
                        break;
                }
            }
        });  //添加监听事件
        //定时器
        mainTimer = new Timer();
        mainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (healthPoint == 0) {
                    System.out.println("生命不足,游戏结束");
                    gameOver(1);
                }
                if (!isBallLaunching) {
//                    if(balls.size()==0){//当场上无小球时,生成小球
//                        balls.add(new Ball());
//                    }
                    balls.get(0).setSpeed(3, 3);//对唯一的ball设置速度
                    setStartBallPosition();//如果游戏开始,但是小球尚未发射,小球就会跟着paddle移动
                    items.clear();//清空场上所有道具
                }
                breakoutComponents.repaint();
                updateBrickWidth();
                boolean winFlag = true;//如果有砖块存活,改为false
                for (Ball ball : balls) {
                    if (ball.moveAndBounce()) {//球的移动,以及对墙碰撞
                        for (Brick brickOne : bricks) {//对砖块撞击判定
                            if (brickOne.isAlive()) {//如果brick存在
                                winFlag = false;//还有brick存在,游戏尚未结束
                                if (ball.collide(brickOne.getX(), brickOne.getY(), brickOne.getBRICK_WIDTH(), Brick.BRICK_HEIGHT)) {//如果和brick发生碰撞
                                    if (judgeCollideDirection(ball, brickOne.brickTan, brickOne.getX(), brickOne.getY()))//判断方向
                                        ball.rebounceX();
                                    else
                                        ball.rebounceY();
                                    /* 进行一次伤害判定,默认伤害为1;如果球被击碎,调用道具生成函数;道具在场上数量不能超过2,连续击碎下无效  */
                                    if (brickOne.hpCheck(1) && items.size() < 3) {
                                        breakoutComponents.generateItem(brickOne);

                                    }
                                    else
                                        soundPlay(1);//播放击中音效
                                    break;
                                }
                                brickOne.setAutoColor();//根据生命值自动设置颜色
                            }
                        }
                        if (ball.collide(paddle.getX(), paddle.getY(), Paddle.getPaddleWidth(), Paddle.getPaddleHeight())) {
                            ball.setY(ball.getY() - 5);//防止ball与paddle进行多次碰撞
                            ball.rebounceY();
                        }
                    } else {//如果小球到底部且不是唯一的小球,则对小球进行移除
                        balls.remove(ball);
                    }
                }
                if (winFlag) gameOver(0);
                itemIterator = items.iterator();
                while (itemIterator.hasNext()) {//判断item是否和paddle碰撞,以及移除到底部的item
                    GameItem itemTemp = itemIterator.next();
                    if (itemTemp.itemMove() == -1) {//如果类型为-1,则说明已经到底部,需要移除
                        items.remove(itemTemp);
                    } else {
                        if (itemTemp.collide(paddle.getX(), paddle.getY(), Paddle.getPaddleWidth(), Paddle.getPaddleHeight())) {
                            //如果道具碰到paddle道具,并且移除
                            itemUse(itemTemp.itemType);
                            soundPlay(3);
                            items.remove(itemTemp);
                        }
                    }
                }

                breakoutComponents.updateHpAndScore(healthPoint, score);//更新显示出来的数据
            }
        }, 0, 14);
        setStartPosition();
    }

    /**
     * 生命值归零,或者玩家主动退出时调用
     *
     * @param result 0为胜利,1为失败
     */
    public void gameOver(int result) {//弹出结束画面,并且记录分数
        mainTimer.cancel();//关闭定时器,游戏结束
        JLabel ggLabel = new JLabel("游戏结束,你的分数为 " + score);
        JButton backBtn = new JButton("返回主菜单");
        ggLabel.setBounds(180, 350, 300, 100);
        ggLabel.setFont(ggFont);
        ggLabel.setVisible(true);
        backBtn.setBounds(200, 450, 200, 100);
        backBtn.setFont(ggFont);//设置字体
        if (result == 0) {//游戏胜利的提示
            ggLabel.setText("游戏胜利!你的分数为 " + score);
        }
        breakoutComponents.add(backBtn);
        breakoutComponents.add(ggLabel);
        backBtn.addActionListener(e -> {
            breakoutComponents.setVisible(false);
            mainMenu.setVisible(true);
        });
        breakoutComponents.repaint();//进行重绘,直接显示ggLabel
    }

    public void launchBall() {//按空格后启动小球
        isBallLaunching = true;
    }

    /** 设置游戏面板实际大小 */
    public void setRealWidthHeight() {
        realWidth = this.getContentPane().getWidth();
        realHeight = this.getContentPane().getHeight();

        System.out.println("游戏面板 宽度为" + realWidth + "  高度为" + realHeight);
    }

    /** 该方法需要在JBreakout初始化完成后调用 用于设置paddle起始位置 */
    public void setStartPosition() {
        breakoutComponents.paddle.setStartPosition();
    }


    /**
     * 初始化brick.并且有随机生命值
     *
     * @return 返回初始化完成的bricks
     */
    private ArrayList<Brick> randInitBricks() {
        //ArrayList<Brick> bricks = new ArrayList<>();


        return bricks;
    }

    /**
     * 正常初始化brick
     *
     * @return 返回初始化完成的bricks
     */
    private ArrayList<Brick> initBricks() {
        ArrayList<Brick> bricks = new ArrayList<>();
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICKS_PER_ROW; j++) {
                Brick brick = new Brick();
                switch (i + 1) {
                    case 1:
                    case 2:
                    case 7:
                    case 8:
                    case 9:
                        brick.setBrickHP(1);
                        break;
                    case 3:
                    case 4:
                        brick.setBrickHP(3);
                        break;
                    case 5:
                    case 6:
                    case 10:
                        brick.setBrickHP(2);
                        break;
                }


                bricks.add(brick);
            }
        }
        return bricks;
    }

    /** 用于更新brick坐标和宽度 */
    private void updateBrickWidth() {
        int i = 0, j = 0;
        int BRICK_WIDTH = (JBreakout.realWidth - (BRICKS_PER_ROW - 1) * BRICK_SEP) / BRICKS_PER_ROW;
        for (Brick brick : bricks) {
            brick.setBRICK_WIDTH(BRICK_WIDTH);
            //x,y为砖块坐标
            int x = j * BRICK_WIDTH + BRICK_SEP * (j + 1);
            brick.setX(x);
            int y = i * Brick.BRICK_HEIGHT + BRICK_SEP * i + 30;//加的30为了给血量显示面板留出空间
            brick.setY(y);
            j++;
            if (j == 10) {
                j = 0;
                i++;
            }
            if (brick.brickTan != 0) brick.countBrickTan();
        }
    }

    /**
     * 判断小球反弹方向
     *
     * @param ball   球对象
     * @param setTan 设定的tan值,由被碰撞物体提供
     * @param objX   被碰撞物体中心点的X轴坐标
     * @param objY   被碰撞物体中心点的Y轴坐标
     * @return true则进行逆转x轴速度的反弹, 为false进行逆转y轴速度的反弹
     */
    public boolean judgeCollideDirection(Ball ball, double setTan, int objX, int objY) {
        double tempTan = (double) (objY - ball.getBallCenterY()) / (objX - ball.getBallCenterX());
        System.out.println("碰撞判定的tan为" + tempTan);
        if (tempTan < setTan) {
            System.out.println("逆转X");
        } else {
            System.out.println("逆转Y");
        }
        return tempTan < setTan;//如果tempTan小于setTan,则说明他们在左右面发生了碰撞,否则表面在上下面发生碰撞
    }

    /** 设置小球初始位置在Paddle的中间顶部 */
    public void setStartBallPosition() {
        balls.get(0).setX(paddle.getX() + Paddle.PADDLE_WIDTH / 2 - Ball.getBallRadius());
        balls.get(0).setY(paddle.getY() - 2 * Ball.getBallRadius());
    }

    /**
     * 当道具碰到paddle时调用
     *
     * @param itemType 道具类型号,决定道具效果
     */
    public void itemUse(int itemType) {
        System.out.println("道具" + itemType + "get√");
        switch (itemType) {
            case 1:
                breakoutComponents.updateItemMessage("挡板长度增加了!");
                paddle.updatePaddleWidth(2 * Paddle.oldWidth, 8000);
                break;
            case 2:
                ballSplit();
                breakoutComponents.updateItemMessage("小球分裂了!");
                break;
            case 3:
                healthPoint++;
                breakoutComponents.updateItemMessage("增加一条生命值!");
                System.out.println("生命值提升了");
                break;
        }
    }

    /** 小球分裂函数 */
    public void ballSplit() {
        Random randBall = new Random();
        Ball ballOne = balls.get(0);//获得第一个球
        int[] directionInt = ballOne.getSpeedDirection();
        balls.add(new Ball(ballOne.getX(), ballOne.getY(), (randBall.nextInt(4) + 2) * directionInt[0], (randBall.nextInt(4) + 2) * directionInt[1]));
        balls.add(new Ball(ballOne.getX(), ballOne.getY(), (randBall.nextInt(4) + 2) * directionInt[0], (randBall.nextInt(4) + 2) * directionInt[1]));
        ballNum += 2;
    }

    /** 预先对音效进行加载 */
    public void preSound() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/itemGetSound.wav")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常抛出,音乐文件未找到");
        }
    }

    /** 各种音效的播放
     * @param soundType 音效类型,1为击中音效,2为击碎音效,3为获得道具音效 */
    public void soundPlay(int soundType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    switch(soundType) {
                        case 1:
                            System.out.println("播放hit音效");
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/hit.wav")));
                            /* https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java */
                            break;
                        case 2:
                            break;
                        case 3:
                            System.out.println("播放获得道具音效");
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/itemGetSound.wav")));
                            break;
                    }
                    clip.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常抛出,音乐文件未找到");
                }
            }
        }).start();
    }
}

