# From Monolith to K8s :: Agenda Service Helm Chart

To install this chart you need to run: 

```
helm repo add fmtok8s https://github.com/salaboy/helm
helm repo update
helm install agenda fmtok8s/fmtok8s-agenda-service
```

## Options

- Enable Redis deployment using Redis Helm Chart `redis.enabled=true`, by default is set to true
- Enable Prometheus Service Monitor: `prometheus.enabled=false`, by default is set to false 
- Enable Knative Service deployment: `knative.enable=false`, by default is set to false 

## Running helm tests

This project uses [helm unittest plugin](https://github.com/helm-unittest/helm-unittest/) to run tests.

After installing unittest plugin, run the following command (on root folder):

```sh
    helm unittest helm/fmtok8s-agenda-service --helm3
```