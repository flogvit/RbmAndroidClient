# RbmAndroidClient
Android client for the RBM backend

Install with gradle

```gradle
repositories {
    maven {
        url 'https://github.com/flogvit/RbmAndroidClient/raw/master/release/'
    }
}

dependencies {
//    ..
    compile 'com.github.nkzawa:engine.io-client:0.5.0'
    compile 'com.cellarlabs:rbmandroidclient:0.1.3@aar'
}
```

You then add the creation of client(s), either to your Activity or .App

```java
RbmAndroidClient rbmClient = new RbmAndroidClient("ws://<server>:<port>");


```

Setup must be done in all activities. If you register any listeners, you need to use the RBM_TAG.
It should be unique for each Activity, and must be canceled when it stops, or else the
garbage collector will not release the Activity

```java

private Integer RBM_TAG = 0;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RBM_TAG = rbmClient.getUniqueTag();
    // ...
}

@Override
protected void onStop () {
    super.onStop();
    if (rbmClient != null) {
        rbmClient.cancelCallbacks(RBM_TAG);
    }
}
```

Sending a message is done this way

```java

Request req = new Request().withCommand("your.command");
RbmClient.send(req);

```

You can get the response in two ways. The first way will
catch all responses to the "your.command" requests

```java

rbmClient.on("your.command", new Listener(RBM_TAG) {
    @Override
    onResponse(Request req) {
       // Do your thing with req
    }
});

```

The second will only catch the response to the one you just sent

```java

Request req = new Request().withCommand("your.command");
rbmClient.send(req, new Listener(RBM_TAG) {
   @Override
   onResponse(Request req) {
      // Do your thing with req
   }
});

```

Example of using populate to execute sub commands and use the results

```java
        Request req = new Request()
                .withCommand("math.add")
                .withParam(
                        new Param()
                                .set(
                                        new Request()
                                                .withCommand("number.get")
                                                .withParam("number", 2)
                                )
                                .addMapping("number", "a")
                )
                .withParam(
                        new Param()
                                .set(
                                        new Request()
                                                .withCommand("number.get")
                                                .withParam("number", 3)
                                )
                                .addMapping("number", "b")
                );

```

or a bit more compact

```java
Request req = new Request()
   .withCommand("math.add")
   .withParam(new Param().set(new Request().withCommand("number.get").withParam("number", 2))
        .addMapping("number", "a"))
   .withParam(new Param().set(new Request().withCommand("number.get").withParam("number", 3))
        .addMapping("number", "b"));
```