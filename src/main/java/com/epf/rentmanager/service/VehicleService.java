package com.epf.rentmanager.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.dao.VehicleDao;

public class VehicleService {

	private VehicleDao vehicleDao;
	public static VehicleService instance;

	private static final int NB_place_min = 1;
	private static final int NB_place_max = 9;
	
	private VehicleService() {
		this.vehicleDao = VehicleDao.getInstance();
	}
	
	public static VehicleService getInstance() {
		if (instance == null) {
			instance = new VehicleService();
		}
		
		return instance;
	}


	public void validerVehicleInfo(Vehicle vehicle) throws ServiceException{
		if (vehicle.getConstructeur().isEmpty()) {
			throw new ServiceException("Le constructeur n'est pas correcte");
		}
		if (vehicle.getModele().isEmpty()) {
			throw new ServiceException("Le model n'est pas correct");
		}
		if (vehicle.getNb_places() < NB_place_min || vehicle.getNb_places() > NB_place_max) {
			throw new ServiceException("Nombre de place incorrect");
		}
	}
	
	
	public long create(Vehicle vehicle) throws ServiceException {
		validerVehicleInfo(vehicle);
		try {
			return this.vehicleDao.create(vehicle);
		} catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public Vehicle findById(long id) throws ServiceException {
		try {
			return this.vehicleDao.findById(id);
		}
		catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public List<Vehicle> findAll() throws ServiceException {
		try {
			return this.vehicleDao.findAll();
		}
		catch (DaoException e){
			e.printStackTrace();
			throw new ServiceException();
		}
	}
	
}
