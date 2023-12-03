package by.bsu.voicemessages.bot.command.impl;

import by.bsu.voicemessages.bot.command.Command;
import by.bsu.voicemessages.util.ChatMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class SetLangCommand extends Command {
    public SetLangCommand(AbsSender bot, String identifier, String description) {
        super(bot, identifier, description);
    }

    @Override
    public void execute(ChatMetaInfo chatMetaInfo) {
        log.debug("SetLangCommand received");
    }
}
