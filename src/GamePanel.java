import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_DIM = 850;
    static final int UNIT_SIZE = 50; // How large objects will be
    static final int UNIT_COUNT = (SCREEN_DIM * SCREEN_DIM) / UNIT_SIZE; // How many units can fit inside frame 
    static final int DELAY = 75;
    final Point[] snakeCoords = new Point[UNIT_COUNT]; // Coordinates of each snake part
    int snakeSize = 3; // Initial size. Add each time food eaten
    int foodEaten;
    Point foodCoord;
    char direction = 'D'; // Direction of snake
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_DIM, SCREEN_DIM));
        this.setBackground(Color.green);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        
        startGame();
    }

    public void startGame() {
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        /* Create checkerboard pattern */

        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            /* Draw objects on screen */
            
            // Create checkerboard pattern
            g.setColor(new Color(0,153,0));
            for (int row = 0; row < SCREEN_DIM / UNIT_SIZE; row++) {
                for (int col=row % 2; col < SCREEN_DIM/UNIT_SIZE; col+=2) {
                    g.fillRect(row*UNIT_SIZE, col*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }    
            }

            // Draw food
            g.setColor(Color.red);
            g.fillOval(this.foodCoord.x, this.foodCoord.y, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < snakeSize; i++) {
                // Head of snake
                if (i == 0) { 
                    g.setColor(Color.blue);
                    g.fillRect(this.snakeCoords[i].x, this.snakeCoords[i].y, UNIT_SIZE, UNIT_SIZE);
                }
                // Body of snake
                else {
                    g.setColor(new Color(68, 85, 90));
                    g.fillRect(this.snakeCoords[i].x, this.snakeCoords[i].y, UNIT_SIZE, UNIT_SIZE);
                }

            }

            // Place score at top
            g.setColor(Color.white);
            FontMetrics metrics = getFontMetrics(g.getFont());
            String score = "Score: " + foodEaten;
            g.setFont(new Font("Helvetica", Font.BOLD, 20));
            g.drawString(score, metrics.stringWidth(score) / 2, g.getFont().getSize());
        }
        else {
            endGame(g);
        }
    }

    public void newFood() {
        /* Generate new coordinates of food */

        this.foodCoord.x = random.nextInt((int) (SCREEN_DIM/UNIT_SIZE)) * UNIT_SIZE;
        this.foodCoord.y = random.nextInt((int) (SCREEN_DIM/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = snakeSize; i > 0; i--) {
            this.snakeCoords[i].setLocation(snakeCoords[i-1]);
        }

        switch(direction) {
            case 'W': this.snakeCoords[0].translate(0, -UNIT_SIZE); break;
            case 'A': this.snakeCoords[0].translate(0, UNIT_SIZE); break;
            case 'S': this.snakeCoords[0].translate(-UNIT_SIZE, 0); break;
            case 'D': this.snakeCoords[0].translate(UNIT_SIZE, 0); break;   
        }
    }

    public void checkFood() {
        /* Check if head is touching food */
        if (this.snakeCoords[0].equals(foodCoord)) {
            snakeSize++;
            foodEaten++;
            newFood();
        }
    }

    public void checkCollisions() {
        /* Check if snake is colliding w/itself or the wall */
        
        // Check if head collides w/body
        for (int i = snakeSize; i > 0; i--) {
            if (this.snakeCoords[0].equals(this.snakeCoords[i])) {
                running = false;
            }
        }

        // Check if head collides w/walls
        if (this.snakeCoords[0].x < 0 || this.snakeCoords[0].x > SCREEN_DIM || this.snakeCoords[0].y < 0 || this.snakeCoords[0].y > SCREEN_DIM) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void endGame(Graphics g) {
        /* Lose screen */
        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.BOLD, 100));
        
        // Place Game Over in center
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String end = "Game Over";
        g.drawString(end, (SCREEN_DIM) - metrics1.stringWidth(end) / 2, SCREEN_DIM / 2);
        
        // Place score at top
f        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String score = "Score: " + foodEaten;
        g.setFont(new Font("Helvetica", Font.BOLD, 20));
        g.drawString(score, metrics2.stringWidth(score) / 2, g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_W: 
                    if (direction != 'S') {
                        direction = 'W';
                    }
                    break;
                case KeyEvent.VK_A: 
                    if (direction != 'D') {
                        direction = 'A';
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direction != 'W') {
                        direction = 'S';
                    }
                    break;
                case KeyEvent.VK_D: 
                    if (direction != 'A') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}