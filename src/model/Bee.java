package model;

public class Bee {
    private Position position;
    private Axis axis;
    public boolean movingUpOrLeft = true;
    public boolean movingDownOrRight = false;

    public Bee(Axis axis, Position position) {
        this.position = new Position(position);
        this.axis = axis;
    }

    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    public Axis getAxis() {
        return this.axis;
    }

    public Position getPosition() {
        return new Position(position);
    }
}
