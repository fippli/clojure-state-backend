FROM clojure:openjdk-8-lein

WORKDIR /usr/src/app

COPY project.clj .

RUN lein deps

COPY . .

ENTRYPOINT [ "lein" ]

CMD [ "trampoline", "run" ]
