package com.epf.rentmanager.ui.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/users/list")
public class ClientListServlet extends HttpServlet {
    @Autowired
    ClientService clientService;
    @Autowired
    ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int compteur;
        try {
            compteur = clientService.getCount();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        int client_id = compteur;
        try {
            req.setAttribute("client", this.clientService.findById(client_id));
            req.setAttribute("reservations", this.reservationService.findResaByClientId(client_id));
            req.setAttribute("clientVehicles", this.reservationService.findVehiclesResaByClient(client_id));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(req, resp);

    }
}
