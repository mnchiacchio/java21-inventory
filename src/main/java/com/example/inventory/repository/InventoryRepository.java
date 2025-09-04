package com.example.inventory.repository;

import com.example.inventory.model.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria que implementa el patrón Repository Pattern.
 * 
 * Este repositorio abstrae el acceso a datos proporcionando una interfaz
 * simple para operaciones CRUD sin depender de una base de datos real.
 * Utiliza estructuras de datos thread-safe para soportar concurrencia.
 */
@Repository
public class InventoryRepository {
    
    // ConcurrentHashMap para almacenamiento thread-safe de items
    // Clave: ID del item, Valor: Item completo
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    
    // AtomicLong para generar IDs únicos de forma thread-safe
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Obtiene todos los items del inventario.
     * 
     * @return Lista con todos los items almacenados
     */
    public List<Item> findAll() {
        return items.values().stream()
                .collect(Collectors.toList());
    }
    
    /**
     * Busca un item por su ID.
     * 
     * @param id ID del item a buscar
     * @return Item encontrado o null si no existe
     */
    public Item findById(Long id) {
        return items.get(id);
    }
    
    /**
     * Guarda un nuevo item en el inventario.
     * Genera automáticamente un ID único para el item.
     * 
     * @param item Item a guardar (sin ID)
     * @return Item guardado con ID asignado
     */
    public Item save(Item item) {
        // Genera un nuevo ID único de forma thread-safe
        Long newId = idGenerator.getAndIncrement();
        item.setId(newId);
        
        // Almacena el item en el mapa concurrente
        items.put(newId, item);
        
        return item;
    }
    
    /**
     * Actualiza un item existente en el inventario.
     * 
     * @param id ID del item a actualizar
     * @param item Datos actualizados del item
     * @return Item actualizado o null si no existe
     */
    public Item update(Long id, Item item) {
        if (items.containsKey(id)) {
            item.setId(id);
            items.put(id, item);
            return item;
        }
        return null;
    }
    
    /**
     * Elimina un item del inventario.
     * 
     * @param id ID del item a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean delete(Long id) {
        return items.remove(id) != null;
    }
    
    /**
     * Verifica si existe un item con el ID especificado.
     * 
     * @param id ID a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(Long id) {
        return items.containsKey(id);
    }
    
    /**
     * Obtiene el número total de items en el inventario.
     * 
     * @return Cantidad total de items
     */
    public long count() {
        return items.size();
    }
}
