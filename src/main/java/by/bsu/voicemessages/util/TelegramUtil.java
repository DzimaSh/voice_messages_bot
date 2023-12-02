package by.bsu.voicemessages.util;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramUtil {
    public static SendMessage buildMessage(String text, Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        message.enableMarkdown(true);
        return message;
    }

    public static SendMessage buildReplyMessage(String text, Long chatId, Integer messageId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(messageId)
                .build();
        message.enableMarkdown(true);
        return message;
    }

    public static File voiceToFile(AbsSender sender, Voice voice) throws TelegramApiException {
        return sender.execute(new GetFile(voice.getFileId()));
    }

    public static VoiceMessageInfo buildVoiceMessageInfo(File file, Integer messageId, Long chatId) {
        return new VoiceMessageInfo(file, messageId, chatId);
    }
}
