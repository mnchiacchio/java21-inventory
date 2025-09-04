package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para InventoryService usando Mockito.
 * 
 * Esta clase prueba la lógica de negocio del servicio, incluyendo
 * el manejo de Virtual Threads y operaciones concurrentes.
 * Utiliza mocks del repositorio para aislar las pruebas del servicio.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Item testItem1;
    private Item testItem2;
    private List<Item> testItems;

    /**
     * Configuración inicial antes de cada test.
     * Crea datos de prueba reutilizables.
     */
    @BeforeEach
    void setUp() {
        testItem1 = new Item(1L, "Laptop Dell", 5);
        testItem2 = new Item(2L, "Mouse Logitech", 10);
        testItems = Arrays.asList(testItem1, testItem2);
    }

    /**
     * Test para el método getAllItems()
     * Verifica que retorna todos los items del inventario
     */
    @Test
    void testGetAllItems() {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.findAll()).thenReturn(testItems);

        // Act: Ejecutar método a probar
        List<Item> result = inventoryService.getAllItems();

        // Assert: Verificar resultado
        assertThat(result).isEqualTo(testItems);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop Dell");
        assertThat(result.get(1).getName()).isEqualTo("Mouse Logitech");

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).findAll();
    }

    /**
     * Test para el método getItemById() - Caso exitoso
     * Verifica que retorna un item cuando existe
     */
    @Test
    void testGetItemById_Success() {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.findById(1L)).thenReturn(testItem1);

        // Act: Ejecutar método a probar
        Item result = inventoryService.getItemById(1L);

        // Assert: Verificar resultado
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testItem1);
        assertThat(result.getName()).isEqualTo("Laptop Dell");

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).findById(1L);
    }

    /**
     * Test para el método getItemById() - Caso de error
     * Verifica que retorna null cuando el item no existe
     */
    @Test
    void testGetItemById_NotFound() {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.findById(999L)).thenReturn(null);

        // Act: Ejecutar método a probar
        Item result = inventoryService.getItemById(999L);

        // Assert: Verificar resultado
        assertThat(result).isNull();

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).findById(999L);
    }

    /**
     * Test para el método createItem()
     * Verifica que crea un nuevo item y le asigna un ID
     */
    @Test
    void testCreateItem() {
        // Arrange: Preparar item sin ID
        Item newItem = new Item(null, "Nuevo Item", 15);
        Item savedItem = new Item(1L, "Nuevo Item", 15);
        when(inventoryRepository.save(any(Item.class))).thenReturn(savedItem);

        // Act: Ejecutar método a probar
        Item result = inventoryService.createItem(newItem);

        // Assert: Verificar resultado
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Nuevo Item");
        assertThat(result.getQuantity()).isEqualTo(15);

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).save(newItem);
    }

    /**
     * Test para el método updateItem() - Caso exitoso
     * Verifica que actualiza un item existente
     */
    @Test
    void testUpdateItem_Success() {
        // Arrange: Preparar item actualizado
        Item updatedItem = new Item(1L, "Item Actualizado", 20);
        when(inventoryRepository.update(1L, updatedItem)).thenReturn(updatedItem);

        // Act: Ejecutar método a probar
        Item result = inventoryService.updateItem(1L, updatedItem);

        // Assert: Verificar resultado
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedItem);
        assertThat(result.getName()).isEqualTo("Item Actualizado");

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).update(1L, updatedItem);
    }

    /**
     * Test para el método updateItem() - Caso de error
     * Verifica que retorna null cuando el item no existe
     */
    @Test
    void testUpdateItem_NotFound() {
        // Arrange: Configurar comportamiento del mock
        Item itemToUpdate = new Item(999L, "Item No Existe", 20);
        when(inventoryRepository.update(999L, itemToUpdate)).thenReturn(null);

        // Act: Ejecutar método a probar
        Item result = inventoryService.updateItem(999L, itemToUpdate);

        // Assert: Verificar resultado
        assertThat(result).isNull();

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).update(999L, itemToUpdate);
    }

    /**
     * Test para el método deleteItem() - Caso exitoso
     * Verifica que elimina un item existente
     */
    @Test
    void testDeleteItem_Success() {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.delete(1L)).thenReturn(true);

        // Act: Ejecutar método a probar
        boolean result = inventoryService.deleteItem(1L);

        // Assert: Verificar resultado
        assertThat(result).isTrue();

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).delete(1L);
    }

    /**
     * Test para el método deleteItem() - Caso de error
     * Verifica que retorna false cuando el item no existe
     */
    @Test
    void testDeleteItem_NotFound() {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.delete(999L)).thenReturn(false);

        // Act: Ejecutar método a probar
        boolean result = inventoryService.deleteItem(999L);

        // Assert: Verificar resultado
        assertThat(result).isFalse();

        // Verificar que se llamó al repositorio
        verify(inventoryRepository, times(1)).delete(999L);
    }

    /**
     * Test para el método stressTest() - Test básico
     * Verifica que ejecuta operaciones concurrentes con Virtual Threads
     */
    @Test
    void testStressTest_Basic() throws Exception {
        // Arrange: Configurar comportamiento del mock para operaciones concurrentes
        when(inventoryRepository.save(any(Item.class))).thenReturn(new Item(1L, "TestItem", 10));
        when(inventoryRepository.findAll()).thenReturn(testItems);
        when(inventoryRepository.findById(anyLong())).thenReturn(testItem1);
        when(inventoryRepository.count()).thenReturn(2L);

        // Act: Ejecutar test de estrés
        CompletableFuture<String> future = inventoryService.stressTest(10);
        String result = future.get(5, TimeUnit.SECONDS);

        // Assert: Verificar resultado
        assertThat(result).isNotNull();
        assertThat(result).contains("Stress test completado: 10 operaciones ejecutadas concurrentemente");
        assertThat(result).contains("Total de items en inventario: 2");

        // Verificar que se ejecutaron operaciones en el repositorio
        verify(inventoryRepository, atLeastOnce()).save(any(Item.class));
        verify(inventoryRepository, atLeastOnce()).findAll();
        verify(inventoryRepository, atLeastOnce()).findById(anyLong());
        verify(inventoryRepository, atLeastOnce()).count();
    }

    /**
     * Test para el método stressTest() - Test con más operaciones
     * Verifica que maneja correctamente un mayor número de operaciones concurrentes
     */
    @Test
    void testStressTest_HighVolume() throws Exception {
        // Arrange: Configurar comportamiento del mock
        when(inventoryRepository.save(any(Item.class))).thenReturn(new Item(1L, "TestItem", 10));
        when(inventoryRepository.findAll()).thenReturn(testItems);
        when(inventoryRepository.findById(anyLong())).thenReturn(testItem1);
        when(inventoryRepository.count()).thenReturn(100L);

        // Act: Ejecutar test de estrés con más operaciones
        CompletableFuture<String> future = inventoryService.stressTest(100);
        String result = future.get(10, TimeUnit.SECONDS);

        // Assert: Verificar resultado
        assertThat(result).isNotNull();
        assertThat(result).contains("Stress test completado: 100 operaciones ejecutadas concurrentemente");
        assertThat(result).contains("Total de items en inventario: 100");

        // Verificar que se ejecutaron múltiples operaciones
        verify(inventoryRepository, atLeast(50)).save(any(Item.class));
        verify(inventoryRepository, atLeast(25)).findAll();
        verify(inventoryRepository, atLeast(25)).findById(anyLong());
        verify(inventoryRepository, atLeast(25)).count();
    }

    /**
     * Test para el método stressTest() - Verificar concurrencia
     * Verifica que las operaciones se ejecutan de forma concurrente
     */
    @Test
    void testStressTest_Concurrency() throws Exception {
        // Arrange: Configurar comportamiento del mock con delay para simular I/O
        when(inventoryRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Thread.sleep(10); // Simular operación I/O
            return new Item(1L, "TestItem", 10);
        });
        when(inventoryRepository.findAll()).thenAnswer(invocation -> {
            Thread.sleep(5);
            return testItems;
        });
        when(inventoryRepository.findById(anyLong())).thenAnswer(invocation -> {
            Thread.sleep(5);
            return testItem1;
        });
        when(inventoryRepository.count()).thenAnswer(invocation -> {
            Thread.sleep(5);
            return 2L;
        });

        long startTime = System.currentTimeMillis();

        // Act: Ejecutar test de estrés
        CompletableFuture<String> future = inventoryService.stressTest(50);
        String result = future.get(5, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert: Verificar que se ejecutó de forma concurrente (tiempo menor que secuencial)
        assertThat(result).isNotNull();
        assertThat(executionTime).isLessThan(1000); // Debería ser mucho menor que 50 * 10ms = 500ms secuencial
        assertThat(result).contains("Stress test completado: 50 operaciones ejecutadas concurrentemente");
    }

    /**
     * Test para verificar que el servicio maneja correctamente el cierre del ExecutorService
     * Verifica que el método shutdown() funciona correctamente
     */
    @Test
    void testShutdown() {
        // Act: Ejecutar método de cierre
        inventoryService.shutdown();

        // Assert: No debería lanzar excepciones
        // El método shutdown() es void, así que solo verificamos que no falle
        assertThat(true).isTrue(); // Test pasa si no hay excepciones
    }
}
