<p style="text-align: center"><img src="./.github/logo.svg" width="200" alt="Interweb Logo"/></p>

# Interweb - Unified API for Information Retrieval

<p style="text-align: center">
<a href="https://opensource.org/licenses/MIT" alt="License: MIT">
   <img src="https://img.shields.io/badge/License-MIT-yellow.svg"/></a>
<a href="https://github.com/l3s-learnweb/interweb/tags" alt="Releases">
   <img src="https://img.shields.io/github/v/tag/l3s-learnweb/interweb"/></a>
<a href="https://github.com/l3s-learnweb/interweb/pkgs/container/interweb" alt="Container">
   <img alt="Static Badge" src="https://img.shields.io/badge/docker-container_image-blue"></a>
<a href="https://github.com/l3s-learnweb/learnweb/packages/1951023" alt="Container">
   <img alt="Static Badge" src="https://img.shields.io/badge/maven-java_client-orange"></a>
</p>

Interweb is a powerful and versatile API that consolidates multiple data providers into one unified interface, simplifying the process of searching and retrieving information.
Whether you're building a chatbot, a data analysis tool, or any application that requires access to various data sources, Interweb can help streamline your development process.

## Features

- **Unified search**: Interweb consolidates multiple search APIs into one, making it easy to search for information from a variety of sources.

- **Unified API**: Interweb offers a single API endpoint to access multiple data providers, reducing the complexity of managing multiple APIs in your project.

- **Preserved History**: When using LLM (ChatGPT), Interweb preserves the conversation history, provides a simple interface for building and maintaining conversational interactions with AI models.

- **Blazingly fast**: Interweb is built on top of Quarkus Reactive, compiled into native executable, and can handle thousands of requests per second.

- **Save quota**: Interweb caches responses from data providers, reducing the number of API calls and saving your quota.

## Use Cases

Interweb can be used in a wide range of applications, but was designed with the following use cases in mind:

- **Chatbots**: Build intelligent chatbots that can answer questions, provide recommendations, and engage in natural conversations with users. Everything you can do with ChatGPT, but conversation history is included.

- **Information Retrieval**: Quickly search and retrieve information from multiple data providers without the hassle of integrating each API individually.

## Implemented Providers (Connectors)

Interweb currently supports the following data providers:

1. **Bing**:
   - Search: the web for images, videos, news, and more.
   - Suggest: get related queries to enhance user search experience.
2. **Flickr**:
   - Search: for photos and images.
   - Describe: a media resource by url.
3. **Giphy**:
   - Search: for variety of gifs in one of the largest gif libraries.
4. **Google**:
   - Suggest: access related search queries from one of the world's leading search engines.
5. **Ipernity**:
   - Search: discover photos and images within one of the largest non-commercial clubs.
   - Describe: obtain photo information using its url.
6. **OpenAI**:
   - Interact with OpenAI's ChatGPT for natural language understanding and generation.
7. **SlideShare**:
   - Search: find presentations and documents for various topics.
8. **Vimeo**:
   - Search: locate videos created by creative content creators.
   - Describe: obtain video information using its url.
9. **YouTube**:
   - Search: for videos in the largest video hosting platform.
   - Describe: obtain detailed information about a video using its url.

## How to Use Interweb

### Using the Docker Image

Interweb is available as a Docker image, making it easy to deploy and use in your projects. Follow these steps:

1. **Pull the Docker Image**:

   ```shell
   docker pull ghcr.io/l3s-learnweb/interweb:latest
   ```

2. **Run the Docker Container**:

   ```shell
   docker run -p 8080:8080 --env-file ./path/to/.env ghcr.io/l3s-learnweb/interweb:latest
   ```

   Replace `./path/to/.env` with the path to your environment file.
   Make sure your configuration file contains the necessary API keys and settings for the supported connectors.
   An example can be found in the [example.env](./interweb-server/example.env) file in the interweb-server subproject.

3. **Access the API spec**:

   You can now read the API specification at [http://localhost:8080/](http://localhost:8080), or you can now make a requests to Interweb using e.g.:

   ```
   GET http://localhost:8080/suggest?q=hello+world
   ```

   For more advanced usage, you need to create an access token and use it in your requests.

## Contributing

We welcome contributions from the community. If you have ideas for new providers, features, or improvements, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
