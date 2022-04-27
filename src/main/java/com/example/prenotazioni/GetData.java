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
            HttpSession session = request.getSession();
            if(session.getAttribute("id") != null && Utils.checkStatus(session.getAttribute("id"), "admin")){
                ArrayList<Docente> docenti = Docente.getDocenti();
                pr.write(new Gson().toJson(docenti));
                System.out.println("DOCENTI:" + new Gson().toJson(docenti));
                pr.flush();
                pr.close();
                return;
            }else {
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }

        }else if(operation.equals("getCourses")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") != null && Utils.checkStatus(session.getAttribute("id"), "admin")){
                ArrayList<Corso> corsi = Corso.getCorsi();
                pr.write(new Gson().toJson(corsi));
                System.out.println("CORSI:" +  new Gson().toJson(corsi));
                pr.flush();
                pr.close();
                return;
            }else {
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }
        }else if(operation.equals("getCoursesWithTeaching")){
            ArrayList<Corso> corsi = Corso.getCorsiConInsegnamenti();
            pr.write(new Gson().toJson(corsi));
            System.out.println("CORSI:" +  new Gson().toJson(corsi));
            pr.flush();
            pr.close();
        }else if(operation.equals("getTeaching")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") != null && Utils.checkStatus(session.getAttribute("id"), "admin")){
                ArrayList<Insegnamento> insegnamenti = Insegnamento.getInsegnamenti();
                System.out.println("INSEGNAMENTI" + new Gson().toJson(insegnamenti));
                pr.write(new Gson().toJson(insegnamenti));
                pr.flush();
                pr.close();
                return;
            }else {
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }
        }else if(operation.equals("getUsers")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") != null && Utils.checkStatus(session.getAttribute("id"), "admin")){
                ArrayList<Utente> utenti = Utente.getUtenti();
                System.out.println("UTENTI:" +new Gson().toJson(utenti));
                pr.write(new Gson().toJson(utenti));
                pr.flush();
                pr.close();
            }else {
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }
        }else if(operation.equals("getReservation")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") != null && Utils.checkStatus(session.getAttribute("id"), "admin")){
                ArrayList<Prenotazione> prenotazioni = Prenotazione.getPrenotazioni();
                System.out.println("PRENOTAZIONI" + new Gson().toJson(prenotazioni));
                pr.write(new Gson().toJson(prenotazioni));
                pr.flush();
                pr.close();
            }else {
                RedirectMessage rm = new RedirectMessage("ko", "Non sei loggato", "/index.html");
                pr.println(new Gson().toJson(rm));
                pr.flush();
                pr.close();
                return;
            }
        }else if(operation.equals("getAvailableTeaching")){
            ArrayList<Insegnamento> insegnamenti = Insegnamento.getInsegnamentiDisponibili();
            System.out.println("INSEGNAMENTI DISPONIBILI:" + new Gson().toJson(insegnamenti));
            pr.write( new Gson().toJson(insegnamenti));
            pr.flush();
            pr.close();
        }else if(operation.equals("getTeachingByCourse")){
            HttpSession session = request.getSession();
            Integer id = (Integer) session.getAttribute("id");
            Utente user;
            if (id != null){
                user = DAO.getUserById(id);
            }else {
                user = null;
            }

            pr.write(Insegnamento.getInsegnamentiDaIdMateria(request.getParameter("id"),user));
            pr.flush();
            pr.close();
        }else if (operation.equals("getMyReservations")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") == null) {
                pr.print("null");
                pr.flush();
                pr.close();
                return;
            }
            Integer id = (Integer) session.getAttribute("id");
            System.out.println(id);
            Utente user = DAO.getUserById(id);
            System.out.println(user);
            if (user != null){
                ArrayList<Prenotazione> prenotazioni = Prenotazione.getPrenotazioniDaUtente(user);
                System.out.println("PRENOTAZIONI TROVATE: " + prenotazioni);
                pr.write( new Gson().toJson(prenotazioni));
                pr.flush();
                pr.close();
            }else{
                pr.write("null");
                pr.flush();
                pr.close();
            }
        }else if (operation.equals("getMyNotifications")){
            HttpSession session = request.getSession();
            if(session.getAttribute("id") == null) {
                pr.print("null");
                pr.flush();
                pr.close();
                return;
            }
            Integer id = (Integer) session.getAttribute("id");
            System.out.println(id);
            Utente user = DAO.getUserById(id);
            System.out.println(user);
            if (user != null){
                ArrayList<Prenotazione> notifiche = Prenotazione.getNotificheDaUtente(user);
                pr.write( new Gson().toJson(notifiche));
                pr.flush();
                pr.close();
            }else{
                pr.write("null");
                pr.flush();
                pr.close();
            }
        }
    }
}
