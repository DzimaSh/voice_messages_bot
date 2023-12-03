package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface Handler {
    void handle(BotApiObject object, ChatMetaInfo chatInfo) throws UnhandledException, TelegramApiException;
}
