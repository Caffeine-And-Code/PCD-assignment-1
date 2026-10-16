package pcd.poool.vsynchronized.controller;

import pcd.poool.vsynchronized.model.Board;
import pcd.poool.vsynchronized.model.V2d;

public class GoUpCommand implements Command{

    @Override
    public void execute(Board board) throws InterruptedException {
        board.kickPlayer(new V2d(0, Command.UPDATE_STEP));
    }
}
