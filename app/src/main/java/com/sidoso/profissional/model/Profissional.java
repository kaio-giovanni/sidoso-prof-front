package com.sidoso.profissional.model;

import java.util.ArrayList;
import java.util.List;

public class Profissional {
    private int id;
    private String name;
    private String photoUrl;
    private String birth;
    private String cpf;
    private String genre;
    private String phoneMain;
    private String phoneSecondary;
    private String email;
    private Profissao profissao;
    private List<Especialidade> especialidades;

    public Profissional(int id, String name, String photoUrl, String birth, String cpf, String genre, String phoneMain, String email, Profissao profissao) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.birth = birth;
        this.cpf = cpf;
        this.genre = genre;
        this.phoneMain = phoneMain;
        this.email = email;
        this.profissao = profissao;
        this.especialidades  = new ArrayList<>();
    }

    public Profissional(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPhoneMain() {
        return phoneMain;
    }

    public void setPhoneMain(String phoneMain) {
        this.phoneMain = phoneMain;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void addEspecialidade(Especialidade especialidade) {
        this.especialidades.add(especialidade);
    }

    public String toString(){
        return "Nome: " + this.name + "\nProfissão: " + this.profissao.getName();
    }
}
