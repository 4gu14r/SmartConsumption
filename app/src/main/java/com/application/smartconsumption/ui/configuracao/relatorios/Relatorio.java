package com.application.smartconsumption.ui.configuracao.relatorios;

public class Relatorio {
    private String Id, Marca, Modelo;

    public Relatorio(String id, String marca, String modelo) {
        Id = id;
        Marca = marca;
        Modelo = modelo;
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
}
