# Telegram Voice Messages Bot

## Description

This bot is designed to handle voice messages on Telegram. It uses the [Wisper](https://github.com/openai/whisper) model to convert voice messages to text, making communication more accessible and convenient.

## Features

- **Voice to Text**: Converts voice messages into text for easy reading.
- **Multilingual Support**: Supports multiple languages for voice to text conversion.
- **Easy to Use**: Simply forward a voice message to the bot and receive the transcribed text.
- **Commands**:
    - `/start`: Start the bot
    - `/lang`: Set default language to decode messages
    - `/help`: Get help info

## Tech Stack

- Java 21
- Gradle 8.4
- Spring Boot
- Python
- Docker
- Docker Compose

## Installation

1. Clone this repository:
    ```bash
    git clone <repositoty_link>
    ```
2. Set up your Telegram Bot token and other environment variables in the `*.env` files located in the `/env` folder.
3. Run the Docker container:
    ```bash
    docker-compose up -d
    ```

## Usage

After starting the bot, you can use it by forwarding voice messages to the bot. The bot will then send back a message containing the transcribed text.

You can access the bot via this link: [@BelVoiceMessagesBot](https://t.me/BelVoiceMessagesBot)

## Contributing

Contributions are welcome! Please read the contributing guidelines before getting started.
