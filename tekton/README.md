# Tekton Service Pipelines

This directory contains a Tekton Pipeline to build the service artifacts that are needed to deploy this service to different environments. 

Because this Pipeline will run inside a Kubernetes Cluster, the sources to build the application needs to be downloaded from a public repository. This means that when you run this pipeline using Tekton, the source code will be cloned from a remote repository. 

This Service Pipeline includes the following steps: 
- **Git Clone**: Clone the service source code from a GitHub repository.
- **Build Service**: Uses `maven` to build the service which is written using Java. Because the service might need other containers to run integration tests, the pipeline uses [Tekton sidecars to run these containers](resources/service-pipeline.yaml). 
- **Build and Push Container to Registry**: Uses `kaniko` to create a container for the service using the specified `Dockerfile`. 
- **Package Helm Chart**: creates a Helm Chart using the Chart definition located inside the `helm` directory. 
- **Publish Helm Chart**: publishes the Helm Chart to a Chart repository, for this example, this chart repository is located in a GitHub repository. 


## Prerequisites

For this Tekton pipeline to work, you need to have installed Tekton in a Kubernetes Cluster and the following Tekton Tasks from the Tekton Hub Catalog. 

- Install Tekton into a Kubernetes Cluster
- Install Tekton Git, Maven, Helm Tasks
- (Optional) Install `tkn` CLI 
- (Optional) Install the Tekton Dashboard
- Create a secret to push Containers to your faviourte Container Registry


## Install the Service Pipeline

Once you have Tekton and the Tekton tasks that we will be using in the Service pipeline installed in your cluster you can install the Pipeline Definition by running: 

```
kubectl apply -f resources/
```

Check the Tekton Pipeline definition here: [Service Pipeline](resources/service-pipeline.yaml).

Once the pipeline definition is installed you can create new `PipelineRuns`. An example is provided in the `service-pipeline-run.yaml`: 

```
apiVersion: tekton.dev/v1
kind: PipelineRun
metadata:
  name: service-pipeline-run-1
spec:
  params: 
    target-registry: salaboy
    target-name: fmtok8s-agenda-service
    target-version: 0.1.0-from-pipeline-run
  workspaces:
    - name: sources
      volumeClaimTemplate: 
        spec:
          accessModes:
          - ReadWriteOnce
          resources:
            requests:
              storage: 1Gi 
    - name: dockerconfig
      secret:  
        secretName: regcred
    - name: maven-settings
      emptyDir: {}
  pipelineRef:
    name: agenda-service-pipeline
```

Notice that if you want to run this pipeline multiple times you will need to change the `metadata.name`. 

Alternatively, you can use the `tkn` CLI tool to create a new `PipelineRun`s: 

```
```

When using the `tkn` CLI you will be asked by the input parameters, you can accept the default by just pressing `enter`.

If configured correctly, the `PipelineRun` should push the created container to the `target-registry` with the `PipelineRun` configured `target-version` tag. 

You can take follow the logs of the pipeline by running: 

```
```

or the suggested command using the `tkn` CLI. 



