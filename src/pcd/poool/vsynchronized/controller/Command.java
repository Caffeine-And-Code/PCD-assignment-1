package pcd.poool.vsynchronized.controller;

import pcd.poool.vsynchronized.model.Board;

public interface Command {
    public float UPDATE_STEP = 0.3f;
    void execute(Board board) throws InterruptedException;
}
