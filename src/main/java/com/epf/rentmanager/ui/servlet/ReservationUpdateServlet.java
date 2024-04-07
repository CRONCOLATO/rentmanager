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

@WebServlet("/rents/update")
public class ReservationUpdateServlet extends HttpServlet {
    @Autowired
    ClientService clientService;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    ReservationService reservationService;
    private int reservation_id;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.reservation_id = Integer.parseInt(request.getParameter("id"));
        try{
            request.setAttribute("reservation",reservationService.findById(reservation_id));
            request.setAttribute("vehicles",vehicleService.findAll());
            request.setAttribute("clients",clientService.findAll());
        }catch (ServiceException e){
            e.printStackTrace();
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/rents/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Vehicle vehicle = null;
        Client client = null;
        try {
            String vehicleIdParam = request.getParameter("vehicle");
            String clientIdParam = request.getParameter("client");

            if (vehicleIdParam != null && !vehicleIdParam.isEmpty()) {
                int vehicleId = Integer.parseInt(vehicleIdParam);
                vehicle = vehicleService.findById(vehicleId);
            }

            if (clientIdParam != null && !clientIdParam.isEmpty()) {
                int clientId = Integer.parseInt(clientIdParam);
                client = clientService.findById(clientId);
            }
        } catch (NumberFormatException | ServiceException e) {
            e.printStackTrace();
        }

        if (vehicle != null && client != null) {
            try {
                Reservation updatedReservation = new Reservation(reservation_id, client, vehicle,
                        LocalDate.parse(request.getParameter("debut")),
                        LocalDate.parse(request.getParameter("fin")));
                reservationService.update(updatedReservation);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Failed to update reservation. Vehicle or client not found.");
        }

        response.sendRedirect("/rentmanager/rents");
    }

}
