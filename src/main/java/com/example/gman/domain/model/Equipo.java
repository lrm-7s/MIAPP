package com.example.gman.domain.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipo {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty capacidad = new SimpleStringProperty();
    private final StringProperty marca = new SimpleStringProperty();
    private final StringProperty modelo = new SimpleStringProperty();
    private final StringProperty serie = new SimpleStringProperty();
    private final StringProperty area = new SimpleStringProperty();
    private final StringProperty planta = new SimpleStringProperty();
    private final StringProperty centroCostos = new SimpleStringProperty();
    private final StringProperty criticidad = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty();

    public Equipo() {}

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String codigo) { this.codigo.set(codigo); }
    public StringProperty codigoProperty() { return codigo; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getCapacidad() { return capacidad.get(); }
    public void setCapacidad(String capacidad) { this.capacidad.set(capacidad); }
    public StringProperty capacidadProperty() { return capacidad; }

    public String getMarca() { return marca.get(); }
    public void setMarca(String marca) { this.marca.set(marca); }
    public StringProperty marcaProperty() { return marca; }

    public String getModelo() { return modelo.get(); }
    public void setModelo(String modelo) { this.modelo.set(modelo); }
    public StringProperty modeloProperty() { return modelo; }

    public String getSerie() { return serie.get(); }
    public void setSerie(String serie) { this.serie.set(serie); }
    public StringProperty serieProperty() { return serie; }

    public String getArea() { return area.get(); }
    public void setArea(String area) { this.area.set(area); }
    public StringProperty areaProperty() { return area; }

    public String getPlanta() { return planta.get(); }
    public void setPlanta(String planta) { this.planta.set(planta); }
    public StringProperty plantaProperty() { return planta; }

    public String getCentroCostos() { return centroCostos.get(); }
    public void setCentroCostos(String centro) { this.centroCostos.set(centro); }
    public StringProperty centroCostosProperty() { return centroCostos; }

    public String getCriticidad() { return criticidad.get(); }
    public void setCriticidad(String criticidad) { this.criticidad.set(criticidad); }
    public StringProperty criticidadProperty() { return criticidad; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }

    @Override
    public String toString() {
        return nombre.get() + " (" + codigo.get() + ")";
    }


}