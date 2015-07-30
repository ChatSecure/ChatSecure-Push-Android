#PushSecureDemo-Android

This is a demo [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Android client.

## Setup ChatSecure Push Server and Google Cloud Messaging

1. Clone and setup the [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Django project.

2. Register a Google Cloud Messaging Application with [Google Developers](https://developers.google.com/mobile/add)

    At the conclusion of the registration process you'll be presented with a `Server API Key` and a `google-services.json` file.

3. Copy the GCM `Server API Key` to `./push/push/local_settings.py` in the ChatSecure Push Server Django project. Copy `google-services.json` to this project's `./example` directory.

## Using the SDK

These are preliminary notes and do not represent the final API. 
TODO : Traditional callback API without lambdas

Currently, you must include ChatSecure Push as a submodule. Releases will be published as Maven artifacts.

### 0. Get the SDK

Add this repository as a git submodule:

```
$ cd your/project/root
$ git submodule add https://github.com/ChatSecure/ChatSecure-Push-Android.git ./submodules/chatsecure-push/
```

Edit your project's root `settings.gradle`:

```groovy
include ':myapp', ':submodules:chatsecure-push:sdk'
```

Edit your application module's `build.gradle`:

```groovy
...
dependencies {
    compile project(':submodules:chatsecure-push:sdk')
}
```


### 1. Create a PushSecureClient

```java
PushSecureClient client = new PushSecureClient("https://chatsecure-push.herokuapp.com/api/v1/");
```

### 2. Register a user account

```java
client.authenticateAccount(requiredUsername, requiredPassword, optionalEmail)
      .subscribe(account -> // Authenticated Account,
                 error -> // an error occurred);
```

### 3. Use a GCM token to register a pushable device with ChatSecure Push

```java
// Retrieve your GCM token as requiredGcmToken
// See [Google's example](https://github.com/googlesamples/google-services/blob/e06754fc7d0e4bf856c001a82fb630abd1b9492a/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/RegistrationIntentService.java#L54)
client.createDevice(requiredGcmToken, optionalName, optionalDeviceId)
      .subscribe(device -> // Created Device,
                 error -> // an error occurred);
```

### 4. Request a push token which represents push access to your device

```java
client.createToken(requiredDevice, optionalName)
      .subscribe(token -> // Created PushToken,
                 error -> // an error occurred);
```
### 5. Share your push token

We recommend sharing a push token with a *single* recipient.

### 6. Send a push message to a push token.

```java
client.sendMessage(requiredPushTokenString, optionalData)
      .subscribe(message -> // Sent Message,
                 error -> // an error occurred);
```

### 7. Parse incoming ChatSecure Push GCM Messages

See [Google's Example](https://github.com/googlesamples/google-services/blob/e06754fc7d0e4bf856c001a82fb630abd1b9492a/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/MyGcmListenerService.java) for a complete `GcmListenerService` implementation. Below we include the additions necessary to parse ChatSecure Push messages.
    
```java
...
import org.chatsecure.pushsecure.gcm.PushParser;
import org.chatsecure.pushsecure.gcm.PushMessage;

public class MyGcmService extends GcmListenerService {

    PushParser parser = new PushParser();

    @Override
    public void onMessageReceived(String from, Bundle data) {

        PushMessage push = parser.onMessageReceived(from, data);

        if (push != null)
            Log.d("GotPush", "Received '" + push.payload + "' via token: " + push.token);
    }
    ...
}
```
