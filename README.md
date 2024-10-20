
# Weather Application

This Weather Application is a Java Spring Boot project that fetches real-time weather data for multiple cities, summarizes daily weather conditions, and sends alerts based on temperature thresholds. It utilizes a RESTful API to provide weather data and notifications.


## Features

- Fetch weather data for multiple cities.
- Calculate daily weather summaries (average, maximum, and minimum temperatures).
- Send email alerts if the temperature exceeds a certain threshold.
- Use of Spring Boot to create a RESTful API.

## Technologies

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- REST API
- Lombok
- JSON
- JPA Repository
- Maven
- MySQL
- JavaMailSender for email notifications

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- A relational database MySQL
- An email account to send alerts (configured in the application properties)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/weather-application.git
   cd weather-application
   ```

2. Build the project:

   ```bash
   mvn clean install
   ```

3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Fetch Weather Data for All Cities

- **Endpoint:** `/weather/fetch`
- **Method:** GET
- **Description:** Retrieves weather data for all configured cities.

### Daily Weather Summary

- **Endpoint:** `/weather/summary/{city}`
- **Method:** GET
- **Description:** Fetches the daily weather summary for the specified city.

### Check Weather Alert

- **Endpoint:** `/weather/alert/{city}`
- **Method:** GET
- **Description:** Checks if there are any weather alerts for the specified city.
