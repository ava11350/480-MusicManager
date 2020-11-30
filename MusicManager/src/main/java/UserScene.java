import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UserScene {
    private Scene ourScene;
    private final String username;
    private String artistQuery;
    private String songQuery;
    private Button searchButton;

    UserScene(String username){
        this.username = username;
        initScene();
    }

    void initScene(){
        TextField artistSearch = new TextField();
        TextField songSearch = new TextField();
        searchButton = new Button("Search");
        artistSearch.setPromptText("Artist");
        songSearch.setPromptText("Song Name");
        VBox searchTextBox = new VBox(artistSearch, songSearch);
        HBox searchField = new HBox(searchTextBox, searchButton);
        ListView<String> searchResults = new ListView<String>();
        VBox mainBox = new VBox(searchField,searchResults);
        mainBox.setAlignment(Pos.CENTER);
        ourScene = new Scene(mainBox, 1000, 1000);

        searchButton.setOnAction(TitleScene.searchButtonClick());
    }



    Scene getScene(){
        return ourScene;
    }

}
