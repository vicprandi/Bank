# Bank
Bank Project for Zup Innovation
-

A project developed for the training plan at Asgard Squad. I used Java for this project in general, because that's the language I feel more confortable about. The framework used was Spring Boot. 
The project was first created at spring.io website, which is a website that helps you to create the project base.

The main request was to simulate a bank application with some operations, as:
- Deposit money.
- Withdraw money.
- Transferm money between accounts.
- Show the balance.
- Show the exctract with all transactions.

Technologies used at Bank Project
-
- Java 
- Spring Boot
- Postgres SQL (via Docker Compose)
- Flyaway
- Maven
- Postman


How can you use it?
-

After you clone the github, you can access the bank package and run the command:
`docker-compose up`

There are some features inside this project that you can test via Postman, such as:

1. Client
- POST: Register Client `localhost:8080/customers`
- DELETE: Delete Client `localhost:8080/customers/delete/{clientCpf}`
- PUT: Update Client by CPF (body request) `localhost:8080/customers/update`
- GET: Get a specific customer by CPF `localhost:8080/customers/{clientCpf}`
- GET: Get all customers `localhost:8080/customers`
2. Account
- POST: Register Account by CPF `localhost:8080/accounts/{clientCpf}`
- DELETE: Delete Account by AccountId `localhost:8080/accounts/delete/{id}`
- GET: Get a specific account by AccountId  `localhost:8080/customers/{id}`
- GET: Get all accounts `localhost:8080/accounts`
3. Transactions (amount is a number like= 100.00)
- POST: Deposit Money `localhost:8080/transaction/deposit/{accountNumber}?amount={amount}`
- POST: Withdraw Money `localhost:8080/transaction/withdraw/{accountNumber}?amount={amount}`
- POST: Transfer Money between Accounts  `localhost:8080/transaction/transfer?amount={amount}&originAccountNumber={originAccountNumber}}&destinationAccountNumber={destinationAccountNumber}`
- GET: Bring all transactions `localhost:8080/transaction`
- GET: Bring specific transaction of a Client by ClientId `localhost:8080/transaction/{clientId}`

Things to remember:
- The id is auto generated.
- When you create a account, the accountNumber is autogenerated.
- You cannot put letters or special characteres at CPF, only 11 numbers.
- You cannot transfer money you don't have.
- You cannot withdraw money you don't have.
- You cannot transfer money between the same accounts.

Project developed by: victoria.moreira@zup.com.br
-
