package com.epf.rentmanager.ui.servlet;

import com.epf.rentmanager.exception.ServiceException;
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

@WebServlet("/cars/delete")
public class VehicleDeleteServlet extends HttpServlet {
    @Autowired
    VehicleService vehicleService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String startParam = req.getParameter("start");
        String endParam = req.getParameter("end");

        LocalDate start = LocalDate.parse(startParam);
        LocalDate end = LocalDate.parse(endParam);

        int vehicleId = Integer.parseInt(req.getParameter("id"));

        try {
            vehicleService.delete(vehicleService.findById(vehicleId), start, end);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/cars");
    }

}
