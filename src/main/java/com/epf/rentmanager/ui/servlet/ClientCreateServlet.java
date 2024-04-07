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


@WebServlet("/users/create")
public class ClientCreateServlet extends HttpServlet {
    @Autowired
    ClientService clientService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String naissanceParam = req.getParameter("naissance");
        LocalDate naissance = null;
        if (naissanceParam != null && !naissanceParam.isEmpty()) {
            naissance = LocalDate.parse(naissanceParam);
        }

        Client newClient = new Client(0, req.getParameter("nom"), req.getParameter("prenom"), req.getParameter("email"), naissance);
        try {
            clientService.create(newClient);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
