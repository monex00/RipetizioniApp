package com.example.prenotazioni;

import dao.Corso;
import dao.Docente;
import dao.Insegnamento;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "teachingOperations", value = "/teachingOperations")
public class TeachingOperation extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idDocente = request.getParameter("idDocente");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");

        String idCorso = request.getParameter("idCorso");
        String corso = request.getParameter("corso");

        String idInsegnamento = request.getParameter("idInsegnamento");


        String giorno = request.getParameter("giorno");
        String ora = request.getParameter("ora");
        String operation = request.getParameter("operation");


        HttpSession session = request.getSession();

        PrintWriter pr = response.getWriter();
        response.setContentType("application/json");

        if(!Utils.checkStatus(session.getAttribute("id"), "admin")){
            Utils.sendMessage(pr, "ko", "Non sei autorizzato ad eseguire questa operazione", "Operazione non ammessa");
            pr.close();
            return;
        }

        if(operation == null || operation.equals("")) {
            Utils.sendMessage(pr, "ko", "Operazione non valida", "Operazione non valida");
            pr.close();
            return;
        }


        if(operation.equals("add")){
            if(idCorso == null || idDocente == null || nome == null || cognome == null || corso == null || giorno == null || ora == null || nome.equals("") || cognome.equals("") || idCorso.equals("") || idDocente.equals("") ||  corso.equals("") || giorno.equals("") || ora.equals("")){
                Utils.sendMessage(pr, "ko", "Inserire tutti i campi", "Campi mancanti");
                pr.close();
                return;
            }

            if(!Insegnamento.addInsegnamentoToDB(Integer.parseInt(idDocente), Integer.parseInt(idCorso), Float.parseFloat(ora), Integer.parseInt(giorno))){
                Utils.sendMessage(pr, "ko", "Errore nell'inserimento dell'insegnamento" , "Errore");
                pr.close();
                return;
            }

            Utils.sendMessage(pr, "ok", "Insegnamento inserito con successo", "Operazine eseguita");
            pr.close();
        }else if(operation.equals("delete")){

            if(idInsegnamento == null || idInsegnamento.equals("")){
                Utils.sendMessage(pr, "ko", "Inserire un id valido", "Campi mancanti");
                pr.close();
                return;
            }

            if(!Insegnamento.deleteInsegnamentoFromDB(Integer.parseInt(idInsegnamento))){
                Utils.sendMessage(pr, "ko", "Errore nella cancellazione dell'insegnamento" ,"Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Insegnamento cancellato con successo", "Operazione eseguita");
            pr.close();
        }


    }
}
