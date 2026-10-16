package pcd.poool.controller;

import pcd.poool.model.Board;
import pcd.poool.model.V2d;

public class GoUpCommand implements Command{

    @Override
    public void execute(Board board) throws InterruptedException {
        board.kickPlayer(new V2d(0, Command.UPDATE_STEP));
    }
}
