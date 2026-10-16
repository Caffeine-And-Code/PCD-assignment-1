package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Scene;

public interface Command {
    float UPDATE_STEP = 0.5f;

    void execute(Scene scene) throws InterruptedException;
}
