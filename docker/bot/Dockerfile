FROM alpine:edge

ENV PROJECT_PATH=/source

RUN apk update

RUN apk add openjdk21; \
    apk add gradle; \
    apk add python3 py3-pip && \
    apk add ffmpeg

RUN python3 -m pip install --break-system-packages --upgrade pip

RUN pip install --break-system-packages triton==2.1.0 && \
    pip install --break-system-packages -U openai-whisper==20231106

VOLUME ["$PROJECT_PATH"]
WORKDIR $PROJECT_PATH

CMD gradle bootRun