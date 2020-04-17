import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主菜单类,从JPanel继承
 */
public class MainMenu extends JPanel {
    ImageIcon icon;
    Image image;
    //GridLayout menuLayout = new GridLayout(9, 1, 10, 20);
    Color fontColor = new Color(255, 111, 0);


    public MainMenu(JBreakout mainFrame) {
        icon = new ImageIcon("src/image/main_menu_image.png");
        image = icon.getImage();

        this.setLayout(null);//使用绝对布局
        //初始化控件
        Font btnFont = new Font("黑体", Font.BOLD, 24);
        Font btnSmallFont = new Font("黑体", Font.BOLD, 16);
        Font nameFont = new Font("黑体", Font.BOLD, 14);
        JButton startBtn = new MenuButton("开始游戏");
        JButton rankBtn = new MenuButton("排行榜");
        JButton exitGameBtn = new MenuButton("退出游戏");
        JButton loginBtn = new MenuButton("登录");
        JButton helpBtn = new MenuButton("?");//帮助按钮
        JCheckBox randomCheckCox = new JCheckBox("随机");
        JTextField nameField = new JTextField();
        JLabel helpLabel = new JLabel();
        //按钮字体设置
        startBtn.setFont(btnFont);
        exitGameBtn.setFont(btnFont);
        rankBtn.setFont(btnFont);
        helpBtn.setFont(new Font("等线", Font.BOLD, 36));
        loginBtn.setFont(btnSmallFont);
        //多选框设置
        randomCheckCox.setForeground(fontColor);
        randomCheckCox.setOpaque(false);
        randomCheckCox.setFocusPainted(false);
        //登录文本框设置
        nameField.setFont(nameFont);
        nameField.setOpaque(false);
        nameField.setForeground(fontColor);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        //帮助文本设置
        helpLabel.setText("<html>← → 移动挡板<br/><br/>空格键发射小球和使用技能<br/><br/>↑ ↓ 键选择技能<br/><br/>Esc键退出游戏</html>");
        helpLabel.setFont(new Font("等线", Font.BOLD, 30));
        helpLabel.setForeground(fontColor);
        helpLabel.setVisible(false);//默认不显示
        //添加组件
        add(randomCheckCox);
        add(startBtn);
        add(rankBtn);
        add(exitGameBtn);
        add(loginBtn);
        add(nameField);
        add(helpBtn);
        add(helpLabel);
        //设置位置和大小
        startBtn.setBounds(210, 240, 180, 60);
        rankBtn.setBounds(210, 400, 180, 60);
        exitGameBtn.setBounds(210, 560, 180, 60);
        randomCheckCox.setBounds(150, 240, 50, 50);
        loginBtn.setBounds(40, 800, 100, 40);
        nameField.setBounds(40, 770, 100, 28);
        helpBtn.setBounds(510,810,60,60);
        helpLabel.setBounds(150,180,300,400);
        //设置触发监听器
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.startGame();
            }
        });
        rankBtn.addActionListener(e -> {
            setVisible(false);
            mainFrame.add(new RankPanel(mainFrame.sqlConn.queryPlayerInfo("", true), mainFrame));

        });
        exitGameBtn.addActionListener(e -> {
            mainFrame.dispose();//关闭窗口
        });
        loginBtn.addActionListener(e -> {
            if (!nameField.getText().equals("") && JBreakout.playerName == null) {//判空
                JBreakout.playerName = nameField.getText();
                nameField.setEditable(false);
                nameField.setBorder(BorderFactory.createEmptyBorder());
                loginBtn.setText("已登录");
            }
        });
        randomCheckCox.addChangeListener(e -> JBreakout.isRandomMap = randomCheckCox.isSelected());
        helpBtn.addActionListener(e->{
            if(helpLabel.isVisible()){
                startBtn.setVisible(true);
                rankBtn.setVisible(true);
                randomCheckCox.setVisible(true);
                helpLabel.setVisible(false);
            }
            else{
                startBtn.setVisible(false);
                rankBtn.setVisible(false);
                randomCheckCox.setVisible(false);
                helpLabel.setVisible(true);
            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
