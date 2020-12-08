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
    private TextField sqlUserField;
    private PasswordField sqlPassField;
    private HBox sqlBox;
    private VBox mainBox;

    private static String sqlUser;
    private static String sqlPass;

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


        sqlUserField = new TextField();
        sqlUserField.setPromptText("Database username");
        sqlPassField = new PasswordField();
        sqlPassField.setPromptText("Database Password");

        sqlBox = new HBox(sqlUserField,sqlPassField);

        Text temporaryHint = new Text("Temp User: Admin Pass: Password");

        mainBox = new VBox(loginBox,sqlBox, temporaryHint);

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
                    try{
                        // Test our connection to make sure the database login works. If so, change scene.
                        MysqlCon temp = new MysqlCon();
                        String testQuery = "SELECT artists,name,year FROM musicDatabase WHERE artists LIKE \"%Frank Ocean%\"";
                        temp.getQuery(testQuery, sqlUserField.getText(), sqlPassField.getText());

                        sqlUser = sqlUserField.getText();
                        sqlPass = sqlPassField.getText();

                        UserScene userScene = new UserScene(user);
                        primaryStage.setScene(userScene.getScene());
                    }catch(Exception f){
                        System.out.println(f);
                        System.out.println("Please enter a correct database login");
                    }
                }
            }
        });
    }

    private void setEnv(){
        loginBox.setPrefHeight(400);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setSpacing(30);

        sqlBox.setPrefHeight(200);
        sqlBox.setAlignment(Pos.CENTER);
        sqlBox.setSpacing(30);

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
                temp = (TextField) v.getChildren().get(2);
                String yearQuery = temp.getText();

                // Generate query string
                MysqlCon searchCon = new MysqlCon();
                String searchQuery = "SELECT artists,name,year FROM musicDatabase WHERE";
                if(artistQuery!=null){
                    searchQuery = searchQuery + " artists LIKE \"%" + artistQuery + "%\"";
                    if(songQuery != null){
                        searchQuery = searchQuery + " AND name LIKE \"%" + songQuery + "%\"";
                    }
                    if(yearQuery != null){
                        searchQuery = searchQuery + " AND year = " + yearQuery;
                    }
                }else if(songQuery != null){
                    searchQuery = searchQuery + " name LIKE \"%" + songQuery + "%\"";
                    if(yearQuery != null){
                        searchQuery = searchQuery + " AND year =" + yearQuery;
                    }
                }else if(yearQuery != null){
                    searchQuery = searchQuery + " year =" + yearQuery;
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
                    try{
                        List<String> answer = searchCon.getQuery(searchQuery, sqlUser, sqlPass);
                        for(String line : answer){
                            resultsView.getItems().add(line);
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
        };
    }
}
