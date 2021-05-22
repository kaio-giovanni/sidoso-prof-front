package com.sidoso.profissional.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Paciente {

    private int id;
    private String token_api;
    private String photoUrl;
    private String name;
    private String dt_birth;
    private String cpf;
    private String genre;
    private String phoneMain;
    private String phoneSecondary;
    private String email;
    private String password;

    public Paciente(int id, String photo, String name, String genre, String email, String password, String cpf, String dt_birth, String phoneMain) {
        this.id = id;
        this.photoUrl = photo;
        this.name = name;
        this.genre = genre;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.dt_birth = dt_birth;
        this.phoneMain = phoneMain;
    }

    public Paciente(){}

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getImage(){
        return this.photoUrl;
    }

    public void setImage(String img){
        this.photoUrl = img;
    }

    public void setToken_api(String token) {
        this.token_api = token;
    }

    public String getToken_api() {
        return token_api;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre(){
        if(this.genre == "F"){
            return "Feminino";
        } else {
            return "Masculino";
        }
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDt_birth() {
        return dt_birth;
    }

    public void setDt_birth(String dt_birth) {
        this.dt_birth = dt_birth;
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

    public int getIdade(){
        DateFormat df = new SimpleDateFormat("YYYY-mm-dd");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        df.setLenient(false);

        try {
            int userYear = df.parse(this.dt_birth).getYear();
            return userYear - currentYear;
        }catch(ParseException e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public String toString(){
        String str = "Nome: "+ this.name + " \n";
        str += "Sexo: " + getGenre() + "\n";
        str += "Data de nascimento: " + this.dt_birth + " \n";
        str += "Telefone I: " + this.phoneMain + " \n";
        str += "Email: " + this.email;
        return str;
    }

}
