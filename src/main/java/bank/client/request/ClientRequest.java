package bank.client.request;

import bank.model.Client;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ClientRequest {
    @NotNull(message = "{validation.field_required")
    private String name;
    @NotNull(message = "{validation.field_required")
    @Pattern(regexp = "^[0-9]{11}$", message = "Cpf deve conter somente números e ser exatamente 11.")
    private String cpf;

    @NotNull(message = "{validation.field_required")
    @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve conter somente numeros e ser de tamanho exato de 8")
    private String postalCode;

    @NotNull(message = "{validation.field_required")
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
    private String street;

    @NotNull(message = "{validation.field_required")
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
    private String state;

    @NotNull(message = "{validation.field_required")
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
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
