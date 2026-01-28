package com.rahman.railwayapp.data.mapper

import com.rahman.railwayapp.data.local.ProductEntity
import com.rahman.railwayapp.data.remote.dto.ProductDto
import com.rahman.railwayapp.domain.model.Product
import kotlin.time.Instant

/* =========================================================
   DTO → ENTITY (REMOTE → LOCAL CACHE)
   ========================================================= */

fun ProductDto.toEntity(): ProductEntity =
    ProductEntity(
        id = id,
        title = title,
        description = description,
        userId = userId,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
        synced = true
    )

/* =========================================================
   ENTITY → DOMAIN (LOCAL → UI)
   ========================================================= */

fun ProductEntity.toDomain(): Product =
    Product(
        id = id,
        title = title,
        description = description,
        userId = userId,
        isSynced = synced
    )

/* =========================================================
   DOMAIN → ENTITY (CREATE / UPDATE OFFLINE)
   ========================================================= */
fun ProductDto.toDomain(): Product =
    Product(
        id = id,
        title = title,
        description = description,
        userId = userId,
        isSynced = true
    )
