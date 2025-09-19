# 🚀 TokenGate - Dual API Rate Limiter

TokenGate is a secure and high-performance Spring Boot API that demonstrates a dual-algorithm rate limiting strategy using **Token Bucket** and **Fixed Window Counter** algorithms. It leverages **Redis** for efficient caching and distributed rate limiting, and is fully containerized with **Docker**.

<img width="1090" height="848" alt="image" src="https://github.com/user-attachments/assets/672f3e81-1261-424f-ba59-2109061f67af" />

<img width="1090" height="616" alt="image" src="https://github.com/user-attachments/assets/caa7367e-10af-4393-a715-584955bb08f3" />


## ✨ Features

- **JWT Authentication**: Secure endpoints using JSON Web Tokens.
- **Redis Caching**: Reduces database latency by over 95%.
- **Dual Rate Limiting**: Protects the API from overuse and abuse.
- **Fully Containerized**: Includes `docker-compose` for the Spring Boot App, PostgreSQL, and Redis.
- **Unit & Integration Tested**: High code coverage using JUnit and Mockito.

## 🛠️ Tech Stack

- Java & Spring Boot
- PostgreSQL
- Redis
- Docker & Docker Compose
- Maven
- JUnit & Mockito

## ▶️ How to Run

The entire backend stack (Spring Application, PostgreSQL database, and Redis) is containerized.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/bagwanmisbah/tokengate.git
    cd tokengate
    ```
2.  **Start the application using Docker Compose:**
    ```bash
    docker-compose up
    ```
   
### 2. Run the Frontend

1.  **Navigate to the frontend directory:**
    ```bash
    # From the project's root directory
    cd frontend
    ```
2.  **Install dependencies and start the development server:**
    ```bash
    npm install
    npm run dev
    ```

<img width="1090" height="781" alt="image" src="https://github.com/user-attachments/assets/e606fc2e-c83d-48b1-b475-4730d84ef844" />

<img width="1090" height="630" alt="image" src="https://github.com/user-attachments/assets/4cd83dd1-b305-4f8a-840d-fc90fe3fa55c" />


The React application will now be running and accessible at `http://localhost:5173`.
This command will build the images and start all the containers. The Spring Boot application will be running and connected to the database and Redis cache automatically. The API will be available at `http://localhost:8080`.
