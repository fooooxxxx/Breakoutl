import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class JBreakout extends JFrame implements CastSkill {
    //游戏参数
    public static final int APPLICATION_WIDTH = 616;
    public static final int APPLICATION_HEIGHT = 939;
    /** 每层砖块的数量 */
    private static final int BRICKS_PER_ROW = 10;
    /** 砖块之间的间隔 */
    private static final int BRICK_SEP = 2;
    /** 砖块距离顶部距离 */
    public static final int BRICK_OFFSET_TOP = 30;
    /** 道具数量上限 */
    final int ITEM_LIMIT = 4;
    /** 初始生命值 */
    final int INIT_HEALTH_POINT = 3;
    //游戏面板实际宽高
    public static int realWidth = 0;
    public static int realHeight = 0;
    public static String playerName = null;
    /*技能相关*/
    static int skillCoolDown = 0;//技能冷却CD,当该值为0时才能释放技能
    static int skillTypeUsing = -1;//使用中的技能类型,如果为-1,说明当前没有技能在释放中
    static int skillTimeCounter = 0;//技能时间计数器,使用这个来设定持续性技能的生效,以及部分技能的动画效果
    static boolean isBallLaunching;//球是否已经发射
    /*随机关卡生成设定*/
    static boolean isRandomMap = false;
    final boolean isSymmetry = true;//默认对称
    //变量
    /** 血量 */
    public static int healthPoint = 0;
    /** 当前分数 */
    public static int score = 0;
    /** 层数 */
    private static int BRICK_ROWS = 10;
    /** 小球数量 */
    public static int ballNum = 1;//当小球数量为0时,生命值减一
    BreakoutComponents breakoutComponents;
    Paddle paddle;
    //Ball ball;
    ArrayList<Brick> bricks;//砖块
    CopyOnWriteArrayList<Ball> balls;//小球列表
    EnergyAdder energyAdder;//能量加成器
    MainMenu mainMenu;//主菜单JPanel
    //static boolean isGameStart = false;//游戏是否开始
    Timer mainTimer;
    /*道具相关*/
    CopyOnWriteArrayList<GameItem> items;//道具列表
    Iterator<GameItem> itemIterator;//道具迭代器
    /** 距离下一次小球启动自动锁定倒计时 */
    int autoLockBrickCountDown;
    //字体变量
    Font ggFont = new Font("黑体", Font.BOLD, 18);
    //字体颜色
    Color fontColor;
    BoSql sqlConn;

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
        fontColor = new Color(232, 92, 17);
        //添加组件

        mainMenu.setVisible(true);
        add(mainMenu);

        sqlConn = new BoSql();
        //startGame();

    }

    /** 开始游戏,设置监听,并且启用监听 */
    public void startGame() {
        mainMenu.setVisible(false);
        autoLockBrickCountDown = 5;
        ballNum = 1;
        healthPoint = 1;//初始血量为3
        score = 0;//清空分数
        skillTypeUsing = -1;
        //构建对象
        paddle = new Paddle();
        balls = new CopyOnWriteArrayList<>();
        balls.add(new Ball());
        energyAdder = new EnergyAdder(this);
        bricks = isRandomMap ? randInitBricks(isSymmetry) : initBricks();//生成砖块
        items = new CopyOnWriteArrayList<>();
        isBallLaunching = false;//将小球设置为未发射状态
        preSound();//音频预加载
        breakoutComponents = new BreakoutComponents(paddle, balls, bricks, items, energyAdder);
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
                        System.out.println("按下并释放了空格");
                        if (!isBallLaunching)//如果不处于发射状态,则进行发射
                            launchBall();
                        else {//否则释放技能
                            int tempResult = energyAdder.useSkill();
                            if (tempResult == 1) {
                                soundPlay(6);
                                System.out.println("能量不足,技能释放失败");
                            } else if (tempResult == 2) {
                                soundPlay(6);
                                System.out.println("技能在CD当中");
                            }
                        }
                        break;
                    case KeyEvent.VK_P://暂停按钮
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        breakoutComponents.energyAdder.switchSkill(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        breakoutComponents.energyAdder.switchSkill(1);
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
        updateBrickWidth();
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
                    balls.get(0).setSpeed(3, 3);//对唯一的ball设置速度
                    setStartBallPosition();//如果游戏开始,但是小球尚未发射,小球就会跟着paddle移动
                    items.clear();//清空场上所有道具
                }
                breakoutComponents.repaint();

                for (Ball ball : balls) {
                    if (ball.moveAndBounce()) {//球的移动,以及对墙碰撞
                        for (Brick brickOne : bricks) {//对砖块撞击判定
                            if (brickOne.isAlive()) {//如果brick存在
                                if (ball.collide(brickOne.getX(), brickOne.getY(), brickOne.getBRICK_WIDTH(), Brick.BRICK_HEIGHT)) {//如果和brick发生碰撞
                                    if (judgeCollideDirection(ball, brickOne.brickTan, brickOne.getCenterX(), brickOne.getCenterY()))//判断方向
                                        ball.rebounceX();
                                    else
                                        ball.rebounceY();
                                    /* 进行一次伤害判定,默认伤害为1;如果球被击碎,调用道具生成函数;道具在场上数量不能超过2,连续击碎下无效  */
                                    if (brickOne.getDestroyable()) {//碰到可被摧毁的砖块
                                        if (brickOne.hpCheck(ball.getBallDamage())) {//如果击碎砖块
                                            if (items.size() < ITEM_LIMIT) breakoutComponents.generateItem(brickOne);
                                            //击碎时增加一点分数能量
                                            score += energyAdder.addEnergy(1) * brickOne.getBrickScore();//加分
                                            energyAdder.reduceEnergy(220);//能量短时间内不再泄漏
                                            soundPlay(2);//播放击碎音效
                                        } else {//没有击碎砖块
                                            energyAdder.addEnergy(1);//击中时增加一点分数能量
                                            energyAdder.reduceEnergy(130);//能量短时间内不再泄漏
                                            soundPlay(1);//播放击中音效
                                        }
                                        if (skillTypeUsing == 3) soundPlay(5);//如果处于双倍伤害状态,播放不同的音效
                                        resetAutoLockBrick();//命中砖块,重置自动锁定装置
                                        break;
                                    } else {//碰到不可被摧毁的砖块
                                        energyAdder.reduceEnergy(130);//能量短时间内不再泄漏
                                    }
                                }
                                brickOne.setAutoColor();//根据生命值自动设置颜色
                            }
                        }
                        if (ball.collide(paddle.getX(), paddle.getY(), Paddle.getPaddleWidth(), Paddle.getPaddleHeight())) {//判断是否与paddle
                            ball.setY(ball.getY() - 5);//防止ball与paddle进行多次碰撞
                            ball.rebounceY();
                            if (isBallLaunching) autoLockBrick(ball);//小球长时间没击中砖块时,将锁定一个砖块
                        }
                    } else {//如果小球到底部且不是唯一的小球,则对小球进行移除
                        balls.remove(ball);
                    }
                }
                if (winCheck()) gameOver(0);
                itemIterator = items.iterator();
                while (itemIterator.hasNext()) {//判断item是否和paddle碰撞,以及移除到底部的item
                    GameItem itemTemp = itemIterator.next();
                    if (itemTemp.itemMove() == -1) {//如果类型为-1,则说明已经到底部,需要移除
                        items.remove(itemTemp);
                    } else {
                        if (itemTemp.collide(paddle.getX(), paddle.getY(), Paddle.getPaddleWidth(), Paddle.getPaddleHeight())) {
                            //如果道具碰到paddle应当触发,并且移除
                            itemUse(itemTemp.itemType);
                            energyAdder.reduceEnergy(220);//碰到道具时重置能量泄漏倒计时
                            energyAdder.addEnergy(1);
                            soundPlay(3);
                            items.remove(itemTemp);
                        }
                    }
                }
                if (skillCoolDown > 0) skillCoolDown--;//技能冷却
                useSkill();//释放复杂的技能
                energyAdder.reduceEnergy(0);//泄漏能量
                //itemUse(2);//测试代码,无限分裂小球,快速胜利
                breakoutComponents.updateHpAndScore(healthPoint, score);//更新数据面板数据
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
        int tempScore;//结算后分数,胜利,分数*1.2
        mainTimer.cancel();//关闭定时器,游戏结束
        JLabel ggLabel = new JLabel("游戏结束,你的分数为 " + score);
        ggLabel.setForeground(fontColor);
        JButton backBtn = new MenuButton("返回主菜单");
        ggLabel.setBounds(150, 350, 300, 100);
        ggLabel.setHorizontalAlignment(JLabel.CENTER);
        ggLabel.setFont(ggFont);
        ggLabel.setVisible(true);
        backBtn.setBounds(200, 450, 200, 100);
        backBtn.setFont(ggFont);//设置字体
        if (result == 0) {//游戏胜利的提示
            tempScore = (int) (score * 1.2);
            ggLabel.setText("游戏胜利!你的分数为 " + tempScore);
        } else {
            tempScore = score;
        }
        skillTimeCounter = 0;//技能清空
        skillTypeUsing = -1;

        if (playerName != null) {//如果玩家登录过姓名
            List<PlayerInfo> tempPlayers = sqlConn.queryPlayerInfo(playerName, false);
            if (tempPlayers.size() != 0) {//如果该玩家信息可以从数据库中读取
                if (tempPlayers.get(0).score < tempScore) {
                    ggLabel.setText("你的分数为 " + tempScore + " 打破记录!");
                    soundPlay(7);
                    sqlConn.updatePlayerInfo(playerName, tempScore);//进行更新
                }
            } else {//将数据插入到数据库
                ggLabel.setText("你的分数为 " + tempScore + " 新记录!");
                soundPlay(7);
                sqlConn.insertPlayerInfo(playerName, tempScore);
            }
        }

        breakoutComponents.add(backBtn);
        breakoutComponents.add(ggLabel);
        backBtn.addActionListener(e -> {
            breakoutComponents.setVisible(false);
            remove(breakoutComponents);
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
     * @param isSymmetry 左右是否对称
     * @return 返回初始化完成的bricks
     */
    private ArrayList<Brick> randInitBricks(boolean isSymmetry) {
        ArrayList<Brick> bricks = new ArrayList<>();
        Random random = new Random();
        BRICK_ROWS = random.nextInt(4) + 8;//随机层数
        int randomCols = BRICKS_PER_ROW;//列数暂时固定
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICKS_PER_ROW; j++) {
                if (isSymmetry && j >= 5) {
                    bricks.add((Brick) bricks.get(i * BRICKS_PER_ROW + BRICKS_PER_ROW - j - 1).clone());
                } else {
                    int randNum = random.nextInt(10);//随机生成0~9
                    Brick brick = new Brick();
                    switch (randNum) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            brick.setBrickHP(1);
                            break;
                        case 4:
                        case 5:
                            brick.setBrickHP(3);
                            break;
                        case 6:
                        case 7:
                        case 8:
                            brick.setBrickHP(2);
                            break;
                        case 9:
                            int r = random.nextInt(3);
                            //brick.setDestroyable(r == 0);//设置1/3的不可破坏砖块
                            brick.setAlive(false);//设置2/3的空白位置
                            break;
                    }
                    bricks.add(brick);
                }
            }
        }
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
                Brick brick = new Brick(true);//都是可被破坏的砖块
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
            int y = i * Brick.BRICK_HEIGHT + BRICK_SEP * i + BRICK_OFFSET_TOP;//空出上面空间
            brick.setY(y);
            j++;
            if (j == 10) {
                j = 0;
                i++;
            }
            if (brick.brickTan == 0) brick.countBrickTan();
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
        if (tempTan <= 0) tempTan = -tempTan;
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
                paddle.updatePaddleWidth(2 * Paddle.oldWidth, 10000, false);
                break;
            case 2:
                ballSplit(2);//分裂出两个小球
                breakoutComponents.updateItemMessage("小球分裂了!");
                break;
            case 3:
                healthPoint++;
                breakoutComponents.updateItemMessage("增加一条生命值!");
                System.out.println("生命值提升了");
                break;
        }
    }

    /**
     * 小球分裂函数
     *
     * @param ballSplitNum 小球分裂数量
     */
    public void ballSplit(int ballSplitNum) {
        Random randBall = new Random();
        Ball ballOne = balls.get(0);//list中第一个球进行分裂
        int[] directionInt = ballOne.getSpeedDirection();
        for (int i = 0; i < ballSplitNum; i++) {
            balls.add(new Ball(ballOne.getX(), ballOne.getY(), (randBall.nextInt(3) + 2) * directionInt[0], (randBall.nextInt(4) + 3) * directionInt[1]));
            ballNum += 1;//小球数量+1
        }

    }

    /** 预先对音效进行加载 */
    public void preSound() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/item_get_sound.wav")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常抛出,音乐文件未找到");
        }
    }

    /**
     * 各种音效的播放
     *
     * @param soundType 音效类型,1为击中音效,2为击碎音效,3为获得道具音效
     */
    public void soundPlay(int soundType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    switch (soundType) {
                        case 1://普通伤害
                            System.out.println("播放hit音效");
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/hit.wav")));
                            /* https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java */
                            break;
                        case 2:
                            System.out.println("击碎音效播放");
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/smash.wav")));
                            break;
                        case 3:
                            System.out.println("播放获得道具音效");
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/item_get_sound.wav")));
                            break;
                        case 4://轨道炮音效
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/rail_gun_sound.wav")));
                            break;
                        case 5://双倍击中音效伤害
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/hit2.wav")));
                            break;
                        case 6://能量不足音效
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/lack_energy.wav")));
                            break;
                        case 7://打破记录或者新记录的音效
                            clip.open(AudioSystem.getAudioInputStream(this.getClass().getResource("sound/cut_the_record.wav")));
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

    //使用复杂技能
    void useSkill() {
        if (skillTypeUsing != -1) {//如果有在生效的技能
            if (--skillTimeCounter <= 0) {//倒计时为0时,结束释放
                skillTypeUsing = -1;
            }
            switch (skillTypeUsing) {
                case 1://轨道炮
                    if (skillTimeCounter < 212) {//延长前期的动画效果
                        if (skillTimeCounter % 70 == 0) {//每980毫秒造成一次伤害,进行三次
                            for (Brick brickOne : bricks) {
                                if (brickOne.isAlive() && brickOne.getDestroyable() && isBrickTrigger(brickOne, paddle.getCenterX() - 40, 0, paddle.getCenterX() + 40, paddle.getY())) {
                                    //砖块需要存活的,可被摧毁的,并且在效果范围内才能生效
                                    brickOne.hpCheck(1);
                                    energyAdder.addEnergy(1);
                                    score += energyAdder.scoreMultiple * brickOne.getBrickScore();
                                }
                            }
                            energyAdder.reduceEnergy(110);
                        }
                    }
                    break;
                case 2://AT力场
                    if (skillTimeCounter == 1) paddle.setX(300 - Paddle.PADDLE_WIDTH / 2);
                    break;
                case 3://双倍伤害
                    if (skillTimeCounter <= 1) {//伤害变为1
                        for (Ball ballOne : balls) ballOne.setBallDamage(1);
                    } else if (skillTimeCounter <= 600)//增加伤害
                        for (Ball ballOne : balls) ballOne.setBallDamage(2);
                    break;

            }
        }
    }

    /**
     * 判断该砖块是否在效果范围内
     *
     * @param x1 左上角点的X轴坐标
     * @param y1 左上角点的Y轴坐标
     * @param x2 右下角点的X轴坐标
     * @param y2 右下角点的Y轴坐标
     * @return 如果在, 返回true, 否则返回false
     */
    boolean isBrickTrigger(Brick brick, int x1, int y1, int x2, int y2) {
        int x = brick.getX();
        int y = brick.getY();
        int width = brick.getBRICK_WIDTH();
        return isPointInRect(x, y, x1, y1, x2, y2)//需要对四个顶点都进行一次判断
                || isPointInRect(x + width, y, x1, y1, x2, y2)
                || isPointInRect(x, y + Brick.BRICK_HEIGHT, x1, y1, x2, y2)
                || isPointInRect(x + width, y + Brick.BRICK_HEIGHT, x1, y1, x2, y2);
    }

    /** 判断一个点是否在一个矩形内 */
    boolean isPointInRect(int x, int y, int x1, int y1, int x2, int y2) {
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    @Override
    public int castSkill(int sType) {//此处sType可以和skillTypeUsing不对应,添加技能更加方便
        if (skillCoolDown == 0) {
            int castFlag;
            switch (sType) {
                case 0://立刻从挡板中心发射一颗默认速度的小球
                    //balls.add(new Ball(0, 0, 3, 3));//测试代码
                    balls.add(new Ball(paddle.getX() + Paddle.PADDLE_WIDTH / 2 - Ball.getBallRadius()
                            , paddle.getY() - 2 * Ball.getBallRadius()));
                    ballNum++;
                    skillCoolDown = 250;
                    break;
                case 1://双倍伤害
                    skillTypeUsing = 3;
                    skillTimeCounter = 600;//大约8.4秒
                    skillCoolDown = 601;
                    break;
                case 2://AT力场
                    skillTypeUsing = 2;
                    skillTimeCounter = 450;
                    skillCoolDown = 451;
                    paddle.updatePaddleWidth(600, 6300, true);
                    break;
                case 3://轨道炮
                    skillTypeUsing = 1;
                    skillTimeCounter = 320;
                    soundPlay(4);
                    skillCoolDown = 200;
                    break;
            }
            System.out.println("释放<" + sType + ">号技能");
        } else {
            System.out.println("释放技能失败,尚在冷却中");
            return 2;
        }
        return 0;
    }

    /** 长期没有小球击中砖块时,让小球改变速度,向一个尚存在的砖块飞行 */
    boolean autoLockBrick(Ball ball) {
        if (--autoLockBrickCountDown <= 0) {
            for (Brick brickOne : bricks) {
                if (brickOne.isAlive() && brickOne.getDestroyable()) {//瞄准存活的而且可破坏的砖块
                    ball.setSpeed((int) Math.round((1.0 * ball.getBallCenterX() - brickOne.getCenterX()) / (ball.getBallCenterY() - brickOne.getCenterY())
                                    * (ball.getVy() + 2 * ball.getSpeedDirection()[1]))
                            , ball.getVy() + 2 * ball.getSpeedDirection()[1]);
                    resetAutoLockBrick();//重设倒计时
                    System.out.println("自动锁定启动");
                    return true;
                }
            }
        }
        return false;
    }

    /** 重置自动小球锁定砖块系统的倒计时 */
    void resetAutoLockBrick() {
        autoLockBrickCountDown = 4;
    }

    boolean winCheck() {
        for (Brick brickOne : bricks) {
            if (brickOne.isAlive() && brickOne.getDestroyable()) return false;
        }
        return true;
    }

}

