package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface Handler {
    void handle(Message message) throws UnhandledException, TelegramApiException;
}
