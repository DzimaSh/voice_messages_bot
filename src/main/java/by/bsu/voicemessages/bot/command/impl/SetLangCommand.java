package by.bsu.voicemessages.bot.command.impl;

import by.bsu.voicemessages.bot.command.Command;
import by.bsu.voicemessages.decode.Language;
import by.bsu.voicemessages.util.ChatMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static by.bsu.voicemessages.bot.util.BotActionConstants.CALLBACK_DATA_SEPARATOR;
import static by.bsu.voicemessages.bot.util.BotActionConstants.SET_LANG_CALLBACK_KEY;
import static by.bsu.voicemessages.util.TelegramUtil.*;

@Slf4j
public class SetLangCommand extends Command {
    public SetLangCommand(AbsSender bot, String identifier, String description) {
        super(bot, identifier, description);
    }

    @Override
    public void execute(ChatMetaInfo chatMetaInfo) throws TelegramApiException {
        log.debug("SetLangCommand received");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(buildSetLangKeyboard());

        sendAnswer(buildReplyMarkupMessage(
                "Select base language form the given list:",
                chatMetaInfo.getChatId(),
                markupInline)
        );
    }

    private List<List<InlineKeyboardButton>> buildSetLangKeyboard() {
        List<InlineKeyboardButton> allButtons = prepareAllLangButtons();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < allButtons.size(); i += 2) {
            keyboard.add(allButtons.subList(i, Math.min(i + 2, allButtons.size())));
        }

        return keyboard;
    }

    private List<InlineKeyboardButton> prepareAllLangButtons() {
        return Arrays.stream(Language.values())
                .map(lang -> buildKeyboardButton(
                        concatStringsWithSeparator(" ", lang.getDescription(), lang.getEmojiCode()),
                        concatStringsWithSeparator(CALLBACK_DATA_SEPARATOR, SET_LANG_CALLBACK_KEY, String.valueOf(lang))
                ))
                .toList();
    }
}
