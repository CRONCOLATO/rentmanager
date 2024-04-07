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

@WebServlet("/cars/update")
public class VehicleUpdateServlet extends HttpServlet {
    @Autowired
    VehicleService vehicleService;
    private int vehicle_id;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.vehicle_id = Integer.parseInt(request.getParameter("id"));
        try {
            request.setAttribute("vehicle", this.vehicleService.findById(vehicle_id));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/vehicles/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Vehicle newVehicle = new Vehicle(vehicle_id, req.getParameter("constructeur"),req.getParameter("modele"), Integer.parseInt(req.getParameter("nb_places")));
        try {
            vehicleService.update(newVehicle);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/cars");
    }
}
