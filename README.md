# WebChat

A web chat implementation, that can run both with an in-memory user repository or Redis as backend for users and messaging.

## In-memory repository execution

Running with the in-memory user repository reduces the deployment complexity, but sacrifice the scalability, in order to allow communication between users, all users must be connected to the same http server.

[![In memory mode](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/without_redis.png)]()

## Redis repository execution

Running with the Redis user repository increse the scalability, this deployment strategy allow communication between users even if they are connected to different http servers, allow the web chat to deployed across multiple servers.

[![In memory mode](https://github.com/guilhermelabigalini/WebChat/blob/master/docs/with_redis.png)]()
