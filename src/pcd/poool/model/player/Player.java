package pcd.poool.model.player;

public interface Player {
    PlayerType getType();
    void setPoints(int points);
    void addPoints(int points);
    int getPoints();
}
