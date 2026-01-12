# Docker Deployment Guide

This document describes how to deploy Influent using Docker containers.

## Automated Deployment

The project includes an automated GitHub Actions workflow that builds and publishes Docker images to GitHub Container Registry (GHCR).

### Workflow Triggers

The deployment workflow runs automatically on:
- Pushes to the `master` branch
- Git tags matching `v*.*.*` (e.g., `v2.0.0`)
- Manual workflow dispatch from GitHub Actions UI

### Published Images

Images are published to: `ghcr.io/org-echo-unchartedsoftware/cogfluence`

Available tags:
- `latest` - Latest build from master branch
- `master` - Latest build from master branch
- `v*.*.*` - Semantic version tags (e.g., `v2.0.0`)
- `<branch>-<sha>` - Branch name with commit SHA

## Manual Docker Build

### Prerequisites

- Docker installed and running
- Java 17 (for building WAR files)
- Maven 3.8.8 or later

### Build Steps

1. **Build the Maven artifacts:**

   ```bash
   mvn clean package -DskipTests -Dspotless.check.skip=true \
     -pl bitcoin,influent-app,kiva,walker \
     -am
   ```

2. **Build the Docker image:**

   ```bash
   docker build -t cogfluence:latest .
   ```

3. **Run the container:**

   ```bash
   docker run -d -p 8080:8080 \
     --name cogfluence \
     -e CATALINA_OPTS="-Xmx10240m" \
     cogfluence:latest
   ```

### Accessing the Applications

Once the container is running, access the applications at:

- Bitcoin: http://localhost:8080/bitcoin
- Influent App: http://localhost:8080/influent-app
- Kiva: http://localhost:8080/kiva
- Walker: http://localhost:8080/walker

## Docker Compose Deployment

Create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  cogfluence:
    image: ghcr.io/org-echo-unchartedsoftware/cogfluence:latest
    ports:
      - "8080:8080"
    environment:
      - CATALINA_OPTS=-Xmx10240m
    restart: unless-stopped
```

Run with:

```bash
docker-compose up -d
```

## Production Deployment Considerations

### Memory Configuration

The default heap size is set to 10GB (`-Xmx10240m`). Adjust this based on your data size:

```bash
docker run -d -p 8080:8080 \
  -e CATALINA_OPTS="-Xmx16384m" \
  cogfluence:latest
```

### Volume Mounts

For persistent data, mount volumes:

```bash
docker run -d -p 8080:8080 \
  -v /path/to/data:/data \
  -v /path/to/logs:/usr/local/tomcat/logs \
  cogfluence:latest
```

### Environment Variables

Configure additional Tomcat settings:

```bash
docker run -d -p 8080:8080 \
  -e CATALINA_OPTS="-Xmx10240m -Duser.timezone=UTC" \
  -e JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom" \
  cogfluence:latest
```

## Container Registry Authentication

To pull private images from GHCR:

1. **Create a Personal Access Token (PAT)** with `read:packages` scope

2. **Login to GHCR:**

   ```bash
   echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin
   ```

3. **Pull the image:**

   ```bash
   docker pull ghcr.io/org-echo-unchartedsoftware/cogfluence:latest
   ```

## Kubernetes Deployment

Example Kubernetes deployment manifest:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cogfluence
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cogfluence
  template:
    metadata:
      labels:
        app: cogfluence
    spec:
      containers:
      - name: cogfluence
        image: ghcr.io/org-echo-unchartedsoftware/cogfluence:latest
        ports:
        - containerPort: 8080
        env:
        - name: CATALINA_OPTS
          value: "-Xmx10240m"
        resources:
          requests:
            memory: "10Gi"
            cpu: "2"
          limits:
            memory: "12Gi"
            cpu: "4"
---
apiVersion: v1
kind: Service
metadata:
  name: cogfluence
spec:
  selector:
    app: cogfluence
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

Apply with:

```bash
kubectl apply -f cogfluence-deployment.yaml
```

## Troubleshooting

### Container fails to start

Check logs:
```bash
docker logs cogfluence
```

### Out of memory errors

Increase heap size:
```bash
docker run -e CATALINA_OPTS="-Xmx16384m" ...
```

### WAR files not found during build

Ensure Maven build completed successfully:
```bash
ls -lh bitcoin/target/*.war
ls -lh influent-app/target/*.war
ls -lh kiva/target/*.war
ls -lh walker/target/*.war
```

## Security Considerations

1. **Keep images updated**: Regularly pull latest images with security patches
2. **Use specific tags**: In production, use version tags instead of `latest`
3. **Scan for vulnerabilities**: Use `docker scan` or similar tools
4. **Network isolation**: Use Docker networks to isolate containers
5. **Resource limits**: Always set memory and CPU limits

## Additional Resources

- [Dockerfile](../Dockerfile)
- [Deployment Documentation](docs/src/community/developer-docs/how-to/deployment/readme.md)
- [Build Status](BUILD_STATUS.md)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
