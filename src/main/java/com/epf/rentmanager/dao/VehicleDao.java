package com.epf.rentmanager.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.persistence.ConnectionManager;

public class VehicleDao {
	
	private static VehicleDao instance = null;
	private VehicleDao() {}
	public static VehicleDao getInstance() {
		if(instance == null) {
			instance = new VehicleDao();
		}
		return instance;
	}
	
	private static final String CREATE_VEHICLE_QUERY = "INSERT INTO Vehicle(constructeur, modele, nb_places) VALUES(?, ?);";
	private static final String DELETE_VEHICLE_QUERY = "DELETE FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLE_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLES_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle;";
	private static final String UPDATE_VEHICLE_QUERY = "UPDATE Vehicle SET constructeur = ?, modele = ?, nb_places = ? WHERE id = ?";
	
	public long create(Vehicle vehicle) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(CREATE_VEHICLE_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, vehicle.getConstructeur());
			statement.setString(2, vehicle.getModele());
			statement.setInt(3, vehicle.getNb_places());
			int id = statement.executeUpdate();
			return id;
		}
		catch (SQLException e){
			throw new DaoException("Erreur création vehicule");
		}
	}

	public long delete(Vehicle vehicle) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(DELETE_VEHICLE_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, vehicle.getId());
			int id = statement.executeUpdate();
			return id;

		}
		catch (SQLException e){
			throw new DaoException("Erreur dans la suppréssion du vehicule");
		}
	}

	public Vehicle findById(long id) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_VEHICLE_QUERY);
			statement.setLong(1, id);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			String constructeur = resultSet.getString("constructeur");
			String modele = resultSet.getString("modele");
			short nb_places = resultSet.getShort("nb_places");

			return new Vehicle((int) id, constructeur, modele, nb_places);
		}
		catch (SQLException e){
			throw new DaoException("ID incorrect");
		}
	}

	public List<Vehicle> findAll() throws DaoException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		try {
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet =statement.executeQuery(FIND_VEHICLES_QUERY);
			while (resultSet.next()){
				int id =resultSet.getInt("id");
				String constructeur = resultSet.getString("constructeur");
				String modele = resultSet.getString("modele");
				short nb_places = resultSet.getShort("nb_places");
				vehicles.add(new Vehicle(id, constructeur, modele, nb_places));
			}
		}
		catch (SQLException e){
			throw new DaoException("Erreur lors de la récupération de la liste");
		}
		return vehicles;
	}

	public void update(Vehicle vehicle) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(UPDATE_VEHICLE_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, vehicle.getConstructeur());
			statement.setString(2, vehicle.getModele());
			statement.setInt(3, vehicle.getNb_places());
			statement.setLong(4, vehicle.getId());
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated == 0) {
				throw new DaoException("Vehicle non trouvé");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erreur lors de l'update");
		}
	}

}
