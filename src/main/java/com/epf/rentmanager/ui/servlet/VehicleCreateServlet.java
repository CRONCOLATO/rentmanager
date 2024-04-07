package com.epf.rentmanager.ui.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cars/create")
public class VehicleCreateServlet extends HttpServlet {

    @Autowired
    VehicleService vehicleService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/vehicles/create.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String nbPlacesParam = req.getParameter("nb_places");
        int nbPlaces = 0;
        if (nbPlacesParam != null) {
            try {
                nbPlaces = Integer.parseInt(nbPlacesParam);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        Vehicle newVehicle = new Vehicle(0, req.getParameter("constructeur"), req.getParameter("modele"), nbPlaces);
        try {
            vehicleService.create(newVehicle);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/cars");
    }

}
