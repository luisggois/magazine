# Magazine

Magazine is a Restful Web Service intended as a micro-service for a bigger application. 
The service enables authenticated users to interact with publications related to that magazine.
Interaction is limited to their authorization role.

## Security

The application is protected with jwt authentication 😈

**Required -** User with ROLE_ADMIN needs to be placed in database before starting app
```java
INSERT INTO auth_users(username,password,enabled)
	values('admin','encoded_password_using_bcrypt',TRUE);
	
INSERT INTO auth_roles(authority)
	values('ROLE_ADMIN');		
		
INSERT INTO auth_users_roles(users_id,roles_id)
	values(1,1);
```

## Usage

```bash
cd project_dir
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Current task lists

- [ ] Finish tests for publication package
- [ ] Update controllers logging

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
