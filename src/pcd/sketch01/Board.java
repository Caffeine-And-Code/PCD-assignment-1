//package pcd.sketch01;
//
//import pcd.poool.model.board.BoardConf;
//import pcd.poool.model.board.Boundary;
//
//import java.util.List;
//
//public class Board {
//
//    private List<Ball> balls;
//    private Ball playerBall;
//    private Ball botBall;
//    private Boundary bounds;
//
//    public Board(){}
//
//    public void init(BoardConf conf) {
//    	balls = conf.getSmallBalls();
//        playerBall = BoardConf.getPlayerBall();
//        botBall = BoardConf.getBotBall();
//    	bounds = conf.getBoardBoundary();
//    }
//
//    public void updateState(long dt) {
//
//    	playerBall.updateState(dt, this);
//        botBall.updateState(dt, this);
//
//    	for (var b: balls) {
//    		b.updateState(dt, this);
//    	}
//
//    	for (int i = 0; i < balls.size() - 1; i++) {
//            for (int j = i + 1; j < balls.size(); j++) {
//                Ball.resolveCollision(balls.get(i), balls.get(j));
//            }
//        }
//    	for (var b: balls) {
//    		Ball.resolveCollision(playerBall, b);
//            Ball.resolveCollision(botBall, b);
//    	}
//
//    }
//
//    public List<Ball> getBalls(){
//    	return balls;
//    }
//
//    public Ball getPlayerBall() {
//    	return playerBall;
//    }
//
//    public Ball getBotBall() {
//        return botBall;
//    }
//
//    public  Boundary getBounds(){
//        return bounds;
//    }
//}
