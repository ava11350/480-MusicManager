import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    public static EventHandler<MouseEvent> getRecs(){
        return new EventHandler<MouseEvent>(){
            public void handle(MouseEvent t){
                ListView<String> searchResults = (ListView<String>) t.getSource();
                HBox display = (HBox) searchResults.getParent();
                ListView<String> recResults = (ListView<String>) display.getChildren().get(1);
                recResults.getItems().add("Test");
                recResults.getItems().add(searchResults.getSelectionModel().getSelectedItem());
            }
        };
    }

    public static EventHandler<ActionEvent> searchButtonClick(){
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t){

                // Get the entered text in search TextFields
                Button b = (Button) t.getSource();
                HBox h = (HBox) b.getParent();
                VBox v = (VBox) h.getChildren().get(0);
                TextField temp = (TextField) v.getChildren().get(0);
                String artistQuery;
                if(!temp.getText().trim().isEmpty()){
                    artistQuery = temp.getText();
                }else{
                    artistQuery = null;
                }
                temp = (TextField) v.getChildren().get(1);
                String songQuery;
                if(!temp.getText().trim().isEmpty()){
                    songQuery = temp.getText();
                }else{
                    songQuery = null;
                }
                temp = (TextField) v.getChildren().get(2);
                String yearQuery;
                if(!temp.getText().trim().isEmpty()){
                    yearQuery = temp.getText();
                }else{
                    yearQuery=null;
                }

                // Generate query string
                MysqlCon searchCon = new MysqlCon();
                String searchQuery = "SELECT artists,name,year FROM musicDatabase WHERE";
                if(artistQuery!= null){
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
                HBox displayArea = (HBox) main.getChildren().get(1);
                ListView<String> resultsView = (ListView<String>) displayArea.getChildren().get(0);
                ListView<String> recommendationResults = (ListView<String>) displayArea.getChildren().get(1);
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
                        System.out.println(searchQuery);
                        System.out.println(e);
                    }
                }
            }
        };
    }

    public String getID( String theArtist, String theName) {
        String theID = " ";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();

            String IDQuery = "SELECT id FROM musicDatabase WHERE `name` LIKE \"%" + theName + "%\"" + "AND " +
                    "WHERE artists LIKE \"%" + theArtist + "%\"";
            ResultSet rs = stmt.executeQuery(IDQuery);
            theID = rs.getString("id");
        }catch(Exception f){
        System.out.println(f);
        System.out.println("Please enter a correct database login");
        }
        return theID;
    }



    //returns query based on year of song
    public List<String> getYear(String ID) throws Exception{
        int theYear;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theYear = rs.getInt("year");

            String getResultsYear = "SELECT artists, `name`, `year` " +
                    "FROM musicdatabase " +
                    "WHERE year = " + theYear +
                    " ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResultsYear);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("name") + " " + rs.getInt("year"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }


    public List<String> getArtist(String ID) throws Exception{
        String theArtist;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT artists FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theArtist = rs.getString("artists");

            String getResultsArtist =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE artists = " + theArtist +
                            " ORDER BY RAND() LIMIT 10;";

            rs = stmt.executeQuery(getResultsArtist);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }



    public List<String> getAcousticness(String ID) throws Exception{
        float theAcoust;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theAcoust = rs.getFloat("acousticness");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE acousticness >= " + theAcoust + "- 0.05 and acousticness <= " + theAcoust +
                            "+ 0.05 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }




    public List<String> getDanceability(String ID) throws Exception{
        float theDanceability;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theDanceability = rs.getFloat("danceability");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE danceability >= " + theDanceability + "- 0.08 and danceability <= " + theDanceability +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }



    public List<String> getEnergy(String ID) throws Exception{
        float theEnergy;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theEnergy = rs.getFloat("energy");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE energy >= " + theEnergy + "- 0.05 and energy <= " + theEnergy +
                            "+ 0.05 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }

    public List<String> getInstrumentalness(String ID) throws Exception{
        float theInst;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theInst = rs.getFloat("instrumentalness");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE instrumentalness >= " + theInst + "- 0.10 and instrumentalness <= " + theInst +
                            "+ 0.10 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }

    public List<String> getLiveness(String ID) throws Exception{
        float theLive;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theLive = rs.getFloat("liveliness");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE liveliness >= " + theLive + "- 0.08 and liveliness <= " + theLive +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }



    public List<String> getSpeechiness(String ID) throws Exception{
        float theSpeech;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theSpeech = rs.getFloat("speechiness");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE speechiness >= " + theSpeech + "- 0.08 and speechiness <= " + theSpeech +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }



    public List<String> getTempo(String ID) throws Exception{
        float theTempo;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theTempo = rs.getFloat("tempo");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE tempo >= " + theTempo + "- 8 and tempo <= " + theTempo +
                            "+ 8 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }

    public List<String> getValence(String ID) throws Exception{
        float theValence;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            theValence = rs.getFloat("valence");

            String getResults =
                    "SELECT artists, `name`, `year` " +
                            "FROM musicdatabase \n" +
                            " WHERE valence >= " + theValence + "- 0.08 and valence <= " + theValence +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                results.add(rs.getString("artists")+ " " + rs.getString("`name`") + " " + rs.getInt("`year`"));
            }

        }catch(Exception f){
            System.out.println(f);
            System.out.println("Please enter a correct database login");
        }
        return results;
    }


}
