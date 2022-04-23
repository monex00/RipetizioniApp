package dao;

import java.sql.*;
import java.util.ArrayList;

public class Prenotazione {
    int id;
    private Utente utente;
    private Insegnamento insegnamento;
    private char stato;

    public static final String[] giorni = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì"};
    public static double[] ore = {15.0, 16.0, 17.0, 18.0, 19.0};

    public Prenotazione(int id, Utente utente, Insegnamento insegnamento, char stato) {
        this.id = id;
        this.utente = utente;
        this.insegnamento = insegnamento;
        this.stato = stato;
    }


    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public char getStato() {
        return stato;
    }

    public Insegnamento getInsegnamento() {
        return insegnamento;
    }

    public static boolean addPrenotazioneToDB(int idUtente, int idInsegnamento, char stato){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            /*
            Select ora, giorno
            FROM insegnamento
            where idInsegnamento = 57

            Select insegnamento.Ora, insegnamento.Giorno
            From prenotazione JOIN insegnamento On(prenotazione.idInsegnamento = insegnamento.idInsegnamento)
            where prenotazione.IdUtente = 1 and prenotazione.stato = 'A' and (insegnamento.ora, insegnamento.Giorno) in (
                Select ora, giorno
                        FROM insegnamento
                        where idInsegnamento = 57)
             */
            //TODO:COMPATTARE IN UNICA QUERY

            //controllo se utente ha già una prenotazione in quel giorno e ora
            int ora = 0;
            int giorno = 0;
            prStatement = conn != null ? conn.prepareStatement("SELECT Ora, Giorno FROM insegnamento WHERE idInsegnamento = ?") : null;
            if(prStatement != null) {
                prStatement.setInt(1, idInsegnamento);
                ResultSet rs = prStatement.executeQuery();
                if(rs.next()) {
                    ora = rs.getInt("Ora");
                    giorno = rs.getInt("Giorno");
                }
            }

            prStatement = conn != null ? conn.prepareStatement("SELECT Ora, Giorno " +
                    "FROM insegnamento JOIN prenotazione ON (insegnamento.idInsegnamento = prenotazione.idInsegnamento) " +
                    "WHERE idUtente = ? AND prenotazione.stato = 'A'") : null;

            if(prStatement != null) {
                prStatement.setInt(1, idUtente);
                ResultSet rs = prStatement.executeQuery();
                while(rs.next()) {
                    if(rs.getInt("Ora") == ora && rs.getInt("Giorno") == giorno) {
                        return false;
                    }
                }
            }

            //String query3 = "SELECT idInsegnamento FROM Insegnamento WHERE idDocente"

            prStatement = conn != null ? conn.prepareStatement("INSERT INTO Prenotazione(idUtente, idInsegnamento, stato) " +
                    "VALUES ((?), (?), ? )") : null;
            if(prStatement != null) {
                prStatement.setInt(1, idUtente);
                prStatement.setInt(2, idInsegnamento);
                prStatement.setString(3, Character.toString(stato));
            }
            prStatement.executeUpdate();
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }finally {
            DAO.closeConnection(conn, prStatement);
        }

        return res;
    }

    public static boolean updatePrenotazione(int idPrenotazione, char stato){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("UPDATE Prenotazione set stato = ? WHERE idPrenotazione = ? ") : null;
            if(prStatement != null) {
                prStatement.setString(1, stato + "");
                prStatement.setInt(2, idPrenotazione);
            }
            prStatement.executeUpdate();
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }finally {
            DAO.closeConnection(conn, prStatement);
        }

        return res;
    }

    public static boolean updateNotifications(int utente){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("UPDATE Prenotazione set toNotify = 0 WHERE idUtente = ? ") : null;
            if(prStatement != null) {
                prStatement.setInt(1, utente);
            }
            prStatement.executeUpdate();
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }finally {
            DAO.closeConnection(conn, prStatement);
        }
        return res;
    }


    public static ArrayList<Prenotazione> getPrenotazioni(){
        /*
        SELECT corso.*, docente.*, utente.idUtente, utente.Nome as NomeUtente, utente.Cognome as CognomeUtente, utente.Email, utente.Ruolo, Insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora, prenotazione.Idprenotazione, prenotazione.stato
        FROM prenotazione JOIN insegnamento ON(prenotazione.idInsegnamento = insegnamento.idInsegnamento)
		JOIN corso ON (insegnamento.idCorso = corso.IdCorso)
        JOIN docente ON (docente.idDocente = insegnamento.idDocente)
        JOIN utente ON (prenotazione.IdUtente = utente.idUtente)
        GROUP BY corso.Titolo, docente.Nome, utente.Nome, utente.cognome, insegnamento.Giorno, insegnamento.Ora, prenotazione.stato
        */

        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Prenotazione> prenotazioni = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT corso.*, docente.*, utente.idUtente, utente.Nome as NomeUtente, utente.Cognome as CognomeUtente, utente.Email, utente.Ruolo," +
                    "insegnamento.idInsegnamento, insegnamento.isAttivo, insegnamento.Giorno, insegnamento.Ora, prenotazione.Idprenotazione, prenotazione.stato" +
                    "        FROM prenotazione JOIN insegnamento ON(prenotazione.idInsegnamento = insegnamento.idInsegnamento)" +
                    "        JOIN corso ON (insegnamento.idCorso = corso.IdCorso) " +
                    "        JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        JOIN utente ON (prenotazione.IdUtente = utente.idUtente)" +
                    "        GROUP BY corso.Titolo, docente.Nome, docente.Cognome, utente.Nome, utente.cognome, insegnamento.Giorno, insegnamento.Ora, prenotazione.stato") : null;

            if(rs != null){
                while (rs.next()){
                    Docente d = new Docente(rs.getInt("idDocente"),rs.getString("Nome"), rs.getString("Cognome"), rs.getBoolean("isAttivo"));
                    Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"), rs.getBoolean("isAttivo"));
                    Utente u = new Utente(rs.getInt("idUtente"), rs.getString("NomeUtente"), rs.getString("CognomeUtente"), rs.getString("Email"), null, rs.getString("Ruolo"));
                    Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"), rs.getBoolean("isAttivo"));
                    prenotazioni.add(new Prenotazione(rs.getInt("idPrenotazione"), u, i, rs.getString("stato").charAt(0)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return prenotazioni;
    }

    public static ArrayList<Prenotazione> getPrenotazioniDaUtente(Utente user) {
        Connection conn = DAO.getConnection();
        PreparedStatement prStatement = null;
        ArrayList<Prenotazione> prenotazioni = new ArrayList<>();


        //TODO: CONTROLLARE QUERY: non restituisce prenotazioni uguali
        try {
            prStatement = conn != null ? conn.prepareStatement("SELECT corso.*, docente.*, utente.idUtente, utente.Nome as NomeUtente, utente.Cognome as CognomeUtente, utente.Email, utente.Ruolo," +
                    "insegnamento.idInsegnamento, insegnamento.isAttivo, insegnamento.Giorno, insegnamento.Ora, prenotazione.Idprenotazione, prenotazione.stato" +
                    "        FROM prenotazione JOIN insegnamento ON(prenotazione.idInsegnamento = insegnamento.idInsegnamento)" +
                    "        JOIN corso ON (insegnamento.idCorso = corso.IdCorso) " +
                    "        JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        JOIN utente ON (prenotazione.IdUtente = utente.idUtente)" +
                    "        WHERE utente.idUtente = ?"): null;//+
                  //  "        GROUP BY corso.Titolo, docente.Nome, docente.Cognome, utente.Nome, utente.cognome, insegnamento.Giorno, insegnamento.Ora, prenotazione.stato") : null;
            if(prStatement != null) {
                prStatement.setInt(1, user.getId());
            }
            ResultSet rs = prStatement.executeQuery();
            while (rs.next()){
                Docente d = new Docente(rs.getInt("idDocente"),rs.getString("Nome"), rs.getString("Cognome"), rs.getBoolean("isAttivo"));
                Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"), rs.getBoolean("isAttivo"));
                Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"), rs.getBoolean("isAttivo"));
                prenotazioni.add(new Prenotazione(rs.getInt("idPrenotazione"), null, i, rs.getString("stato").charAt(0)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, prStatement);
        }
        return prenotazioni;
    }


    public static ArrayList<Prenotazione> getNotificheDaUtente(Utente user) {
        Connection conn = DAO.getConnection();
        PreparedStatement prStatement = null;
        ArrayList<Prenotazione> prenotazioni = new ArrayList<>();

        try {
            prStatement = conn != null ? conn.prepareStatement("SELECT corso.*, docente.*, utente.idUtente, utente.Nome as NomeUtente, utente.Cognome as CognomeUtente, utente.Email, utente.Ruolo," +
                    "insegnamento.idInsegnamento, insegnamento.isAttivo, insegnamento.Giorno, insegnamento.Ora, prenotazione.Idprenotazione, prenotazione.stato" +
                    "        FROM prenotazione JOIN insegnamento ON(prenotazione.idInsegnamento = insegnamento.idInsegnamento)" +
                    "        JOIN corso ON (insegnamento.idCorso = corso.IdCorso) " +
                    "        JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        JOIN utente ON (prenotazione.IdUtente = utente.idUtente)" +
                    "        WHERE utente.idUtente = ? AND prenotazione.toNotify = TRUE "): null;//+
            //  "        GROUP BY corso.Titolo, docente.Nome, docente.Cognome, utente.Nome, utente.cognome, insegnamento.Giorno, insegnamento.Ora, prenotazione.stato") : null;
            if(prStatement != null) {
                prStatement.setInt(1, user.getId());
            }
            ResultSet rs = prStatement.executeQuery();
            while (rs.next()){
                Docente d = new Docente(rs.getInt("idDocente"),rs.getString("Nome"), rs.getString("Cognome"), rs.getBoolean("isAttivo"));
                Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"), rs.getBoolean("isAttivo"));
                Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"), rs.getBoolean("isAttivo"));
                prenotazioni.add(new Prenotazione(rs.getInt("idPrenotazione"), null, i, rs.getString("stato").charAt(0)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, prStatement);
        }
        updateNotifications(user.getId());
        return prenotazioni;
    }


    /*
    public static ArrayList<Prenotazione> getPrenotazioniPossibili(){   //restituisce un array contenente tutte le combinazioni di prenotazioni possibili nella settimana
        ArrayList<Prenotazione> allPrenotazioniPossible = new ArrayList<>();
        ArrayList<Insegnamento> insegnamenti = Insegnamento.getInsegnamenti();

        for (int i = 0; i < insegnamenti.size(); i++) {
            Insegnamento in = insegnamenti.get(i);
            for (String giorno : giorni) {
                for (double ora : ore) {
                    allPrenotazioniPossible.add(new Prenotazione(in.getDocente(), null, in.getCorso(), giorno, ora));
                }
            }
        }
        return allPrenotazioniPossible;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prenotazione that = (Prenotazione) o;
        return this.docente.equals(that.docente) && this.corso.equals(that.corso) && this.utente.equals(that.utente)
                && this.giorno.equals(that.getGiorno()) && (this.ora == that.getOra());
    }

    public boolean myEquals(Prenotazione that) { //non viene effettuato il controllo sugli attributi
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        return this.docente.equals(that.docente) && this.corso.equals(that.corso)
                && this.giorno.equals(that.getGiorno()) && (this.ora == that.getOra());
    }

    private void prepare(PreparedStatement prStatement) throws SQLException {
        if(prStatement != null) {
            prStatement.setString(1, docente.getNome());
            prStatement.setString(2, docente.getCognome());
            prStatement.setString(3, corso.getNome());
            prStatement.setString(4, utente.getEmail());
            prStatement.setString(5, this.giorno);
            prStatement.setDouble(6, this.ora);

            prStatement.executeUpdate();
        }
    }



    @Override
    public String toString() {
        return corso.getNome() +
                " " + docente.getNome() + " " + docente.getNome() +
                " " + utente.getEmail() +
                " " + giorno + " " + ora;
    }
    */
}
