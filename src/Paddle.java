import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Paddle extends JComponent {

    public static final int PADDLE_HEIGHT = 12;
    //offset 距离窗体底部距离
    static final int PADDLE_Y_OFFSET = 110;
    //宽高
    public static int PADDLE_WIDTH = 80;
    /** 该paddle是否在受width增长道具或技能的影响标志,0为无影响,1为被道具影响,2为被技能影响 */
    static int paddleItemWidthFlag = 0;
    /** 初始width,记录道具获得之前的PADDLE宽度 */
    static int oldWidth = 0;
    double paddleTan = 0;//中心到顶点的tan值
    //移动计时器
    Timer paddleMoveTimer;
    TimerTask paddleMoveTask;
    //道具效果计时器
    Timer paddleItemTimer;
    TimerTask paddleItemWidthTask;
    /** 按下左移动按钮指示量 */
    int paddleLeftMoveFlag = 0;
    /** 按下右移动按钮指示量 */
    int paddleRightMoveFlag = 0;
    Image atFieldImage;
    //paddle 坐标设置
    private int x = 0;
    private int y = 0;
    //paddle移动速度
    private final int speed = 4;
    /** 移动定时器启动间隔,越短移动越快 */
    private final int movePeriod = 16;

    Paddle() {
        PADDLE_WIDTH = 80;
        paddleMoveTimer = new Timer();
        paddleItemTimer = new Timer();
        oldWidth = PADDLE_WIDTH;//记录初始宽度
        countPaddleTan();
        atFieldImage = new ImageIcon("src/image/skill/at_field_skill.png").getImage();
    }

    public static int getPaddleWidth() {
        return PADDLE_WIDTH;
    }

    public static int getPaddleHeight() {
        return PADDLE_HEIGHT;
    }

    /** 重写draw函数 */
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.blue);
        if (JBreakout.skillTypeUsing == 2) g2.drawImage(atFieldImage, x, y - 20, this);//绘制技能
        else g2.drawRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        //System.out.println(getX() + " ---"+getY());
    }

    /** 设置paddle起始位置, 使用前,应当先设置游戏面板实际大小 */
    public void setStartPosition() {
        x = (JBreakout.realWidth - PADDLE_WIDTH) / 2;
        y = JBreakout.realHeight - PADDLE_Y_OFFSET;
        System.out.println("paddle起始位置设置完毕");
    }

    /** 将paddle向左移动speed个单位 */
    public void moveLeft() {
        if (paddleLeftMoveFlag == 0)//如果已经在移动,则不继续添加移动定时器
            paddleLeftMoveFlag++;//指示量+1.说明当前按下左移动按钮数量增加
        moveStart(0); //执行向左移动任务
    }

    /** 将paddle向右移动speed个单位 */
    public void moveRight() {
        if (paddleRightMoveFlag == 0)//
            paddleRightMoveFlag++;//指示量+1.说明当前按下右移动按钮数量增加
        moveStart(1);//执行向右移动任务
    }

    /**
     * 根据参数创建实际的移动任务
     *
     * @param direction 0为向左,1为向右
     */
    void moveStart(int direction) {
        if (paddleMoveTask != null)
            this.moveCancel();
        if (direction == 0) {
            paddleMoveTask = new TimerTask() {
                @Override
                public void run() {
                    if (x >= speed)
                        x -= speed;
                    else {
                        x = 0;
                        this.cancel();
                    }
                }
            };
        } else {
            paddleMoveTask = new TimerTask() {
                @Override
                public void run() {
                    if (x + speed <= JBreakout.realWidth - PADDLE_WIDTH)
                        x += speed;
                    else {
                        x = JBreakout.realWidth - PADDLE_WIDTH;
                        this.cancel();
                    }
                }
            };
        }
        paddleMoveTimer.schedule(paddleMoveTask, 1, movePeriod); //统一执行移动任务
    }

    /** 取消移动定时器 */
    public void moveCancel() {
        paddleMoveTask.cancel();
        paddleMoveTask = null;
    }

    /** 取消左移动定时器,释放按钮时调用 */
    public void moveLeftCancel() {
        //System.out.print("左按钮释放前flag为" +paddleLeftMoveFlag);
        if (paddleLeftMoveFlag > 0) paddleLeftMoveFlag--;//指示量-1
        //System.out.println("----->左按钮释放后flag为" +paddleLeftMoveFlag);
        moveCancel();
        if (paddleRightMoveFlag > 0) moveStart(1);
    }

    /** 取消右移动定时器,释放按钮时调用 */
    public void moveRightCancel() {
        //System.out.print("右按钮释放前flag为" +paddleRightMoveFlag);
        if (paddleRightMoveFlag > 0) paddleRightMoveFlag--;//指示量-1
        //System.out.println("----->右按钮释放后flag为" +paddleRightMoveFlag);
        moveCancel();
        if (paddleLeftMoveFlag > 0) moveStart(0);
    }

    /** 计算并更新paddle中心到各顶点的tan值 */
    public void countPaddleTan() {
        paddleTan = (double) PADDLE_HEIGHT / PADDLE_WIDTH;
        System.out.println("paddleTan" + paddleTan);
    }

    /**
     * 用于设置paddle的宽度
     *
     * @param width     被设置的宽度
     * @param time      宽度修改持续时间,单位为毫秒,如果该值为0则为永久修改
     * @param isATField 是不是来自技能的,普通道具无法影响来自技能的改变宽度效果
     */
    public void updatePaddleWidth(int width, int time, boolean isATField) {
        if (paddleItemWidthFlag != 2) {//宽度受技能效果影响时,道具无效
            if (paddleItemWidthFlag == 1) paddleItemWidthTask.cancel();//paddle如果在受道具影响,则取消TimerTask,以便进行重置道具影响时间
            if (time > 0) {//如果time大于0,设置让宽度恢复的计时器
                paddleItemWidthTask = new TimerTask() {
                    @Override
                    public void run() {
                        PADDLE_WIDTH = oldWidth;
                        paddleItemWidthFlag = 0;
                    }
                };
                paddleItemTimer.schedule(paddleItemWidthTask, time);
            }
            PADDLE_WIDTH = width;
            paddleItemWidthFlag = isATField ? 2 : 1;
            if (isATField) x = 0;
        }
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    /** 返回paddle对称线的X轴坐标 */
    public int getCenterX() {
        return x + PADDLE_WIDTH / 2;
    }
}
