import javax.swing.*;
import java.awt.*;

/**
 *用于计算能量和分数倍数
 * 当小球击中或击碎砖块,以及拾取道具时获得能量,一定能量后提升分数倍数
 * 同时设置技能能量槽,能量槽满后可以选择技能释放
 */
public class EnergyAdder extends JComponent {
    private final int x = 5;//x轴起始坐标
    private final int y = 830;//y轴起始坐标
    private final int[] xBorderList;//外轮廓x轴坐标数组
    private final int[] yBorderList;
    private int[] xFillList;//内填充x轴坐标数组
    private int[] yFillList;

    EnergyAdder(){
        xBorderList = new int[]{x,x+10,x+17,x+24,x+34,x+22,x+22,x+34,x+24,x+17,x+10,x,x+12,x+12};//外轮廓坐标顺序为顺时针,从左上角开始
        yBorderList = new int[]{y,y,y+15,y,y,y+24,y+25,y+49,y+49,y+34,y+49,y+49,y+25,y+24};
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xBorderList,yBorderList,14);//绘制乘号外轮廓
        ;
    }
}
