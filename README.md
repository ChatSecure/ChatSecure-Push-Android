#PushSecureDemo-Android

This is a demo [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Android client.

# Setup ChatSecure Push Server and Google Cloud Messaging

1. Clone and setup the [ChatSecure Push Server](https://github.com/ChatSecure/ChatSecure-Push-Server) Django project.

2. Register a Google Cloud Messaging Application with [Google Developers](https://developers.google.com/mobile/add)

    At the conclusion of the registration process you'll be presented with a `Server API Key` and a `google-services.json` file.

3. Copy the GCM `Server API Key` to `./push/push/local_settings.py` in the ChatSecure Push Server Django project. Copy `google-services.json` to this project's `./example` directory.