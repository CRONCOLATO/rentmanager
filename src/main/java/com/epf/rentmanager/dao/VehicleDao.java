package com.epf.rentmanager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
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

	private static final String CREATE_VEHICLE_QUERY = "INSERT INTO Vehicle(constructeur, modele, nb_places) VALUES(?, ?, ?);";
	private static final String DELETE_VEHICLE_QUERY = "DELETE FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLE_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLES_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle;";
	private static final String UPDATE_VEHICLE_QUERY = "UPDATE Vehicle SET constructeur = ?, modele = ?, nb_places = ? WHERE id = ?";
	private  static final String COUNT_VEHICLES_QUERY = "SELECT COUNT(*) as count FROM Vehicle";

	public long create(Vehicle vehicle) throws DaoException {
		try (Connection connection = ConnectionManager.getConnection();
			 PreparedStatement statement = connection.prepareStatement(CREATE_VEHICLE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, vehicle.getConstructeur());
			statement.setString(2, vehicle.getModele());
			statement.setInt(3, vehicle.getNb_places());
			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				throw new DaoException("La création du véhicule a échoué, aucun ID généré.");
			}
		} catch (SQLException e) {
			throw new DaoException("Erreur lors de la création du véhicule", e);
		}
	}

	public long delete(Vehicle vehicle) throws DaoException {
		try (Connection connection = ConnectionManager.getConnection();
			 PreparedStatement statement = connection.prepareStatement(DELETE_VEHICLE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
			statement.setLong(1, vehicle.getId());
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException("Erreur lors de la suppression du véhicule", e);
		}
	}

	public Vehicle findById(int id) throws DaoException {
		try (Connection connection = ConnectionManager.getConnection();
			 PreparedStatement statement = connection.prepareStatement(FIND_VEHICLE_QUERY)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String constructeur = resultSet.getString("constructeur");
				String modele = resultSet.getString("modele");
				int nb_places = resultSet.getInt("nb_places");
				return new Vehicle(id, constructeur, modele, nb_places);
			} else {
				throw new DaoException("Aucun véhicule trouvé avec l'ID spécifié.");
			}
		} catch (SQLException e) {
			throw new DaoException("Erreur lors de la recherche du véhicule par ID", e);
		}
	}

	public List<Vehicle> findAll() throws DaoException {
		List<Vehicle> vehicles = new ArrayList<>();
		try {
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(FIND_VEHICLES_QUERY);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String constructeur = resultSet.getString("constructeur");
				String modele = resultSet.getString("modele");
				int nb_places = resultSet.getInt("nb_places");
				vehicles.add(new Vehicle(id, constructeur, modele, nb_places));
			}
		} catch (SQLException e) {
			System.out.println("Erreur SQL lors de la récupération des véhicules : " + e.getMessage());
			e.printStackTrace();
			throw new DaoException("Erreur lors de la récupération de la liste de véhicules", e);
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
			statement.setInt(4, vehicle.getId());
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated == 0) {
				throw new DaoException("Vehicle non trouvé");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erreur lors de l'update");
		}
	}


	public int getCount() throws DaoException{
		try{
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(COUNT_VEHICLES_QUERY);
			resultSet.next();
			return resultSet.getInt("count");
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException();
		}
	}
}
