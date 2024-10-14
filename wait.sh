#!/bin/bash


timeout=300
elapsed=0

while [ $elapsed -lt $timeout ]; do
    pending_services=$(docker ps --format "{{.Status}}" | grep -E "starting|unhealthy" | wc -l)
    if [ $pending_services -eq 0 ]; then
        echo "All services are up and running."
        exit 0
    fi
    sleep 1
    ((elapsed++))
done

echo "Timeout reached. Some services did not start."
exit 1