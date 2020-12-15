package com.example_ktorYT

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson { }
    }
    task()
}

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
                        false -> call.respond(HttpStatusCode.NotFound, Error("Task with id $id not found"))
                    }
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
                            null -> call.respond(HttpStatusCode.NotFound, Error("Task ID $data not found"))
                            else -> {
                                task_list.remove(task_lt)
                                call.respond(HttpStatusCode.OK, Error("Task ID $data deleted"))
                            }
                        }
                    }
                }
            }

            delete("/remove"){
                task_list.clear()
                call.respond(HttpStatusCode.OK, Error("All data is remove $task_list"))
            }
        }
    }
}

