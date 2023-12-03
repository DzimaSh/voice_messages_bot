package by.bsu.voicemessages.bot.handler.impl;

import by.bsu.voicemessages.bot.handler.CallbackHandler;
import by.bsu.voicemessages.decode.Language;
import by.bsu.voicemessages.exception.UnhandledCallbackException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import com.vdurmont.emoji.EmojiParser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static by.bsu.voicemessages.bot.util.BotActionConstants.CALLBACK_DATA_SEPARATOR;
import static by.bsu.voicemessages.util.TelegramUtil.buildEditMessage;

@Slf4j
public class SetLangCallbackHandler extends CallbackHandler {

    public SetLangCallbackHandler(AbsSender bot) {
        super(bot);
    }

    @Override
    public void handleCallback(CallbackQuery callbackQuery, ChatMetaInfo chatInfo) throws TelegramApiException {
        try {
            Language newLang = parseLanguageFromCallback(callbackQuery);
            chatInfo.setLanguage(newLang);

            updateMessage(
                    EmojiParser.parseToUnicode(
                            String.format("Language is successfully changed to %s :white_check_mark:", newLang.getDescription())
                    ),
                    callbackQuery.getMessage()
            );
        } catch (IllegalArgumentException e) {
            log.error("Unhandled language! " + e.getMessage());
            throw new UnhandledCallbackException("Unknown language");
        }
    }

    private Language parseLanguageFromCallback(CallbackQuery callbackQuery) {
        return Language.valueOf(callbackQuery
                .getData()
                .split(CALLBACK_DATA_SEPARATOR)[1]
        );
    }

    private void updateMessage(String text, @NonNull Message message) throws TelegramApiException {
        sendAnswer(buildEditMessage(
                text,
                message.getMessageId(),
                message.getChatId()
        ));
    }
}
