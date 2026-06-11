package com.example

import com.example.data.ProductRepository
import com.example.replication.ReplicationClient
import io.ktor.server.application.*

fun Application.configureApp(
    repo: ProductRepository,
    replicationClient: ReplicationClient?,
    serviceRole: String
) {
    configureStatusPages()
    configureSerialization()
    configureRouting(repo, replicationClient, serviceRole)
}
