package model;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import persistence.Highscore;
import persistence.HighScores;
import resource.ResourceLoader;

public class Game {
    private final HashMap<String, GameLevel>  gameLevelDatabase;
    private       Iterator <String>           traverseGameIDs;
    private final Highscore                   database;
    private       int                         timer = 0;
    public        int                         lives = 3;
    private       boolean                     keepHighScore = false;
    private       GameLevel                   currentGameLevel = null;

    public Game() throws SQLException {
        gameLevelDatabase = new HashMap<>();
        database = new Highscore(10);
        readLevel();
    }
    public void startNewLevel(String id) {
        currentGameLevel = new GameLevel(gameLevelDatabase.get(id));
        keepHighScore = false;
    }
    public void startNewLevel() {
        if (!allLevelsDiscovered()) {
            startNewLevel(traverseGameIDs.next());
        }
    }

    public void restart() {
        traverseGameIDs = gameLevelDatabase.keySet().iterator();
        keepHighScore = false;
        timer = 0;
        lives = 3;
    }

    public boolean takeAStep(Direction d) {
        return currentGameLevel.movePlayer(d, this);
    }

    public int calculateScore() {
        return (10000-timer) * Integer.parseInt(currentGameLevel.gameID);
    }

    public void makeRangersMoveAround() {
        currentGameLevel.moveRanger(this);
    }

    private void readLevel() {
        InputStream is;
        is = ResourceLoader.loadResource("resource/levels.txt");

        try (Scanner sc = new Scanner(is)) {
            String line = readNextLine(sc);
            List<String> gameLevelRows = new ArrayList<>();

            while(!line.isEmpty()) {
                String id = readGameID(line);
                if (id == null) return;

                gameLevelRows.clear();
                line = readNextLine(sc);
                while (!line.isEmpty() && line.trim().charAt(0) != ';') {
                    gameLevelRows.add(line);
                    line = readNextLine(sc);
                }
                gameLevelDatabase.put(id, new GameLevel(gameLevelRows, id));
            }
        } catch (Exception e) {
            System.out.println("Error while loading the maps");
        }
        restart();
    }

    private String readNextLine(Scanner sc) {
        String line = "";
        while (sc.hasNextLine() && line.trim().isEmpty()) {
            line = sc.nextLine();
        }
        return line;
    }

    private String readGameID(String line) {
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) != ';') return null;
        Scanner s = new Scanner(line);
        s.next();
        return s.next();
    }

    public Collection<String> getLevelsOfDifficulty() { return gameLevelDatabase.keySet();}
    public boolean isLevelLoaded() { return currentGameLevel != null; }
    public int getLevelRowNumber() { return currentGameLevel.row; }
    public int getLevelColNumber() { return currentGameLevel.col; }
    public LevelItem getItem(int row, int col) { return currentGameLevel.level[row][col];}
    public String getGameID() { return (currentGameLevel != null) ? currentGameLevel.gameID : null; }
    public int getCollectedBasket() { return (currentGameLevel != null)? currentGameLevel.getFinalBaskets() : 0; }
    public int getLives() { return lives;}
    public int getElapsedTime() { return (currentGameLevel != null) ? currentGameLevel.getProceededTime() : 0; }
    public int getOverallTime() { return (currentGameLevel != null) ? timer++ : 0;}
    public boolean isGameEnded() { return currentGameLevel != null && (currentGameLevel.isGameEnded() || lives == 0); }
    public boolean isKeepHighScore() { return keepHighScore; }
    public Position getPlayerPosition() { return new Position(currentGameLevel.player.x, currentGameLevel.player.y); }
    public List<Bee> getRangers() { return new ArrayList<Bee>(currentGameLevel.rangers); }
    public List<HighScores> getHighScores() { return database.getHighScores(); }
    public boolean isAlive() { return lives > 0; }
    public boolean allLevelsDiscovered() { return !traverseGameIDs.hasNext(); }

    public void saveScore(String name) throws SQLException {
        database.putHighScore(name, currentGameLevel.gameID, calculateScore());
    }

}
