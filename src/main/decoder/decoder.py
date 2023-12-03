import argparse
import os
import whisper


def encode_to_unicode(input_string):
    return ''.join('\\u{:04x}'.format(ord(c)) for c in input_string)


def print_result(detected_language_text, result_text, encode=False):
    if encode:
        detected_language_text = encode_to_unicode(detected_language_text)
        result_text = encode_to_unicode(result_text)

    print(detected_language_text)
    print(result_text)


def validate_input_params(model_name, voice_file_path, language, encode):
    if not isinstance(model_name, str) or not isinstance(voice_file_path, str):
        raise ValueError("model_name and voice_file_path must be strings")

    if language is not None and not isinstance(language, str):
        raise ValueError("language must be a string")

    if not isinstance(encode, bool):
        raise ValueError("encode must be a boolean")

    if not os.path.isfile(voice_file_path):
        raise ValueError("voice_file_path must be a valid file path")


def parse_arguments():
    parser = argparse.ArgumentParser(description="Decode audio file.")
    parser.add_argument("model_name", help="Name of the model.")
    parser.add_argument("voice_file_path", help="Path to the voice file.")
    parser.add_argument("--language", default=None, metavar="LANGUAGE",
                        help="Language to use for decoding.")
    parser.add_argument("--encode", default=False, action=argparse.BooleanOptionalAction,
                        help="Whether to encode the output.")

    return parser.parse_args()


if __name__ == "__main__":
    args = parse_arguments()

    # Unwrap the args
    model_name = args.model_name
    voice_file_path = args.voice_file_path
    language = args.language
    encode = args.encode

    # Validate the input parameters
    validate_input_params(model_name, voice_file_path, language, encode)

    try:
        model = whisper.load_model(model_name)

        # load audio and pad/trim it to fit 30 seconds
        audio = whisper.load_audio(voice_file_path)
        audio = whisper.pad_or_trim(audio)

        # make log-Mel spectrogram and move to the same device as the model
        mel = whisper.log_mel_spectrogram(audio).to(model.device)

        # detect the spoken language
        if language is None:
            _, probs = model.detect_language(mel)
            language = max(probs, key=probs.get)
            detected_language = f"Detected language: {language}"
        else:
            detected_language = f"Provided language: {language}"

        # decode the audio
        options = whisper.DecodingOptions(fp16=False, language=language)
        result = whisper.decode(model, mel, options)

        # print the recognized text
        print_result(detected_language, result.text, encode)

    except Exception as e:
        print(f"An error occurred: {e}")
