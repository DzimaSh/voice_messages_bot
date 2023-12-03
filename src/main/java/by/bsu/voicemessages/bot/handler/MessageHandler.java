package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledCommandException;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static by.bsu.voicemessages.bot.util.BotActionConstants.*;

@Slf4j
public abstract class MessageHandler implements Handler {
    @Override
    public void handle(BotApiObject message, ChatMetaInfo chatInfo) throws TelegramApiException {
        log.debug("Message received");
        if (message instanceof Message) {
            handleMessage((Message) message, chatInfo);
        } else {
            throw new UnhandledException("Trying to handle non-message object using MessageHandler");
        }
        log.debug("Message handled");
    }

    public abstract void handleMessage(Message message, ChatMetaInfo chatInfo) throws TelegramApiException;

    public static String retrieveActionFromMessage(Message message) {
        if (Objects.nonNull(message.getText())) {
            if (message.getText().startsWith(COMMAND_PREFIX)) {
                return COMMAND_KEY;
            }
        } else if (Objects.nonNull(message.getVoice())) {
            return DECODE_VOICE_MESSAGE_KEY;
        }
        throw new UnhandledCommandException("Unsupported request!");
    }
}
