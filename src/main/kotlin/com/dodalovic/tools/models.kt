package com.dodalovic.tools

import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

data class SearchResult(
    val basename: String,
    val data: String,
    val path: String,
    val filename: String,
    val id: String,
    val ref: String,
    val startline: String,
    val projectId: String
)

data class AllProjectsRequest(
    @field:QueryParam("private_token") var token: String,
    @field:QueryParam("per_page") var perPage: Int = 100,
    @field:QueryParam("page") var page: Int = 1
)

data class SearchTerm(
    @field:QueryParam("private_token") var token: String,
    @field:QueryParam("search") var search: String,
    @field:PathParam("project_id") var projectId: Int,
    @field:QueryParam("scope") var scope: String = "blobs",
    @field:QueryParam("ref") var ref: String = "master"
)