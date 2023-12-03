package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public abstract class CallbackHandler implements Handler {

    private final AbsSender bot;


    @Override
    public void handle(BotApiObject callbackQuery, ChatMetaInfo chatInfo) throws UnhandledException, TelegramApiException {
        if (callbackQuery instanceof CallbackQuery) {
            handleCallback((CallbackQuery) callbackQuery, chatInfo);
        } else {
            throw new UnhandledException("");
        }
    }

    public abstract void handleCallback(CallbackQuery callbackQuery, ChatMetaInfo chatInfo);

    protected void sendAnswer(SendMessage answer) throws TelegramApiException {
        TelegramUtil.sendMessage(bot, answer);
    }
}
