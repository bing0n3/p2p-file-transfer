# p2p-file-transfer

## How to Run 

```java
java p2p
```

Connect 

```
p2p> connect <ip> <port>
```

Get 

```
p2p> get <filename>
```

Leave
```
p2p> leave
```

Exit
```
p2p> Exit
```

## Handle Duplicate Intermediate query message

The releated code is in `Utils/QueryAction.java`. I build a static `Set` to handle it. If we see the query message before, we just ignore it. Because we see it means we aldready broadcast the same query message to our neighboor, and each query message have thir own id, it is impossible to repeate, although a server request the same file. We use uuid as our id generator, so it is amolst impossible to have the same query message from the same server.
