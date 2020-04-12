import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class Skill extends JLabel {

    static int skillStartX;//最左侧技能的起始X轴坐标
    static int skillY;
    static int skillNameY = 820;//技能名字Y轴坐标
    int skillX;//当前技能X轴坐标
    int skillCenterX;//所需能量在技能能量槽X轴坐标,同时也是图片的对称线X轴坐标
    private final int imageSize = 25;//技能图片宽高为25
    int needEnergy;//所需技能能量值
    private String skillName;
    String skillDescription;//技能效果描述
    private final int arrowHalfWidth = 4;
    private final int arrowHeight = 10;//箭头高度
    private final int arrowOffset = 4;//距离技能图标高度
    //boolean isSelect = false;//是否被选中

    Skill(ImageIcon img,int needEnergy,String skillName){
        super(img);
        this.needEnergy = needEnergy;
        this.skillName = skillName;
        skillCenterX = (int)(1.0*needEnergy/EnergyAdder.maxSkillEnergy*EnergyAdder.sEnergyWidth+EnergyAdder.sEnergyX);
        skillX = skillCenterX-imageSize/2;
        setBounds(skillX,skillY,imageSize,imageSize);

    }

    void draw(Graphics g,boolean isSelect){
        Graphics2D g2 = (Graphics2D)g;
        if(EnergyAdder.skillEnergy>needEnergy)
            g2.setColor(Color.GREEN);
        else
            g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(skillX-1,skillY-1,imageSize+2,imageSize+2);
        g2.setStroke(new BasicStroke((1.0f)));
        g2.drawLine(skillCenterX,skillY+imageSize,skillCenterX,869);
        if(isSelect){//如果被选中,绘制一个三角形,对准技能
            int[] arrowX = {skillCenterX-arrowHalfWidth,skillCenterX+arrowHalfWidth,skillCenterX};
            int [] arrowY = {skillY-arrowOffset-arrowHeight,skillY-arrowOffset-arrowHeight,skillY-arrowOffset};
            g2.fillPolygon(arrowX,arrowY,3);
        }
        //绘制技能名字
    }

    /**检查当前技能能量值是否足够来释放该技能*/
    boolean checkCastEnergy(){
        return needEnergy<EnergyAdder.skillEnergy;
    }



}
