package com.example.prenotazioni;

import com.google.gson.Gson;
import dao.*;
import utils.RedirectMessage;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "getData", value = "/getData")
public class GetData extends HttpServlet {
    public void init() {
        DAO.registerDriver();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if(operation == null || operation.equals("")) {
            return;
        }

        PrintWriter pr = response.getWriter();
        response.setContentType("application/json");

        if(operation.equals("getUserData")) {
            HttpSession session = request.getSession();
            if(session.getAttribute("id") == null){
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }

            int id = (int) session.getAttribute("id");

            Utente u = DAO.getUserById(id);
            if(u != null) {
                String tojson = new Gson().toJson(u);
                pr.print(tojson);
                pr.flush();
                pr.close();
            } else {
                RedirectMessage rm = new RedirectMessage("ko", "Utente non trovato", "/index.html");
                pr.println(new Gson().toJson(rm));
                return;
            }
        }else if(operation.equals("getTeachers")) {
            ArrayList<Docente> docenti = Docente.getDocenti();
            pr.write(new Gson().toJson(docenti));
            System.out.println("DOCENTI:" + new Gson().toJson(docenti));
            pr.flush();
            pr.close();
        }else if(operation.equals("getCourses")){
            ArrayList<Corso> corsi = Corso.getCorsi();
            pr.write(new Gson().toJson(corsi));
            System.out.println("CORSI:" +  new Gson().toJson(corsi));
            pr.flush();
            pr.close();
        }else if(operation.equals("getTeaching")){
            ArrayList<Insegnamento> insegnamenti = Insegnamento.getInsegnamenti();
            System.out.println("INSEGNAMENTI" + new Gson().toJson(insegnamenti));
            pr.write(new Gson().toJson(insegnamenti));
            pr.flush();
            pr.close();
        }else if(operation.equals("getUsers")){
            ArrayList<Utente> utenti = Utente.getUtenti();
            System.out.println("UTENTI:" +new Gson().toJson(utenti));
            pr.write(new Gson().toJson(utenti));
            pr.flush();
            pr.close();
        }else if(operation.equals("getReservation")){
            ArrayList<Prenotazione> prenotazioni = Prenotazione.getPrenotazioni();
            System.out.println("PRENOTAZIONI" + new Gson().toJson(prenotazioni));
            pr.write(new Gson().toJson(prenotazioni));
            pr.flush();
            pr.close();
        }else if(operation.equals("getAvailableTeaching")){
            ArrayList<Insegnamento> insegnamenti = Insegnamento.getInsegnamentiDisponibili();
            System.out.println("INSEGNAMENTI DISPONIBILI:" + new Gson().toJson(insegnamenti));
            pr.write( new Gson().toJson(insegnamenti));
            pr.flush();
            pr.close();
        }else if(operation.equals("getTeachingByCourse")){
            String  teaching_json = Insegnamento.getInsegnamentiDaIdMateria(request.getParameter("id"));
            pr.write(teaching_json);
            System.out.println("INSEGNAMENTI DISPONIBILI:" + teaching_json);
            pr.flush();
            pr.close();
        }else if (operation.equals("getMyReservations")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") == null)
                return;
            Integer id = (Integer) session.getAttribute("id");
            Utente user = DAO.getUserById(id);
            if (user != null){
                ArrayList<Prenotazione> prenotazioni = Prenotazione.getPrenotazioniDaUtente(user);
                System.out.println("MIE PRENOTAZIONI" + new Gson().toJson(prenotazioni));
                pr.write(new Gson().toJson(prenotazioni));
                pr.flush();
                pr.close();
            }
        }
    }
}
