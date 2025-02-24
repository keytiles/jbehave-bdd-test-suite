
!-- these "Before" steps are executed before all scenarios
Lifecycle:
Before:
Given WireMock setup is flushed
Given the Go application controlled by bean goServiceController with the following environment variables is running: fsfile://${setupFolder}/app-startup-envvars.json

Scenario: 1  GetNotifications test - with an existing user who has "notifications" key-value stored

Given WireMock will respond the following way: 
 | reqMethod | reqUrl                                                       | reqHeaders                               | respBody                                                                                              | respContentType  | respStatusCode |
 | GET       | /api/v1/management/keyvalue/user/test-user-id1/notifications | Authorization:Basic dGVzdDEyMzpwYXNzMTIz | fsfile://${filesFolder}/getNotificationsTests/mock-test-user-id1-keyvalue-notifications-response.json | application/json | 200            |

!-- Given 90 seconds passed

When the following HTTP request is sent:
    | method | url                                                                         | headers                                  |
    | GET    | http://localhost:8080/api/v1/notifications/user/test-user-id1/notifications | Authorization:Basic dGVzdDEyMzpwYXNzMTIz |
Then the following HTTP response is returned and order does not matter and body contains:
  | status | body                                                                           | contentType      |
  | 200    | fsfile://${filesFolder}/getNotificationsTests/test-user-id1-notifications.json | application/json |
And WireMock was invoked accordingly:
  | times | reqMethod | reqUrl                                                       | headers                                  |
  | 1     | GET       | /api/v1/management/keyvalue/user/test-user-id1/notifications | Authorization:Basic dGVzdDEyMzpwYXNzMTIz |

When the following HTTP request is sent:
    | method | url                                                                             | headers                                  |
    | GET    | http://localhost:8080/api/v1/notifications/user/test-user-id1/notifications/new | Authorization:Basic dGVzdDEyMzpwYXNzMTIz |
Then the following HTTP response is returned and order does not matter and body contains:
    | status | body                                                                                   | contentType      |
    | 200    | fsfile://${filesFolder}/getNotificationsTests/test-user-id1-notifications-newonly.json | application/json |


Scenario: 2  GetNotifications test - with an existing user but who has not yet "notifications" key-value

Given WireMock will respond the following way: 
 | reqMethod | reqUrl                                                       | reqHeaders                               | respBody                                                                                                      | respContentType  | respStatusCode |
 | GET       | /api/v1/management/keyvalue/user/test-user-id1/notifications | Authorization:Basic dGVzdDEyMzpwYXNzMTIz | {"message":"Request failed - check problems","problems":[{"severity":"error","message":"There is no user!"}]} | application/json | 404            |

When the following HTTP request is sent:
    | method | url                                                                         | headers                                  |
    | GET    | http://localhost:8080/api/v1/notifications/user/test-user-id1/notifications | Authorization:Basic dGVzdDEyMzpwYXNzMTIz |
Then the following HTTP response is returned and order does not matter and body contains:
    | status | body                  | contentType      |
    | 200    | {"new":[], "seen":[]} | application/json |

When the following HTTP request is sent:
    | method | url                                                                             | headers                                  |
    | GET    | http://localhost:8080/api/v1/notifications/user/test-user-id1/notifications/new | Authorization:Basic dGVzdDEyMzpwYXNzMTIz |
Then the following HTTP response is returned and order does not matter and body contains:
    | status | body | contentType      |
    | 200    | []   | application/json |
