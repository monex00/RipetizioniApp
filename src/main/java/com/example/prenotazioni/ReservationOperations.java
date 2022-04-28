package com.example.prenotazioni;

import dao.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "reservationOperations", value = "/reservationOperations")
public class ReservationOperations extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idUtente = request.getParameter("idUtente");
        String idInsegnamento = request.getParameter("idInsegnamento");
        String idPrenotazione = request.getParameter("idPrenotazione");
        //request.getParamete("stato");
        String operation = request.getParameter("operation");

        HttpSession session = request.getSession();

        PrintWriter pr = response.getWriter();
        response.setContentType("application/json");


        if(operation == null || operation.equals("")) {
            Utils.sendMessage(pr, "ko", "Operazione non valida", "Operazione non valida");
            pr.close();
            return;
        }


        if(operation.equals("add")) {
            if(!Utils.checkStatus(session.getAttribute("id"), "admin")){
                Utils.sendMessage(pr, "ko", "Non sei autorizzato ad eseguire questa operazione" ,"Operazione non ammessa");
                pr.close();
                return;
            }

            if(request.getParameter("stato") == null) {
                Utils.sendMessage(pr, "ko", "Nessun stato inserito" ,"Stato mancante");
                pr.close();
                return;
            }

            char stato = request.getParameter("stato").charAt(0);

            if(idUtente == null || idUtente.equals("") || idInsegnamento == null || idInsegnamento.equals("")){
                Utils.sendMessage(pr, "ko", "Parametri non validi", "Parametri mancanti");
                pr.close();
                return;
            }

            if (!Prenotazione.addPrenotazioneToDB(Integer.parseInt(idUtente), Integer.parseInt(idInsegnamento), stato,1)) {
                Utils.sendMessage(pr, "ko", "Errore nell'aggiunta della prenotazione", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Prenotazione aggiunta con successo", "Operazione eseguita");
            pr.close();

        } else if(operation.equals("update")) {
            if(!Utils.checkStatus(session.getAttribute("id"), "admin")){
                Utils.sendMessage(pr, "ko", "Non sei autorizzato ad eseguire questa operazione" ,"Operazione non ammessa");
                pr.close();
                return;
            }

            if(request.getParameter("stato") == null) {
                Utils.sendMessage(pr, "ko", "Nessun stato inserito");
                pr.close();
                return;
            }
            char stato = request.getParameter("stato").charAt(0);
            if(idPrenotazione == null || idPrenotazione.equals("")){
                Utils.sendMessage(pr, "ko", "Parametri non validi" ,"Parametri mancanti");
                pr.close();
                return;
            }

            if (!Prenotazione.updatePrenotazione(Integer.parseInt(idPrenotazione), stato, true)) {
                Utils.sendMessage(pr, "ko", "Errore nell'aggiornamento della prenotazione" , "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Stato della prenotazione aggiornato con successo", "Operazione eseguita");
            pr.close();
        }else if (operation.equals("userAdd")){
            if (session.getAttribute("id") == null){
                Utils.sendMessage(pr, "ko_auth", "Devi essere autenticato per poterti prenotare!", "Non sei autenticato");
                pr.close();
                return;
            }

            int id = (Integer) session.getAttribute("id");

            if(idInsegnamento == null || idInsegnamento.equals("") || DAO.getUserById(id) == null){
                Utils.sendMessage(pr, "ko", "Qualcosa è andato storto, riprova", "Errore");
                pr.close();
                return;
            }
            if (!Prenotazione.addPrenotazioneToDB(id, Integer.parseInt(idInsegnamento), 'A', 0)) {
                Utils.sendMessage(pr, "ko", "C'è stato un errore con la tua prenotazione, potrebbe non essere più disponibile, riprova.", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "La tua prenotazione è avvenuta con successo, potrà essere visualizzata nella sezione delle tue prenotazioni attive.", "Successo");
            pr.close();
        }else if(operation.equals("userUpdate")) {
            if (session.getAttribute("id") == null){
                Utils.sendMessage(pr, "ko", "Qualcosa è andato storto, riprova", "Errore");
                pr.flush();
                pr.close();
                return;
            }

            int id = (Integer) session.getAttribute("id");
            if(idPrenotazione == null || idPrenotazione.equals("") || request.getParameter("stato") == null || DAO.getUserById(id) == null){
                Utils.sendMessage(pr, "ko", "Qualcosa è andato storto, riprova.", "Errore");
                pr.close();
                return;
            }
            char stato = request.getParameter("stato").charAt(0);
            if (!Prenotazione.updatePrenotazione(Integer.parseInt(idPrenotazione), stato, false)) {
                Utils.sendMessage(pr, "ko", "Qualcosa è andato storto, riprova.", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Operazione avvenuta con successo.", "Successo");
            pr.close();
        }
    }
}
