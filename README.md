# From Monolith To K8s :: Agenda Service

Run with Docker locally: 
```
docker run -p 8080:8080 ghcr.io/salaboy/fmtok8s-agenda-service
```


Create new agenda item using `httpie`

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
