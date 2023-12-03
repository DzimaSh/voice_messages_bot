package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class MessageHandler implements Handler {
    @Override
    public void handle(BotApiObject message, ChatMetaInfo chatInfo) throws TelegramApiException {
        if (message instanceof Message) {
            handleMessage((Message) message, chatInfo);
        } else {
            throw new UnhandledException("Trying to handle non-message object using MessageHandler");
        }
    }

    public abstract void handleMessage(Message message, ChatMetaInfo chatInfo) throws TelegramApiException;
}
