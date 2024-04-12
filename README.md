# Wear Again

Wear Again is an e-commerce platform dedicated to sustainable fashion, allowing users to purchase clothing with an
emphasis on environmental responsibility. The project uses Java with Spring Boot for the backend, Thymeleaf for the
frontend, Maven for dependency management, and MongoDB as a database, offering both a graphical user interface and a
command-line interface.


## Functionalities

- **User Registration and Authentication**: Users can register and manage their profiles, with support for both 
traditional and OAuth2 authentication mechanisms (Google Sign-In).

- **Catalog Management**: The application offers a dynamic catalog where items can be added, listed, updated, or 
removed. Each item includes details such as type, brand, size, price, and availability status.

- **Sales Processing**: It allows tracking of sales transactions, enabling users to record and retrieve sales 
information linked to specific items and users.

- **Search and Filter**: Users can search for available items and filter them based on type, brand, or price criteria.

- **Data Initialization**: On startup, the application can initialize the database with users, items, and sales data 
from CSV files for a ready-to-use setup.

- **CLI Support**: For non-GUI interactions, the application provides a Command-Line Interface for operations such as 
viewing items, processing sales, and managing users.

- **Security**: Implements security features like encrypted passwords and authentication failure handling.

- **Responsive UI**: Through Thymeleaf, it offers a responsive web interface for user interaction with the 
application's features.


## Requisites

Before running the application, ensure that the following prerequisites are installed on your system:

- **Java JDK**: Version 21 or higher.

- **Maven**: Maven is used for managing project dependencies.

- **MongoDB**: The application uses MongoDB as its database.

- All other dependencies, including Spring Boot starters for web development, security, OAuth2 client support, 
data management with MongoDB and Thymeleaf for server-side templating, are handled by Maven and will be automatically 
downloaded when you build the project.

- Ensure that the `JAVA_HOME` and `M2_HOME` environment variables are set up correctly to point to your Java JDK 
and Maven installations, respectively.

### Configuration

To set up your development environment, you will need to configure the application properties. The most important settings are for MongoDB and any external service integrations such as OAuth2. Below are the steps you should follow:

1. **MongoDB Atlas Configuration**:
    - Obtain your MongoDB Atlas connection string from the MongoDB Atlas dashboard.
    - Replace the `spring.data.mongodb.uri` property in the `application.properties` file with your connection string.
      Example: 
```bash
spring.data.mongodb.uri=mongodb+srv://yourusername:yourpassword@yourcluster.mongodb.net/yourdbname?retryWrites=true&w=majority
```

2. **OAuth2 Configuration** (if you are using OAuth2):
- Set the Google client ID and client secret for OAuth2 in the properties:
```bash
spring.security.oauth2.client.registration.google.client-id=yourGoogleClientId
spring.security.oauth2.client.registration.google.client-secret=yourGoogleClientSecret
```
- Obtain these from your Google Cloud Console where you have set up your OAuth2 credentials.

3. **CSV Export Path**:
- If your application supports exporting data to CSV, specify the export directory with `export.path`.
- Ensure that the path exists and your application has write permissions to that directory.

### Installation

To set up the project on your local machine, follow these steps:

1. **Clone the repository**:
   Use the following command to clone the project repository:
```bash
   git clone https://github.com/lilianagorga/wear-again
```
2. **Navigate to the project directory**:
    After cloning, move into the project directory:
```bash
cd wear-again
```

### Usage

Once the installation is done, you can run the application using the following commands:

1. **Build the project**:
   This command compiles the project and runs tests to ensure everything is set up correctly.It also packages the 
   application into an executable JAR file.
```bash
mvn package
```
> Note: mvn install can also be used for installation, but mvn package is preferred when preparing to run 
the application as it skips the installation of the package into the local repository, which is not necessary for 
running the application.

2. **Run the application**:
   To launch the Spring Boot application, execute:
```bash
mvn spring-boot:run
```
Alternatively, if you prefer to run the packaged JAR directly, use:
```bash
java -jar target/wear-again-0.0.1-SNAPSHOT.jar
```

#### For UI

After starting the application, you can access the web interface by navigating to `http://localhost:8080` in your web
browser. The web interface provides access to the following functionalities:

- **Home Page**: The default landing page of the application, accessible directly at `http://localhost:8080/`.
- **Login**: For authentication, navigate to `http://localhost:8080/login`. Here, you can log in using your credentials 
or through Google OAuth2 if you have set up OAuth2 integration.
- **User Profile**: Once authenticated, you can view and update your user profile at `http://localhost:8080/profile`.
- **Item Catalog**: View the list of available items at `http://localhost:8080/items`. Individual item details can be 
viewed by clicking on an item or navigating directly to `http://localhost:8080/items/{id}` with the item's ID.
- **Sales History**: Authenticated users can view their sales history at `http://localhost:8080/sales`.

> Note: Login is required for pages that need authentication.
Ensure you are logged in to access pages that require authentication. The application supports both traditional login 
and Google OAuth2 for convenience.


#### For CLI:

The application also provides a Command-Line Interface (CLI) for interacting with the system directly through the 
terminal. To use the CLI, ensure the application is built using Maven, and then execute the following command:

```bash
java -jar target/wear-again-0.0.1-SNAPSHOT.jar --cli
```
> Note: 
> - The CLI mode requires the application to be packaged into a JAR file. The mvn package command can be used to
build the JAR file before running the above command.
> - The --cli option is required to start the application in Command Line Interface (CLI) mode. Without specifying this 
option, the application will default to starting in Graphical User Interface (GUI) mode.

### CLI Commands

Here's a list of available commands and their functionalities:

1. **List All Items**:
    - Command: `1`
    - Description: Lists all items in the catalog, displaying details for each.

2. **Purchase Item**:
    - Command: `2`
    - Description: Allows you to purchase an item. You will be prompted to enter the item ID and user ID.

3. **Return Item**:
    - Command: `3`
    - Description: Enables returning an item. You need to enter the sale ID of the item you wish to return.

4. **Add User**:
    - Command: `4`
    - Description: Adds a new user to the system. Follow the on-screen prompts to enter user details such as ID, name, 
    surname, birthdate, address, document ID, email, username, and password.

5. **Export Available Items to CSV**:
    - Command: `5`
    - Description: Exports details of all available items to a CSV file. The file will be saved to the path specified 
    by the `export.path` property in `application.properties`.

6. **Check Item Availability**:
    - Command: `6`
    - Description: Checks the availability of a specific item. You will need to enter the item ID.

7. **Exit**:
    - Command: `0`
    - Description: Exits the CLI mode.

> Note: The commands are inputted as numbers corresponding to the action you wish to perform. Follow the on-screen 
instructions for further guidance on each command.

#### Test

This project uses [JUnit](https://junit.org/junit5/) for unit testing and 
[Spring Test](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html) for integration testing.
To run the tests, follow these steps:

1. **Run Unit Tests**: Unit tests can be run with Maven using the following command:
```bash
   mvn test
```
2. **Run Integration Tests**: Integration tests are configured to automatically use an embedded database. You can run 
them using Maven without any additional setup.
   ```bash
   mvn verify
   ```
   - **If you want to execute only the integration tests and skip the unit tests, you can use**:
   ```bash
   mvn verify -DskipUTs
   ```

### Contributing

We welcome contributions to the Wear Again project! If you're looking to contribute, here's how you can get started:

- **Fork the Repository**: Begin by forking the repository and then cloning your fork locally.
- **Make Your Changes**: After setting up your local development environment as described in the **Installation** and
**Usage** sections, feel free to make your changes.
- **Submit a Pull Request**: Once you've made your changes, push them to your fork and then submit a pull request 
to the main repository for review.
 
For more detailed information on the contribution process, refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file.

Thank you for your interest in contributing to Wear Again!

### License

This project is licensed under the MIT License.
