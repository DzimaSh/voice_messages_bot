package by.bsu.voicemessages.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class Constants {
    @Value("${python.bucket.location}")
    private String bucketLocation;

}
