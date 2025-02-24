
Composite: Given Our fake AgileCore components have made some basic logs
Given the following HTTP requests are all sent successfully:
    | method | url                                                                      |
    | GET    | http://localhost:8080/make-log?logger=root&message=log-msg-1&level=debug |
    | GET    | http://localhost:8080/make-log?logger=root&message=log-msg-2&level=error |
    | GET    | http://localhost:8080/make-log?logger=root&message=log-msg-3&level=info  |
And the following HTTP requests are all sent successfully:
    | method | url                                                                        |
    | GET    | http://localhost:8081/make-log?logger=root&message=log-msg-1&level=error   |
    | GET    | http://localhost:8081/make-log?logger=root&message=log-msg-2&level=warning |
    | GET    | http://localhost:8081/make-log?logger=root&message=log-msg-3&level=info    |
    | GET    | http://localhost:8081/make-log?logger=root&message=log-msg-4&level=debug   |