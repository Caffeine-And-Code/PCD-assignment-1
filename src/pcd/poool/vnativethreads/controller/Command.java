package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;

public interface Command {
    float UPDATE_STEP = 0.5f;

    void execute(Scene scene) throws InterruptedException;
}
