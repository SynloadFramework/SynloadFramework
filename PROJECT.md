# SynloadFramework - Project Definition

## Overview

**SynloadFramework** is a Java-based modular web application framework (v1.4.8.1) created by Nathaniel Davidson. It provides an integrated server runtime that combines HTTP serving, real-time WebSocket communication, an annotation-driven event system, a plugin/module architecture with hot-reloading, an ORM-style SQL layer, inter-server communication (ServerTalk), distributed event sharing (EventShare), and optional RSA encryption — all built on top of Jetty with SPDY support.

The framework is designed so that application developers build **modules** (packaged as JAR files) that are dynamically loaded at runtime. Modules declare event handlers, HTTP endpoints, and WebSocket actions via Java annotations, and the framework wires everything together automatically.

## Core Capabilities

| Capability | Description |
|---|---|
| **HTTP Server** | Jetty-based HTTP handler with annotation-driven routing (`@Get`, `@Post`, `@Http`), static file serving, module resource serving, byte-range requests, and MIME type detection |
| **WebSocket Server** | Real-time bidirectional communication via Jetty WebSocket with JSON-serialized request/response, message queuing, per-connection session state, and flag-based permissions |
| **Event System** | Publish/subscribe event bus with annotation-based handler registration (`@WSEvent`, `@Event`), supporting both synchronous and threaded event dispatch |
| **Module System** | Dynamic JAR-based plugin loading with hot-reload detection, custom classloading, module metadata (`module.ini`), embedded web resources (`www/`), and a module registry |
| **SQL ORM** | Reflection-based ORM (`Model` base class) with annotation-driven column mapping, auto-migration, QuerySet API, caching, and relationship support (`@HasOne`, `@HasMany`) |
| **ServerTalk** | TCP socket-based inter-server communication protocol with pluggable connection types (file transfer, commands, information exchange) and shared key authentication |
| **EventShare** | Distributed event bus allowing multiple SynloadFramework instances to share and proxy events across servers, with reconnection support |
| **Security** | RSA public-key encryption for WebSocket traffic (via BouncyCastle), AES utilities, spam detection, IP banning, and public key server registration |
| **Graph Database** | Optional Neo4j embedded graph database integration |

## Architecture Diagram

```
+===========================================================================+
|                         SynloadFramework Runtime                          |
|                      (SynloadFramework.java - main)                       |
+===========================================================================+
|                                                                           |
|  +------------------+    +-----------------+    +----------------------+  |
|  |   CLI Parser     |    |  Configuration  |    |     Logging (Log)    |  |
|  | (commons-cli)    |    |  (config.ini)   |    |     (log4j)          |  |
|  +------------------+    +-----------------+    +----------------------+  |
|                                                                           |
|  +=============================+    +==================================+  |
|  |     Jetty Server (SPDY)     |    |         Module System            |  |
|  |  ExecutorThreadPool 50-200  |    |                                  |  |
|  +--------------+--------------+    |  +----------------------------+  |  |
|  |              |              |    |  |     ModuleLoader           |  |  |
|  |  HTTP        | WebSocket   |    |  | - JAR scanning & loading   |  |  |
|  |  Handler     | Handler     |    |  | - Custom ClassLoader       |  |  |
|  |              |              |    |  | - Hot-reload (CheckNewJar) |  |  |
|  +--------------+--------------+    |  +----------------------------+  |  |
|                                     |  |     ModuleRegistry         |  |  |
|                                     |  | - Loaded module tracking   |  |  |
|                                     |  +----------------------------+  |  |
|                                     |  |     ModuleResource         |  |  |
|                                     |  | - Embedded www/ assets     |  |  |
|                                     +==================================+  |
|                                                                           |
|  +=====================================================================+  |
|  |                        Event System                                 |  |
|  |                                                                     |  |
|  |  +---------------------+     +----------------------------------+   |  |
|  |  |  HandlerRegistry    |     |      EventPublisher              |   |  |
|  |  | Map<Class,          |     | - raise(event, target)           |   |  |
|  |  |   List<EventTrigger>|---->| - Matches triggers by type       |   |  |
|  |  |  >                  |     | - Invokes via reflection         |   |  |
|  |  +---------------------+     | - Routes to local or EventShare  |   |  |
|  |                              +----------------------------------+   |  |
|  |                                                                     |  |
|  |  Annotation-Driven Registration:                                    |  |
|  |  @WSEvent(method, action) --> WebSocket event handlers              |  |
|  |  @Event(name, description) --> Custom event handlers                |  |
|  |  @Get / @Post / @Http     --> HTTP route handlers                   |  |
|  |  @Module                  --> Module class declaration              |  |
|  |  @SQLTable                --> SQL table registration                |  |
|  +=====================================================================+  |
|                                                                           |
|  +=========================+    +======================================+  |
|  |     HTTP Layer          |    |       WebSocket Layer                |  |
|  |                         |    |                                      |  |
|  |  HTTPRouting            |    |  WSRouting                           |  |
|  |  - Route map            |    |  - Delegates to EventPublisher      |  |
|  |  - Regex path matching  |    |                                      |  |
|  |  - Static file serving  |    |  WSHandler (@WebSocket)              |  |
|  |  - Module resource      |    |  - Per-connection session data       |  |
|  |    serving              |    |  - Message queue + send thread       |  |
|  |                         |    |  - Flag-based permissions            |  |
|  |  HTTPRegistry           |    |  - Optional RSA encryption           |  |
|  |  - Scans @Get/@Post     |    |  - JSON Request/Response protocol    |  |
|  |    annotations          |    |                                      |  |
|  +=========================+    +======================================+  |
|                                                                           |
|  +=========================+    +======================================+  |
|  |     SQL / ORM Layer     |    |       Security Layer                 |  |
|  |                         |    |                                      |  |
|  |  Model (base class)     |    |  PKI                                 |  |
|  |  - _find(), _insert()   |    |  - RSA key generation                |  |
|  |  - _save(), _delete()   |    |  - Encrypt/Decrypt (chunked RSA)    |  |
|  |  - _related()           |    |  - Public key server sync            |  |
|  |  - Result caching       |    |                                      |  |
|  |                         |    |  AesUtil                              |  |
|  |  QuerySet               |    |  - AES-CBC encryption                |  |
|  |  - Fluent query builder |    |                                      |  |
|  |  - .all(), .count()     |    |  SpamDetection                       |  |
|  |                         |    |  - Rate limiting                      |  |
|  |  SQLRegistry            |    |                                      |  |
|  |  - Auto table creation  |    |  AccessViolation                     |  |
|  |  - Schema versioning    |    |  - IP ban management                 |  |
|  |                         |    |                                      |  |
|  |  Annotations:           |    +======================================+  |
|  |  @SQLTable, @StringCol  |                                             |
|  |  @BigIntegerCol, etc.   |                                             |
|  +=========================+                                             |
|                                                                           |
|  +=========================+    +======================================+  |
|  |    ServerTalk System    |    |      EventShare (Distributed)        |  |
|  |    (TCP Inter-Server)   |    |                                      |  |
|  |                         |    |  EventShare                           |  |
|  |  ServerTalk (server)    |    |  - Cross-server event bus             |  |
|  |  - TCP socket listener  |    |  - Shares local handlers remotely    |  |
|  |  - Key authentication   |    |  - Proxies events between servers    |  |
|  |                         |    |  - ExpiringMap for request tracking   |  |
|  |  Client (connection)    |    |  - Auto-reconnection                  |  |
|  |  - Read/Write threads   |    |                                      |  |
|  |  - JSON serialization   |    |  Connection Types:                    |  |
|  |                         |    |  - ESSharedEvent (register)           |  |
|  |  Connection Types:      |    |  - ESRemoveEvent (unregister)         |  |
|  |  - syn-fp (file xfer)   |    |  - ESPush (event dispatch)            |  |
|  |  - syn-cmd (commands)   |    |  - ESData (response routing)          |  |
|  |  - syn-info (info)      |    |                                      |  |
|  +=========================+    +======================================+  |
|                                                                           |
|  +==================================+                                    |
|  |   Optional: Neo4j Graph DB       |                                    |
|  |   (embedded, config-driven)      |                                    |
|  +==================================+                                    |
|                                                                           |
+===========================================================================+

                         External Module JARs
                    (loaded from modules/ directory)

    +----------------+  +----------------+  +----------------+
    |  Module A.jar  |  |  Module B.jar  |  |  Module C.jar  |
    |                |  |                |  |                |
    | module.ini     |  | module.ini     |  | module.ini     |
    | @Module class  |  | @Module class  |  | @Module class  |
    | @WSEvent       |  | @Get/@Post     |  | @SQLTable      |
    | @Event         |  | @WSEvent       |  | @WSEvent       |
    | www/ assets    |  | @SQLTable      |  | www/ assets    |
    +----------------+  +----------------+  +----------------+
```

## Request Flow

```
                    Client (Browser)
                         |
            +------------+------------+
            |                         |
        HTTP Request            WebSocket Message
            |                         |
            v                         v
      HTTPHandler              WebsocketHandler
            |                         |
            v                         v
      HTTPRouting.page()        WSHandler.onWebSocketText()
            |                         |
            |                    [Optional RSA Decrypt]
            |                         |
            +-- Static file?          v
            |   Yes -> serve file   JSON -> Request object
            |                         |
            +-- Module resource?      v
            |   Yes -> serve asset  WSRouting.page()
            |                         |
            +-- @Get/@Post match?     v
            |   Yes -> invoke via   EventPublisher.raiseEvent()
            |          reflection     |
            |                    +----+----+
            +-- No match?        |         |
                404 / fall       |         |
                through       Local     EventShare
                             Handler    (remote server)
                               |         |
                               v         v
                           Response    ESPush -> Remote
                           via WS      -> ESData -> Response
```

## Package Structure

```
com.synload.eventsystem          Core event bus (EventPublisher, HandlerRegistry, EventTrigger)
  .events                        Built-in event types (Request, Web, Connect, Close, Encrypt, etc.)
  .events.annotations            Event annotations (@Event, @ES)

com.synload.framework            Main framework entry point, config, logging
  .elements                      WebSocket response DTOs (Connected, EncryptAuth, LoginBox, etc.)
  .forms                         Form abstraction (Form, Text, Password, Select, Checkbox, etc.)
  .handlers                      Request/Response/Data model classes
  .http                          HTTP routing, handler, registry, annotations (@Get, @Post, @Http)
  .http.modules                  HTTPResponse, UploadedFile
  .js                            JavaScript file management
  .modules                       Module loading, registry, resource management, annotations (@Module)
  .security                      PKI, AES, spam detection, access control
  .sql                           ORM layer (Model, QuerySet, SQLRegistry, annotations)
  .ws                            WebSocket routing, handler, annotations (@WSEvent, @Perms)

com.synload.talksystem           Inter-server TCP communication (ServerTalk, Client)
  .commands                      Command protocol type
  .connectionCheck               Ping/Pong health checks
  .eventShare                    Distributed event sharing system
  .filetransfer                  File transfer protocol type
  .info                          Information exchange protocol type
  .statistics                    Transmission statistics
  .systemMessages                Error/unrecognized message types

org.xeustechnologies.jcl         Vendored JarClassLoader library (custom classloading)
```

## Key Dependencies

| Dependency | Purpose |
|---|---|
| Jetty 9.3 (+ SPDY) | HTTP/WebSocket server |
| Jackson 2.7 | JSON serialization/deserialization |
| MySQL Connector 5.1 | Database connectivity |
| Neo4j 3.0 (server-api) | Optional graph database |
| BouncyCastle 1.54 | RSA/crypto provider |
| Apache HttpClient 4.5 | Outbound HTTP requests (PKI key sync) |
| Commons CLI 1.3 | Command-line argument parsing |
| Log4j 1.2 | Logging |
| Guava 19 | Utility library |
| Gson 2.6 | Additional JSON support |
| ExpiringMap 0.5 | TTL-based map for EventShare requests |
| CGLib 2.0 | Dynamic proxy generation (module system) |
| Pi4J 1.0 | GPIO extension (IoT/Raspberry Pi support) |
| KefirBB 1.3 | BBCode parsing |

## Configuration

The framework is configured via `config.ini` (auto-generated on first run) with properties including:

- `port` - HTTP/WebSocket server port
- `dbenabled`, `jdbc`, `dbuser`, `dbpass` - MySQL connection
- `modulePath` - Directory for module JARs
- `encrypt`, `encryptLevel` - RSA encryption toggle and key size
- `serverTalkEnable`, `serverTalkPort`, `serverTalkKey` - Inter-server communication
- `eventShareServers` - Distributed event bus connections
- `graphDBEnable`, `graphDBPath`, `graphDBConfig` - Neo4j settings
- `siteDefaults`, `enableUploads`, `uploadPath`, `maxUploadSize` - Web features
- `loglevel`, `debug` - Logging configuration

## Build & Run

- **Build System**: Gradle (Java 7+ source compatibility)
- **Entry Point**: `com.synload.framework.SynloadFramework`
- **CLI Options**: `--sitepath`, `--config`, `--port`, `--id`, `--cb`, `--scb`
- **Artifact**: Published as `com.synload:synloadframework:1.4.8.1`
