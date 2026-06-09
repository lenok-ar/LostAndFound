package com.example.data.repository

import com.example.data.local.ItemDao
import com.example.data.local.toDomain
import com.example.data.local.toEntity
import com.example.domain.model.Category
import com.example.domain.model.Filters
import com.example.domain.model.Item
import com.example.domain.model.Status
import com.example.domain.repository.ILostFoundRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LostFoundRepository @Inject constructor(
    private val itemDao: ItemDao
) : ILostFoundRepository {

    override suspend fun getItems(filters: Filters?): Result<List<Item>> = runCatching {
        seedDemoItemsIfNeeded()
        itemDao.getAll()
            .map { it.toDomain() }
            .filter { item ->
                filters?.category == null || item.category == filters.category
            }
            .filter { item ->
                filters?.status == null || item.status == filters.status
            }
            .filter { item ->
                filters?.location.isNullOrBlank() ||
                    item.location.contains(filters.location.orEmpty(), ignoreCase = true)
            }
            .filter { item ->
                filters?.query.isNullOrBlank() ||
                    item.title.contains(filters.query.orEmpty(), ignoreCase = true) ||
                    item.description.contains(filters.query.orEmpty(), ignoreCase = true) ||
                    item.location.contains(filters.query.orEmpty(), ignoreCase = true)
            }
    }

    override suspend fun getItemById(id: String): Result<Item?> = runCatching {
        seedDemoItemsIfNeeded()
        itemDao.getById(id)?.toDomain()
    }

    override suspend fun addItem(item: Item): Result<String> = runCatching {
        itemDao.insert(item.toEntity())
        item.id
    }

    override suspend fun updateItem(item: Item): Result<Unit> = runCatching {
        itemDao.update(item.toEntity())
    }

    override suspend fun deleteItem(id: String): Result<Unit> = runCatching {
        itemDao.deleteById(id)
    }

    private suspend fun seedDemoItemsIfNeeded() {
        if (itemDao.count() > 0) return

        val now = System.currentTimeMillis()
        itemDao.insertAll(
            listOf(
                Item(
                    id = "1",
                    title = "Беспроводные наушники",
                    description = "Черные наушники, потеряны в аудитории 2226",
                    category = Category.ELECTRONICS,
                    location = "2 корпус, ауд. 2226",
                    status = Status.LOST,
                    date = now,
                    imageUrl = null,
                    authorId = "user1",
                    authorName = "Иван Иванов",
                    authorContact = "ivan@example.com"
                ),
                Item(
                    id = "2",
                    title = "Студенческий билет",
                    description = "Найден студенческий билет у столовой",
                    category = Category.DOCUMENTS,
                    location = "Столовая, 1 этаж, 2 корпус",
                    status = Status.FOUND,
                    date = now - 1,
                    imageUrl = null,
                    authorId = "user2",
                    authorName = "Мария Петрова",
                    authorContact = "maria@example.com"
                ),
                Item(
                    id = "3",
                    title = "Черный зонт",
                    description = "Складной зонт найден на входе",
                    category = Category.ACCESSORIES,
                    location = "Вахта 1 корпуса",
                    status = Status.FOUND,
                    date = now - 2,
                    imageUrl = null,
                    authorId = "user3",
                    authorName = "Елена Васнецова",
                    authorContact = "elena@example.com"
                )
            ).map { it.toEntity() }
        )
    }
}
