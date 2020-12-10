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
        TextField yearSearch = new TextField();
        searchButton = new Button("Search");
        artistSearch.setPromptText("Artist");
        songSearch.setPromptText("Song Name");
        yearSearch.setPromptText("Year");
        VBox searchTextBox = new VBox(artistSearch, songSearch, yearSearch);
        HBox searchField = new HBox(searchTextBox, searchButton);
        ListView<String> searchResults = new ListView<String>();
        ListView<String> recommendationResults = new ListView<String>();
        HBox displayArea = new HBox(searchResults, recommendationResults);
        VBox mainBox = new VBox(searchField,displayArea);
        mainBox.setAlignment(Pos.CENTER);
        ourScene = new Scene(mainBox, 1000, 1000);

        searchButton.setOnAction(TitleScene.searchButtonClick());
        searchResults.setOnMouseClicked(TitleScene.getRecs());
    }



    Scene getScene(){
        return ourScene;
    }

}
