package by.bsu.voicemessages.bot;

import by.bsu.voicemessages.bot.handler.Handler;
import by.bsu.voicemessages.bot.handler.MessageHandler;
import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.exception.UnhandledException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class VoiceMessagesBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final Handler messageHandler;

    public VoiceMessagesBot(BotProperties botProperties, DecoderProperties decoderProperties) {
        super(botProperties.getBotToken());
        this.botProperties = botProperties;
        this.messageHandler = new MessageHandler(botProperties, decoderProperties, this);
    }

    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            messageHandler.handle(update.getMessage());
        } catch (UnhandledException e) {
            log.debug("Unhandled type of message received");
        } catch (TelegramApiException e) {
            log.error("Voice message is not accessible");
        }
    }
}
