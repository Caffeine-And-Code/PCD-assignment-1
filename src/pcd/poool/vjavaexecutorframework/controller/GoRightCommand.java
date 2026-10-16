package pcd.poool.vjavaexecutorframework.controller;


import pcd.poool.vjavaexecutorframework.model.Board;
import pcd.poool.vjavaexecutorframework.model.Scene;
import pcd.poool.vjavaexecutorframework.model.V2d;

public class GoRightCommand implements Command {

    @Override
    public void execute(Scene scene) throws InterruptedException {
        scene.updateBoard((Board board) -> board.kickPlayer(new V2d(UPDATE_STEP, 0)));
    }
}
