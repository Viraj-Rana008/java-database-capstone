# Section 1: Architecture summary
The architecture will be divided into three tiers: presentation tier, application teir, data tier.
Presentation tier: The user interface will use both MVC and REST controllers. Thymleaf templates would be used for Admin dashboard and Doctor dashboard. REST APIs would be used for Patient dashboard, Appointment, Patient record.

Application tier: Layer where controllers, services, and business logic is implemented. This layer would be based on Spring Boot framework for modularity and scalability. It would be responsible for routing all the request from UI and delegating requests to appropraite data repositories. 

Data tier: Data will stored in two databases - MySQL for structured data of patient, doctor, apointment, admin records and MongoDB for unstructured prescription documents.

# Section 2: Numbered flow

- Events get triggered at UI. Calls are made for fetching updated Thymleaf html pages or REST APIs.
- Events are captured by Thymleaf controller or REST controller. They delegate tasks to the service layer get the updated html or API response.
- Service layer execute the services and related business logic.
- Requests are made to database repositories MySQL or MongoDB for fetching data.
- Database repositories query the underlying databases.
- Once data is retrieved from the databases it is mapped to Java model classes the application can work with.
- Updated model state is passed to view layer to reflect change at the UI.
