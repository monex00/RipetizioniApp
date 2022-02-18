package com.example.prenotazioni;

import dao.Corso;
import dao.DAO;
import dao.Docente;
import dao.Utente;
import utils.Message;
import utils.SimpleMessage;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "courseOperations", value = "/courseOperations")
public class CourseOperations extends HttpServlet {
    public void init() {
        DAO.registerDriver();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idCorso = request.getParameter("idCorso");
        String nome = request.getParameter("name");
        String operation = request.getParameter("operation");

        HttpSession session = request.getSession();

        PrintWriter pr = response.getWriter();
        response.setContentType("application/json");

        //TODO: move controls of admin status to a static class

        if(!Utils.checkStatus(session.getAttribute("id"), "admin")){
            Utils.sendMessage(pr, "ko", "Non sei autorizzato ad eseguire questa operazione", "Operazione non ammessa");
            pr.close();
            return;
        }


        if(nome == null  || nome.equals("")) {
            Utils.sendMessage(pr, "ko", "Nome non valido", "Nome non valido");
            pr.close();
            return;
        }

        if(operation == null || operation.equals("")) {
            Utils.sendMessage(pr, "ko", "Operazione non valida", "Operazione non valida");
            pr.close();
            return;
        }


        if(operation.equals("add")) {
            if (!Corso.addCorsoToDB(nome)) {
                Utils.sendMessage(pr, "ko", "Errore nell'aggiunta del corso", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Corso aggiunto con successo", "Operazione eseguita");
            pr.close();

        }else if(operation.equals("delete")) {
            if (!Corso.removeCorsoFromDB(Integer.parseInt(idCorso))) {
                Utils.sendMessage(pr, "ko", "Errore nella rimozione del corso", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Corso rimosso con successo", "Operazione eseguita");
            pr.flush();
        }
    }


}
