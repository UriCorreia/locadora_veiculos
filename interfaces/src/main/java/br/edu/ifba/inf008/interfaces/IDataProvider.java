package br.edu.ifba.inf008.interfaces;

import br.edu.ifba.inf008.model.Customer;
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;

import java.util.List;

public interface IDataProvider {

    // Métodos de Leitura (Buscar dados)
    List<Customer> getAllCustomers();
    List<Vehicle> getAllVehicles();
    List<Vehicle> getVehiclesByType(Vehicle.VehicleType type);
    List<Rental> getActiveRentals();

    // Métodos de Escrita (Salvar/Alterar dados)
    boolean saveRental(Rental rental);
    boolean updateVehicleStatus(String licensePlate, Vehicle.VehicleStatus newStatus);
    boolean updateRentalReturn(int rentalId, java.time.LocalDateTime endDate, java.math.BigDecimal totalValue);
}