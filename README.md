# Movie Management Application 

## Overview
A **full-stack web application** where:
-  **Admins** manage movies using the OMDB API 
-  **Users** browse, search, and rate movies
- *(First registered user becomes admin automatically)*

---

## Key Features

###  Admin Dashboard
-  **Role-Based Access Control** (First user = Admin)
-  **OMDB API Integration** - Search movies
-  **Add/Remove Movies** (Single or batch operations)
-  **Database Management** (MySQL)
-  **Pagination Support**

###  User Dashboard
-  **Browse Movies** 
-  **Rate Movies** (1-5 stars)
-  **Advanced Search**
-  **Movie Details** 
---

## Tech Stack 
| Layer       | Technologies                  |  
|-------------|-------------------------------|
| **Frontend**| Angular, Bootstrap|
| **Backend** | Spring Boot, Java 17          |
| **Database**| MySQL                         |
| **Tools**   | Swagger, Maven, Postman       |

---

## Getting Started

### Prerequisites
- Java 17+, Maven, Node.js, Angular CLI
- MySQL Database
- OMDB API Key *(Get it from [omdbapi.com](https://www.omdbapi.com/))*

### Database Setup
1. Create MySQL database:
   ```sql
   CREATE DATABASE movie_db;
2. Configure database (src/main/resources/application.properties):

## Run the Application

### 🔹 Backend (Spring Boot)
    git clone https://github.com/Shay-maa/movie-management-app.git
    cd movie-management-app/Movies-api
    mvn clean install
    mvn spring-boot:run


### 🔹 Frontend (Angular)
    git clone https://github.com/Shay-maa/movie-management-app.git
    cd movie-management-app/movies-ui
    npm install
    ng serve

## First-Time Admin Setup

1. First registration automatically gets admin privileges
2. Subsequent users will have regular user privileges by default
