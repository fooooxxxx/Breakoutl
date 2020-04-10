import javax.swing.*;
import java.awt.*;

/**
 * 能量加成器
 * 用于计算能量和分数倍数
 * 当小球击中或击碎砖块,以及拾取道具时获得能量,一定能量后提升分数倍数
 * 同时设置技能能量槽,技能能量槽满后可以选择技能释放
 */
public class EnergyAdder extends JComponent {
    private final int x = 5;//x轴起始坐标
    private final int y = 830;//y轴起始坐标
    private final int ADDER_WIDTH = 35;//分数能量槽宽度
    private final int ADDER_HEIGHT = 50;//分数能量槽高度
    private final int ADDER_BOTTOM_WIDTH = 9;//底部宽度
    private final int CENTER_LINE_X = 17;//中间对称线X轴坐标
    //坐标数组
    private final int[] xBorderList;//外轮廓x轴坐标数组
    private final int[] yBorderList;
    private int[] xFillList;//内填充x轴坐标数组
    private int[] yFillList;
    private int[] xFillList2;//用于两个多边形填充
    //private int[] yFillList2;
    private int fillPointNum;//内填充所需坐标数

    final int[] scoreLine = {0, 10, 20, 30, 40, 50};//阶段所需分数,分别为提升到2,3,4,5倍分数加成所需的分数加成器能量,最后一个50为5倍时的能量上限
    private int scoreMeter;//分数加成器当前能量,达到一定阶段所需分数是,提升分数倍数,将该值设置为0
    int scoreMultiple = 1;//分数倍数,默认为1
    Color scoreAdderColor;//分数加成器的内填充颜色,随scoreMeter改变而改变
    Color scoreMultipleColor;//分数倍数字体颜色,随scoreMultiple改变而改变
    private int skillEnergy;//技能能量
    private final int skillEnergyUpperLimit = 1000;//技能能量上限值为1000
    private int reduceCountDown;//减少能量倒计时,倒计时为0时,减少一点能量,此外击中砖块或者获得道具可以重设倒计时


    EnergyAdder() {
        /*外轮廓多绘制了两个没用的点*/
        xBorderList = new int[]{x, x + 10, x + 17, x + 24, x + 34, x + 22, x + 22, x + 34, x + 24, x + 17, x + 10, x, x + 12, x + 12};//外轮廓坐标顺序为顺时针,从左上角开始
        yBorderList = new int[]{y, y, y + 15, y, y, y + 24, y + 25, y + 49, y + 49, y + 34, y + 49, y + 49, y + 25, y + 24};
        scoreMeter = 0;
        skillEnergy = 0;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xBorderList, yBorderList, 14);//绘制加成器外轮廓
        g2.setColor(autoSelectColor());
        int tempFillFlag = calculateFillPoint();
        if (tempFillFlag!=0) {//返回false,说明当前倍数下累计能量为0,无需绘制
            g2.fillPolygon(xFillList, yFillList, fillPointNum);
            if(tempFillFlag==2) {//根据计算结果绘制第二个多边形
                g2.fillPolygon(xFillList2,yFillList,fillPointNum);
            }
        }
    }

    /** 根据当前分数加成器能量计算加成器填充多边形坐标 */
    int calculateFillPoint() {
        int tempScoreMeter = scoreMeter - scoreLine[scoreMultiple - 1];//临时分数加成器能量,减去了非当前倍数内累计的能量,用于绘制分数能量槽
        if (tempScoreMeter == 0) {//分数能量为0时,无需绘制里面的填充多边形
            return 0;
        } else if (scoreMultiple == 5) {//倍数等于5时,分数加成器内填充永远为满

        } else {
            int[] stageHeight = {16,24,25,33};//这4个为特殊高度,其中小于16时,需要绘制两个多边形
            int currentScoreLine = scoreLine[scoreMultiple];//当前分数倍数下分数能量的上限
            int tempHeight = (int)Math.ceil(1.0*scoreMeter/currentScoreLine*48);//分数能量槽高度
            //System.out.println("当前能量高度为" + tempHeight);
            int leftStartX;//左上角起始点X轴坐标
            if(tempHeight < stageHeight[0]){//当tempHeight高度小于16时,需要绘制两个多边形
                leftStartX = (int)Math.ceil((tempHeight+1)/2.0);
                xFillList = new int[]{leftStartX,leftStartX+ADDER_BOTTOM_WIDTH,ADDER_BOTTOM_WIDTH,1};
                yFillList = new int[]{ADDER_HEIGHT-1-tempHeight,ADDER_HEIGHT-1-tempHeight,ADDER_HEIGHT-1,ADDER_HEIGHT-1};
                xFillList2 = new int[]{(CENTER_LINE_X-xFillList[1])*2+xFillList[1]+1,(CENTER_LINE_X-xFillList[0])*2+xFillList[0]//此处第一个点x轴+1用于修正坐标
                        ,ADDER_WIDTH-2,ADDER_WIDTH-ADDER_BOTTOM_WIDTH-1};
                for(int i = 0;i < 4;i++){xFillList[i] = xFillList[i]+x;yFillList[i]=yFillList[i]+y;xFillList2[i] = xFillList2[i]+x;}
                fillPointNum = 4;
                return 2;
            }else if(tempHeight > stageHeight[3]){
                fillPointNum = 12;
            }else if(tempHeight > stageHeight[2]){
                fillPointNum = 9;
            }else{

                fillPointNum = 7;
            }


        }
        return 1;
    }

    /**
     * 增加分数能量和技能能量,如果分数能量达到下一倍数所需能量,则提升分数倍数,并且将scoreMeter置0
     *
     * @param energy 增加的能量
     * @return 返回值为当前分数倍数
     */
    public int addEnergy(int energy) {
        scoreMeter += energy;
        if (scoreMultiple == 5) {//如果已经为5倍,则检查是否超过分数能量上限
            if (scoreMeter > scoreLine[5]) scoreMeter = scoreLine[5];
        } else {//分数倍数不为5倍,则计算能量是否超过当前提升倍数所需能量值,如果超过,则提升能量倍数,并且将scoreMeter置0,同时重设能量泄漏倒计时
            if (scoreMeter > scoreLine[scoreMultiple]) {
                scoreMultiple++;
                scoreMeter = 0;//不计算提升倍数所溢出能量
            }
        }
        skillEnergy += energy*scoreMultiple;//增加技能能量,技能能量的增加也受分数倍数影响
        if(skillEnergy>skillEnergyUpperLimit) skillEnergy = skillEnergyUpperLimit;//不允许超过技能能量上限
        System.out.println("阶段能量为"+scoreMeter + " 技能能量为"+skillEnergy);
        return scoreMultiple;
    }

    /**
     * 减少能量泄漏倒计时方法,如果reduceCountDown为0时,减少一点能量;当参数非0时,也可以重设倒计时
     *
     * @param countDown 为0时正常减少reduceCountDown,非0时重设reduceCountDown为该值
     * @return 返回值为0, 说明该方法被用于重设倒计时, 否则返回值为当前分数加成倍数
     */
    public int reduceEnergy(int countDown) {
        if (countDown != 0) {
            if (reduceCountDown < countDown) reduceCountDown = countDown;//如果新的能量泄露倒计时值大于原有的,才重设,否则重设无效
            return 0;
        }
        if (--reduceCountDown <= 0) {
            --scoreMeter;
            if(scoreMeter<=0){//当scoreMeter小于0的时候,说明当前阶段分数能量已经被耗尽
                if (scoreMultiple == 1) {//如果倍数为1,并且没有任何分数能量,则能量依然为0
                    scoreMeter = 0;
                } else{//否则降低倍数,并且将scoreMeter设置为前一阶段可达到的分数
                    scoreMultiple--;
                    scoreMeter = scoreLine[scoreMultiple] - 1;
                }
            }
            System.out.println("能量泄漏,当前能量为"+scoreMeter);
            reduceCountDown = 30;
        }
        return scoreMultiple;
    }

    /** 根据当前分数倍数自动选择颜色 */
    public Color autoSelectColor(){
        switch(scoreMultiple){
            case 1:
                return new Color(0,255,0);

            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:

        }
        return new Color(0,255,255);
    }

}
