# docker-dbwrapper
Repo for database query wrapper (RESTful APIs)


Reaching these APIs
1. All the APIs are defined with a URL and can be supplied with one boolean parameter (true/false), to indicate slow query.
2. Example normal query URL: http://localhost:8080/dbwrapper/query/execute//join/false
3. Example slow query execution: http://localhost:8080/dbwrapper/query/execute//join/true
4. The application currently connects to a local instance of MySQL database on port 3306 with default username and no password and will be configurable. 
