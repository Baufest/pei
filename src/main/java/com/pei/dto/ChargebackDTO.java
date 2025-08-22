package com.pei.dto;

public class ChargebackDTO {
    private String fechaCreacion;
    private int monto;
    private boolean aceptado;
    private String fechaPago;

    public ChargebackDTO() {}
    public String getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public int getMonto() {
        return monto;
    }
    public void setMonto(int monto) {
        this.monto = monto;
    }
    public boolean isAceptado() {
        return aceptado;
    }
    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }
    public String getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    
}
    