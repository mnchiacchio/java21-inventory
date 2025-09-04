package com.example.inventory.repository;

import com.example.inventory.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para InventoryRepository usando implementación real.
 * 
 * Esta clase prueba la implementación real del repositorio con ConcurrentHashMap
 * y AtomicLong, incluyendo operaciones concurrentes para verificar thread-safety.
 * No usa mocks ya que necesitamos probar el comportamiento real de las estructuras de datos.
 */
class InventoryRepositoryTest {

    private InventoryRepository inventoryRepository;

    /**
     * Configuración inicial antes de cada test.
     * Crea una nueva instancia del repositorio para cada prueba.
     */
    @BeforeEach
    void setUp() {
        inventoryRepository = new InventoryRepository();
    }

    /**
     * Test para el método save() - Caso básico
     * Verifica que guarda un item y le asigna un ID automáticamente
     */
    @Test
    void testSave_NewItem() {
        // Arrange: Preparar item sin ID
        Item item = new Item(null, "Test Item", 10);

        // Act: Guardar item
        Item savedItem = inventoryRepository.save(item);

        // Assert: Verificar que se asignó ID y se guardó correctamente
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getId()).isEqualTo(1L);
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getQuantity()).isEqualTo(10);

        // Verificar que se puede recuperar
        Item retrievedItem = inventoryRepository.findById(savedItem.getId());
        assertThat(retrievedItem).isEqualTo(savedItem);
    }

    /**
     * Test para el método save() - Múltiples items
     * Verifica que se asignan IDs secuenciales correctamente
     */
    @Test
    void testSave_MultipleItems() {
        // Arrange: Preparar múltiples items
        Item item1 = new Item(null, "Item 1", 5);
        Item item2 = new Item(null, "Item 2", 10);
        Item item3 = new Item(null, "Item 3", 15);

        // Act: Guardar items
        Item savedItem1 = inventoryRepository.save(item1);
        Item savedItem2 = inventoryRepository.save(item2);
        Item savedItem3 = inventoryRepository.save(item3);

        // Assert: Verificar IDs secuenciales
        assertThat(savedItem1.getId()).isEqualTo(1L);
        assertThat(savedItem2.getId()).isEqualTo(2L);
        assertThat(savedItem3.getId()).isEqualTo(3L);

        // Verificar que todos se pueden recuperar
        assertThat(inventoryRepository.findById(1L)).isEqualTo(savedItem1);
        assertThat(inventoryRepository.findById(2L)).isEqualTo(savedItem2);
        assertThat(inventoryRepository.findById(3L)).isEqualTo(savedItem3);
    }

    /**
     * Test para el método findAll()
     * Verifica que retorna todos los items guardados
     */
    @Test
    void testFindAll() {
        // Arrange: Guardar varios items
        Item item1 = inventoryRepository.save(new Item(null, "Item 1", 5));
        Item item2 = inventoryRepository.save(new Item(null, "Item 2", 10));
        Item item3 = inventoryRepository.save(new Item(null, "Item 3", 15));

        // Act: Obtener todos los items
        List<Item> allItems = inventoryRepository.findAll();

        // Assert: Verificar que se retornaron todos los items
        assertThat(allItems).hasSize(3);
        assertThat(allItems).contains(item1, item2, item3);
    }

    /**
     * Test para el método findById() - Caso exitoso
     * Verifica que encuentra un item existente
     */
    @Test
    void testFindById_Success() {
        // Arrange: Guardar un item
        Item savedItem = inventoryRepository.save(new Item(null, "Test Item", 10));

        // Act: Buscar por ID
        Item foundItem = inventoryRepository.findById(savedItem.getId());

        // Assert: Verificar que se encontró el item correcto
        assertThat(foundItem).isNotNull();
        assertThat(foundItem).isEqualTo(savedItem);
        assertThat(foundItem.getName()).isEqualTo("Test Item");
    }

    /**
     * Test para el método findById() - Caso de error
     * Verifica que retorna null cuando el item no existe
     */
    @Test
    void testFindById_NotFound() {
        // Act: Buscar item que no existe
        Item foundItem = inventoryRepository.findById(999L);

        // Assert: Verificar que no se encontró
        assertThat(foundItem).isNull();
    }

    /**
     * Test para el método update() - Caso exitoso
     * Verifica que actualiza un item existente
     */
    @Test
    void testUpdate_Success() {
        // Arrange: Guardar un item y preparar actualización
        Item originalItem = inventoryRepository.save(new Item(null, "Original Item", 10));
        Item updatedItem = new Item(null, "Updated Item", 20);

        // Act: Actualizar item
        Item result = inventoryRepository.update(originalItem.getId(), updatedItem);

        // Assert: Verificar que se actualizó correctamente
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(originalItem.getId());
        assertThat(result.getName()).isEqualTo("Updated Item");
        assertThat(result.getQuantity()).isEqualTo(20);

        // Verificar que se puede recuperar actualizado
        Item retrievedItem = inventoryRepository.findById(originalItem.getId());
        assertThat(retrievedItem).isEqualTo(result);
    }

    /**
     * Test para el método update() - Caso de error
     * Verifica que retorna null cuando el item no existe
     */
    @Test
    void testUpdate_NotFound() {
        // Arrange: Preparar item para actualizar
        Item itemToUpdate = new Item(null, "Non-existent Item", 20);

        // Act: Intentar actualizar item que no existe
        Item result = inventoryRepository.update(999L, itemToUpdate);

        // Assert: Verificar que no se actualizó
        assertThat(result).isNull();
    }

    /**
     * Test para el método delete() - Caso exitoso
     * Verifica que elimina un item existente
     */
    @Test
    void testDelete_Success() {
        // Arrange: Guardar un item
        Item savedItem = inventoryRepository.save(new Item(null, "Item to Delete", 10));

        // Act: Eliminar item
        boolean deleted = inventoryRepository.delete(savedItem.getId());

        // Assert: Verificar que se eliminó correctamente
        assertThat(deleted).isTrue();
        assertThat(inventoryRepository.findById(savedItem.getId())).isNull();
        assertThat(inventoryRepository.existsById(savedItem.getId())).isFalse();
    }

    /**
     * Test para el método delete() - Caso de error
     * Verifica que retorna false cuando el item no existe
     */
    @Test
    void testDelete_NotFound() {
        // Act: Intentar eliminar item que no existe
        boolean deleted = inventoryRepository.delete(999L);

        // Assert: Verificar que no se eliminó
        assertThat(deleted).isFalse();
    }

    /**
     * Test para el método existsById()
     * Verifica que detecta correctamente la existencia de items
     */
    @Test
    void testExistsById() {
        // Arrange: Guardar un item
        Item savedItem = inventoryRepository.save(new Item(null, "Test Item", 10));

        // Act & Assert: Verificar existencia
        assertThat(inventoryRepository.existsById(savedItem.getId())).isTrue();
        assertThat(inventoryRepository.existsById(999L)).isFalse();
    }

    /**
     * Test para el método count()
     * Verifica que cuenta correctamente el número de items
     */
    @Test
    void testCount() {
        // Arrange: Verificar estado inicial
        assertThat(inventoryRepository.count()).isEqualTo(0);

        // Act: Guardar varios items
        inventoryRepository.save(new Item(null, "Item 1", 5));
        inventoryRepository.save(new Item(null, "Item 2", 10));
        inventoryRepository.save(new Item(null, "Item 3", 15));

        // Assert: Verificar conteo
        assertThat(inventoryRepository.count()).isEqualTo(3);

        // Act: Eliminar un item
        inventoryRepository.delete(1L);

        // Assert: Verificar conteo actualizado
        assertThat(inventoryRepository.count()).isEqualTo(2);
    }

    /**
     * Test de concurrencia - Múltiples threads guardando items
     * Verifica que el repositorio es thread-safe con ConcurrentHashMap y AtomicLong
     */
    @Test
    void testConcurrentSaves() throws InterruptedException {
        // Arrange: Configurar concurrencia
        int numberOfThreads = 10;
        int itemsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // Act: Ejecutar operaciones concurrentes
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        Item item = new Item(null, "Thread-" + threadId + "-Item-" + j, j);
                        Item savedItem = inventoryRepository.save(item);
                        if (savedItem != null && savedItem.getId() != null) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Esperar a que terminen todos los threads
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Assert: Verificar que todas las operaciones fueron exitosas
        assertThat(successCount.get()).isEqualTo(numberOfThreads * itemsPerThread);
        assertThat(inventoryRepository.count()).isEqualTo(numberOfThreads * itemsPerThread);

        // Verificar que no hay IDs duplicados
        List<Item> allItems = inventoryRepository.findAll();
        long uniqueIds = allItems.stream()
                .map(Item::getId)
                .distinct()
                .count();
        assertThat(uniqueIds).isEqualTo(numberOfThreads * itemsPerThread);
    }

    /**
     * Test de concurrencia - Operaciones mixtas (guardar, leer, actualizar, eliminar)
     * Verifica que el repositorio maneja correctamente operaciones concurrentes mixtas
     */
    @Test
    void testConcurrentMixedOperations() throws InterruptedException {
        // Arrange: Configurar concurrencia
        int numberOfThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Act: Ejecutar operaciones mixtas concurrentes
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    // Cada thread hace diferentes operaciones
                    if (threadId % 4 == 0) {
                        // Guardar items
                        for (int j = 0; j < 20; j++) {
                            inventoryRepository.save(new Item(null, "Thread-" + threadId + "-Item-" + j, j));
                        }
                    } else if (threadId % 4 == 1) {
                        // Leer items
                        for (int j = 0; j < 20; j++) {
                            inventoryRepository.findAll();
                        }
                    } else if (threadId % 4 == 2) {
                        // Actualizar items (si existen)
                        for (int j = 1; j <= 20; j++) {
                            Item item = inventoryRepository.findById((long) j);
                            if (item != null) {
                                Item updatedItem = new Item(null, "Updated-" + j, j * 2);
                                inventoryRepository.update((long) j, updatedItem);
                            }
                        }
                    } else {
                        // Eliminar items (si existen)
                        for (int j = 1; j <= 20; j++) {
                            inventoryRepository.delete((long) j);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Esperar a que terminen todos los threads
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Assert: Verificar que el repositorio sigue siendo consistente
        List<Item> allItems = inventoryRepository.findAll();
        assertThat(allItems).isNotNull();
        
        // Verificar que no hay IDs duplicados
        long uniqueIds = allItems.stream()
                .map(Item::getId)
                .distinct()
                .count();
        assertThat(uniqueIds).isEqualTo(allItems.size());
    }
}
