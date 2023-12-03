package by.bsu.voicemessages.bot.handler.impl;

import by.bsu.voicemessages.bot.handler.CallbackHandler;
import by.bsu.voicemessages.util.ChatMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class SetLangCallbackHandler extends CallbackHandler {

    public SetLangCallbackHandler(AbsSender bot) {
        super(bot);
    }

    @Override
    public void handleCallback(CallbackQuery callbackQuery, ChatMetaInfo chatInfo) {

    }
}
