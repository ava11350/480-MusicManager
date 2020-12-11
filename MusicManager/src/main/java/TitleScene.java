import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        primaryStage.setTitle("Deitz Dynamic Ditty and Dance Directory");

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

        mainBox.setStyle("-fx-background-color: #d220c2;");
    }

    public static EventHandler<ActionEvent> getRecs(){
        return new EventHandler<ActionEvent>(){
            public void handle(ActionEvent t) {
                Button b = (Button) t.getSource();
                VBox bBox = (VBox) b.getParent();
                HBox searchBox = (HBox) bBox.getParent();
                ComboBox comboBox = (ComboBox) searchBox.getChildren().get(1);
                VBox main = (VBox) searchBox.getParent();
                HBox display = (HBox) main.getChildren().get(1);
                ListView<String> searchResults = (ListView<String>) display.getChildren().get(0);
                //ListView<String> searchResults = (ListView<String>) t.getSource();
                //HBox display = (HBox) searchResults.getParent();
                ListView<String> recResults = (ListView<String>) display.getChildren().get(1);

                recResults.getItems().clear();

                String results = searchResults.getSelectionModel().getSelectedItem();
                if (results != null) {
                    char[] chars = results.toCharArray();

                    StringBuilder temp = new StringBuilder();
                    StringBuilder temp2 = new StringBuilder();

                    int wordCount = 1;

                    for (char x : chars) {
                        if (x != ']' && wordCount == 1) {
                            temp.append(x);
                        } else if (x == ']' && wordCount == 1) {
                            temp.append(x);
                            wordCount += 1;
                        } else if (x == ' ' && wordCount == 2) {
                            wordCount += 1;
                        } else {
                            temp2.append(x);
                        }
                    }

                    String artist = temp.toString();
                    String everythingElse = temp2.toString();
                    String songName = everythingElse.substring(0, everythingElse.length() - 5);
                    String yearVal = everythingElse.substring(everythingElse.length() - 4);

                    MysqlCon recCon = new MysqlCon();

                    recResults.getItems().clear();

                    String ourRecType = (String) comboBox.getSelectionModel().getSelectedItem();

                    ObservableList<String> resultsView = FXCollections.observableArrayList();

                    String id = getID(artist, songName);
                    try {

                        switch (ourRecType) {
                            case "Year":
                                resultsView = FXCollections.observableArrayList(getYear(id));
                                break;
                            case "Artist":
                                resultsView = FXCollections.observableArrayList(getArtist(id));
                                break;
                            case "Acousticness":
                                resultsView = FXCollections.observableArrayList(getAcousticness(id));
                                break;
                            case "Danceability":
                                resultsView = FXCollections.observableArrayList(getDanceability(id));
                                break;
                            case "Energy":
                                resultsView = FXCollections.observableArrayList(getEnergy(id));
                                break;
                            case "Instrumentalness":
                                resultsView = FXCollections.observableArrayList(getInstrumentalness(id));
                                break;
                            case "Liveness":
                                resultsView = FXCollections.observableArrayList(getLiveness(id));
                                break;
                            case "Speechiness":
                                resultsView = FXCollections.observableArrayList(getSpeechiness(id));
                                break;
                            case "Tempo":
                                resultsView = FXCollections.observableArrayList(getTempo(id));
                                break;
                            case "Valence":
                                resultsView = FXCollections.observableArrayList(getValence(id));
                                break;
                            case "I'm Feeling Groovy":
                                String recQuery = "SELECT (danceability * 100),name,year FROM musicDatabase WHERE";
                                if (artist != null) {
                                    recQuery = recQuery + " artists LIKE \"%" + artist + "%\"";
                                    if (songName != null) {
                                        recQuery = recQuery + " AND name LIKE \"%" + songName + "%\"";
                                    }
                                    if (yearVal != null) {
                                        recQuery = recQuery + " AND year LIKE " + yearVal;
                                    }
                                } else if (songName != null) {
                                    recQuery = recQuery + " name LIKE \"%" + songName + "%\"";
                                    if (yearVal != null) {
                                        recQuery = recQuery + " AND year =" + yearVal;
                                    }
                                } else if (yearVal != null) {
                                    recQuery = recQuery + " year =" + yearVal;
                                } else {
                                    recQuery = "NO";
                                }
                                String danceabilityResult = null;

                                try {
                                    List<String> answer = recCon.getQuery(recQuery, sqlUser, sqlPass);
                                    if (answer.size() != 0) {
                                        danceabilityResult = answer.get(0);
                                    }
                                } catch (Exception e) {
                                    System.out.println(recQuery);
                                    System.out.println(e);
                                }
                                char[] resultBuilder = danceabilityResult.toCharArray();

                                StringBuilder temp3 = new StringBuilder();

                                for (char x : resultBuilder) {
                                    if (Character.isDigit(x) || x == '.') {
                                        temp3.append(x);
                                    } else {
                                        break;
                                    }
                                }
                                String danceabilityVal = temp3.toString();

                                String recQuery2 = "SELECT artists,name,year FROM musicDatabase WHERE (danceability * 100) <= ("
                                        + danceabilityVal + " + 5) AND  (danceability * 100) >= ("
                                        + danceabilityVal + " - 5) AND year <= ("
                                        + yearVal + " + 10) AND year >= ("
                                        + yearVal + " - 10) ORDER BY Rand() LIMIT 16";

                                try {
                                    List<String> answer = recCon.getQuery(recQuery2, sqlUser, sqlPass);
                                    resultsView = FXCollections.observableArrayList(answer);
                                } catch (Exception e) {
                                    System.out.println(recQuery2);
                                    System.out.println(e);
                                }
                                break;
                            default:
                                // shouldn't get here
                                resultsView = FXCollections.observableArrayList("No results returned.");
                                break;
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
                    recResults.setItems(resultsView);
                }else{
                    recResults.getItems().add("No song selected to get recommendations for.");
                }
            }
        };
    }

    public static EventHandler<ActionEvent> searchButtonClick(){
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t){

                // Get the entered text in search TextFields
                Button b = (Button) t.getSource();
                VBox bBox = (VBox) b.getParent();
                HBox h = (HBox) bBox.getParent();
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

    public static String getID( String theArtist, String theName) {
        String theID = " ";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            theArtist = theArtist.split("\'")[1];
            String IDQuery = "SELECT id, name, artists FROM musicDatabase WHERE name LIKE \"%" + theName + "%\"" + "AND " + "artists LIKE \"%" + theArtist + "%\"";
            ResultSet rs = stmt.executeQuery(IDQuery);
            if(rs!=null){
                rs.next();
                theID = rs.getString("id");
            }
        }catch(Exception f){
        System.out.println(f);
        System.out.println("Please enter a correct database login");
        }
        return theID;
    }



    //returns query based on year of song
    public static List<String> getYear(String ID) throws Exception{
        int theYear=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null){
                rs.next();
                theYear = rs.getInt("year");
            }

            String getResultsYear = "SELECT artists, name, year " +
                    "FROM musicdatabase " +
                    "WHERE year = " + Integer.toString(theYear) +
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


    public static List<String> getArtist(String ID) throws Exception{
        String theArtist ="";
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null){
                rs.next();
                theArtist = rs.getString("artists");
                theArtist = theArtist.split("'")[1];
            }


            String getResultsArtist =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE artists LIKE \"%" + theArtist +
                            "%\" ORDER BY RAND() LIMIT 10;";

            rs = stmt.executeQuery(getResultsArtist);

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



    public static List<String> getAcousticness(String ID) throws Exception{
        float theAcoust = 0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theAcoust = rs.getFloat("acousticness");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE acousticness >= " + theAcoust + "- 0.05 and acousticness <= " + theAcoust +
                            "+ 0.05 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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




    public static List<String> getDanceability(String ID) throws Exception{
        float theDanceability=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theDanceability = rs.getFloat("danceability");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE danceability >= " + theDanceability + "- 0.08 and danceability <= " + theDanceability +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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



    public static List<String> getEnergy(String ID) throws Exception{
        float theEnergy=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theEnergy = rs.getFloat("energy");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE energy >= " + theEnergy + "- 0.05 and energy <= " + theEnergy +
                            "+ 0.05 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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

    public static List<String> getInstrumentalness(String ID) throws Exception{
        float theInst=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theInst = rs.getFloat("instrumentalness");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE instrumentalness >= " + theInst + "- 0.10 and instrumentalness <= " + theInst +
                            "+ 0.10 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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

    public static List<String> getLiveness(String ID) throws Exception{
        float theLive=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theLive = rs.getFloat("liveness");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE liveness >= " + theLive + "- 0.08 and liveness <= " + theLive +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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



    public static List<String> getSpeechiness(String ID) throws Exception{
        float theSpeech=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theSpeech = rs.getFloat("speechiness");
            }

            String getResults =
                    "SELECT artists, name, year, tempo " +
                            "FROM musicdatabase" +
                            " WHERE speechiness >= " + theSpeech + "- 0.08 and speechiness <= " + theSpeech +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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



    public static List<String> getTempo(String ID) throws Exception{
        float theTempo=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theTempo = rs.getFloat("tempo");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE tempo >= " + theTempo + "- 8 and tempo <= " + theTempo +
                            "+ 8 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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

    public static List<String> getValence(String ID) throws Exception{
        float theValence=0;
        List<String> results = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager", sqlUser, sqlPass);
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            String getYearQuery = "SELECT * FROM musicDatabase WHERE id LIKE \"%" + ID + "%\"";
            ResultSet rs = stmt.executeQuery(getYearQuery);
            if(rs!=null) {
                rs.next();
                theValence = rs.getFloat("valence");
            }

            String getResults =
                    "SELECT artists, name, year " +
                            "FROM musicdatabase" +
                            " WHERE valence >= " + theValence + "- 0.08 and valence <= " + theValence +
                            "+ 0.08 ORDER BY RAND() LIMIT 10";

            rs = stmt.executeQuery(getResults);

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


}
