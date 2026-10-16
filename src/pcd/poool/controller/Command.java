package pcd.poool.controller;

import pcd.poool.model.Board;

public interface Command {
    public float UPDATE_STEP = 0.3f;
    void execute(Board board) throws InterruptedException;
}
