package pcd.poool.vsynchronized.controller;

import pcd.poool.vsynchronized.model.Board;
import pcd.poool.vsynchronized.model.V2d;

public class GoLeftCommand implements Command{

    @Override
    public void execute(Board board) throws InterruptedException {
        board.kickPlayer(new V2d(-Command.UPDATE_STEP, 0));
    }
}
