import pcd.poool.view.Renderer;
import pcd.poool.view.ui.MainPanel;


public void main() {
    final var width = 1200;
    final var height = 800;
    var renderer = new Renderer(width, height);
    var mainStage = new MainPanel(width, height, renderer);

}
