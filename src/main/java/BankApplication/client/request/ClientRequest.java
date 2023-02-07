package BankApplication.client.request;

import BankApplication.model.Client;
import jakarta.validation.constraints.NotNull;

public class ClientRequest {
    @NotNull(message = "{validation.field_required")
    private String name;
    @NotNull(message = "{validation.field_required")
    private String cpf;

    @NotNull(message = "{validation.field_required")
    private String postalCode;

    @NotNull(message = "{validation.field_required")
    private String street;

    @NotNull(message = "{validation.field_required")
    private String state;

    @NotNull(message = "{validation.field_required")
    private String city;

    public ClientRequest(){
    }

    public ClientRequest(String name, String cpf, String postalCode, String street, String state, String city) {
        this.name = name;
        this.cpf = cpf;
        this.postalCode = postalCode;
        this.street = street;
        this.state = state;
        this.city = city;
    }
    public Client clientObjectRequest() {
        Client client = new Client();
        client.setName(this.name);
        client.setCpf(this.cpf);
        client.setPostalCode(this.postalCode);
        client.setState(this.state);
        client.setStreet(this.street);
        client.setCity(this.city);
        return client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
}
