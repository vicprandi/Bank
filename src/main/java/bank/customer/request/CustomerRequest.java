package bank.customer.request;

import bank.model.Customer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CustomerRequest {
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

    public CustomerRequest(){
    }

    public CustomerRequest(String name, String cpf, String postalCode, String street, String state, String city) {
        this.name = name;
        this.cpf = cpf;
        this.postalCode = postalCode;
        this.street = street;
        this.state = state;
        this.city = city;
    }
    public Customer customerObjectRequest() {
        Customer customer = new Customer();
        customer.setName(this.name);
        customer.setCpf(this.cpf);
        customer.setPostalCode(this.postalCode);
        customer.setState(this.state);
        customer.setStreet(this.street);
        customer.setCity(this.city);
        return customer;
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
