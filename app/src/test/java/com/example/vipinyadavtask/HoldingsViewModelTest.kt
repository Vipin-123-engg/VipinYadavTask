package com.example.vipinyadavtask

import com.example.vipinyadavtask.data.local.LocalDataSource
import com.example.vipinyadavtask.data.remote.RemoteDataSource
import com.example.vipinyadavtask.data.repository.HoldingsRepositoryImpl
import com.example.vipinyadavtask.domain.model.Holding
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HoldingsRepositoryImplTest {
    private class FakeRemote(var result: Result<List<Holding>>) : RemoteDataSource(
        api = object : com.example.vipinyadavtask.data.remote.HoldingsApi { override suspend fun getHoldings() = throw NotImplementedError() }
    ) {
        override suspend fun fetchHoldings(): List<Holding> = result.getOrThrow()
    }

    private class FakeLocal(initial: List<Holding>) : LocalDataSource(
        dao = object : com.example.vipinyadavtask.data.local.db.HoldingsDao {
            override suspend fun getAll(): List<com.example.vipinyadavtask.data.local.db.HoldingEntity> = emptyList()
            override suspend fun clear() {}
            override suspend fun insertAll(items: List<com.example.vipinyadavtask.data.local.db.HoldingEntity>) {}
        }
    ) {
        private val store: MutableList<Holding> = initial.toMutableList()

        override suspend fun getHoldings(): List<Holding> = store.toList()
        override suspend fun replaceHoldings(items: List<Holding>) { store.apply { clear(); addAll(items) } }
    }

    @Test
    fun `network success updates local and returns fresh`() = runBlocking {
        val fresh = listOf(Holding("A", 2, 10.0, 8.0, 9.0))
        val remote = FakeRemote(Result.success(fresh))
        val local = FakeLocal(initial = emptyList())

        val repo = HoldingsRepositoryImpl(remote, local)
        val result = repo.getHoldings().getOrThrow()

        assertEquals(fresh, result)
        // local must now contain fresh
        assertEquals(fresh, local.getHoldings())
    }

    @Test
    fun `network failure falls back to local`() = runBlocking {
        val cached = listOf(Holding("B", 1, 5.0, 6.0, 4.0))
        val remote = FakeRemote(Result.failure(IllegalStateException("boom")))
        val local = FakeLocal(initial = cached)

        val repo = HoldingsRepositoryImpl(remote, local)
        val result = repo.getHoldings().getOrThrow()

        assertEquals(cached, result)
    }
}
