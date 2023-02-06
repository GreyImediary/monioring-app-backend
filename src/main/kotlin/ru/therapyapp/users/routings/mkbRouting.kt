package ru.therapyapp.users.routings

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.therapyapp.base_api.ResponseError
import ru.therapyapp.base_consts.API_VERSION
import ru.therapyapp.base_db.dbQuery
import ru.therapyapp.users.db.MkbDAO
import ru.therapyapp.users.model.Mkb
import ru.therapyapp.users.model.toMkb

fun Application.configureMkbRouting() {
    routing {
        route("$API_VERSION/mkb") {
            get {
                try {
                    val mkbs = dbQuery { MkbDAO.all().map { it.toMkb() }.reversed() }

                    call.respond(HttpStatusCode.OK, mkbs)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Произошла ошибка во время получения МКБ"))
                }
            }

            post {
                val mkbRequest = call.receive<Mkb>()
                try {
                    val mkbs = dbQuery {
                        MkbDAO.new {
                            code = mkbRequest.code
                            name = mkbRequest.name
                        }

                        MkbDAO.all().map { it.toMkb() }
                    }

                    call.respond(HttpStatusCode.OK, mkbs)
                } catch (e: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest, ResponseError("Произошла ошибка во время добавления МКБ"))
                }
            }
        }
    }
}