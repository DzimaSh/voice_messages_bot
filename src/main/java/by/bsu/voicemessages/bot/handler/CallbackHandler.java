package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static by.bsu.voicemessages.bot.util.BotActionConstants.SET_LANG_CALLBACK_KEY;

@Slf4j
@RequiredArgsConstructor
public abstract class CallbackHandler implements Handler {

    private final AbsSender bot;

    @Override
    public void handle(BotApiObject callbackQuery, ChatMetaInfo chatInfo) throws UnhandledException, TelegramApiException {
        log.debug("Callback received");
        if (callbackQuery instanceof CallbackQuery) {
            handleCallback((CallbackQuery) callbackQuery, chatInfo);
        } else {
            throw new UnhandledException("Trying to handle non-callback object using CallbackHandler");
        }
        log.debug("Callback handled");
    }

    public abstract void handleCallback(CallbackQuery callbackQuery, ChatMetaInfo chatInfo) throws TelegramApiException;

    protected void sendAnswer(EditMessageText answer) throws TelegramApiException {
        TelegramUtil.updateMessage(bot, answer);
    }

    public static String retrieveCallbackActionFromCallbackQuery(CallbackQuery callbackQuery) {
        if (Objects.nonNull(callbackQuery.getData())) {
            if (callbackQuery.getData().startsWith(SET_LANG_CALLBACK_KEY)) {
                return SET_LANG_CALLBACK_KEY;
            }
        }
        return null;
    }
}
