package by.bsu.voicemessages.decode;

import by.bsu.voicemessages.bot.util.BotProperties;
import by.bsu.voicemessages.bot.util.DecoderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class MessageConsumer implements Runnable {
    private final BlockingQueue<File> files;
    private final BotProperties botProperties;
    private final DecoderProperties decoderProperties;

    @Override
    public void run() {
        while (true) {
            try {
                File fileToDecode = files.take();;
                log.debug("Taking message... " + files.size() + " still waiting.");

                String fileUrl = fileToDecode.getFileUrl(botProperties.getBotToken());
                String fileLocation = prepareFileLocation();

                saveVoiceFile(fileUrl, fileLocation);

                String decodedMessage = decodeMessage(fileLocation);
                log.debug(decodedMessage);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append('\n');
        }

        int exitCode = process.waitFor();
        String exitMessage = "Script executed with exit code: " + exitCode;
        log.debug(
                exitMessage +
                        (exitCode == 0
                                ? ". Message decoded."
                                : ". Message is not decoded."
                        )
        );
        return builder.append(exitMessage).toString();
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
