package br.edu.ifba.inf008.model;

public class Customer {

    private String cpf;
    private String name;
    private String email;
    private String phone;

    public Customer() {}

    public Customer(String cpf, String name, String email, String phone) {
        this.cpf = cpf;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return email; // Retorna o email para aparecer no ComboBox
    }
}
