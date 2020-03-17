import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JBreakout extends JFrame {
    //游戏参数
    public static final int APPLICATION_WIDTH = 616;
    public static final int APPLICATION_HEIGHT = 939;
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
    Ball ball;
    ArrayList<Brick> bricks;
    MainMenu mainMenu ;//主菜单JPanel
    static boolean isGameStart = false;//游戏是否在进行中标志

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

    /** 开始游戏,设置监听,并且启用监听*/
    public void startGame(){
        mainMenu.setVisible(false);

        paddle = new Paddle();
        ball = new Ball();
        bricks = initBricks();
        breakoutComponents = new BreakoutComponents(paddle, ball,bricks);
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
                        isGameStart = true;
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                breakoutComponents.repaint();
                ball.moveAndBounce();//球的移动,以及对墙碰撞
                updateBrickWidth();
                if(!isGameStart) setStartBallPosition();//如果游戏尚未开始,小球就会跟着paddle移动
                for(Brick brickOne : bricks) {//对砖块撞击判定
                    if(brickOne.isAlive() && ball.collide(brickOne.getX(),brickOne.getY(),brickOne.getBRICK_WIDTH(),Brick.BRICK_HEIGHT)) {//如果和brick发生碰撞
                        if(judgeCollideDirection(ball,brickOne.brickTan,brickOne.getX(),brickOne.getY()))
                            ball.rebounceX();
                        else
                            ball.rebounceY();
                        brickOne.setAlive(false);
                        break;
                    }
                }
                if(ball.collide(paddle.getX(),paddle.getY(),Paddle.getPaddleWidth(),Paddle.getPaddleWidth())) {
                    ball.setY(ball.getY()-6);
                    ball.rebounceY();
                }
            }
        }, 0, 10);
        setStartPosition();
        breakoutComponents.setFocusable(true);//将组件设置为焦点
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

    private ArrayList<Brick> initBricks() {
        ArrayList<Brick> bricks = new ArrayList<>();
        for(int i = 0;i <BRICK_ROWS;i++){
            for(int j =0;j<BRICKS_PER_ROW;j++){
                Brick brick = new Brick();
                switch(i+1){
                    case 1:
                    case 2:
                        brick.setColor(Color.RED);
                        break;
                    case 3:
                    case 4:
                        brick.setColor(Color.ORANGE);
                        break;
                    case 5:
                    case 6:
                        brick.setColor(Color.YELLOW);
                        break;
                    case 7:
                    case 8:
                        brick.setColor(Color.GREEN);
                        break;
                    case 9:
                    case 10:
                        brick.setColor(Color.CYAN);
                        break;
                }
                bricks.add(brick);
            }
        }
        return bricks;
    }

    /** 用于更新brick坐标和宽度*/
    private void updateBrickWidth(){
        int i = 0,j = 0;
        int BRICK_WIDTH = (JBreakout.realWidth - (BRICKS_PER_ROW - 1) *BRICK_SEP) / BRICKS_PER_ROW;
        for(Brick brick : bricks){
            brick.setBRICK_WIDTH(BRICK_WIDTH);
            //x,y为砖块坐标
            int x = j * BRICK_WIDTH +BRICK_SEP*(j+1);
            brick.setX(x);
            int y = i*Brick.BRICK_HEIGHT +BRICK_SEP*i;
            brick.setY(y);
            j++;
            if( j == 10){
                j=0;
                i++;
            }
        if(brick.brickTan!=0) brick.countBrickTan();
        }
    }

    /** 判断小球反弹方向
     * @param ball 球对象
     * @param setTan 设定的tan值,由被碰撞物体提供
     * @param objX 被碰撞物体中心点的X轴坐标
     * @param objY 被碰撞物体中心点的Y轴坐标
     * @return true则进行逆转x轴速度的反弹,为false进行逆转y轴速度的反弹*/
    public boolean judgeCollideDirection(Ball ball,double setTan,int objX,int objY){
        double tempTan = (double)(objY-ball.getBallCenterY())/(objX-ball.getBallCenterX());
        System.out.println("碰撞判定的tan为"+tempTan);
        if(tempTan < setTan){
            System.out.println("逆转X");
        }
        else{
            System.out.println("逆转Y");
        }
        return tempTan < setTan;//如果tempTan小于setTan,则说明他们在左右面发生了碰撞,否则表面在上下面发生碰撞
    }

    /** 设置小球初始位置在Paddle的中间顶部*/
    public void setStartBallPosition(){
        ball.setX(paddle.getX()+Paddle.PADDLE_WIDTH/2-Ball.getBallRadius());
        ball.setY(paddle.getY()-2*Ball.getBallRadius());
    }


}

