package ru.therapyapp.base_db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DatabaseFactory {
    var database: Database = Database.connect("jdbc:postgresql://localhost:5432/oneapp", driver = "org.postgresql.Driver",
        user = "postgres", password = "postpost")

}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO, DatabaseFactory.database) { block() }

