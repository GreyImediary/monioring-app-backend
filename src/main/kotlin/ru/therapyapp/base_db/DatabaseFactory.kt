package ru.therapyapp.base_db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DatabaseFactory {
    var database: Database =
        Database.connect("jdbc:postgresql://db:5432/monitoringoneapp?user=postgres", driver = "org.postgresql.Driver")
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO, DatabaseFactory.database) { block() }

