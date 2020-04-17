import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Random;

/**
 * 掉落的游戏道具类
 */
public class GameItem implements CollideInterface, ImageObserver {
    final static int ITEM_HEIGHT = 30;
    final static int ITEM_WIDTH = 30;
    final static int itemSpeed = 2;
    //坐标应该来自于被击毁的brick中间
    int x;
    int y;
    /**
     * 道具效果,当itemType为-1时,说明道具消失或者被使用
     * 0-测试道具,无效果,1-增加Paddle长度,2-小球分裂为3个,3-增加一条生命值
     */
    int itemType;
    Image itemImage;

    GameItem(int x, int y) {
        this.x = x;
        this.y = y;
        Random r = new Random();
        int randNum = r.nextInt(100);//随机roll点,决定道具类型
        if (randNum >= 90) {
            itemType = 3;//10%概率为加命道具
            itemImage = new ImageIcon("src/image/add_hp.png").getImage();
        } else if (randNum >= 43) {
            itemType = 2;//分裂
            itemImage = new ImageIcon("src/image/ball_split.png").getImage();
        } else {
            itemType = 1;
            itemImage = new ImageIcon("src/image/paddle_long.png").getImage();
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //g2.setColor(Color.RED);
        //g2.drawOval(x, y, ITEM_WIDTH, ITEM_HEIGHT);

        g2.drawImage(itemImage, x, y, this);
        //g2.drawOval(x, y, ITEM_WIDTH, ITEM_HEIGHT);
    }

    /**
     * 道具的移动函数,应放在计时器中定时执行
     *
     * @return 返回道具类型
     */
    public int itemMove() {
        y += itemSpeed;
        if (y + ITEM_HEIGHT >= JBreakout.realHeight) {//判定球是否落到底部
            itemType = -1;//判断该道具无效,应当销毁
            System.out.println("道具落到地面,销毁");
        }
        return itemType;
    }

    @Override
    public boolean collide(int object_x, int object_y, int object_width, int object_height) {
        //判断是否发生碰撞
        return this.x + ITEM_WIDTH > object_x && this.x < object_x + object_width
                && this.y + ITEM_HEIGHT > object_y && this.y < object_y + object_height;
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }
}
