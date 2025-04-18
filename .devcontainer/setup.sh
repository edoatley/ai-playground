#!/bin/bash

# Ensure the credentials directory exists
mkdir -p /workspaces/ai-playground/credentials

# Write the GOOGLE_APPLICATION_CREDENTIALS_JSON secret to a file
echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" > /workspaces/ai-playground/credentials/service-account-key.json
chmod 600 /workspaces/ai-playground/credentials/service-account-key.json
echo "GOOGLE_APPLICATION_CREDENTIALS_JSON written to /workspaces/ai-playground/credentials/service-account-key.json"

# Check if the GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable is set
if [ -n "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
    echo "GOOGLE_APPLICATION_CREDENTIALS is set to $GOOGLE_APPLICATION_CREDENTIALS"
else
    echo "GOOGLE_APPLICATION_CREDENTIALS is not set. Setting it to /workspaces/ai-playground/credentials/service-account-key.json"
    export GOOGLE_APPLICATION_CREDENTIALS=/workspaces/ai-playground/credentials/service-account-key.json
    echo 'export GOOGLE_APPLICATION_CREDENTIALS=/workspaces/ai-playground/credentials/service-account-key.json' >> /home/vscode/.bashrc
    echo "GOOGLE_APPLICATION_CREDENTIALS is now set to $GOOGLE_APPLICATION_CREDENTIALS"
fi

export GEMINI_PROJECT_ID=$GEMINI_PROJECT_ID
# if bashrc does not have GEMINI_PROJECT_ID, add it
if ! grep -q "GEMINI_PROJECT_ID" /home/vscode/.bashrc; then
    echo "GEMINI_PROJECT_ID is not set in bashrc. Setting it to $GEMINI_PROJECT_ID"
    export GEMINI_PROJECT_ID=$GEMINI_PROJECT_ID
fi

# Install additional tools
sudo apt-get update && sudo apt-get install -y httpie