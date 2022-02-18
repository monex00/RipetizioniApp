package dao;

import java.sql.*;

public class DAO {

    private static final String url = "jdbc:mysql://localhost:3306/dbripetizioni";
    private static final String user = "root";
    private static final String password = "";

    public static void registerDriver() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            System.out.println("Driver correttamente registrato");
        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    protected static Connection getConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Utente login(String email, String password){
        Utente result = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement("SELECT * FROM Utente WHERE Email = ? AND Password = ?");
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if(rs == null){
                return null;
            }

            if (rs.next()){
                result = new Utente(rs.getInt("idUtente"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("password"), rs.getString("ruolo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn, statement);
        }
        return result;
    }


    public static Utente getUserById(int id){
        Utente result = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement("SELECT * FROM Utente WHERE idUtente = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if(rs == null){
                return null;
            }

            if (rs.next()){
                result = new Utente(rs.getInt("idUtente"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("password"), rs.getString("ruolo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn, statement);
        }
        return result;
    }

    protected static void closeConnection(Connection conn, Statement statement){
        if (conn != null && statement != null){
            try {
                conn.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void closeConnection(Connection conn){
        if (conn != null){
            try {
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
