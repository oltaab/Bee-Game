package view;

import resource.ResourceLoader;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import model.Game;
import model.LevelItem;
import model.Position;
import model.Bee;


public class Board extends JPanel {
    private Game game;
    private final Image honeyBasket, bear, wall, empty, bee;
    private double scale;
    private int scaled_size;
    private final int tile_size = 32;

    public Board(Game g) throws IOException {
        game = g;
        scale = 1.0;
        scaled_size = (int) (scale * tile_size);

        honeyBasket = ResourceLoader.loadImage("resource/1.png");
        bear = ResourceLoader.loadImage("resource/4.png");
        wall = ResourceLoader.loadImage("resource/2.png");
        empty = ResourceLoader.loadImage("resource/8.png");
        bee = ResourceLoader.loadImage("resource/5.png");
    }

    public boolean setScale(double scale) {
        this.scale = scale;
        scaled_size = (int) (scale * tile_size);
        return refresh();
    }

    public boolean refresh() {
        if (!game.isLevelLoaded()) return false;
        Dimension dim = new Dimension(game.getLevelColNumber() * scaled_size, game.getLevelRowNumber() * scaled_size);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (!game.isLevelLoaded()){
            return;
        }

        Graphics2D gr = (Graphics2D) graphics;
        int width = game.getLevelColNumber();
        int height = game.getLevelRowNumber();
        Position position = game.getPlayerPosition();
        List<Bee> rangers = game.getRangers();
        Iterator<Bee> iterator = rangers.iterator();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Image img = null;
                LevelItem item = game.getItem(i, j);
                switch (item) {
                    case BASKET:
                        img = honeyBasket;
                        break;
                    case WALL:
                        img = wall;
                        break;
                    case EMPTY:
                        img = empty;
                        break;
                }
                if (position.x == j && position.y == i) img = bear;
                if (img == null) continue;
                gr.drawImage(img, j * scaled_size, i * scaled_size, scaled_size, scaled_size, null);
            }
        }
        while (iterator.hasNext()) {
            Bee r = iterator.next();
            int x = r.getPosition().x;
            int y = r.getPosition().y;
            gr.drawImage(bee, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
        }
    }
}
