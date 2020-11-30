import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TitleScene extends Application {
    private TextField username;
    private PasswordField password;
    private Button loginButton;
    private HBox loginBox;
    private VBox mainBox;

    private boolean loggedIn = false;

    public static void main(String[] args){
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Music Manager");

        username = new TextField();
        username.setPromptText("Username");
        password = new PasswordField();
        password.setPromptText("Password");

        loginButton = new Button("Login");

        loginBox = new HBox(username, password, loginButton);

        Text temporaryHint = new Text("Temp User: Admin Pass: Password");

        mainBox = new VBox(loginBox, temporaryHint);

        setEnv();

        Scene menu = new Scene(mainBox, 1000, 1000);
        primaryStage.setScene(menu);
        primaryStage.show();

        loginButton.setOnMouseClicked(e -> {
            String user = username.getText();
            String pass = password.getText();

            // TODO add password handling for more than one user
            if(user.equals("Admin")){
                if(pass.equals("Password")){
                    UserScene userScene = new UserScene(user);
                    primaryStage.setScene(userScene.getScene());
                    //MysqlCon temp = new MysqlCon();
                    //String ourQuery = "SELECT artists,name,year FROM musicDatabase WHERE artists LIKE \"%Frank Ocean%\"";
                    //temp.getQuery(ourQuery);

                }
            }
        });
    }

    private void setEnv(){
        loginBox.setPrefHeight(500);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setSpacing(30);

        mainBox.setAlignment(Pos.CENTER);
        mainBox.setSpacing(20);
    }

    public static EventHandler<ActionEvent> searchButtonClick(){
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t){

                // Get the entered text in search TextFields
                Button b = (Button) t.getSource();
                HBox h = (HBox) b.getParent();
                VBox v = (VBox) h.getChildren().get(0);
                TextField temp = (TextField) v.getChildren().get(0);
                String artistQuery = temp.getText();
                temp = (TextField) v.getChildren().get(1);
                String songQuery = temp.getText();

                // Generate query string
                MysqlCon searchCon = new MysqlCon();
                String searchQuery = "SELECT artists,name,year FROM musicDatabase WHERE";
                if(artistQuery!=null){
                    searchQuery = searchQuery + " artists LIKE \"%" + artistQuery + "%\"";
                    if(songQuery != null){
                        searchQuery = searchQuery + "AND name LIKE \"%" + songQuery + "%\"";
                    }
                }else if(songQuery != null){
                    searchQuery = searchQuery + " name LIKE \"%" + songQuery + "%\"";
                }else{
                    searchQuery = "NO";
                }

                // Find the ListView to enter results to.
                VBox main = (VBox) h.getParent();
                ListView<String> resultsView = (ListView<String>) main.getChildren().get(1);
                resultsView.getItems().clear();

                // Run query if successfully generated string
                if(searchQuery.equals("NO")){
                    resultsView.getItems().add("Nothing was entered to search for.");
                }else{
                    List<String> answer = searchCon.getQuery(searchQuery);
                    for(String line : answer){
                        resultsView.getItems().add(line);
                    }

                }


            }
        };
    }

}
