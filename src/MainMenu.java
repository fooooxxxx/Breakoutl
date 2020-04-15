import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主菜单类,从JPanel继承
 */
public class MainMenu extends JPanel {
    ImageIcon icon;
    Image image;
    GridLayout menuLayout = new GridLayout(9, 1, 10, 20);

    public MainMenu(JBreakout mainFrame) {
        icon = new ImageIcon("src/image/main_menu_image.png");
        image = icon.getImage();


        this.setLayout(null);//使用绝对布局
        //初始化控件
        Font btnFont = new Font("黑体", Font.BOLD, 24);
        JButton startBtn = new MenuButton("开始游戏");
        JButton exitGameBtn = new MenuButton("退出游戏");
        JCheckBox randomCheckCox = new JCheckBox("随机");

        startBtn.setFont(btnFont);
        exitGameBtn.setFont(btnFont);
        randomCheckCox.setForeground(new Color(255,111,0));
        randomCheckCox.setOpaque(false);

        add(randomCheckCox);
        add(startBtn);
        add(exitGameBtn);

        //设置位置和大小
        startBtn.setBounds(200, 240, 180, 60);
        exitGameBtn.setBounds(200, 600, 180, 60);
        randomCheckCox.setBounds(150,240,50,50);

        //设置触发监听器
        startBtn.addActionListener(e -> mainFrame.startGame());
        exitGameBtn.addActionListener(e -> {
            mainFrame.dispose();//关闭窗口
        });
        randomCheckCox.addChangeListener(e -> JBreakout.isRandomMap = randomCheckCox.isSelected());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
