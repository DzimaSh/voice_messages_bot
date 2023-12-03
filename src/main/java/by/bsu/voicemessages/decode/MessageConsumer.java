package by.bsu.voicemessages.decode;

import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import by.bsu.voicemessages.util.VoiceMessageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import static by.bsu.voicemessages.util.TelegramUtil.buildReplyMessage;
import static by.bsu.voicemessages.util.TelegramUtil.decodeUnicodeString;

@Slf4j
@RequiredArgsConstructor
public class MessageConsumer implements Runnable {
    private final BlockingQueue<VoiceMessageInfo> files;
    private final BotProperties botProperties;
    private final DecoderProperties decoderProperties;
    private final AbsSender bot;

    @Override
    public void run() {
        while (true) {
            try {
                VoiceMessageInfo retrievedPair = files.take();
                File fileToDecode = retrievedPair.getFile();
                log.debug("Taking message... " + files.size() + " still waiting.");

                String fileUrl = fileToDecode.getFileUrl(botProperties.getBotToken());
                String fileLocation = prepareFileLocation();

                saveVoiceFile(fileUrl, fileLocation);

                String decodedMessage = decodeMessage(fileLocation);
                bot.execute(
                        buildReplyMessage(decodedMessage,
                                retrievedPair.getChatId(),
                                retrievedPair.getMessageId()
                        )
                );
            } catch (InterruptedException | IOException | TelegramApiException e) {
                log.error("Error occurred while handling decoding voice message");
            }
        }
    }

    private String prepareFileLocation() {
        return decoderProperties.getBucketLocation() + "/voice.mp3";
    }

    private void saveVoiceFile(String fileUrl, String fileLocation) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(new URI(fileUrl).toURL().openStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(fileLocation)
                )
        ) {
            byte[] byteMessage = inputStream.readAllBytes();
            outputStream.write(byteMessage);
            outputStream.flush();
            log.debug("Saved message to the file. Ready to decode...");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String decodeMessage(String fileLocation) throws IOException, InterruptedException {
        Process process = buildDecodeProcess();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder builder = new StringBuilder();
        String utf8encoded;
        while ((utf8encoded = reader.readLine()) != null) {
            builder.append(decodeUnicodeString(utf8encoded)).append('\n');
        }

        int exitCode = process.waitFor();
        log.debug(
                "Script executed with exit code: " + exitCode +
                        (exitCode == 0
                                ? ". Message decoded."
                                : ". Message is not decoded."
                        )
        );
        return builder.toString();
    }

    @NotNull
    private Process buildDecodeProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "D:/Java/JavaProjects/voicemessages/src/main/decoder/decode.bat",
                "D:/Java/JavaProjects/voicemessages/src/main/decoder/decoder.py",
                decoderProperties.getDecoderModel(),
                "D:/Java/JavaProjects/voicemessages/src/main/decoder/voiceBucket/voice.mp3"
        );
        processBuilder.redirectErrorStream(true);

        return processBuilder.start();
    }
}
