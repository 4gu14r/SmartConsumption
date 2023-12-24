package com.application.smartconsumption.ui.home;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Home {
    private String id, marca, modelo, kmRegistro, kmPecorrido, tanqueRegistro, precosRegistro, combustivel, valor, consumo, litrosAbastecido;
    private long timestamp;

    public Home(String id, String marca, String modelo, String kmRegistro, String kmPecorrido, String tanqueRegistro, String precosRegistro, String combustivel, String valor, String consumo, String litrosAbastecido, long timestamp) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.kmRegistro = kmRegistro;
        this.kmPecorrido = kmPecorrido;
        this.tanqueRegistro = tanqueRegistro;
        this.precosRegistro = precosRegistro;
        this.combustivel = combustivel;
        this.valor = valor;
        this.consumo = consumo;
        this.litrosAbastecido = litrosAbastecido;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getKmRegistro() {
        return kmRegistro;
    }

    public String getKmPecorrido() {
        return kmPecorrido;
    }

    public void setKmRegistro(String kmRegistro) {
        this.kmRegistro = kmRegistro;
    }

    public String getTanqueRegistro() {
        return tanqueRegistro;
    }

    public void setTanqueRegistro(String tanqueRegistro) {
        this.tanqueRegistro = tanqueRegistro;
    }

    public String getPrecosRegistro() {
        return precosRegistro;
    }

    public void setPrecosRegistro(String precosRegistro) {
        this.precosRegistro = precosRegistro;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getConsumo(){
        return consumo;
    }

    public String getLitrosAbastecido() {
        return litrosAbastecido;
    }

    public long getTimestamp() {
        return timestamp*1000;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String dataString(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date(getTimestamp()));

    }

    public String horaString(){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        return df.format(new Date(getTimestamp()));
    }
}
