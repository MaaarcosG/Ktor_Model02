package com.example_ktorYT

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson { }
    }
    task()
}

data class Item(val id: Long?, val task: String?, val complete: Boolean?)
data class Error(val message: String?)
val task_list = mutableListOf<Item>()

fun Application.task(){
    routing{
        route("/todo"){
            /* all task in json*/
            get{
                call.respond(task_list)
            }

            /* post new task*/
            post{
                val data = call.receive<Item>()
                task_list.add(data)
                call.respond(HttpStatusCode.Created)
            }

            put {
                val data = call.receive<Item>()
                val id: Long? = call.parameters["id"]?.toLongOrNull()
                val result =
                    when(task_list.contains(data)){
                        true->{
                            task_list[task_list.indexOf(data)] = data
                            call.respond(HttpStatusCode.OK)
                        }
                        false -> call.respond(HttpStatusCode.NotFound, Error("User with id $id not found"))
                    }
            }
            /*delete*/
            delete {
                task_list.remove(call.receive())
            }
            get("/{id}"){
                val data: Long? = call.parameters["id"]?.toLongOrNull()
                val result: Unit? = when(data){
                    null -> call.respond(HttpStatusCode.BadRequest, Error("ID must be long"))
                    else -> {
                        val tl: Item? = task_list.firstOrNull { it.id == data }
                        when(tl){
                            null -> call.respond(HttpStatusCode.NotFound, Error("Task with id $data not found"))
                            else -> {
                                call.respond(tl)
                            }
                        }
                    }
                }
            }

            delete("/{id}") {
                val data = call.parameters["id"]?.toLongOrNull()

                val result = when(data){
                    null -> call.respond(HttpStatusCode.BadRequest, Error("ID must be long"))
                    else -> {
                        val task_lt = task_list.firstOrNull { it.id == data }
                        when(task_lt){
                            null -> call.respond(HttpStatusCode.NotFound, Error("User ID $data not found"))
                            else -> {
                                task_list.remove(task_lt)
                                call.respond(HttpStatusCode.OK, Error("User ID $data deleted"))
                            }
                        }
                    }
                }
            }

            delete("/remove"){
                task_list.clear()
                call.respond(HttpStatusCode.OK, Error("All data is remove"))
            }
        }
    }
}

