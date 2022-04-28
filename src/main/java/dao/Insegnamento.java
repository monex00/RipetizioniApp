package dao;

import java.sql.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class Insegnamento{
    private int id;
    private Docente docente;
    private Corso corso;
    private float ora;
    private int giorno;
    private boolean isAttivo;

    public Insegnamento(int id, Docente docente, Corso corso, float ora, int giorno, boolean isAttivo) {
        this.id = id;
        this.docente = docente;
        this.corso = corso;
        this.ora = ora;
        this.giorno = giorno;
        this.isAttivo = isAttivo;
    }

    public Insegnamento(Docente docente, Corso corso, float ora, int giorno) {
        this.id = -1;
        this.docente = docente;
        this.corso = corso;
        this.ora = ora;
        this.giorno = giorno;
    }

    public Insegnamento(int id, float ora, int giorno, boolean isAttivo) {
        this.id = id;
        this.docente = null;
        this.corso = null;
        this.ora = ora;
        this.giorno = giorno;
        this.isAttivo = isAttivo;
    }

    public int getId() {
        return id;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Corso getCorso() {
        return corso;
    }

    public float getOra() {
        return ora;
    }

    public int getGiorno() {
        return giorno;
    }

    public void setCorso(Corso corso) {
        this.corso = corso;
    }

    public boolean isAttivo() {
        return isAttivo;
    }

    public void setAttivo(boolean attivo) {
        this.isAttivo = attivo;
    }

    public static boolean addInsegnamentoToDB(int idDocente, int idCorso, float ora, int giorno){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            //evito di inserire insegnameno dello stesso docente nello stesso giorno non a distanza di 1 ora
            prStatement = conn != null ? conn.prepareStatement("SELECT ora FROM insegnamento WHERE idDocente = ? AND giorno = ? AND isAttivo=1 AND idCorso = ?") : null;
            if(prStatement != null) {
                prStatement.setInt(1, idDocente);
                prStatement.setInt(2, giorno);
                prStatement.setInt(3, idCorso);
                ResultSet rs = prStatement.executeQuery();
                while(rs.next()){
                    if(Math.abs(rs.getFloat("ora") - ora) < 1){
                        return false;
                    }
                }
            }


            //metto attivo l'insegnamento se esisteva già altrimenti lo ricreo
            int changed = 0;
            prStatement = conn != null ? conn.prepareStatement("UPDATE insegnamento set isAttivo = 1 WHERE idDocente = ? AND idCorso=? AND ora = ? AND giorno = ?") : null;
            if(prStatement != null) {
                prStatement.setInt(1, idDocente);
                prStatement.setInt(2, idCorso);
                prStatement.setFloat(3, ora);
                prStatement.setInt(4, giorno);
                changed = prStatement.executeUpdate();
            }

            if(changed > 0) {
                return true;
            }

            prStatement = conn != null ? conn.prepareStatement("INSERT INTO Insegnamento(idDocente,idCorso, Ora, Giorno) VALUES (( ? ), (? ), ?, ?)") : null;
            if(prStatement != null) {
                prStatement.setInt(1, idDocente);
                prStatement.setInt(2, idCorso);
                prStatement.setFloat(3, ora);
                prStatement.setInt(4, giorno);
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

    public static boolean deleteInsegnamentoFromDB(int id){
        Connection conn = DAO.getConnection();
        Boolean res = true;
        PreparedStatement prStatement = null;

        try {
            prStatement = conn != null ? conn.prepareStatement("UPDATE insegnamento set isAttivo = 0 WHERE idInsegnamento = ?" ) : null;
            if(prStatement != null) {
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


    public static ArrayList<Insegnamento> getInsegnamenti(){
        /*
        SELECT corso.idCorso, corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora
        FROM insegnamento JOIN corso ON (insegnamento.idCorso = corso.IdCorso) JOIN docente ON (docente.idDocente = insegnamento.idDocente)
        GROUP BY corso.Titolo, docente.Nome, docente.Cognome
         */
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Insegnamento> insegnamenti = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT corso.idCorso, corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora" +
                    "        FROM insegnamento JOIN corso ON (insegnamento.idCorso = corso.IdCorso) JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        WHERE insegnamento.isAttivo = 1" +
                    "        GROUP BY corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.Giorno, insegnamento.Ora") : null;

            if(rs != null){
                while (rs.next()){
                    Docente d = new Docente(rs.getInt("idDocente"), rs.getString("Nome"), rs.getString("Cognome"), true);
                    Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"),true);
                    Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"),true);
                    insegnamenti.add(i);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return insegnamenti;
    }

    public static ArrayList<Insegnamento> getInsegnamentiDisponibili(){
        /*
        SELECT corso.idCorso, corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora
         FROM insegnamento JOIN corso ON (insegnamento.idCorso = corso.IdCorso) JOIN docente ON (docente.idDocente = insegnamento.idDocente)
         WHERE insegnamento.idInsegnamento NOT IN ( SELECT prenotazione.idInsegnamento FROM prenotazione WHERE prenotazione.stato = 'A')
         */
        Connection conn = DAO.getConnection();
        Statement statement = null;
        ArrayList<Insegnamento> insegnamenti = new ArrayList<>();

        try {
            statement = conn != null ? conn.createStatement() : null;
            ResultSet rs = statement != null ? statement.executeQuery("SELECT corso.idCorso, corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora" +
                    "        FROM insegnamento JOIN corso ON (insegnamento.idCorso = corso.IdCorso) JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        WHERE  insegnamento.isAttivo = 1 AND insegnamento.idInsegnamento NOT IN ( SELECT prenotazione.idInsegnamento FROM prenotazione WHERE prenotazione.stato = 'A')" +
                    "        GROUP BY corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.Giorno, insegnamento.Ora") : null;

            if(rs != null){
                while (rs.next()){
                    Docente d = new Docente(rs.getInt("idDocente"), rs.getString("Nome"), rs.getString("Cognome"),true);
                    Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"),true);
                    Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"),true);
                    insegnamenti.add(i);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, statement);
        }
        return insegnamenti;
    }

    public static ArrayList<InsegnamentiDaIdMateria.InsegnamentiPerDocente> getInsegnamentiDaIdMateria(String id, Utente user){
        System.out.println("ID PROVA: " + user);
        Connection conn = DAO.getConnection();
        PreparedStatement prStatement = null;
        InsegnamentiDaIdMateria insegnamenti = new InsegnamentiDaIdMateria();

        try {
            prStatement = conn != null ? conn.prepareStatement("SELECT docente.idDocente, docente.Nome, docente.Cognome,corso.idCorso, corso.Titolo, insegnamento.idInsegnamento, insegnamento.Giorno, insegnamento.Ora" +
                    "        FROM insegnamento JOIN corso ON (insegnamento.idCorso = corso.IdCorso) JOIN docente ON (docente.idDocente = insegnamento.idDocente)" +
                    "        WHERE corso.IdCorso = ? AND insegnamento.isAttivo = 1 AND insegnamento.idInsegnamento NOT IN ( SELECT prenotazione.idInsegnamento FROM prenotazione WHERE prenotazione.stato = 'A')" +
                    "        GROUP BY corso.Titolo, docente.idDocente, docente.Nome, docente.Cognome, insegnamento.Giorno, insegnamento.Ora") : null;
            if(prStatement != null) {
                prStatement.setString(1, id);
            }
            ResultSet rs = prStatement.executeQuery();

            if(rs != null){
                while (rs.next()){
                    Docente d = new Docente(rs.getInt("idDocente"), rs.getString("Nome"), rs.getString("Cognome"),true);
                    Corso c = new Corso(rs.getInt("idCorso"), rs.getString("Titolo"),true);
                    Insegnamento i = new Insegnamento(rs.getInt("idInsegnamento"), d, c, rs.getFloat("Ora"), rs.getInt("Giorno"),true);
                    insegnamenti.addInsegnamento(i);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DAO.closeConnection(conn, prStatement);
        }

        insegnamenti.removeNotAllowedDayHour(user);

        return insegnamenti.insegnamenti;
    }

    public static class InsegnamentiDaIdMateria {
        private ArrayList<InsegnamentiPerDocente> insegnamenti;

        public InsegnamentiDaIdMateria() {
            this.insegnamenti = new ArrayList<>();
        }

        public void addInsegnamento(Insegnamento i) {
            boolean there_is = false;
            for (InsegnamentiPerDocente in : insegnamenti) {
                if (in.getDocente().equals(i.getDocente())) {
                    System.out.println("trovato");
                    in.addInsegnamento(i);
                    there_is = true;
                }
            }
            if (!there_is) {
                System.out.println("aggiungo insegnamneto");
                InsegnamentiPerDocente new_i = new InsegnamentiPerDocente(i.getDocente());
                new_i.addInsegnamento(i);
                insegnamenti.add(new_i);
            }
        }

        public void removeNotAllowedDayHour(Utente user) {
            ArrayList<Prenotazione> prenotazioniTutte = Prenotazione.getPrenotazioni();
            ArrayList<Integer> toRemove = new ArrayList<>();
            for (InsegnamentiPerDocente insDoc : insegnamenti) {
                ArrayList<Integer> toRem = new ArrayList<>();
                for (Insegnamento ins : insDoc.insegnamenti) {
                    if (user != null) {
                        for (Prenotazione pren : Prenotazione.getPrenotazioniDaUtente(user)) {
                            if (pren.getStato() == 'A') {
                                if (pren.getInsegnamento().getGiorno() == ins.getGiorno() && pren.getInsegnamento().getOra() == ins.getOra()) {
                                    ins.setAttivo(false);
                                }
                            }
                        }
                    }

                    for (Prenotazione pren : prenotazioniTutte) {
                        if (pren.getStato() == 'A' && pren.getInsegnamento().getGiorno() == ins.getGiorno() && pren.getInsegnamento().getOra() == ins.getOra() && pren.getInsegnamento().getDocente().getId() == ins.getDocente().getId() && pren.getInsegnamento().getCorso() != ins.getCorso()) {
                            System.out.println("Rimuovo" + insDoc.insegnamenti.indexOf(ins));
                            toRem.add(insDoc.insegnamenti.indexOf(ins));
                        }
                    }


                }
                for (Integer i: toRem) {
                    insDoc.insegnamenti.remove(i.intValue());
                }
                if (insDoc.insegnamenti.size() == 0) toRemove.add(insegnamenti.indexOf(insDoc));
            }
            for (Integer i : toRemove) {
                insegnamenti.remove(i.intValue());
            }
        }

        public static class InsegnamentiPerDocente {
            private Docente docente;
            private ArrayList<Insegnamento> insegnamenti;

            public InsegnamentiPerDocente(Docente docente) {
                this.docente = docente;
                this.insegnamenti = new ArrayList<>();
            }

            public void addInsegnamento(Insegnamento insegnamento) {
                this.insegnamenti.add(insegnamento);
            }

            public Docente getDocente() {
                return docente;
            }

            public void setDocente(Docente docente) {
                this.docente = docente;
            }

            public ArrayList<Insegnamento> getInsegnamenti() {
                return insegnamenti;
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insegnamento that = (Insegnamento) o;
        return this.docente.equals(that.docente) && this.corso.equals(that.corso);
    }

}
