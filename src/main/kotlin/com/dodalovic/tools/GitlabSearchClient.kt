package com.dodalovic.tools

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.json.JsonArray
import javax.ws.rs.BeanParam
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response


@Path("/api/v4")
@RegisterRestClient
interface GitlabSearchClient {

    @GET
    @Path("/projects")
    @Produces("application/json")
    fun getAllProjects(@BeanParam search: AllProjectsRequest): Response

    @GET
    @Path("/projects/{project_id}/search/")
    @Produces("application/json")
    fun searchBySearchTerm(@BeanParam search: SearchTerm): JsonArray
}