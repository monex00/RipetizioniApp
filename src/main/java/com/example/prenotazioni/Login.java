package com.example.prenotazioni;


import dao.DAO;
import dao.Utente;
import utils.Message;
import utils.RedirectMessage;
import utils.SimpleMessage;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Login", value = "/login")
public class Login extends HttpServlet {
    public void init() {
        DAO.registerDriver();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        out.print("{\"status\":\"ko\", \"message\":\"GET non consentito\"}");
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String guest = request.getParameter("guest");

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        if(guest != null && guest.equals("true")) {
            HttpSession session = request.getSession();
            session.invalidate();
            Message msg = new RedirectMessage("ok", "logging as guest", "homepage.html");
            out.print(msg.toJson());
            out.flush();
            out.close();
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("Provando login " + email + password);


        if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Message msg = new SimpleMessage("ko", "Parametri mancanti");
            out.print(msg.toJson());
            out.flush();
            out.close();
            return;
        }

        if(!email.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$")) {
            Message msg = new SimpleMessage("ko", "Email non valida");
            out.print(msg.toJson());
            out.flush();
            out.close();
            return;
        }

        Utente utente = DAO.login(email, password);

        if(utente == null) {
            Message msg = new SimpleMessage("ko", "Email o password errati");
            out.print(msg.toJson());
            out.flush();
        }else{
            HttpSession session = request.getSession();
            session.setAttribute("id", utente.getId());

            Message msg = new RedirectMessage("ok", "redirecting...", "homepage.html");
            out.print(msg.toJson());
            out.flush();
        }
    }
}
