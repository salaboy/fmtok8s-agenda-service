# From Monolith To K8s :: Agenda Service

This repository contains the Agenda Service for the Conference Application. You can read more about [this application here](https://github.com/salaboy/from-monolith-to-k8s).

## Running Locally from source

Before starting the Agenda Service you need to start the application infrastructure. We do that by running the following command: 

```
docker-compose up
```

You can use Maven (`mvn`) to start the service locally: 

```
mvn spring-boot:run
```


## Interacting with the service

You can use `curl` or `httpie` to send requests to the exposed REST Endpoints: 

Create new agenda item:

```
http post :8080 @agenda-item.json
```

Get all the Agenda Items: 

```
http :8080
```

Get Highlighted items only: 

```
http :8080/highlights
```


## Service Pipelines

This repository contains three service pipeline definitions:
- Tekton: you can find the Tekton Pipeline definition under the [`tekton` directory](tekton/README.md). 
- Dagger: you can find the Dagger pipeline defintion inside the []`pipeline.go` file](pipeline.go). 
- GitHub Actions: you can find the GitHub action definition under the [`.github/workflows/` directory](.github/workflows/ci_workflow.yml).


