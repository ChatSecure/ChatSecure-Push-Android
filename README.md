#PushSecureDemo-Android

This is a demo [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Android client.

# Requirements 

This Android demo requires a **ChatSecure Push Server** and a **Google Cloud Messaging account**.

1. Clone and setup the [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Django project. You can also test against our demo heroku instance at `https://chatsecure-push.herokuapp.com/api/v1/`.

2. Register a Google Cloud Messaging Application with [Google Developers](https://developers.google.com/mobile/add)

    At the conclusion of the registration process you'll be presented with a `Server API Key` and a `google-services.json` file.

3. Copy the GCM `Server API Key` to `./push/push/local_settings.py` in the ChatSecure Push Server Django project. Copy `google-services.json` to this project's `./example` directory.

## Using the SDK

Currently, you must include ChatSecure Push as a submodule. Releases will be published as Maven artifacts.

### 1. Get the SDK

Add this repository as a git submodule:

```
$ cd your/project/root
$ git submodule add https://github.com/ChatSecure/ChatSecure-Push-Android.git ./submodules/chatsecure-push/
```

Edit your project's root `settings.gradle`. This makes the PushSecure submodule's gradle module available to any other gradle modules within your project.

```groovy
include ':myapp', ':submodules:chatsecure-push:sdk'
```

Edit your application module's `build.gradle`. This informs gradle that your application's gradle module depends on the PushSecure gradle module (submodule). Say that five times fast.

```groovy
...
dependencies {
    compile project(':submodules:chatsecure-push:sdk')
}
```

### 2. Create a PushSecureClient

The API client is designed to operate against any compatible ChatSecure-Push backend.

```java
PushSecureClient client = new PushSecureClient("https://chatsecure-push.herokuapp.com/api/v1/");
```

### 3. Authenticate a user account

You'll need to have an `Account` registered with the API client to perform requests.
You can create or login to an existing account with the `authenticateAccount` method.
You should generally do this once per app-launch to ensure you have a fresh Account authentication token.

```java
client.authenticateAccount(requiredUsername, requiredPassword, optionalEmail,
                           new RequestCallback<Account>() {
                              @Override
                              public void onSuccess(Account response) {
                                // Authenticated Account
                                // Register this account with the api client
                                // to perform authenticated requests
                                client.setAccount(response);
                              }

                              @Override
                              public void onFailure(Throwable throwable) {
                                // An error occurred
                              }
                           });
```

If you have a persisted `Account` object you can set that at any time. This might be useful if you're managing multiple `Account`s from a single device.

```java
client.setAccount(account);
```

### 4. Register a Pushable Device 

On Android, we'll obtain a GCM token and register a GCM device with ChatSecure Push.

```java
// Retrieve your GCM token as requiredGcmToken
// See [Google's example](https://github.com/googlesamples/google-services/blob/e06754fc7d0e4bf856c001a82fb630abd1b9492a/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/RegistrationIntentService.java#L54)
client.createDevice(requiredGcmToken, optionalName, optionalDeviceId,
                            new RequestCallback<Device>() {
                              @Override
                              public void onSuccess(Device response) {
                                // Registered Device
                              }

                              @Override
                              public void onFailure(Throwable throwable) {
                                // An error occurred
                              }
                           });
```

### 5. Request a Whitelist Token

A Whitelist token gives its bearer push access to your device. It can be revoked at any time (see 5a).

```java
client.createToken(requiredDevice, optionalName,
                    new RequestCallback<PushToken>() {
                      @Override
                      public void onSuccess(PushToken response) {
                        // Created push token. Share this with a pal
                        // to let them send your device push messages!
                      }

                      @Override
                      public void onFailure(Throwable throwable) {
                        // An error occurred
                      }
                   });
```

### 5a. Revoke a Whitelist token

This removes its affiliation with your device, and you will no longer receive pushes from uers who "know you" by this token. 

Holders of the revoked token won't be immediately notified, but they will be told their token "does not exist" the next time they try to use it.

```java
client.deleteToken(requiredTokenString,
                    new RequestCallback<Void>() {
                      @Override
                      public void onSuccess(Void response) {
                        // Revoked push token
                      }

                      @Override
                      public void onFailure(Throwable throwable) {
                        // An error occurred
                      }
                   });
```

### 6. Send a Push Message

Push Message Recipients are always identified by their Whitelist token.

```java
client.sendMessage(requiredWhitelistTokenString, optionalData,
                    new RequestCallback<Message>() {
                      @Override
                      public void onSuccess(Message response) {
                        // Sent message
                      }

                      @Override
                      public void onFailure(Throwable throwable) {
                        // An error occurred
                      }
                    });
```

### 7. Parse incoming ChatSecure Push GCM Messages

See [Google's Example](https://github.com/googlesamples/google-services/blob/e06754fc7d0e4bf856c001a82fb630abd1b9492a/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/MyGcmListenerService.java) for a complete `GcmListenerService` implementation. Below we include the additions necessary to parse ChatSecure Push messages.

```java

public class MyGcmService extends GcmListenerService {

    PushParser parser = new PushParser();

    @Override
    public void onMessageReceived(String from, Bundle data) {

        PushMessage push = parser.parseBundle(from, data);

        if (push != null)
            Log.d("GotPush", "Received '" + push.payload + "' via token: " + push.token);
    }
    ...
}
```
