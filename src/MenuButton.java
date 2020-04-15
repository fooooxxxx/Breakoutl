import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *美化后的按钮
 */


public class MenuButton extends JButton {
    Color defaultColor = new Color(255,111,0);//默认字体颜色,以及鼠标移到按钮上后背景颜色
    Color enterFontColor = Color.BLACK;//鼠标移动到按钮上后字体颜色
    Color pressBackgroundColor = new Color(252,158,86);//按下后背景颜色
    ImageIcon pressIcon = new ImageIcon("src/image/press_button_background.png");

    MenuButton(){
        setMoveEffect();
    }
    MenuButton(String text){
        super(text);
        setMoveEffect();
    }

    void setMoveEffect(){
        this.setOpaque(false);//内容透明
        this.setContentAreaFilled(false);
        setBorderPainted(false);//去边框
        setForeground(defaultColor);
        setPressedIcon(pressIcon);//无效
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//鼠标进入后
                super.mouseEntered(e);
                setContentAreaFilled(true);//重新填充背景
                setBackground(defaultColor);
                setForeground(enterFontColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setContentAreaFilled(false);//使背景透明
                setBackground(enterFontColor);
                setForeground(defaultColor);
            }

        });
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }
}
