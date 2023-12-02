package by.bsu.voicemessages.bot.handler;

import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.exception.UnhandledException;
import by.bsu.voicemessages.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static by.bsu.voicemessages.util.TelegramUtil.buildMessage;

@Component
@Slf4j
public class MessageHandler implements Handler {

    private final BotProperties botProperties;
    private final BlockingQueue<File> filesToDecode;

    public MessageHandler(BotProperties botProperties) {
        this.botProperties = botProperties;
        this.filesToDecode = new LinkedBlockingQueue<>(botProperties.getMaxMessages());
    }

    @Override
    public void handle(AbsSender sender, Message message) throws UnhandledException, TelegramApiException {
        log.debug("Message received");
        if (Objects.isNull(message.getVoice())) {
            String error = "Non-voice messages are not currently supported";
            sender.execute(buildMessage(error, message.getChatId()));
            throw new UnhandledException(error);
        }
        File voiceFile = TelegramUtil.voiceToFile(sender, message.getVoice());

        String fileUrl = voiceFile.getFileUrl(botProperties.getBotToken());

        try (BufferedInputStream inputStream = new BufferedInputStream(new URI(fileUrl).toURL().openStream())) {

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
