package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Servicio que implementa el patrón Service Layer Pattern.
 * 
 * Esta clase centraliza toda la lógica de negocio relacionada con el inventario
 * y actúa como intermediario entre el controlador y el repositorio.
 * Utiliza Virtual Threads de Java 21 para manejo eficiente de concurrencia.
 */
@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    // ExecutorService con Virtual Threads para operaciones concurrentes
    // Los Virtual Threads son más eficientes para operaciones I/O intensivas
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    
    /**
     * Constructor que implementa Dependency Injection (IoC) de Spring.
     * Spring inyecta automáticamente la dependencia del repositorio.
     * 
     * @param inventoryRepository Repositorio de inventario a inyectar
     */
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * Obtiene todos los items del inventario.
     * 
     * @return Lista de todos los items
     */
    public List<Item> getAllItems() {
        return inventoryRepository.findAll();
    }
    
    /**
     * Busca un item por su ID.
     * 
     * @param id ID del item a buscar
     * @return Item encontrado o null si no existe
     */
    public Item getItemById(Long id) {
        return inventoryRepository.findById(id);
    }
    
    /**
     * Crea un nuevo item en el inventario.
     * 
     * @param item Item a crear
     * @return Item creado con ID asignado
     */
    public Item createItem(Item item) {
        return inventoryRepository.save(item);
    }
    
    /**
     * Actualiza un item existente.
     * 
     * @param id ID del item a actualizar
     * @param item Datos actualizados del item
     * @return Item actualizado o null si no existe
     */
    public Item updateItem(Long id, Item item) {
        return inventoryRepository.update(id, item);
    }
    
    /**
     * Elimina un item del inventario.
     * 
     * @param id ID del item a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean deleteItem(Long id) {
        return inventoryRepository.delete(id);
    }
    
    /**
     * Ejecuta un test de estrés con múltiples operaciones concurrentes.
     * 
     * Este método demuestra el uso de Virtual Threads para manejar
     * grandes volúmenes de operaciones concurrentes de forma eficiente.
     * 
     * @param requests Número de operaciones concurrentes a ejecutar
     * @return CompletableFuture que se completa cuando terminan todas las operaciones
     */
    public CompletableFuture<String> stressTest(int requests) {
        // Crear un array de CompletableFuture para todas las operaciones
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[requests];
        
        // Lanzar múltiples operaciones concurrentes usando Virtual Threads
        for (int i = 0; i < requests; i++) {
            final int requestNumber = i;
            
            // Cada operación se ejecuta en un Virtual Thread separado
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    // Simular diferentes tipos de operaciones
                    if (requestNumber % 4 == 0) {
                        // Crear un nuevo item
                        Item newItem = new Item("StressTestItem_" + requestNumber, requestNumber * 10);
                        inventoryRepository.save(newItem);
                    } else if (requestNumber % 4 == 1) {
                        // Leer todos los items
                        inventoryRepository.findAll();
                    } else if (requestNumber % 4 == 2) {
                        // Buscar un item específico
                        inventoryRepository.findById((long) (requestNumber % 100));
                    } else {
                        // Obtener el conteo de items
                        inventoryRepository.count();
                    }
                    
                    // Simular un pequeño delay para simular operaciones I/O
                    Thread.sleep(10);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, virtualThreadExecutor);
        }
        
        // Esperar a que todas las operaciones terminen
        return CompletableFuture.allOf(futures)
                .thenApply(v -> {
                    long totalItems = inventoryRepository.count();
                    return String.format("Stress test completado: %d operaciones ejecutadas concurrentemente. " +
                            "Total de items en inventario: %d", requests, totalItems);
                });
    }
    
    /**
     * Cierra el ExecutorService de Virtual Threads.
     * Este método debería llamarse al cerrar la aplicación.
     */
    public void shutdown() {
        virtualThreadExecutor.shutdown();
        try {
            if (!virtualThreadExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                virtualThreadExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            virtualThreadExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
