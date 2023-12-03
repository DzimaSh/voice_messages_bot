package by.bsu.voicemessages.bot;

import by.bsu.voicemessages.bot.handler.CommandHandler;
import by.bsu.voicemessages.bot.handler.DecodeVoiceMessageHandler;
import by.bsu.voicemessages.bot.handler.Handler;
import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.exception.UnhandledCommandException;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static by.bsu.voicemessages.bot.util.BotActionConstants.*;

@Slf4j
@Component
public class VoiceMessagesBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final DecoderProperties decoderProperties;
    private final Map<String, Handler> handlerList = new HashMap<>();
    private final Map<Long, ChatMetaInfo> chatsInfo = new ConcurrentHashMap<>();

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

        log.info("Handlers initialized");
    }

    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update received");

        try {
            Long chatId = update.getMessage().getChatId();
            try {
                Message message = update.getMessage();
                ChatMetaInfo chatInfoToPut = new ChatMetaInfo(message.getChatId());
                ChatMetaInfo chatInfo = Optional
                        .ofNullable(chatsInfo
                                .putIfAbsent(chatId, chatInfoToPut)
                        )
                        .orElse(chatInfoToPut);

                String action = retrieveActionFromMessage(message);

                if (Objects.isNull(action)) {
                    this.execute(TelegramUtil
                            .buildMessage("Unsupported request! Try /help for further info", chatInfo.getChatId()));
                } else {
                    handlerList.get(action).handle(message, chatInfo);
                    log.debug("Update handled");
                }
            } catch (UnhandledCommandException e) {
                this.execute(TelegramUtil
                        .buildMessage("Unsupported command! Try /help for further info", chatId));
            } catch (UnhandledException e) {
                log.error("Unhandled type of message received");
            }

        } catch (TelegramApiException e) {
            log.error("Telegram Api Error: " + e.getMessage());
        }
    }

    private String retrieveActionFromMessage(Message message) throws TelegramApiException {
        if (Objects.nonNull(message.getText())) {
            if (message.getText().startsWith(COMMAND_PREFIX)) {
                return COMMAND_KEY;
            }
        } else if (Objects.nonNull(message.getVoice())) {
            return DECODE_VOICE_MESSAGE_KEY;
        }
        return null;
    }
}
