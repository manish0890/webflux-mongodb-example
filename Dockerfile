FROM openjdk:13-jdk-alpine3.10

# Define a work directory
WORKDIR /service

# Add all files to work directory
ADD . /service
#ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.7.3/wait /service/wait
#
#RUN chmod +x /service/wait

# Update libraries
RUN apk update && apk add wget --no-cache

# -B = Run in non-interactive (batch)
# -T = Thread count = 4
RUN ./mvnw -B -T 4 package -DskipTests=true

# Expose port for rest interface
EXPOSE 8090

# Provide entry-point
CMD ["/bin/sh", "./bin/entry-point.sh"]