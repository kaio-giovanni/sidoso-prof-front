package com.sidoso.profissional.model;

public class Consulta {
    private int id;
    private String title;
    private String date;
    private Profissional profissional;
    private Paciente paciente;
    private String obs;
    private String status;
    private Double lat;
    private Double lng;

    public Consulta(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getStatus() {
        switch (status){
            case "001":
                return "MARCADA";
            case "002":
                return "CONCLUIDA";
            case "003":
                return "CANCELADA";
            default:
                return "NDA";
        }
    }

    public void setStatus(String status) {
        if(status.contains("[") && status.contains("]")){
            this.status = status.replace("[","")
                    .replace("]","")
                    .replace("\"", "");
        } else {
            this.status = status;
        }
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
