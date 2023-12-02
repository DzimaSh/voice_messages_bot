package by.bsu.voicemessages.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class TelegramUtil {
    public static SendMessage buildMessage(String text, Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        message.enableMarkdown(true);
        return message;
    }
}
