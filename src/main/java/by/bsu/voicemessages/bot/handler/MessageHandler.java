package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.decode.MessageConsumer;
import by.bsu.voicemessages.exception.UnhandledException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static by.bsu.voicemessages.util.TelegramUtil.buildMessage;
import static by.bsu.voicemessages.util.TelegramUtil.voiceToFile;

@Component
@Slf4j
public class MessageHandler implements Handler {
    private final BlockingQueue<File> filesToDecode;
    private final MessageConsumer messageConsumer;

    public MessageHandler(BotProperties botProperties, DecoderProperties decoderProperties) {
        this.filesToDecode = new LinkedBlockingQueue<>(botProperties.getMaxMessages());
        this.messageConsumer = new MessageConsumer(filesToDecode, botProperties, decoderProperties);
    }

    @PostConstruct
    public void initMessageConsumer() {
        new Thread(messageConsumer).start();
        log.info("Consumer started...");
    }

    @Override
    public void handle(AbsSender sender, Message message) throws UnhandledException, TelegramApiException {
        log.debug("Message received");
        if (Objects.isNull(message.getVoice())) {
            String error = "Non-voice messages are currently not supported";
            sender.execute(buildMessage(error, message.getChatId()));
            throw new UnhandledException(error);
        }

        boolean isAdded = filesToDecode.offer(voiceToFile(sender, message.getVoice()));
        log.debug(String.format(
                "Voice file is %s. Current queue size is %d%s",
                isAdded ? "added" : "not added",
                filesToDecode.size(),
                isAdded ? "." : ". Queue is full..."
        ));

        sender.execute(buildMessage(
                buildResponse(isAdded),
                message.getChatId()
        ));
    }

    private String buildResponse(boolean isAddedToQueue) {
        StringBuilder builder = new StringBuilder();
        if (isAddedToQueue) {
            builder.append("Your message is in queue. It's position is: ")
                    .append(filesToDecode.size());
        } else {
            builder.append("Unfortunately, now we cannot proceed your request. ")
                    .append("Try again a bit later!\n")
                    .append("Apologize for the inconvenience...");
        }
        return builder.toString();
    }
}
