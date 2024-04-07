package com.epf.rentmanager.ui.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/users/update")
public class ClientUpdateServlet extends HttpServlet {
    @Autowired
    ClientService clientService;
    private int client_id;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.client_id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("client", this.clientService.findById(client_id));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Client newClient = new Client(client_id, req.getParameter("nom"),req.getParameter("prenom"),req.getParameter("email"), LocalDate.parse(req.getParameter("naissance")));
        try {
            clientService.update(newClient);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
