package com.gergelydaniel.jogjegyzet.persistence.favorite

import com.gergelydaniel.jogjegyzet.domain.Document

inline fun mapToEntity(document: Document) =
        FavoriteEntity(
                docId = document.id,
                categoryId = document.categoryId,
                fileUrl = document.fileUrl,
                name = document.name,
                date = document.date,
                size = document.size,
                downloads = document.downloads,
                desc = document.desc,
                posRatings = document.posRatings,
                negRatings = document.negRatings
        )

inline fun mapFromEntity(entity: FavoriteEntity) =
        Document(
                id = entity.docId,
                categoryId = entity.categoryId,
                fileUrl = entity.fileUrl,
                name = entity.name,
                date = entity.date,
                size = entity.size,
                downloads = entity.downloads,
                desc = entity.desc,
                posRatings = entity.posRatings,
                negRatings = entity.negRatings
        )