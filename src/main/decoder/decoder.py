import whisper
import sys


def encode_to_unicode(input_string):
    return ''.join('\\u{:04x}'.format(ord(c)) for c in input_string)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py <model_name> <voice_file_path>")
        sys.exit(1)

    model_name = sys.argv[1]
    voice_file_path = sys.argv[2]

    try:
        model = whisper.load_model(model_name)

        # load audio and pad/trim it to fit 30 seconds
        audio = whisper.load_audio(voice_file_path)
        audio = whisper.pad_or_trim(audio)

        # make log-Mel spectrogram and move to the same device as the model
        mel = whisper.log_mel_spectrogram(audio).to(model.device)

        # detect the spoken language
        _, probs = model.detect_language(mel)
        detected_language = encode_to_unicode(f"Detected language: {max(probs, key=probs.get)}")
        print(detected_language)

        # decode the audio
        options = whisper.DecodingOptions(fp16=False)
        result = whisper.decode(model, mel, options)

        # print the recognized text
        encoded_result = encode_to_unicode(result.text)
        print(encoded_result)

    except Exception as e:
        print(f"An error occurred: {e}")
