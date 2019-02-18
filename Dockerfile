FROM java:openjdk-8

ENV SOURCE_HOME="/source" \
    APP_HOME="/app"

WORKDIR $SOURCE_HOME

# Copy only what we need to get the dependencies
COPY build.gradle $SOURCE_HOME/
COPY gradlew $SOURCE_HOME/
COPY gradle $SOURCE_HOME/gradle

RUN ./gradlew downloadDependencies

RUN mkdir -p $APP_HOME
COPY . $SOURCE_HOME

RUN ./gradlew assemble -x test \
    && ln -s $SOURCE_HOME/config/app_config.yml $APP_HOME/app_config.yml \
    && ln -s $SOURCE_HOME/build/libs/service-frontend.jar $APP_HOME/app.jar \
    && ln -s $SOURCE_HOME/app.sh $APP_HOME/app.sh

WORKDIR $APP_HOME

EXPOSE 8080

RUN chmod +x app.sh

CMD ["./app.sh"]