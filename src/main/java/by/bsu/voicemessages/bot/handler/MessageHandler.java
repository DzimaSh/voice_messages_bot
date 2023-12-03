package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.decode.MessageConsumer;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.VoiceMessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static by.bsu.voicemessages.util.TelegramUtil.*;

@Slf4j
public class MessageHandler implements Handler {

    private final BotProperties botProperties;
    private final AbsSender bot;
    private final BlockingQueue<VoiceMessageInfo> filesToDecode;
    private final MessageConsumer messageConsumer;

    public MessageHandler(BotProperties botProperties, DecoderProperties decoderProperties, AbsSender bot) {
        this.botProperties = botProperties;
        this.bot = bot;
        this.filesToDecode = new LinkedBlockingQueue<>(botProperties.getMaxMessages());
        this.messageConsumer = new MessageConsumer(filesToDecode, botProperties, decoderProperties, bot);
        initMessageConsumer();
    }

    public void initMessageConsumer() {
        new Thread(messageConsumer).start();
        log.info("Consumer started...");
    }

    @Override
    public void handle(Message message) throws UnhandledException, TelegramApiException {
        log.debug("Message received");
        if (Objects.isNull(message.getVoice())) {
            String error = "Non-voice messages are currently not supported";
            bot.execute(buildMessage(error, message.getChatId()));
            throw new UnhandledException(error);
        }

        int length = filesToDecode.size();
        log.debug(String.format(
                "Voice file is going to be added to queue. Current queue size is %d%s",
                length,
                botProperties.getMaxMessages().equals(length) ? ". Queue is full..." : "."
        ));
        boolean isAdded = filesToDecode.offer(
                buildVoiceMessageInfo(voiceToFile(bot, message.getVoice()),
                        message.getMessageId(),
                        message.getChatId())
        );

        bot.execute(buildMessage(
                buildResponse(isAdded, length),
                message.getChatId()
        ));
        log.debug("Answer sent.");
    }

    private String buildResponse(boolean isAddedToQueue, int position) {
        StringBuilder builder = new StringBuilder();
        if (isAddedToQueue) {
            builder.append("Your message is in queue. It's position is: ")
                    .append(position)
                    .append(position == 0 ? "! Processing your request..." : "... Waiting...");
        } else {
            builder.append("Unfortunately, now we cannot proceed your request. ")
                    .append("Try again a bit later!\n")
                    .append("Apologize for the inconvenience...");
        }
        return builder.toString();
    }
}
