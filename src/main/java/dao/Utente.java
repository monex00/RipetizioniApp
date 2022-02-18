package dao;

import java.sql.*;
import java.util.ArrayList;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo;

    public Utente(int id, String nome, String cognome, String email, String password, String ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    /*
    public boolean addUtenteToDB(){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("INSERT INTO Utente(Email, Password, Ruolo) VALUES (?,?,?)") : null;
            if(prStatement != null){
                prStatement.setString(1, this.email);
                prStatement.setString(2, this.password);
                prStatement.setString(2, this.ruolo);
                prStatement.executeUpdate();
            }
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }finally {
            DAO.closeConnection(conn, prStatement);
        }

        return res;
    }

    public boolean removeUtenteFromDB(){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("DELETE FROM Utente WHERE Email = ? AND Password = ?") : null;
            if(prStatement != null){
                prStatement.setString(1, this.email);
                prStatement.setString(2, this.password);
                prStatement.executeUpdate();
            }
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }finally {
            DAO.closeConnection(conn, prStatement);
        }

        return res;
    }
*/
    public static ArrayList<Utente> getUtenti(){
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Utente> utenti = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT * FROM Utente") : null;

            if(rs != null){
                while (rs.next()){
                    Utente u = new Utente(rs.getInt("idUtente"), rs.getString("Nome"), rs.getString("Cognome"), rs.getString("Email"), rs.getString("Password"), rs.getString("Ruolo"));
                    utenti.add(u);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return utenti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente that = (Utente) o;
        return this.email.equals(that.getEmail()); //cannot exist 2 different with same email
    }

}
