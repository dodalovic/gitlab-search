package com.dodalovic.tools

import kotlinx.coroutines.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.inject.Inject
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/search")
class SearchResource {

    @ConfigProperty(name = "gitlab.api")
    lateinit var api: String

    @ConfigProperty(name = "gitlab.token")
    lateinit var apiToken: String

    @Inject
    @field: RestClient
    lateinit var gitlabSearchClient: GitlabSearchClient

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun search(@QueryParam("searchTerm") searchTerm: String, @QueryParam("allProjects") includeAllProjects: Boolean? = false): Response {
        val result = mutableListOf<JsonArray>()
        var projectsToBeSearchedThrough = listOf<JsonObject>()
        runBlocking(Dispatchers.IO) {
            val firstPageResponse =
                gitlabSearchClient.getAllProjects(AllProjectsRequest(apiToken))
            val firstPageProjects = firstPageResponse.readEntity(JsonArray::class.java)
            val projectsJobs = mutableListOf<Deferred<JsonArray>>()
            for (i in 2..totalProjectPages(firstPageResponse)) {
                projectsJobs += async {
                    gitlabSearchClient.getAllProjects(AllProjectsRequest(token = apiToken, page = i))
                        .readEntity(JsonArray::class.java)
                }
            }
            val allGitlabProjects = projectsJobs.awaitAll().toMutableList()
            allGitlabProjects += firstPageProjects

            val all = allGitlabProjects.flatMap { it.toList() } as List<JsonObject>
            projectsToBeSearchedThrough = if (includeAllProjects!!) all else all.filter {
                it.getString("name").endsWith("-service")
            }
            val searchResultsJobs = mutableListOf<Deferred<JsonArray>>()
            for (project in projectsToBeSearchedThrough) {
                searchResultsJobs += async {
                    gitlabSearchClient.searchBySearchTerm(
                        SearchTerm(
                            token = apiToken,
                            search = searchTerm,
                            projectId = project.getInt("id")
                        )
                    )
                }
            }
            result += searchResultsJobs.awaitAll()
        }
        return Response.ok((result.flatMap { it.toList() } as List<JsonObject>).map {
            Json.createObjectBuilder(it)
                .add("project_name", projectName(it.getInt("project_id"), projectsToBeSearchedThrough))
                .build()
        }).build()
    }

    private fun projectName(projectId: Int, servicesOnly: List<JsonObject>): String =
        servicesOnly.first { it.getInt("id") == projectId }.getString("web_url")

    private fun totalProjectPages(firstPageResponse: Response) =
        (firstPageResponse.headers.getValue("X-Total-Pages").first() as String).toInt()
}