import javax.swing.*;
import java.awt.*;

/**
 * 掉落的游戏道具类
 */
public class GameItem extends JComponent {
    final static int sITEM_HEIGHT = 8;
    final static int sITEM_WIDTH = 30;
    //坐标应该来自于被击毁的brick中间
    int x;
    int y;
    /** 道具效果,当itemType为-1时,说明道具消失或者被使用
     *  0为测试道具,无效果,1-增加Paddle长度,2-小球分裂为3个,3-增加一条生命值
     * */
    int itemType = 0 ;


    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

    }
}
