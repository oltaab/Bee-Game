
// DONE

package model;

public enum LevelItem {
    BASKET('*'),
    WALL('#'),
    EMPTY(' ');
    LevelItem(char rep) { representation = rep; }
    public final char representation;
}
