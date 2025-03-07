# GroqImageAnalyser

GroqImageAnalyser is a Kotlin-based web application built with Ktor, HTMX, Thymeleaf, and Tailwind CSS. It allows users to upload images and get detailed descriptions using the [GroqClientApiKotlin](https://github.com/gazolla/GroqClientApiKotlin) library.

<div style="text-align: center;">
<img src="groqimageanalyser.gif" width="280" height="350" alt="Descrição do GIF">
</div> 

## Features

- Upload images (JPG, PNG, GIF) up to 10MB.
- Detailed image analysis using the Groq API.
- Responsive and user-friendly UI with Tailwind CSS.
- Asynchronous updates and partial page rendering with HTMX.
- Server-side rendering with Thymeleaf templates.

## Technologies Used

- **Kotlin**: Main programming language.
- **Ktor**: Web server framework.
- **HTMX**: For dynamic and partial page updates.
- **Thymeleaf**: Template engine for rendering HTML.
- **Tailwind CSS**: For styling the frontend.
- **GroqClientApiKotlin**: Library to interact with the Groq API.

## Setup and Installation

### Prerequisites

- JDK 17 or higher
- Gradle
- Groq API Key (set as an environment variable `GROQ_API_KEY`)

### Clone the Repository

```bash
git clone https://github.com/gazolla/GroqImageAnalyser.git
cd GroqImageAnalyser
```

### Configure Environment Variables

Create an `.env` file in the project root:

```bash
GROQ_API_KEY=your_groq_api_key_here
```

### Build and Run the Application

```bash
./gradlew build
./gradlew run
```

The application will be available at [http://localhost:8080](http://localhost:8080).

## Project Structure

- **src/main/kotlin/com/gazapps**: Kotlin source files.
- **src/main/resources/templates**: Thymeleaf templates (`index.html`, `reset.html`).
- **uploads**: Directory for uploaded files.

## Key Endpoints

- **GET /** - Home page for uploading images.
- **POST /upload** - Uploads an image and returns its description.
- **GET /reset** - Resets the form and clears results.

## How It Works

1. User uploads an image via the form.
2. The server saves the image in the `uploads` directory.
3. Groq API generates a description of the image.
4. The description is displayed without reloading the page using HTMX.

## Dependencies

- [GroqClientApiKotlin](https://github.com/gazolla/GroqClientApiKotlin)
- Ktor
- Thymeleaf
- Tailwind CSS
- HTMX
- Jackson (for JSON serialization)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

- [Sebastião Gazolla C Jr](https://github.com/gazolla)