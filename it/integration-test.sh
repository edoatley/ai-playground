#!/bin/bash

# Array to store PIDs
declare -A APP_PIDS

# Function to wait for an application to be ready
wait_for_app() {
    local port=$1
    local max_attempts=30
    local attempt=1
    
    echo "Waiting for application on port $port..."
    while ! curl -s "http://localhost:$port/actuator/health" | grep -q "UP"; do
        if [ $attempt -ge $max_attempts ]; then
            echo "Application on port $port failed to start"
            exit 1
        fi
        echo "Attempt $attempt: Application on port $port not ready yet..."
        sleep 2
        ((attempt++))
    done
    echo "Application on port $port is ready!"
}

# Function to run tests against an endpoint and save results
run_tests() {
    local port=$1
    local model=$2
    local output_dir="/workspaces/ai-playground/build/test_results/$model"
    
    mkdir -p "$output_dir"
    
    echo "Running tests against $model on port $port..."
    
    # Run chat test
    http -f POST "http://localhost:$port/api/chat" message="Tell me a joke" > "$output_dir/chat_response.json"
    
    # Run summarize test
    http -f POST "http://localhost:$port/api/summarize" file@"/workspaces/ai-playground/gemini/src/test/resources/FDBP.pdf" > "$output_dir/summarize_response.json"
}

# Clean up function
cleanup() {
    echo "Cleaning up..."
    for pid in "${APP_PIDS[@]}"; do
        kill $pid 2>/dev/null
    done
    exit
}

# Set up trap for cleanup
trap cleanup EXIT INT TERM

# Create test results directory
rm -rf /workspaces/ai-playground/build/test_results
mkdir -p /workspaces/ai-playground/build/test_results

# Build the applications
echo "Building applications..."
cd .. && ./gradlew clean build

# Read configuration and start applications
echo "Starting applications..."
readarray -t MODELS < <(yq eval '.models[].name' it/config.yaml)
readarray -t PORTS < <(yq eval '.models[].port' it/config.yaml)
readarray -t JARS < <(yq eval '.models[].jarPath' it/config.yaml)

for i in "${!MODELS[@]}"; do
    echo "Starting ${MODELS[$i]} application..."
    java -jar "${JARS[$i]}" &
    APP_PIDS[${MODELS[$i]}]=$!
    wait_for_app "${PORTS[$i]}"
done

# Run tests against all endpoints
for i in "${!MODELS[@]}"; do
    run_tests "${PORTS[$i]}" "${MODELS[$i]}"
done

# Compare results
echo "Comparing results..."
for test in chat_response summarize_response; do
    echo "Differences in $test:"
    # Compare first model with all others
    for i in "${!MODELS[@]}"; do
        if [ $i -eq 0 ]; then continue; fi
        echo "Comparing ${MODELS[0]} with ${MODELS[$i]}:"
        diff -y --suppress-common-lines \
            "/workspaces/ai-playground/build/test_results/${MODELS[0]}/${test}.json" \
            "/workspaces/ai-playground/build/test_results/${MODELS[$i]}/${test}.json"
    done
done

echo "Test execution completed. Check /workspaces/ai-playground/build/test_results directory for full outputs."