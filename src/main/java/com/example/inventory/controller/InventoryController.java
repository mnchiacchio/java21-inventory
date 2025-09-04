package com.example.inventory.controller;

import com.example.inventory.model.Item;
import com.example.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST que implementa el patrón MVC (Model-View-Controller).
 * 
 * Esta clase maneja las peticiones HTTP y expone los endpoints de la API.
 * Actúa como la capa de presentación, delegando la lógica de negocio al servicio
 * y devolviendo respuestas HTTP apropiadas.
 */
@RestController
@RequestMapping("/api/items")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    /**
     * Constructor que implementa Dependency Injection (IoC) de Spring.
     * Spring inyecta automáticamente la dependencia del servicio.
     * 
     * @param inventoryService Servicio de inventario a inyectar
     */
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * Endpoint GET para obtener todos los items del inventario.
     * 
     * @return Lista de todos los items con código HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    /**
     * Endpoint GET para obtener un item específico por su ID.
     * 
     * @param id ID del item a buscar
     * @return Item encontrado con código HTTP 200, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Item item = inventoryService.getItemById(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Endpoint POST para crear un nuevo item en el inventario.
     * 
     * @param item Item a crear (sin ID, se genera automáticamente)
     * @return Item creado con código HTTP 201
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item createdItem = inventoryService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
    
    /**
     * Endpoint PUT para actualizar un item existente.
     * 
     * @param id ID del item a actualizar
     * @param item Datos actualizados del item
     * @return Item actualizado con código HTTP 200, o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updatedItem = inventoryService.updateItem(id, item);
        if (updatedItem != null) {
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Endpoint DELETE para eliminar un item del inventario.
     * 
     * @param id ID del item a eliminar
     * @return Código HTTP 204 si se eliminó correctamente, o 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        boolean deleted = inventoryService.deleteItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Endpoint POST para ejecutar un test de estrés con operaciones concurrentes.
     * 
     * Este endpoint demuestra el uso de Virtual Threads para manejar
     * grandes volúmenes de operaciones concurrentes de forma eficiente.
     * 
     * @param requests Número de operaciones concurrentes a ejecutar
     * @return CompletableFuture con el resultado del test de estrés
     */
    @PostMapping("/stress/{requests}")
    public CompletableFuture<ResponseEntity<String>> stressTest(@PathVariable int requests) {
        // Validar que el número de requests sea razonable
        if (requests <= 0 || requests > 10000) {
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body("El número de requests debe estar entre 1 y 10000")
            );
        }
        
        // Ejecutar el test de estrés de forma asíncrona usando Virtual Threads
        return inventoryService.stressTest(requests)
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(throwable -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error durante el test de estrés: " + throwable.getMessage());
                });
    }
    
    /**
     * Endpoint GET para obtener información sobre el estado del inventario.
     * 
     * @return Información básica del inventario
     */
    @GetMapping("/info")
    public ResponseEntity<String> getInventoryInfo() {
        List<Item> items = inventoryService.getAllItems();
        return ResponseEntity.ok(String.format(
            "Inventario actual: %d items almacenados. " +
            "Sistema ejecutándose con Virtual Threads de Java 21.", 
            items.size()
        ));
    }
}
