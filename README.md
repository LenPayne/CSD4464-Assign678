# CPD-4414 Assignments #6, 7 and 8

This is a sample solution for an assignment to:

1. Build a Model/Controller relationship for a series of Message objects, and 
   expose it through RESTful Web Services
2. Persist that Model/Controller relationship to a database on-change
3. Expose the Message system through WebSockets

Connects to a basic database provided through the IPRO server on-campus. Any
desire to run this on localhost will require re-building the very basic table:

    CREATE TABLE message (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255),
        contents TEXT,
        author VARCHAR(255),
        senttime DATE);