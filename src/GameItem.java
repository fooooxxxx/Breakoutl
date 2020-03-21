import javax.swing.*;
import java.awt.*;

/**
 * 掉落的游戏道具类
 */
public class GameItem extends JComponent implements CollideInterface{
    final static int ITEM_HEIGHT = 8;
    final static int ITEM_WIDTH = 30;
    final static int itemSpeed = 2;
    //坐标应该来自于被击毁的brick中间
    int x;
    int y;
    /** 道具效果,当itemType为-1时,说明道具消失或者被使用
     *  0-测试道具,无效果,1-增加Paddle长度,2-小球分裂为3个,3-增加一条生命值
     * */
    public int itemType = 0 ;

    GameItem(int x,int y){
        this.x = x;
        this.y = y;
        itemType = 0;//每种类型道具概率为1/3
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.drawOval(x,y,ITEM_WIDTH,ITEM_HEIGHT);
        switch(itemType){//不同道具样式不一样
            case 0:
                break;
        }
    }

    /** 道具的移动函数,应放在计时器中定时执行
     * @return 返回道具类型 */
    public int itemMove(){
        y+=itemSpeed;
        if(y+ITEM_HEIGHT >= JBreakout.realHeight){//判定球是否落到底部
            itemType = -1;//判断该道具无效,应当销毁
            System.out.println("道具落到地面,销毁");
        }
        return itemType;
    }

    @Override
    public boolean collide(int object_x,int object_y,int object_width,int object_height){
        if(this.x+ITEM_WIDTH>object_x && this.x<object_x+object_width
                && this.y+ITEM_HEIGHT > object_y && this.y<object_y+object_height){//判断是否发生碰撞
            return true;
        }
        return false;
    }
}
