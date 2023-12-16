// DONE

package model;
public class Position {
    public int x, y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(Position initial) {
        this.x = initial.x;
        this.y = initial.y;
    }
    public Position moveInThisDirection(Direction direction){
        return new Position(x + direction.x, y + direction.y);
    }
}
