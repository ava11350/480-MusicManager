import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MysqlCon {
    public MysqlCon(){

    }

    public static void getQuery(String ourQuery){
        //String selectSql = "SELECT * FROM musicDatabase WHERE artists LIKE \"%Frank Ocean%\";";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicManager","root","Password");
            System.out.println("Connection made.");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(ourQuery);
            if(rs!=null){
                System.out.println("Result from query acquired.");
            }
            while(rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
