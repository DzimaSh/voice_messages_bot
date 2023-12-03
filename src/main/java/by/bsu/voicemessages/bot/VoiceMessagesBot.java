package by.bsu.voicemessages.bot;

import by.bsu.voicemessages.bot.handler.CommandHandler;
import by.bsu.voicemessages.bot.handler.DecodeVoiceMessageHandler;
import by.bsu.voicemessages.bot.handler.Handler;
import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static by.bsu.voicemessages.bot.util.BotActionConstants.COMMAND_KEY;
import static by.bsu.voicemessages.bot.util.BotActionConstants.DECODE_VOICE_MESSAGE_KEY;

@Slf4j
@Component
public class VoiceMessagesBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final DecoderProperties decoderProperties;
    private final Map<String, Handler> handlerList = new HashMap<>();
    private final Map<Long, ChatMetaInfo> chatInfo = new ConcurrentHashMap<>();

    public VoiceMessagesBot(BotProperties botProperties, DecoderProperties decoderProperties) {
        super(botProperties.getBotToken());
        this.botProperties = botProperties;
        this.decoderProperties = decoderProperties;
    }

    @PostConstruct
    public void initializeHandlers() {
        handlerList.put(DECODE_VOICE_MESSAGE_KEY, new DecodeVoiceMessageHandler(
                botProperties, decoderProperties, this)
        );
        handlerList.put(COMMAND_KEY, new CommandHandler(this));
    }

    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update received");

        try {
            handlerList.get(DECODE_VOICE_MESSAGE_KEY).handle(update.getMessage(), chatInfo.get(update.getMessage().getChatId()));
        } catch (UnhandledException e) {
            log.debug("Unhandled type of message received");
        } catch (TelegramApiException e) {
            log.error("Voice message is not accessible");
        }
    }
}
