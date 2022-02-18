package com.example.prenotazioni;

import dao.*;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "PrenotazioniServlet", value = "/pre-servlet")
public class PrenotazioniServlet extends HttpServlet {
    private String message;
    private ArrayList<Docente> docenti;
    private ArrayList<Corso> corsi;
    private ArrayList<Prenotazione> prenotazioniEffettuate;
    private ArrayList<Prenotazione> prenotazioniPossibili;



    public void init() {
        DAO.registerDriver();
        docenti = Docente.getDocenti();
        corsi = Corso.getCorsi();
        //prenotazioniEffettuate = Prenotazione.getPrenotazioniEffettuate();
        //prenotazioniPossibili = Prenotazione.getPrenotazioniPossibili();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        //Rimuovo dalle prenotazioni possibili quelle già effettuate
        /*
        for (int i = 0; i < prenotazioniPossibili.size(); i++) {
            Prenotazione prenotazione = prenotazioniPossibili.get(i);
            for (int j = 0; j < prenotazioniEffettuate.size(); j++) {
                Prenotazione pr = prenotazioniEffettuate.get(j);
                if(pr.myEquals(prenotazione)){
                    prenotazioniPossibili.remove(i);
                    break;
                }
            }
        }

        String corsoPrec = "";
        String giornoPrec = "";
        for (Prenotazione pr : prenotazioniPossibili) {
            String corso = pr.getCorso().getNome();
            String giorno = pr.getGiorno();

            out.print("<p>");

            if(!corso.equals(corsoPrec)){
                out.print("<h1>" + corso + "</h1>");
                corsoPrec = corso;
            }
            if(!giorno.equals(giornoPrec)){
                out.print("<br>");
                giornoPrec = giorno;
            }

            out.println("&emsp;" + pr.getDocente().getNome() + " " + pr.getDocente().getCognome() + " " + pr.getGiorno() + " " + pr.getOra() + "</p>");
        }

        out.println("</body></html>");
        */

    }

    public void destroy() {
    }
}