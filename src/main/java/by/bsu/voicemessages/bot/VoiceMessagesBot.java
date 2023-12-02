package by.bsu.voicemessages.bot;

import by.bsu.voicemessages.bot.handler.Handler;
import by.bsu.voicemessages.util.BotProperties;
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

    public VoiceMessagesBot(BotProperties botProperties, Handler messageHandler) {
        super(botProperties.getBotToken());
        this.botProperties = botProperties;
        this.messageHandler = messageHandler;
    }


    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            messageHandler.handle(this, update.getMessage());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

//        try {
//            Message message = update.getMessage();
//            Long chatId = message.getChatId();
//
//            if (Objects.isNull(chatStatus.get(chatId))) {
//                chatStatus.put(chatId, Status.WAIT_FOR_COMMAND);
//            }
//
//            String action = retrieveActionFromMessage(message, chatId);
//
//            if (Objects.isNull(action)) {
//                this.execute(TelegramUtil
//                        .buildMessage("Unsupported request! Try `/help` for further info", chatId));
//            } else {
//                handlerList.get(action).handle(this, message);
//                updateChatStatus(action, message, chatId);
//            }
//
//        } catch (TelegramApiException e) {
//            log.error("External Telegram exception!");
//            log.error(e.getMessage());
//        } catch (UnhandledException e) {
//            log.error("Unhandled message received!");
//            log.error(e.getMessage());
//        }
    }

//    private String retrieveActionFromMessage(Message message, Long chatId) throws TelegramApiException {
//        if (message.getText().startsWith(COMMAND_PREFIX)) {
//            return COMMAND_KEY;
//        } else if (chatStatus.get(chatId).equals(Status.WAIT_FOR_CURRENCY_SYMBOL)) {
//            return CURRENCY_PRICE_MESSAGE_KEY;
//        } else if (chatStatus.get(chatId).equals(Status.WAIT_FOR_CURRENCY_SUBSCRIBE)) {
//            return CURRENCY_SUBSCRIBE_MESSAGE_KEY;
//        }
//        return null;
//    }
//
//    private void updateChatStatus(String action, Message message, Long chatId) {
//        if (action.contains(COMMAND_KEY)) {
//            CommandDetails command = TelegramUtil.getCommandByIdentifier(message.getText());
//            switch (Objects.requireNonNull(command)) {
//                case CHECK_CURRENCY -> chatStatus.put(chatId, Status.WAIT_FOR_CURRENCY_SYMBOL);
//                case SUBSCRIBE_CURRENCY -> chatStatus.put(chatId, Status.WAIT_FOR_CURRENCY_SUBSCRIBE);
//                default -> chatStatus.put(chatId, Status.WAIT_FOR_COMMAND);
//            }
//        } else {
//            chatStatus.put(chatId, Status.WAIT_FOR_COMMAND);
//        }
//    }
//
//    @AllArgsConstructor
//    private enum Status {
//        WAIT_FOR_COMMAND,
//        WAIT_FOR_CURRENCY_SYMBOL,
//        WAIT_FOR_CURRENCY_SUBSCRIBE,
//        WAIT_FOR_SETTINGS
//    }
}
