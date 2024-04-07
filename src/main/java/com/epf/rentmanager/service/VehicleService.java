package com.epf.rentmanager.service;

import java.time.LocalDate;
import java.util.List;

import com.epf.rentmanager.dao.ReservationDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.dao.VehicleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

	private VehicleDao vehicleDao;
	public static VehicleService instance;
	@Autowired
	private ReservationDao reservationDao;

	private static final int NB_place_min = 0;
	private static final int NB_place_max = 100;
	private ReservationService reservationService;

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

	public Vehicle findById(int id) throws ServiceException {
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

	public void delete(Vehicle vehicle, LocalDate start, LocalDate end) throws ServiceException {
		try {
			List<Reservation> reservations = reservationService.findResaByVehicleId(vehicle.getId(), start, end);
			if (!reservations.isEmpty()) {
				throw new ServiceException("Impossible de supprimer ce véhicule : des réservations y sont associées.");
			}
			vehicleDao.delete(vehicle);
		} catch (DaoException e) {
			throw new ServiceException("Une erreur est survenue lors de la suppression du véhicule.", e);
		}
	}




	public void update(Vehicle vehicle) throws ServiceException {
		validerVehicleInfo(vehicle);
		try {
			this.vehicleDao.update(vehicle);
		} catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public int getCount() throws ServiceException {
		try{
			return this.vehicleDao.getCount();
		}
		catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}


}
