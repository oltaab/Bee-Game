package view;

import persistence.HighScores;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class HighScoreTableModel extends AbstractTableModel {
    private final List<HighScores> highScores;
    private final String[] colName = new String[] { "Name", "Level", "Score" };

    public HighScoreTableModel(List<HighScores> highScores){
        this.highScores = highScores;
    }

    @Override
    public int getRowCount() {
        return highScores.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int r, int c) {
        HighScores h = highScores.get(r);
        if      (c == 0) return h.name;
        else if (c == 1) return h.level;
        else return h.score;
    }

    @Override
    public String getColumnName(int i) {
        return colName[i];
    }
}
