import java.sql.*;

public class Sql {
    private String url;
    private Connection con;
    private Statement stmt;
    
    public Sql(){
        url="jdbc:sqlserver://localhost;databaseName=lms;integratedSecurity=true;";
        
        try { 
            con=DriverManager.getConnection(url);
            stmt=con.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void execUpdate(String statement){
        try {
            stmt.executeUpdate(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public ResultSet execQuery(String statement){
        try {
            return stmt.executeQuery(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void finalize() throws Throwable{
        stmt.close();
        con.close();
    }
    
}
