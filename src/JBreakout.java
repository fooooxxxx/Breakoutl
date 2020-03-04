import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class JBreakout extends JFrame implements KeyListener {
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;
    //游戏面板实际宽高
    public static int realWidth = 0;
    public static int realHeight = 0;

    //变量
    BreakoutComponents breakoutComponents = null;
    Paddle paddle = null;
    Ball ball = null;


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
        breakoutComponents = new BreakoutComponents(paddle, ball);
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


}
