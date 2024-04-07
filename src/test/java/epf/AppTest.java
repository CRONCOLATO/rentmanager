package epf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.dao.ReservationDao;
import com.epf.rentmanager.dao.VehicleDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Period;

/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppTest {
    @Mock
    private ClientDao clientDao;

    @Mock
    private VehicleDao vehicleDao;

    @Mock
    private ReservationDao reservationDao;

    @InjectMocks
    private ClientService clientService;

    @InjectMocks
    private VehicleService vehicleService;

    @InjectMocks
    private ReservationService reservationService;

    @Before
    public void setUp() {
    }

    @Test
    public void testAddClient() throws ServiceException, DaoException {
        LocalDate dateActuelle = LocalDate.now();
        LocalDate dateIlYa20Ans = dateActuelle.minus(Period.ofYears(20));
        Client client = new Client((int)1L, "Roncolato", "Clement", "roncolato.clement@email.com", dateIlYa20Ans);
        Mockito.when(clientDao.create(client)).thenReturn(1L);
        clientService.create(client);

        assertEquals(1L, client.getId());
    }

    @Test
    public void testCreateVehicle() throws ServiceException, DaoException {
        Vehicle vehicle = new Vehicle((int) 1L, "Tesla", "Model S", (short) 4);
        Mockito.lenient().when(vehicleDao.create(vehicle)).thenReturn(1L);
        vehicleService.update(vehicle);
        assertEquals(1L, vehicle.getId());
    }


    @Test
    public void testDeleteReservation() throws ServiceException, DaoException {
        Reservation reservation = reservationService.findById(1);
        Mockito.when(reservationDao.delete(reservation)).thenReturn(1L);
        reservationService.delete(reservation);
        Mockito.verify(reservationDao).delete(reservation);
    }
}
