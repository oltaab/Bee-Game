package model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLevel {
    public final String        gameID;
    public final int            row;
    public final int            col;
    private      int            finalBaskets;
    private      int            numOfBaskets;
    public final LevelItem[][]  level;
    private      int            proceededTime = 0;
    public       List <Bee>  rangers = new ArrayList<>();
    public       Position       player = new Position(0,0);
    private      Position       initialPlayerPosition = new Position(0,0);
    public GameLevel(List<String> gameLevelRows, String id) {
        this.gameID = id;
        int c = 0;

        for (String s : gameLevelRows) {
            if (s.length() > c){
                c = s.length();
            }
        }

        row = gameLevelRows.size();
        col = c;
        level = new LevelItem[row][col];
        numOfBaskets = 0;
        finalBaskets = 0;

        for (int i = 0; i < row; i++) {
            String s = gameLevelRows.get(i);

            for (int j = 0; j < s.length(); j++) {
                char currentChar = s.charAt(j);

                switch (currentChar) {
                    case '@':
                        player = new Position(j, i);
                        initialPlayerPosition = new Position(j, i);
                        level[i][j] = LevelItem.EMPTY;
                        break;
                    case '#':
                        level[i][j] = LevelItem.WALL;
                        break;
                    case '*':
                        level[i][j] = LevelItem.BASKET;
                        numOfBaskets++;
                        break;
                    case 'H':
                        rangers.add(new Bee(Axis.HORIZONTAL, new Position(j, i)));
                        level[i][j] = LevelItem.EMPTY;
                        break;
                    case 'V':
                        rangers.add(new Bee(Axis.VERTICAL, new Position(j, i)));
                        level[i][j] = LevelItem.EMPTY;
                        break;
                    default:
                        level[i][j] = LevelItem.EMPTY;
                        break;
                }
            }
            for (int j = s.length(); j < col; j++) {
                level[i][j] = LevelItem.EMPTY;
            }
        }

    }

    public GameLevel(GameLevel gameLevel) {
        gameID = gameLevel.gameID;
        row = gameLevel.row;
        col = gameLevel.col;

        numOfBaskets = gameLevel.numOfBaskets;
        finalBaskets = gameLevel.finalBaskets;

        level = new LevelItem[row][col];
        player = new Position(gameLevel.player);
        rangers = new ArrayList<> (gameLevel.rangers);

        for (int i = 0; i < row; i++) {
            System.arraycopy(gameLevel.level[i], 0, level[i], 0, col);
        }
        initialPlayerPosition = new Position (gameLevel.initialPlayerPosition);
    }

    public boolean isGameEnded() { return numOfBaskets <= finalBaskets;  }
    public boolean isValidPosition(Position position) {
        return (position.x >= 0 &&
                position.y >= 0 &&
                position.x < col &&
                position.y < row &&
                level[position.y][position.x] != LevelItem.WALL);
    }

    public void moveRanger(Game gamelevel) {
        Iterator <Bee> iterator = rangers.iterator();
        while (iterator.hasNext()) {
            Bee ranger = iterator.next();
            Position position = ranger.getPosition();

            boolean isHorizontal = (ranger.getAxis() == Axis.HORIZONTAL);
            Position nextposition = position.moveInThisDirection(isHorizontal ? Direction.LEFT:Direction.UP);

            if (ranger.movingUpOrLeft && isValidPosition(nextposition)) {
                ranger.setPosition(nextposition);
            } else {
                ranger.movingUpOrLeft = false;
                ranger.movingDownOrRight = true;
            }

            nextposition = position.moveInThisDirection(isHorizontal ? Direction.RIGHT:Direction.DOWN);

            if ((ranger.movingDownOrRight) && isValidPosition(nextposition)){
                ranger.setPosition(nextposition);
            }else {
                ranger.movingUpOrLeft = true;
                ranger.movingDownOrRight = false;
            }
        }
        if (didCrash()) {
            crashedInto(gamelevel);
        }
    }
    public void crashedInto(Game game) {
        if (game.lives == 0){
            System.out.println("------- DIED ---------");
            return;
        }
        game.lives--;
        if (game.lives > 0) {
            player = new Position(initialPlayerPosition);
        }
    }

    public boolean movePlayer(Direction direction, Game game) {
        if (isGameEnded()) return false;
        Position next = player.moveInThisDirection(direction);
        if (!isValidPosition(next)) return false;
        if (didCrash(next)) {
            crashedInto(game);
            return true;
        }

        player = next;
        if (level[next.y][next.x] == LevelItem.BASKET) {
            level[next.y][next.x] = LevelItem.EMPTY;
            finalBaskets++;
        }
        return true;
    }

    public boolean didCrash(Position next) {
        Iterator<Bee> iterator = rangers.iterator();

        while(iterator.hasNext()) {
            Bee ranger = iterator.next();
            if (ranger.getPosition().x == next.x && ranger.getPosition().y == next.y) {
                return true;
            }
        }
        return false;
    }

    public boolean didCrash() {
        return didCrash(player);
    }

    public void printLevel(){
        int x = player.x, y = player.y;
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (i == y && j == x)
                    System.out.print('@');
                else 
                    System.out.print(level[i][j].representation);
            }
            System.out.println("");
        }
    }
    public int getFinalBaskets() {
        return finalBaskets;
    }
    public int getProceededTime() {
        return proceededTime++;
    }
}
