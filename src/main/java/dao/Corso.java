package dao;

import java.sql.*;
import java.util.ArrayList;

public class Corso {
    private int id;
    private String nome;
    private boolean isAttivo;

    public Corso(int id, String nome, boolean isAttivo) {
        this.nome = nome;
        this.id = id;
        this.isAttivo = isAttivo;
    }

    public Corso( String nome) {
        this.nome = nome;
        this.id = -1;
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

    public static boolean addCorsoToDB(String titolo){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            int changed = 0;
            prStatement = conn != null ? conn.prepareStatement("UPDATE corso set isAttivo = 1 WHERE Titolo = ?") : null;
            if(prStatement != null){
                prStatement.setString(1, titolo);
                changed = prStatement.executeUpdate();
            }

            if(changed > 0){
                return true;
            }

            prStatement = conn != null ? conn.prepareStatement("INSERT INTO corso(Titolo) VALUES (?)") : null;
            if(prStatement != null){
                prStatement.setString(1, titolo);
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


    public static boolean removeCorsoFromDB(int idCorso){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("UPDATE corso set isAttivo = 0 WHERE idCorso = ?") : null;
            if(prStatement != null){
                prStatement.setInt(1, idCorso);
                prStatement.executeUpdate();
            }
            prStatement = conn != null ? conn.prepareStatement("UPDATE insegnamento set isAttivo = 0 WHERE idCorso = ?") : null;
            if(prStatement != null){
                prStatement.setInt(1, idCorso);
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

    public static ArrayList<Corso> getCorsi(){
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Corso> corsi = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT * FROM Corso WHERE isAttivo = 1") : null;

            if(rs != null){
                while (rs.next()){
                    corsi.add(new Corso(rs.getInt("idCorso"), rs.getString("Titolo"), rs.getBoolean("isAttivo")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return corsi;
    }


    public static ArrayList<Corso> getCorsiConInsegnamenti(){
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Corso> corsi = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT * FROM corso JOIN insegnamento ON (corso.idCorso = insegnamento.idCorso) WHERE corso.isAttivo = 1 AND insegnamento.isAttivo = 1 AND insegnamento.idInsegnamento NOT IN ( SELECT prenotazione.idInsegnamento FROM prenotazione WHERE prenotazione.stato = 'A') GROUP BY corso.idCorso") : null;

            if(rs != null){
                while (rs.next()){
                    corsi.add(new Corso(rs.getInt("idCorso"), rs.getString("Titolo"), rs.getBoolean("isAttivo")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return corsi;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corso that = (Corso) o;
        return nome.equals(that.getNome());
    }


}
