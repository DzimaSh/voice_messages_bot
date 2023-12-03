package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.bot.command.Command;
import by.bsu.voicemessages.bot.command.CommandDetails;
import by.bsu.voicemessages.bot.command.impl.SetLangCommand;
import by.bsu.voicemessages.exception.UnhandledCommandException;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.HashMap;
import java.util.Objects;

import static by.bsu.voicemessages.bot.command.CommandDetails.SET_LANG;

@Slf4j
public class CommandHandler implements Handler {

    private final AbsSender bot;
    private final HashMap<CommandDetails, Command> commands = new HashMap<>();

    public CommandHandler(AbsSender bot) {
        this.bot = bot;
        initializeCommands();
    }

    @Override
    public void handle(Message message, ChatMetaInfo chatInfo) throws UnhandledException {
        CommandDetails command = retrieveCommandFromMessage(message);

        if (Objects.isNull(command)) {
            throw new UnhandledCommandException("Command " + message.getText() + " is not supported");
        }

        commands.get(command).execute(chatInfo);
    }

    public void initializeCommands() {
        commands.put(SET_LANG, new SetLangCommand(
                bot, SET_LANG.getCommandIdentifier(), SET_LANG.getCommandDescription())
        );

        log.info("Command handler initialized");
        log.info("Available commands list: " + commands.keySet()
                .stream()
                .map(CommandDetails::getCommandIdentifier)
                .toList()
        );
    }

    private CommandDetails retrieveCommandFromMessage(Message message) throws UnhandledCommandException {
        try {
            return TelegramUtil.getCommandByIdentifier(message.getText());
        } catch (IllegalArgumentException ex) {
            throw new UnhandledCommandException("Command " + message.getText() + " is not supported");
        }
    }
}
