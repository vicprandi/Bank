package BankApplication.requests;

import BankApplication.model.Client;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ClientRequest {
    @NotNull(message = "{validation.field_required")
    private String name;
    @NotNull(message = "{validation.field_required")
    private String CPF;

    @NotNull(message = "{validation.field_required")
    private String postalCode;

    @NotNull(message = "{validation.field_required")
    private String street;

    @NotNull(message = "{validation.field_required")
    private String state;

    @NotNull(message = "{validation.field_required")
    private String city;

    @NotNull(message = "{validation.field_required")
    private LocalDate createdData;

    public ClientRequest(){
    }

    public ClientRequest(String name, String CPF, String postalCode, String street, String state, String city) {
        this.name = name;
        this.CPF = CPF;
        this.postalCode = postalCode;
        this.street = street;
        this.state = state;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCpf(String CPF) {
        this.CPF = CPF;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getCreatedData() {
        return createdData;
    }

    public void setCreatedData(LocalDate createdData) {
        this.createdData = createdData;
    }

    public Client clientObjectRequest() {
        Client client = new Client();
        client.setName(this.name);
        client.setCpf(this.CPF);
        client.setPostalCode(this.postalCode);
        client.setStreet(this.street);
        client.setCity(this.city);
        client.setCreatedData(LocalDate.now());
        return client;
    }



}
