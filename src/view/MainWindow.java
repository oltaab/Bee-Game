package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.sql.SQLException;

import model.Game;
import model.Direction;

public class MainWindow extends JFrame {
    private final Game      game;
    private       Board     board;
    private final JLabel    livesLabel;
    private final JLabel    currentLevel;
    private final JLabel    timerForEveryGameLabel;
    private final JLabel    collectBasketLabel;
    private final Timer     rangerMovement;
    private final Timer     proceededAllTimer;

    public MainWindow() throws IOException, SQLException {
        game = new Game();
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Options");
        JMenu menuGameLevel = new JMenu("Choose level");

        createGameLevelMenuItems(menuGameLevel);

        setTitle("Yogi Bear Game");
        setSize(800, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
       JMenuItem menuScoreBoard = new JMenuItem(new AbstractAction("Scoreboard") {
           @Override
           public void actionPerformed(ActionEvent e) {
               new HighScoreWindow(game.getHighScores(), MainWindow.this);
           }
       });

        menuGame.add(menuGameLevel);
        menuGame.add(menuScoreBoard);
        menuGame.addSeparator();
        menuBar.add(menuGame);
        menuGame.add(createRestartMenuItem());


        setJMenuBar(menuBar);
        setLayout(new BorderLayout(0, 10));
        JPanel gameStatPanel = new JPanel();
        gameStatPanel.setLayout(new GridLayout(2,2));
        gameStatPanel.setBackground(new Color(212, 199, 150));
        livesLabel = new JLabel();
        collectBasketLabel = new JLabel();
        timerForEveryGameLabel = new JLabel();
        currentLevel = new JLabel();

        refreshGameStatLabel();
        gameStatPanel.add(livesLabel);
        gameStatPanel.add(collectBasketLabel);
        gameStatPanel.add(timerForEveryGameLabel);
        gameStatPanel.add(currentLevel);

        add(gameStatPanel, BorderLayout.NORTH);

        try {
            add(board = new Board(game), BorderLayout.CENTER);
        } catch (IOException ex) {}

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);

                if (!game.isLevelLoaded()) return;
                int kk = ke.getKeyCode();
                Direction direction = null;

                switch (kk) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        direction = Direction.UP;
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        direction = Direction.DOWN;
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        direction = Direction.LEFT;
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        direction = Direction.RIGHT;
                        break;
                    case KeyEvent.VK_ESCAPE:
                        game.startNewLevel(game.getGameID());
                        break;
                    default:
                        break;
                }
                board.repaint();

                if (direction != null && game.takeAStep(direction)) {
                    if (game.isGameEnded()) {
                        try {
                            if (game.allLevelsDiscovered()) {
                                String msg = "YOU REACHED THE END! CONGRATULATIONS";
                                if (game.isKeepHighScore()) {
                                    msg += " YOUR SCORE IS THE HIGHEST FROM EVERYONE!";
                                }
                                prompt(msg, "REACHED THE END!", true);
                                game.restart();
                            } else {
                                if (game.isAlive()) {
                                    prompt("YOU JUST FINISHED " + game.getGameID(), "DONE", false);
                                } else {
                                    rangerMovement.stop();
                                    deadAction();
                                    rangerMovement.start();
                                    game.restart();
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        game.startNewLevel();
                        board.refresh();
                        pack();
                    }
                }
            }
        });

        rangerMovement = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (game.isLevelLoaded()) {
                    game.makeRangersMoveAround();
                    try {
                        deadAction();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    board.repaint();
                }
            }
        });

        proceededAllTimer = new Timer (900, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                refreshGameStatLabel();
            }
        });
        setResizable(false);
        setLocationRelativeTo(null);
        game.startNewLevel();
        rangerMovement.start();
        proceededAllTimer.start();
        board.setScale(2.0);
        pack();
        refreshGameStatLabel();
        setVisible(true);
    }

    private void prompt(String msg, String title, boolean askForName) throws SQLException {
        proceededAllTimer.stop();
        JOptionPane.showMessageDialog(MainWindow.this, msg, title, JOptionPane.INFORMATION_MESSAGE);
        if (askForName) {
            askForNameAndStore();
        }
        proceededAllTimer.start();
    }
    private void deadAction() throws SQLException {
        if (!game.isAlive()) {
            String msg = "YOU DIED! :(";
            prompt("YOU DIED! :(", "GAME OVER", true);
            game.restart();
            proceededAllTimer.start();
            game.startNewLevel();
            board.refresh();
            pack();
        }
    }
    private void askForNameAndStore() throws SQLException {
        AskForNameDialog AskForNameDialog = new AskForNameDialog(MainWindow.this, "Enter your name");
        AskForNameDialog.setSize(250,100);
        while ( AskForNameDialog.getButtonCode() != 1 ) AskForNameDialog.setVisible(true);

        String playerName = AskForNameDialog.getValue();
        while(playerName.isEmpty()){
            AskForNameDialog = new AskForNameDialog(MainWindow.this, "Name is required");
            AskForNameDialog.setSize(250,100);
            while ( AskForNameDialog.getButtonCode() != 1 ) AskForNameDialog.setVisible(true);
            playerName = AskForNameDialog.getValue();
        }

        System.out.println("player name: "+ playerName);
        game.saveScore(playerName);
    }
    private void refreshGameStatLabel() {
        livesLabel.setText("LEFT LIVES: " + game.getLives());
        collectBasketLabel.setText("COLLECTED HONEY BASKET: " + game.getCollectedBasket());
        timerForEveryGameLabel.setText("PROCEEDED TIME: " + game.getOverallTime());
        currentLevel.setText("CURRENT LEVEL: " + game.getGameID());
    }
    private void createGameLevelMenuItems(JMenu menuGameLevel) {
        for (String id: game.getLevelsOfDifficulty()) {
            JMenuItem menuItem = new JMenuItem(new AbstractAction("Level -> " + id) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.startNewLevel(id);
                    board.refresh();
                    pack();
                }
            });
            menuGameLevel.add(menuItem);
        }
    }
    private JMenuItem createRestartMenuItem() {
        JMenuItem menuGameRestart = new JMenuItem(new AbstractAction("Restart") {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.restart();
                proceededAllTimer.start();
                game.startNewLevel();
                board.refresh();
                pack();
            }
        });
        return menuGameRestart;
    }
}
