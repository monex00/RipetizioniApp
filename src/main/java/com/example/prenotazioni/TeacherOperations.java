package com.example.prenotazioni;


import dao.DAO;
import dao.Docente;
import dao.Utente;
import utils.Message;
import utils.SimpleMessage;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;


@WebServlet(name = "teacherOperations", value = "/teacherOperations")
public class TeacherOperations extends HttpServlet {
    private String message;

    public void init() {
        DAO.registerDriver();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("idTeacher");
        String nome = request.getParameter("name");
        String cognome =  request.getParameter("surname");
        String operation = request.getParameter("operation");

        HttpSession session = request.getSession();

        PrintWriter pr = response.getWriter();
        response.setContentType("application/json");


        if(!Utils.checkStatus(session.getAttribute("id"), "admin")){
            Utils.sendMessage(pr, "ko", "Non sei autorizzato ad eseguire questa operazione", "Operazione non ammessa");
            pr.close();
            return;
        }

        if(nome == null || cognome == null || nome.equals("") || cognome.equals("")) {
            Utils.sendMessage(pr, "ko", "Nome o cognome non validi", "Dati non validi");
            pr.close();
            return;
        }

        if(operation == null || operation.equals("")) {
            Utils.sendMessage(pr, "ko", "Operazione non valida", "Dati non validi");
            pr.close();
            return;
        }


        if(operation.equals("add")) {
            if (!Docente.addDocenteToDB(nome, cognome)) {
                Utils.sendMessage(pr, "ko", "Errore nell'aggiunta del docente" , "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Docente aggiunto con successo", "Operazione eseguita");
            pr.close();

        }else if(operation.equals("delete")) {
            if (!Docente.removeDocenteFromDB(Integer.parseInt(id))) {
                Utils.sendMessage(pr, "ko", "Errore nella rimozione del docente", "Errore");
                pr.close();
                return;
            }
            Utils.sendMessage(pr, "ok", "Docente rimosso con successo", "Operazione eseguita");
            pr.close();
        }
    }


    public void destroy() {
    }
}