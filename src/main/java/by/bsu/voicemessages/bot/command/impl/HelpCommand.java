package by.bsu.voicemessages.bot.command.impl;

import by.bsu.voicemessages.bot.command.Command;
import by.bsu.voicemessages.util.ChatMetaInfo;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static by.bsu.voicemessages.util.TelegramUtil.buildMessage;

@Slf4j
public class HelpCommand extends Command {

    private final String helpMessage = """
            :microphone: Welcome to @BelVoiceMessagesBot! :notes:
                        
            Use the VoiceBot by sending voice messages directly to the chat. Our system will recognize the speech in your messages and transcribe it for you.
            
            Commands available:
            :earth_americas: /lang: Set the language for voice recognition. For example /lang for Belarusian, etc.
            
            :studio_microphone: Simply send a voice message and our bot will transcribe it for you, providing the recognized text.
            
            Note: Our system supports multiple languages for voice recognition. To change the language, use the /lang command followed by the appropriate language code.
            
            Need assistance or have questions? Feel free to reach out anytime!
            """;

    public HelpCommand(AbsSender bot, String identifier, String description) {
        super(bot, identifier, description);
    }

    @Override
    public void execute(ChatMetaInfo chatMetaInfo) throws TelegramApiException {
        log.debug("HelpCommand received");

        sendAnswer(buildMessage(
                EmojiParser.parseToUnicode(helpMessage),
                chatMetaInfo.getChatId()
        ));
    }
}
