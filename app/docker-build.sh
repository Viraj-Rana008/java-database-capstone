#!/bin/bash
# Quick Docker Build and Run Script for CMS Backend
# Usage: ./docker-build.sh [option]

set -e

echo "======================================"
echo "CMS Backend - Docker Build Script"
echo "======================================"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="cms-backend"
IMAGE_TAG="latest"
CONTAINER_NAME="cms-app"
PORT="8080:8080"

# Function to display usage
usage() {
    echo "Usage: $0 [build|run|compose|stop|clean|logs]"
    echo ""
    echo "Commands:"
    echo "  build     - Build Docker image only"
    echo "  run       - Build and run container (H2 database)"
    echo "  compose   - Run with docker-compose (MySQL + MongoDB)"
    echo "  stop      - Stop all running containers"
    echo "  clean     - Remove containers and images"
    echo "  logs      - Show application logs"
    exit 1
}

# Build Docker image
build_image() {
    echo -e "${GREEN}Building Docker image...${NC}"
    docker build -t $IMAGE_NAME:$IMAGE_TAG .
    echo -e "${GREEN}✓ Build complete!${NC}"
}

# Run container with H2
run_container() {
    build_image
    echo -e "${GREEN}Starting container...${NC}"
    
    # Stop existing container if running
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    
    docker run -d \
        --name $CONTAINER_NAME \
        -p $PORT \
        -e SPRING_PROFILES_ACTIVE=h2 \
        $IMAGE_NAME:$IMAGE_TAG
    
    echo -e "${GREEN}✓ Container started!${NC}"
    echo -e "${YELLOW}Access application at: http://localhost:8080${NC}"
    echo -e "${YELLOW}H2 Console at: http://localhost:8080/h2-console${NC}"
}

# Run with docker-compose
run_compose() {
    echo -e "${GREEN}Starting services with docker-compose...${NC}"
    docker-compose up --build -d
    echo -e "${GREEN}✓ Services started!${NC}"
    echo -e "${YELLOW}Access application at: http://localhost:8080${NC}"
    echo ""
    echo "View logs: docker-compose logs -f"
}

# Stop containers
stop_containers() {
    echo -e "${YELLOW}Stopping containers...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker-compose down 2>/dev/null || true
    echo -e "${GREEN}✓ Containers stopped!${NC}"
}

# Clean up
clean_all() {
    echo -e "${YELLOW}Cleaning up...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    docker-compose down -v 2>/dev/null || true
    docker rmi $IMAGE_NAME:$IMAGE_TAG 2>/dev/null || true
    echo -e "${GREEN}✓ Cleanup complete!${NC}"
}

# Show logs
show_logs() {
    echo -e "${GREEN}Showing logs (Ctrl+C to exit)...${NC}"
    if docker ps | grep -q $CONTAINER_NAME; then
        docker logs -f $CONTAINER_NAME
    elif docker-compose ps | grep -q app; then
        docker-compose logs -f app
    else
        echo "No running containers found!"
    fi
}

# Main script
case "${1:-}" in
    build)
        build_image
        ;;
    run)
        run_container
        ;;
    compose)
        run_compose
        ;;
    stop)
        stop_containers
        ;;
    clean)
        clean_all
        ;;
    logs)
        show_logs
        ;;
    *)
        usage
        ;;
esac
