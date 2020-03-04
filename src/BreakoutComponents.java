import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class BreakoutComponents extends JComponent {
    Paddle paddle;
    Ball ball;
    ArrayList<Brick> bricks;

    BreakoutComponents(Paddle paddle, Ball ball) {
        this.paddle = paddle;
        this.ball = ball;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
    }

}
