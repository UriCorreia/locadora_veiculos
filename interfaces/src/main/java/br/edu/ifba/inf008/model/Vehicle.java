package br.edu.ifba.inf008.model;

public class Vehicle {

    public enum VehicleType { ECONOMY, COMPACT, SUV, LUXURY, VAN, ELECTRIC }
    public enum VehicleStatus { AVAILABLE, RENTED, MAINTENANCE, CLEANING, RESERVED }
    public enum FuelType { GASOLINE, DIESEL, ELECTRIC, HYBRID, CNG }
    public enum Transmission { MANUAL, AUTOMATIC }

    private String id; // Placa
    private String make;
    private String model;
    private int year;
    private int odometer;
    private VehicleType type;
    private VehicleStatus status;
    private FuelType fuelType;
    private Transmission transmission;

    public Vehicle() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getOdometer() { return odometer; }
    public void setOdometer(int odometer) { this.odometer = odometer; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public Transmission getTransmission() { return transmission; }
    public void setTransmission(Transmission transmission) { this.transmission = transmission; }

    @Override
    public String toString() {
        return make + " " + model + " (" + id + ")";
    }
}
