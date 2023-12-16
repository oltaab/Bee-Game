package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class Highscore {
    private final int maxScores;
    private final Connection conn;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private final List<HighScores> highScores = new ArrayList<>();

    public Highscore(int maxScores) throws SQLException {
        this.maxScores = maxScores;
        Connection c = null;
        try {
            Properties connectionProps = new Properties();

            connectionProps.put("user", "root");
            connectionProps.put("password", "root");
            connectionProps.put("serverTimezone", "UTC");
            String dbURL = "jdbc:mysql://localhost:3306/highscores";
            c = DriverManager.getConnection(dbURL, connectionProps);

            String insertQuery = "INSERT INTO HIGHSCORES (NAME, LEVEL, SCORE) VALUES (?, ?, ?)";
            insertStatement = c.prepareStatement(insertQuery);
            String deleteQuery = "DELETE FROM HIGHSCORES WHERE SCORE=?"; // + score;
            deleteStatement = c.prepareStatement(deleteQuery);
        } catch (Exception ex) {
            System.out.println("No connection");
        }
        this.conn = c;
        loadHighScores();

    }

    private void loadHighScores() {
        try (Statement stmt = conn.createStatement()) {
            highScores.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM highscores order by score desc");
            while (rs.next()) {
                String name = rs.getString("name");
                String level = rs.getString("level");
                int score = rs.getInt("score");
                highScores.add(new HighScores(name, level, score));
            }
        } catch (Exception e){ System.out.println("loadHighScores error: " + e.getMessage()); }
    }

    public List<HighScores> getHighScores() { loadHighScores(); return new ArrayList<>(highScores); }

    public void putHighScore(String name, String level, int score) throws SQLException {
        loadHighScores();
        if (highScores.size() < maxScores) {
            insertScore(name, level, score);
        } else {
            int leastScore = highScores.get(highScores.size() -1).score;
            if (score > leastScore) {
                deleteScore(leastScore);
                insertScore(name, level, score);
            }
        }
    }

    public void insertScore(String name, String level, int score) throws SQLException {
        System.out.println(name + ", " + level + ", " + score);
        insertStatement.setString(1, name);
        insertStatement.setString(2, level);
        insertStatement.setInt(3, score);
        insertStatement.executeUpdate();
    }

    public void deleteScore(int score) throws SQLException {
        deleteStatement.setInt(1, score);
        deleteStatement.executeUpdate();
    }

}

