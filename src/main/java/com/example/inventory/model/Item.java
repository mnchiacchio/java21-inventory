package com.example.inventory.model;

/**
 * Clase de dominio que representa un producto en el inventario.
 * Implementa el patrón Model del patrón MVC (Model-View-Controller).
 * 
 * Esta entidad encapsula los datos de un item del inventario y proporciona
 * acceso controlado a través de getters y setters.
 */
public class Item {
    
    private Long id;
    private String name;
    private int quantity;
    
    // Constructor por defecto requerido para Spring
    public Item() {}
    
    // Constructor con parámetros para facilitar la creación de instancias
    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
    
    // Constructor completo
    public Item(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }
    
    // Getters y Setters
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
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
