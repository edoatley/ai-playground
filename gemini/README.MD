# Boot Gemini

Looking at setting up a simple app using Google Gemini following https://loiane.com/2025/01/intelligent-java-applications-using-spring-ai-and-gemini/

## Other references:

- The official Spring AI Documentation: https://docs.spring.io/spring-ai/reference/api/chat/google-vertexai.html
- Google Gemini API Docs: https://ai.google/

## Setup steps:

1. Create a google cloud project
2. Enable Gemini API
3. Setup local:

```bash
gcloud init
export GEMINI_PROJECT_ID="<project id>"
gcloud auth application-default login
```

4. Run the spring boot application
5. Run the [test](rest/simple-test.http)
