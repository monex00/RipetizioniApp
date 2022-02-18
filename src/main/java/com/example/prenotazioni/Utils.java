package com.example.prenotazioni;

import dao.DAO;
import dao.Utente;
import utils.Message;
import utils.ModalMessage;
import utils.SimpleMessage;

import java.io.PrintWriter;

public class Utils {

    public static void sendMessage(PrintWriter pr, String status, String msg) {
        Message m = new SimpleMessage(status, msg);
        pr.write(m.toJson());
        pr.flush();
    }

    public static void sendMessage(PrintWriter pr, String status, String msg, String title) {
        Message m = new ModalMessage(status, msg, title);
        pr.write(m.toJson());
        pr.flush();
    }


    public static boolean checkStatus(Object id, String status) {
        if(!(id instanceof Integer)) {
            return false;
        }

        Utente user = DAO.getUserById((int) id);

        if(user == null) {
            return false;
        }

        if(!user.getRuolo().equals(status)){
            return false;
        }

        return true;
    }
}
