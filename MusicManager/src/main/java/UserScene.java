import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private Button recsButton;

    UserScene(String username){
        this.username = username;
        initScene();
    }

    void initScene(){
        ObservableList<String> recTypes = FXCollections.observableArrayList(
                "Year",
                "Artist",
                "Acousticness",
                "Danceability",
                "Energy",
                "Instrumentalness",
                "Liveness",
                "Speechiness",
                "Tempo",
                "Valence",
                "I'm Feeling Groovy"
        );

        TextField artistSearch = new TextField();
        TextField songSearch = new TextField();
        TextField yearSearch = new TextField();
        ComboBox comboBox = new ComboBox(recTypes);
        searchButton = new Button("Search");
        searchButton.setPrefWidth(150);
        recsButton = new Button("Get Recommendations");
        recsButton.setPrefWidth(150);
        artistSearch.setPromptText("Artist");
        songSearch.setPromptText("Song Name");
        yearSearch.setPromptText("Year");
        VBox searchTextBox = new VBox(artistSearch, songSearch, yearSearch);
        searchTextBox.setSpacing(10);
        VBox buttonBox = new VBox(searchButton, recsButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        HBox searchField = new HBox(searchTextBox, comboBox, buttonBox);
        searchField.setSpacing(10);
        searchField.setAlignment(Pos.CENTER);
        ListView<String> searchResults = new ListView<String>();
        searchResults.setPrefWidth(400);
        ListView<String> recommendationResults = new ListView<String>();
        recommendationResults.setPrefWidth(400);
        HBox displayArea = new HBox(searchResults, recommendationResults);
        displayArea.setPrefWidth(1000);
        displayArea.setSpacing(50);
        displayArea.setAlignment(Pos.CENTER);
        VBox mainBox = new VBox(searchField,displayArea);
        mainBox.setPrefWidth(1000);
        mainBox.setSpacing(20);
        mainBox.setAlignment(Pos.CENTER);
        ourScene = new Scene(mainBox, 1000, 1000);

        searchButton.setOnAction(TitleScene.searchButtonClick());
        recsButton.setOnAction(TitleScene.getRecs());


    }



    Scene getScene(){
        return ourScene;
    }

}
