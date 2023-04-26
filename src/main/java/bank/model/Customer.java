package bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "cpf", length = 11, nullable = false)
    @Pattern(regexp = "^[0-9]{11}$", message = "Cpf deve conter somente números e ser exatamente 11.")
    private String cpf;

    @Column(name = "postal_code", length = 50, nullable = false)
    @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve conter somente numeros e ser de tamanho exato de 8")
    private String postalCode;

    @Column(name = "street", length = 100, nullable = false)
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
    private String street;

    @Column(name = "state", length = 50, nullable = false)
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
    private String state;

    @Column(name = "city", length = 50, nullable = false)
    @Pattern(regexp = "^[\\p{L}\\-]+(?: [\\p{L}\\-]+)*$", message = "Deve conter somente letras, caracteres especiais e o caractere '-'")
    private String city;

    @Column(name = "created_data")
    private LocalDate createdData = LocalDate.now();

    @OneToOne(mappedBy = "customer")
    @JsonIgnore
    private Account account;

}


