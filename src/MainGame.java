public class MainGame {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JBreakout breakout = new JBreakout();
                breakout.setVisible(true);
                breakout.setRealWidthHeight();//获取窗口实际大小
                breakout.setLocationRelativeTo(null);//设置窗口居中
                breakout.setStartPosition();//设置paddle初始位置

            }
        });
    }
}
