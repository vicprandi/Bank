package BankApplication.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "cpf", length = 11, nullable = false)
    private String cpf;

    @Column(name = "postal_code", length = 50, nullable = false)
    private String postalCode;

    @Column(name = "street", length = 100, nullable = false)
    private String street;

    @Column(name = "state", length = 50, nullable = false)
    private String state;

    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @Column(name = "created_data")
    private LocalDate createdData = LocalDate.now();

    @OneToOne(mappedBy = "client")
    private Account account;

    public Client() {
    }

    public Client(Long id, String name, String cpf, String postalCode, String street, String state, String city, LocalDate createdData, Account account) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.postalCode = postalCode;
        this.street = street;
        this.state = state;
        this.city = city;
        this.createdData = createdData;
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getCreatedData() {
        return createdData;
    }

    public void setCreatedData(LocalDate createdData) {
        this.createdData = createdData;
    }
}


