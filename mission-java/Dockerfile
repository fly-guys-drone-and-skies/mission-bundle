FROM ubuntu
RUN apt update -y
RUN apt install -y openjdk-11-jdk python3
COPY ./ /app
RUN ls /app
RUN chmod +x /app/gradlew run
#RUN cd /app && /app/gradlew run
