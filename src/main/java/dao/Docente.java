package dao;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;

public class Docente {
    private int id;
    private String nome;
    private String cognome;
    private boolean isAttivo;

    public Docente(int id, String nome, String cognome, boolean isAttivo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.isAttivo = isAttivo;
    }

    public Docente( String nome, String cognome) {
        this.id = -1;
        this.nome = nome;
        this.cognome = cognome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static boolean addDocenteToDB(String nome, String cognome){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            int changed = 0;
            prStatement = conn != null ? conn.prepareStatement("UPDATE docente set isAttivo = 1 WHERE Nome = ? AND Cognome = ?") : null;
            if(prStatement != null){
                prStatement.setString(1, nome);
                prStatement.setString(2, cognome);
                changed = prStatement.executeUpdate();
            }

            if (changed > 0){
                return true;
            }

            prStatement = conn != null ? conn.prepareStatement("INSERT INTO Docente(Nome,Cognome) VALUES (?,?)") : null;
            if(prStatement != null) {
                prStatement.setString(1, nome);
                prStatement.setString(2, cognome);
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

    public static boolean removeDocenteFromDB(int id){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("UPDATE docente set isAttivo = 0 WHERE idDocente = ?") : null;
            if(prStatement != null){
                prStatement.setInt(1, id);
                prStatement.executeUpdate();
            }
            prStatement = conn != null ? conn.prepareStatement("UPDATE insegnamento set isAttivo = 0 WHERE idDocente = ?") : null;
            if(prStatement != null){
                prStatement.setInt(1, id);
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

    public static ArrayList<Docente> getDocenti(){
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Docente> docenti = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT * FROM Docente WHERE isAttivo = 1") : null;

            if(rs != null){
                while (rs.next()){
                    docenti.add(new Docente(rs.getInt("idDocente") ,rs.getString("Nome"), rs.getString("Cognome"), rs.getBoolean("isAttivo")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return docenti;
    }
/*
    public static ArrayList<Docente> getDocentiDisponibili(String giorno, double ora, Corso c){
        /*
        SELECT *
        FROM docente D JOIN insegnamento ON (D.idDocente = insegnamento.idDocente) JOIN corso on (corso.IdCorso = insegnamento.idCorso)
        WHERE NOT EXISTS (SELECT * FROM prenotazione JOIN corso ON (prenotazione.IdCorso =  corso.IdCorso)
                  WHERE prenotazione.IdDocente = D.idDocente AND Giorno ="Venerdì" AND Ora = 15.30 )
              AND corso.Titolo = "Informatica")
         */
    /*

        Connection conn = DAO.getConnection();
        PreparedStatement prStatement = null;
        ArrayList<Docente> docenti = new ArrayList<>();

        try {
            prStatement = conn != null ? conn.prepareStatement("SELECT D.Nome, D.Cognome FROM docente D " +
                    "JOIN insegnamento ON (D.idDocente = insegnamento.idDocente) JOIN corso on (corso.IdCorso = insegnamento.idCorso)" +
                    "WHERE NOT EXISTS (SELECT * FROM prenotazione JOIN corso ON (prenotazione.IdCorso =  corso.IdCorso) " +
                                        "WHERE prenotazione.IdDocente = D.idDocente AND Giorno = ? AND Ora = ? ) " +
                    "AND corso.Titolo = ?") : null;
            ResultSet rs = null;
            if(prStatement != null) {
                prStatement.setString(1, giorno);
                prStatement.setDouble(2, ora);
                prStatement.setString(3, c.getNome());
                rs = prStatement.executeQuery();
            }

            if(rs != null){
                while (rs.next()){
                    docenti.add(new Docente(rs.getString("Nome"), rs.getString("Cognome")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, prStatement);
        }
        return docenti;
    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Docente that = (Docente) o;
        return this.nome.equals(that.getNome()) && this.cognome.equals(that.getCognome());
    }

}
