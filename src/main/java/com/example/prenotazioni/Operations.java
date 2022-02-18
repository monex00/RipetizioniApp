package com.example.prenotazioni;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "operations", value = "/operations")
public class Operations extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String entity = request.getParameter("entity");

        if(entity == null || entity.equals("")) {
            return;
        }

        if (entity.equals("teacher")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/teacherOperations");
            dispatcher.forward(request, response);
        }else if(entity.equals("courses")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/courseOperations");
            dispatcher.forward(request, response);
        }else if(entity.equals("teaching")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/teachingOperations");
            dispatcher.forward(request, response);
        }else if(entity.equals("reservation")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/reservationOperations");
            dispatcher.forward(request, response);
        }
    }
}
