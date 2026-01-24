package br.edu.ifba.inf008.interfaces;

import java.math.BigDecimal;
import java.util.Map;

public interface IVehiclePlugin {

    public String getVehicleType(); //Retorna o nome do tipo do carro e, consequentemente o plugin que será ativado
    public BigDecimal calculateTotal(int days, BigDecimal baseRate, Map<String, Double> fees); //Calcular a conta final do aluguel
}