#!/bin/bash

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
    kill $GEMINI_PID $OPENAI_PID 2>/dev/null
    exit
}

# Set up trap for cleanup
trap cleanup EXIT INT TERM

# Create test results directory
rm -rf /workspaces/ai-playground/build/test_results
mkdir -p /workspaces/ai-playground/build/test_results

# Build the applications
echo "Building applications..."
cd .. && ./gradlew :gemini:build :openai:build

# Start both applications in background
echo "Starting Gemini application..."
java -jar gemini/build/libs/gemini-0.0.1-SNAPSHOT.jar &
GEMINI_PID=$!

echo "Starting OpenAI application..."
java -jar openai/build/libs/openai-0.0.1-SNAPSHOT.jar &
OPENAI_PID=$!

# Wait for both applications to be ready
wait_for_app 8080
wait_for_app 8081

# Run tests against both endpoints
run_tests 8080 "gemini"
run_tests 8081 "openai"

# Compare results
echo "Comparing results..."
for test in chat_response summarize_response; do
    echo "Differences in $test:"
    diff -y --suppress-common-lines /workspaces/ai-playground/build/test_results/gemini/${test}.json /workspaces/ai-playground/build/test_results/openai/${test}.json
done

echo "Test execution completed. Check /workspaces/ai-playground/build/test_results directory for full outputs."