import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JBreakout extends JFrame implements KeyListener {
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;
    //游戏面板实际宽高
    public static int realWidth = 0;
    public static int realHeight = 0;

    /** 每层砖块的数量 */
    private static final int BRICKS_PER_ROW = 10;
    /** 层数 */
    private static final int BRICK_ROWS = 10;
    /** 砖块之间的间隔 */
    private static final int BRICK_SEP = 4;
    //变量
    BreakoutComponents breakoutComponents;
    Paddle paddle;
    Ball ball;
    ArrayList<Brick> bricks;


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
        paddle = new Paddle();
        ball = new Ball();
        bricks = initBricks();
        breakoutComponents = new BreakoutComponents(paddle, ball,bricks);
        //paddleMoveTimer = new Timer();

        //将组件设置为焦点
        breakoutComponents.setFocusable(true);

        //添加组件
        add(breakoutComponents);

        //添加监听事件
        breakoutComponents.addKeyListener(this);

        //定时器
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                breakoutComponents.repaint();
                ball.moveAndBounce();
                updateBrickWidth();

            }
        }, 0, 5);
    }

    /** 设置游戏面板实际大小 */
    public void setRealWidthHeight() {
        realWidth = this.getContentPane().getWidth();
        realHeight = this.getContentPane().getHeight();

        System.out.println("游戏面板 宽度为" + realWidth + "  高度为" + realHeight);
    }

    /** 该方法由MainGame主进程调用 用于设置paddle起始位置 */
    public void setStartPosition() {
        breakoutComponents.paddle.setStartPosition();
    }

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
            int x = j * BRICK_WIDTH +4*(j+1);
            brick.setX(x);
            int y = i*Brick.BRICK_HEIGHT +4*i;
            brick.setY(y);
            j++;
            if( j == 10){
                j=0;
                i++;
            }
        }
    }


}
