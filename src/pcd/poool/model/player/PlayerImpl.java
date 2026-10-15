package pcd.poool.model.player;

public class PlayerImpl implements Player {
    private final PlayerType playerType;
    private int points;

    public PlayerImpl(PlayerType playerType) {
        this.playerType = playerType;
        this.points = 0;
    }

    @Override
    public PlayerType getType() {
        return this.playerType;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public int getPoints() {
        return this.points;
    }
}
