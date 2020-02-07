# gitlab-search project
Simple `API` server providing capability to search through given Gitlab instance for any arbitrary text

## Configuration
`API` requires two configuration parameters, passed as an environment variables:
* GITLAB_API, for instance https://my.server.here/api/v4
* GITLAB_TOKEN
Your [personal access token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html) with the `api` scope. 

## Quick run
The easiest way is to run publicly available docker image:
```
# Run API as a docker image and expose it on port 8080
$ docker run -e GITLAB_API=XXXXXXXXXXXX/api/v4 -e GITLAB_TOKEN=MY_TOKEN_HERE --rm -p 8080:8080 dodalovic/gitlab-search

# Use any HTTP client to call search API
$ curl --url 'http://localhost:8080/search?searchTerm=triggerContentCapabilities&allProjects=true'--header 'accept: application/json' 
```

## Running the application in dev mode
You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev -Dgitlab.api=https://my.server.here/api/v4 -Dgitlab.token=xxxxxxxxxxxxxxxxxxxxx
```

## Performing search
`API` will be available via  `http://localhost:8080/search` 

Query params: 

* `searchTerm` - mandatory, text you want to search for
* `allProjects` - optional - search through all the projects (without this param, searches only these projects ending with `-service`)

An example call:
```
curl --url 'http://localhost:8080/search?searchTerm=triggerContentCapabilities&allProjects=false' \
  --header 'accept: application/json'
```

## Packaging and running the application
The application is packageable using `./mvnw package`.
It produces the executable `gitlab-search-1.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/gitlab-search-1.0.0-SNAPSHOT-runner.jar -Dgitlab.api=https://my.server.here/api/v4 -Dgitlab.token=xxxxxxxxxxxxxxxxxxxxx`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/gitlab-search-1.0.0-SNAPSHOT-runner -Dgitlab.api=https://my.server.here/api/v4 -Dgitlab.token=xxxxxxxxxxxxxxxxxxxxx`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .
