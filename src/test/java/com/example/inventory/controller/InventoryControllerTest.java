package com.example.inventory.controller;

import com.example.inventory.model.Item;
import com.example.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para InventoryController usando @WebMvcTest.
 * 
 * Esta clase prueba todos los endpoints REST del controlador de forma aislada,
 * mockeando el servicio para simular diferentes escenarios de respuesta.
 * Utiliza MockMvc para simular peticiones HTTP sin necesidad de un servidor real.
 */
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test para el endpoint GET /api/items
     * Verifica que retorna una lista de items con código HTTP 200
     */
    @Test
    void testGetAllItems() throws Exception {
        // Arrange: Preparar datos de prueba
        List<Item> items = Arrays.asList(
                new Item(1L, "Laptop Dell", 5),
                new Item(2L, "Mouse Logitech", 10)
        );
        when(inventoryService.getAllItems()).thenReturn(items);

        // Act & Assert: Ejecutar petición y verificar respuesta
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Dell"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Mouse Logitech"))
                .andExpect(jsonPath("$[1].quantity").value(10));
    }

    /**
     * Test para el endpoint GET /api/items/{id} - Caso exitoso
     * Verifica que retorna un item existente con código HTTP 200
     */
    @Test
    void testGetItemById_Success() throws Exception {
        // Arrange: Preparar item de prueba
        Item item = new Item(1L, "Laptop Dell", 5);
        when(inventoryService.getItemById(1L)).thenReturn(item);

        // Act & Assert: Ejecutar petición y verificar respuesta
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Dell"))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    /**
     * Test para el endpoint GET /api/items/{id} - Caso de error
     * Verifica que retorna código HTTP 404 cuando el item no existe
     */
    @Test
    void testGetItemById_NotFound() throws Exception {
        // Arrange: Simular que el item no existe
        when(inventoryService.getItemById(999L)).thenReturn(null);

        // Act & Assert: Ejecutar petición y verificar respuesta 404
        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test para el endpoint POST /api/items
     * Verifica que crea un nuevo item y retorna código HTTP 201
     */
    @Test
    void testCreateItem() throws Exception {
        // Arrange: Preparar datos de entrada y respuesta
        Item inputItem = new Item(null, "Nuevo Item", 15);
        Item savedItem = new Item(1L, "Nuevo Item", 15);
        when(inventoryService.createItem(any(Item.class))).thenReturn(savedItem);

        // Act & Assert: Ejecutar petición POST y verificar respuesta
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nuevo Item"))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    /**
     * Test para el endpoint PUT /api/items/{id} - Caso exitoso
     * Verifica que actualiza un item existente y retorna código HTTP 200
     */
    @Test
    void testUpdateItem_Success() throws Exception {
        // Arrange: Preparar datos de actualización
        Item updatedItem = new Item(1L, "Item Actualizado", 20);
        when(inventoryService.updateItem(anyLong(), any(Item.class))).thenReturn(updatedItem);

        // Act & Assert: Ejecutar petición PUT y verificar respuesta
        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item Actualizado"))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    /**
     * Test para el endpoint PUT /api/items/{id} - Caso de error
     * Verifica que retorna código HTTP 404 cuando el item no existe
     */
    @Test
    void testUpdateItem_NotFound() throws Exception {
        // Arrange: Simular que el item no existe para actualizar
        Item itemToUpdate = new Item(999L, "Item No Existe", 20);
        when(inventoryService.updateItem(anyLong(), any(Item.class))).thenReturn(null);

        // Act & Assert: Ejecutar petición PUT y verificar respuesta 404
        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test para el endpoint DELETE /api/items/{id} - Caso exitoso
     * Verifica que elimina un item existente y retorna código HTTP 204
     */
    @Test
    void testDeleteItem_Success() throws Exception {
        // Arrange: Simular eliminación exitosa
        when(inventoryService.deleteItem(1L)).thenReturn(true);

        // Act & Assert: Ejecutar petición DELETE y verificar respuesta
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test para el endpoint DELETE /api/items/{id} - Caso de error
     * Verifica que retorna código HTTP 404 cuando el item no existe
     */
    @Test
    void testDeleteItem_NotFound() throws Exception {
        // Arrange: Simular que el item no existe para eliminar
        when(inventoryService.deleteItem(999L)).thenReturn(false);

        // Act & Assert: Ejecutar petición DELETE y verificar respuesta 404
        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test para el endpoint POST /api/items/stress/{requests}
     * Verifica que ejecuta el test de estrés y retorna código HTTP 200
     */
    @Test
    void testStressTest() throws Exception {
        // Arrange: Preparar respuesta del test de estrés
        String expectedResult = "Stress test completado: 100 operaciones ejecutadas concurrentemente. Total de items en inventario: 5";
        CompletableFuture<String> future = CompletableFuture.completedFuture(expectedResult);
        when(inventoryService.stressTest(100)).thenReturn(future);

        // Act & Assert: Ejecutar petición POST y verificar respuesta
        mockMvc.perform(post("/api/items/stress/100"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }

    /**
     * Test para el endpoint POST /api/items/stress/{requests} - Caso de error
     * Verifica que retorna código HTTP 400 cuando el número de requests es inválido
     */
    @Test
    void testStressTest_InvalidRequests() throws Exception {
        // Act & Assert: Ejecutar petición con número inválido de requests
        mockMvc.perform(post("/api/items/stress/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El número de requests debe estar entre 1 y 10000"));
    }

    /**
     * Test para el endpoint POST /api/items/stress/{requests} - Límite superior
     * Verifica que retorna código HTTP 400 cuando el número de requests excede el límite
     */
    @Test
    void testStressTest_ExceedsLimit() throws Exception {
        // Act & Assert: Ejecutar petición con número excesivo de requests
        mockMvc.perform(post("/api/items/stress/15000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El número de requests debe estar entre 1 y 10000"));
    }

    /**
     * Test para el endpoint GET /api/items/info
     * Verifica que retorna información del inventario con código HTTP 200
     */
    @Test
    void testGetInventoryInfo() throws Exception {
        // Arrange: Preparar lista de items para simular estado del inventario
        List<Item> items = Arrays.asList(
                new Item(1L, "Item 1", 5),
                new Item(2L, "Item 2", 10)
        );
        when(inventoryService.getAllItems()).thenReturn(items);

        // Act & Assert: Ejecutar petición GET y verificar respuesta
        mockMvc.perform(get("/api/items/info"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventario actual: 2 items almacenados. Sistema ejecutándose con Virtual Threads de Java 21."));
    }
}
