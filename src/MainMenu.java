import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *主菜单类,从JPanel继承
 */
public class MainMenu extends JPanel {
    ImageIcon icon;
    Image image;
    GridLayout menuLayout = new GridLayout(9,1,10,20);

    public MainMenu(JBreakout mainFrame){
        icon = new ImageIcon("bg.jpg");
        image=icon.getImage();



        this.setLayout(null);//使用绝对布局
        //初始化控件
        Font btnFont = new Font("黑体",Font.BOLD,24);
        JButton startBtn = new JButton("开始游戏");
        JButton exitGameBtn = new JButton("退出游戏");

        startBtn.setFont(btnFont);
        exitGameBtn.setFont(btnFont);

        add(startBtn);
        add(exitGameBtn);

        //设置位置和大小
        startBtn.setBounds(200,240,200,70);
        exitGameBtn.setBounds(200,600,200,70);

        //设置触发监听器
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.startGame();
            }
        });
        exitGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();//关闭窗口
            }
        });


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
    }
}