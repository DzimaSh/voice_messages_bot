package by.bsu.voicemessages.bot.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DecoderProperties {

    @Value("${decoder.bucket.location}")
    private String bucketLocation;

    @Value("${decoder.model}")
    private String decoderModel;

    @Value("${decoder.file}")
    private String decoderFile;

    @Value("${decoder.executor}")
    private String decoderExecutor;
}
