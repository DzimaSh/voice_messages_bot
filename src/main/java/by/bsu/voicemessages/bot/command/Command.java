package by.bsu.voicemessages.bot.command;

import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class Command extends BotCommand {

    private final AbsSender bot;

    protected Command(AbsSender bot, String identifier, String description) {
        super(identifier, description);
        this.bot = bot;
    }

    protected void sendAnswer(String text, ChatMetaInfo chatMetaInfo) throws TelegramApiException {
        SendMessage message = TelegramUtil.buildMessage(text, chatMetaInfo.getChatId());

        bot.execute(message);
        log.info("Answer sent to user");
    }

    public abstract void execute(ChatMetaInfo chatMetaInfo);
}