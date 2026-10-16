package pcd.poool.controller;


import pcd.poool.model.Board;
import pcd.poool.model.V2d;

public class GoRightCommand implements Command {

    @Override
    public void execute(Board board) throws InterruptedException {
        board.kickPlayer(new V2d(Command.UPDATE_STEP, 0));
    }
}
