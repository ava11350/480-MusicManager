import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UserScene {
    private Scene ourScene;
    private final String username;

    UserScene(String username){
        this.username = username;
        initScene();
    }

    void initScene(){
        Text tempText = new Text("We successfully made it here");
        VBox mainBox = new VBox(tempText);
        mainBox.setAlignment(Pos.CENTER);
        ourScene = new Scene(mainBox, 1000, 1000);
    }

    Scene getScene(){
        return ourScene;
    }
}
