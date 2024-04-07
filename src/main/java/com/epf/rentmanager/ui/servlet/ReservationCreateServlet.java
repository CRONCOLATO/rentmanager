package com.epf.rentmanager.ui.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;



@WebServlet("/rents/create")
public class ReservationCreateServlet extends HttpServlet {

    @Autowired
    ClientService clientService;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("vehicles", vehicleService.findAll());
            request.setAttribute("clients", clientService.findAll());
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/rents/create.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Vehicle vehicle = null;
        Client client = null;
        try {
            String vehicleIdParam = req.getParameter("vehicle_id");
            String clientIdParam = req.getParameter("client_id");
            String startDateParam = req.getParameter("start");
            String endDateParam = req.getParameter("end");

            if (vehicleIdParam != null && clientIdParam != null && startDateParam != null && endDateParam != null) {
                int vehicleId = Integer.parseInt(vehicleIdParam);
                int clientId = Integer.parseInt(clientIdParam);
                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);

                vehicle = vehicleService.findById(vehicleId);
                client = clientService.findById(clientId);

                Reservation newReservation = new Reservation(0, client, vehicle, startDate, endDate);
                reservationService.Create(newReservation);
                resp.sendRedirect(req.getContextPath() + "/rents");
                ;
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Un ou plusieurs paramètres manquants lors de la création de la réservation");
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur de conversion lors de la création de la réservation");
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }

    }
}