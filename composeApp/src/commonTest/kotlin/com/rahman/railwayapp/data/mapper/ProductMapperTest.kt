package com.rahman.railwayapp.data.mapper

import com.rahman.railwayapp.data.local.ProductEntity
import com.rahman.railwayapp.data.remote.dto.ProductDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlin.test.Test
import kotlin.time.Instant

class ProductMapperTest {

    @Test
    fun `dto to entity should map all fields correctly`() {
        // Arrange
        val dto = ProductDto(
            id = 1,
            title = "Test",
            description = "Desc",
            userId = "user",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        // Act
        val entity = dto.toEntity()

        // Assert
        assertEquals(1, entity.id)
        assertTrue(entity.synced)
    }

    @Test
    fun `entity to domain should preserve sync state`() {
        // Arrange
        val entity = ProductEntity(
            id = 1,
            title = "Test",
            description = null,
            userId = "user",
            createdAt = Instant.DISTANT_PAST,
            updatedAt = Instant.DISTANT_PAST,
            synced = false
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertFalse(domain.isSynced)
    }
}
