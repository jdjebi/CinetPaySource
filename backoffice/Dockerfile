FROM debian:9

RUN apt-get update -yq

RUN apt-get install curl -yq

RUN curl -sL https://deb.nodesource.com/setup_16.x | bash

RUN apt-get install nodejs -yq \
    && apt-get clean -y

RUN npm install -g nodemon

ADD . /app/

WORKDIR /app

RUN npm install

RUN npm update

EXPOSE 3000

VOLUME /app/logs

CMD npm run-script run