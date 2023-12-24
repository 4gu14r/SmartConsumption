package com.application.smartconsumption.ui.configuracao.veiculos;

public class Veiculos {

    private String Id, Marca, Modelo, Motor, Hodometro, Consumo, Tanque;

    public Veiculos(String id, String marca, String modelo, String motor, String hodometro, String consumo, String tanque) {
        Id = id;
        Marca = marca;
        Modelo = modelo;
        Motor = motor;
        Hodometro = hodometro;
        Consumo = consumo;
        Tanque = tanque;
    }

    public String getId() {
        return Id;
    }
    public String getMarca() {
        return Marca;
    }
    public String getModelo() {
        return Modelo;
    }
    public String getMotor(){
        return Motor;
    }
    public String getHodometro() {
        return Hodometro;
    }
    public String getConsumo() {
        return Consumo;
    }
    public String getTanque() {
        return Tanque;
    }
}
