package by.bsu.voicemessages.bot;

import by.bsu.voicemessages.bot.handler.CallbackHandler;
import by.bsu.voicemessages.bot.handler.MessageHandler;
import by.bsu.voicemessages.bot.handler.impl.CommandHandler;
import by.bsu.voicemessages.bot.handler.impl.DecodeVoiceMessageHandler;
import by.bsu.voicemessages.bot.handler.Handler;
import by.bsu.voicemessages.bot.handler.impl.SetLangCallbackHandler;
import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.exception.UnhandledCallbackException;
import by.bsu.voicemessages.exception.UnhandledCommandException;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.ChatMetaInfo;
import by.bsu.voicemessages.util.TelegramUtil;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
        handlerList.put(SET_LANG_CALLBACK_KEY, new SetLangCallbackHandler(this));

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
            if (Objects.nonNull(update.getMessage())) {
                handleReceivedMessage(update.getMessage());
            } else if (Objects.nonNull(update.getCallbackQuery())) {
                handleReceivedCallbackQuery(update.getCallbackQuery());
            }
        } catch (UnhandledException e) {
            log.error("Unhandled object received: " + e.getMessage());
        } catch (TelegramApiException e) {
            log.error("Telegram Api Error: " + e.getMessage());
        }

        log.debug("Update handled");
    }

    private void handleReceivedCallbackQuery(@NonNull CallbackQuery callbackQuery) throws TelegramApiException {
        Long chatId = retrieveChatId(callbackQuery.getMessage());

        try {
            ChatMetaInfo chatInfo = putOrGetChatMetaInfoIfAbsent(chatId);

            String action = Objects.requireNonNull(
                    CallbackHandler.retrieveCallbackActionFromCallbackQuery(callbackQuery)
            );

            handlerList.get(action).handle(callbackQuery, chatInfo);
        } catch (UnhandledCallbackException e) {
            log.error("Unhandled type of callback received: " + e.getMessage());
            this.execute(TelegramUtil
                    .buildMessage(e.getMessage(), chatId));
        }
    }

    private void handleReceivedMessage(@NonNull Message message) throws TelegramApiException {
        Long chatId = retrieveChatId(message);
        try {
            ChatMetaInfo chatInfo = putOrGetChatMetaInfoIfAbsent(chatId);

            String action = Objects.requireNonNull(
                    MessageHandler.retrieveActionFromMessage(message)
            );

            handlerList.get(action).handle(message, chatInfo);
        } catch (UnhandledCommandException e) {
            this.execute(TelegramUtil
                    .buildMessage("Unsupported command! Try /help for further info", chatId));
        }
    }

    private ChatMetaInfo putOrGetChatMetaInfoIfAbsent(Long chatId) {
        ChatMetaInfo chatInfoToPut = new ChatMetaInfo(chatId);
        return Optional
                .ofNullable(chatsInfo
                        .putIfAbsent(chatId, chatInfoToPut)
                )
                .orElse(chatInfoToPut);
    }

    private Long retrieveChatId(@NonNull Message message) {
        return message.getChatId();
    }
}
