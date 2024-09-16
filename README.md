# Project: Rent & Housing Platform

## Project Introduction:
The Rent & Housing Platform is a web application designed to streamline the housing search process for students. Whether they are looking for shared apartments, individual rental properties, or housing information, our platform offers a seamless and efficient user experience
Key features include user registration and management, posting and searching rental listings, and collecting rental details.

## Objectives

The primary objectives of this web application are to:
- User Registration and Profile Management: Students can create accounts, manage their profiles, and save listings.
- Rental Listings: Users can post, browse, and filter rental properties based on various criteria such as location, price, and property type.
- Advanced Search and Filtering: Our platform allows students to search properties using filters like price range, proximity to universities, and property amenities.
- Real-time Performance Enhancements: our platform ensures fast and efficient data access, providing users with quick responses and real-time updates.
- Scalable and Reliable Backend: Built using Java and powered by DynamoDB, the system is designed to handle large volumes of data while maintaining performance, ensuring the platform can grow as more users join.

## Tech Stack

- **Frontend**: Vue.js for responsive and dynamic user interfaces.
- **Backend**: Java (AWS Lambda) for handling business logic and server-side processes.
- **Database**: AWS DynamoDB Service for data storage and management.

## Installation and Setup

### Prerequisites
- **Java**: Version 17 or higher
- **Vue.js**: Version 3.53 or higher
- **Dynamodb**: AWS DynamoDB Service

### Steps
1. **Clone the Repository**
   ```bash
   git clone https://github.com/BUMETCS673/seprojects-cs673f24a2team2.git
   ```
2. **Frontend(Vue.js)**
   ```bash
   cd code/frontend
   npm install
   ```
3. **Backend(Java Spring Boot)**
   ```bash
   cd code/backend
   1. install sam cli(You will need an AWS account and properly configured AWS credentials.) 
   2. sam local invoke <function name> --debug-port <your_port> --event <your_test_event.json>
   ```
4. **Database Configuration:**
- Make sure your AWS credentials are set up to access DynamoDB.
     
5. **Access the Application**
- Frontend: Open your browser and navigate to http://localhost:8081 to access the platform.
- Backend API: The backend is running at http://localhost:8080.

## Team Introduction:
- Yongjing Wu (Team Leader)
- Xueqi Zhou (Requirement Leader)
- Yueyang He (Design and Implementation Leader) 
- Rundong Zhong (Configuration Leader)
- Xiang Zhang (QA Leader)
- Jiacheng Ding (Security Leader)

## Related Resources:
- Jira: https://rentandhousing.atlassian.net/jira/software/projects/SCRUM/boards/1
- Google Drive: https://drive.google.com/drive/u/3/folders/1tMHmvLavN5HOh6yXjT-2AVVOctPHYFme
- Github: https://github.com/BUMETCS673/seprojects-cs673olf24team2
