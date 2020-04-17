import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 排行榜
 */
public class RankPanel extends JPanel {
    //ArrayList<PlayerInfo> players;
    Image bgImage;
    JLabel rankLabel;
    JButton backBtn;
    JScrollPane scrollPane;
    String rankStr;

    Color fontColor = new Color(255, 111, 0);

    RankPanel(ArrayList<PlayerInfo> players, JBreakout mainFrame) {
        //this.players = players;
        this.setLayout(null);//使用绝对布局
        ImageIcon icon = new ImageIcon("src/image/rank_bg.png");
        bgImage = icon.getImage();//背景图片
        Font btnFont = new Font("黑体", Font.BOLD, 24);
        Font rankFont = new Font("微软雅黑", Font.BOLD, 18);

        rankLabel = new JLabel();
        backBtn = new MenuButton("返回");

        backBtn.setFont(btnFont);
        backBtn.setBounds(400, 700, 140, 50);
        rankLabel.setForeground(fontColor);
        rankLabel.setFont(rankFont);
        rankLabel.setHorizontalAlignment(JLabel.CENTER);
        rankStr = "<html><table cellspacing=\"70\"><tr><td>排名</td><td>名字</td><td>分数</td></tr>";
        int i = 1;
        for (PlayerInfo playerOne : players) {
            rankStr += "<tr><td>" + i + "</td><td>" + playerOne.playerName + "</td><td>" + playerOne.score + "</td></tr>";
            i++;
        }
        rankStr += "</table></html>";
        rankLabel.setText(rankStr);
        scrollPane = new JScrollPane(rankLabel);
        scrollPane.setBounds(0, 0, 600, 600);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane);
        add(backBtn);

        backBtn.addActionListener(e -> {
            mainFrame.mainMenu.setVisible(true);
            mainFrame.remove(this);
        });


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
