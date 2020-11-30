import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlCon {
    public MysqlCon(){

    }

    public static List<String> getQuery(String ourQuery){
        //String selectSql = "SELECT * FROM musicDatabase WHERE artists LIKE \"%Frank Ocean%\";";
        List<String> answer = new ArrayList<>();
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
                answer.add(rs.getString(1)+ " " + rs.getString(2) + " " + rs.getInt(3));
                System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            }
            con.close();
            return answer;
        }catch(Exception e){
            System.out.println(e);
        }
        return answer;
    }
}
