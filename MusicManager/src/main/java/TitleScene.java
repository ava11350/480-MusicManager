import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;



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
}
