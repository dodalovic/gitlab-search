package com.dodalovic.tools

import kotlinx.coroutines.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.inject.Inject
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


const val searchThroughAllProjectsQueryParamValue = "__all__"

@Path("/search")
class SearchResource {

    @ConfigProperty(name = "gitlab.token")
    lateinit var apiToken: String

    @Inject
    @field: RestClient
    lateinit var gitlabProjectsClient: GitlabProjectsClient

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun search(
        @QueryParam("searchTerm") searchTerm: String, @DefaultValue(searchThroughAllProjectsQueryParamValue) @QueryParam(
            "pattern"
        ) projectsSearchPattern: String
    ): Response {
        val includeAllProjects =
            projectsSearchPattern == searchThroughAllProjectsQueryParamValue
        println("Searching through ${if (includeAllProjects) "all" else "*$projectsSearchPattern*"} projects")
        val result = mutableListOf<JsonArray>()
        val allProjects = mutableListOf<JsonObject>()
        runBlocking(Dispatchers.IO) {
            val firstPageResponse = if (includeAllProjects) {
                gitlabProjectsClient.getAllProjects(AllProjectsRequest(apiToken))
            } else {
                gitlabProjectsClient.searchAllProjectsByName(ProjectsSearch(token = apiToken, search = "-service"))
            }
            val firstPageProjects = firstPageResponse.readEntity(JsonArray::class.java)
            val projectsJobs = mutableListOf<Deferred<JsonArray>>()
            val totalProjectPages = totalProjectPages(firstPageResponse)
            for (i in 2..totalProjectPages) {
                projectsJobs += async {
                    if (includeAllProjects) {
                        gitlabProjectsClient.getAllProjects(AllProjectsRequest(token = apiToken, page = i))
                            .readEntity(JsonArray::class.java)
                    } else {
                        gitlabProjectsClient.searchAllProjectsByName(
                            ProjectsSearch(
                                token = apiToken,
                                search = "-service",
                                page = i
                            )
                        ).readEntity(JsonArray::class.java)
                    }
                }
                println("Got page $i/$totalProjectPages of projects pages")
            }
            val allGitlabProjects = projectsJobs.awaitAll().toMutableList()
            allGitlabProjects += firstPageProjects
            allProjects += allGitlabProjects.flatMap { it.toList() } as List<JsonObject>
            val searchResultsJobs = mutableListOf<Deferred<JsonArray>>()
            println(
                "Projects matched: ${allProjects.size}\n\t${allProjects.joinToString(
                    separator = "\n\t",
                    transform = { jsonObject -> jsonObject.getString("name") })}"
            )
            for (project in allProjects) {
                searchResultsJobs += async {
                    gitlabProjectsClient.searchProjectBySearchTerm(
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
                .add("project_name", projectName(it.getInt("project_id"), allProjects))
                .build()
        }).build()
    }

    private fun projectName(projectId: Int, servicesOnly: List<JsonObject>): String =
        servicesOnly.firstOrNull { it.getInt("id") == projectId }?.getString("web_url") ?: "No web url"

    private fun totalProjectPages(firstPageResponse: Response) =
        (firstPageResponse.headers.getValue("X-Total-Pages").first() as String).toInt()
}