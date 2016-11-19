# WebChat

A web chat implementation, that can run both with an in-memory user repository or Redis as backend for users and messaging. Application is developed in Spring boot with WebSocket's.

## In-memory repository execution

Running with the in-memory user repository reduces the deployment complexity, but sacrifice the scalability, in order to allow communication between users, all users must be connected to the same http server.

[![In memory mode](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/without_redis.png)]()

## Redis repository execution

Running with the Redis user repository increse the scalability, this deployment strategy allow communication between users even if they are connected to different http servers, this allow the web chat http server to deployed across multiple servers.

To start the application in this mode, the value "redis" must be passed on the app.chat.channel parameter, like:
```
java -jar target\WebChat-1.0-SNAPSHOT.jar --server.port=9090 --management.port=9000 --app.chat.channel=redis
```

[![In memory mode](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/with_redis.png)]()

## Room list and room

Multiple rooms are also supported and each room have its own user list, inside the meeting user, users can exchange private messages.

[![Room](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/room.png)]()

[![Room list](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/roomlist.png)]()


