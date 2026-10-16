package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Board;
import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.model.V2d;

public class GoLeftCommand implements Command {

    @Override
    public void execute(Scene scene) throws InterruptedException {
        scene.updateBoard((Board board) -> board.kickPlayer(new V2d(-UPDATE_STEP, 0)));
    }
}
