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
import java.util.concurrent.atomic.AtomicBoolean;

import static by.bsu.voicemessages.util.TelegramUtil.buildReplyMessage;
import static by.bsu.voicemessages.util.TelegramUtil.decodeUnicodeString;

@Slf4j
@RequiredArgsConstructor
public class MessageConsumer implements Runnable {
    private final BlockingQueue<VoiceMessageInfo> files;
    private final BotProperties botProperties;
    private final DecoderProperties decoderProperties;
    private final AbsSender bot;

    private final AtomicBoolean isWritingAudio = new AtomicBoolean(false);

    @Override
    public void run() {
        while (true) {
            if (isWritingAudio.get()) continue;
            try {
                VoiceMessageInfo messageInfo = files.take();
                File fileToDecode = messageInfo.getFile();

                isWritingAudio.compareAndSet(false, true);
                log.debug("Taking message... " + files.size() + " still waiting.");

                String fileUrl = fileToDecode.getFileUrl(botProperties.getBotToken());
                String voiceFfileLocation = prepareVoiceFileLocation();

                saveVoiceFile(fileUrl, voiceFfileLocation);
                isWritingAudio.compareAndSet(true, false);

                String decodedMessage = decodeMessage(voiceFfileLocation, messageInfo.getLang());
                bot.execute(
                        buildReplyMessage(decodedMessage,
                                messageInfo.getChatId(),
                                messageInfo.getMessageId()
                        )
                );
            } catch (InterruptedException | IOException | TelegramApiException e) {
                log.error("Error occurred while handling decoding voice message");
                e.printStackTrace();
            }
        }
    }

    private String prepareVoiceFileLocation() {
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

    private String decodeMessage(String voiceFileLocation, Language lang) throws IOException, InterruptedException {
        Process process = buildDecodeProcess(voiceFileLocation, lang);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder builder = new StringBuilder();
        String unicodeEncoded;
        while ((unicodeEncoded = reader.readLine()) != null) {
            builder.append(decodeUnicodeString(unicodeEncoded)).append('\n');
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
    private Process buildDecodeProcess(String voiceFileLocation, Language lang) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                decoderProperties.getDecodeScript(),
                decoderProperties.getDecoderFile(),
                decoderProperties.getDecoderModel(),
                voiceFileLocation,
                "--encode",
                !lang.equals(Language.RECOGNIZE) ? String.format("--language=%s", Language.BE.getLang()) : ""
        );
        processBuilder.redirectErrorStream(true);

        return processBuilder.start();
    }
}
