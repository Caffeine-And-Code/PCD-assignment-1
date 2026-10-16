package pcd.poool.vsynchronized.view;

import pcd.poool.vsynchronized.model.Board;
import pcd.poool.vsynchronized.model.P2d;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius, boolean disabled) {
}

public class ViewModel {

	private final ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private BallViewInfo bot;
	private BallViewInfo playerGoal;
	private BallViewInfo botGoal;
	private int playerScore;
	private int botScore;
	private int framePerSec;
	
	public ViewModel() {
		balls = new ArrayList<>();
		framePerSec = 0;
	}
	
    public void update(Board board) {
        var state = board.snapshot();
        update(state, state.framePerSec());
    }

    public void update(Board board, int framePerSec) {
        update(board.snapshot(), framePerSec);
    }

    // Snapshot first, then lock the view model: never acquire Board while holding this lock.
    private synchronized void update(Board.State state, int framePerSec) {
		balls.clear();
		for (var b: state.balls()) {
			balls.add(new BallViewInfo(b.pos(), b.radius(), b.isDisabled()));
		}
		this.framePerSec = framePerSec;
		var p = state.player();
		player = new BallViewInfo(p.pos(), p.radius(), p.isDisabled());
		p = state.bot();
		bot = new BallViewInfo(p.pos(), p.radius(), p.isDisabled());
		playerScore = state.playerScore();
		botScore = state.botScore();

		p = state.botGoal();
		botGoal = new BallViewInfo(p.pos(), p.radius(), p.isDisabled());

		p = state.playerGoal();
		playerGoal = new BallViewInfo(p.pos(), p.radius(), p.isDisabled());
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		var copy = new ArrayList<BallViewInfo>();
		copy.addAll(balls);

		return copy;
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

	public synchronized BallViewInfo getBotBall() {
		return bot;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public int getBotScore() {
		return botScore;
	}

	public BallViewInfo getPlayerGoal() {
		return playerGoal;
	}

	public BallViewInfo getBotGoal() {
		return botGoal;
	}
}
