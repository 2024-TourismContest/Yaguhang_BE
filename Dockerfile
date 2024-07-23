FROM openjdk:17.0.1-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Install dependencies
RUN apt-get -y update && \
    apt -y install wget unzip curl gnupg2 xvfb && \
    apt-get clean

# Download and install Chrome
RUN wget -q -O /tmp/chrome-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/126.0.6478.126/linux64/chrome-linux64.zip && \
    unzip /tmp/chrome-linux64.zip -d /opt/google && \
    rm /tmp/chrome-linux64.zip

# Download and install Chromedriver
RUN wget -q -O /tmp/chromedriver-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/126.0.6478.126/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver-linux64.zip -d /opt/google && \
    mv /opt/google/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm /tmp/chromedriver-linux64.zip

# Update PATH environment variable to include Chrome and Chromedriver
ENV PATH="/opt/google/chrome-linux64:/usr/local/bin:${PATH}"

# Argument for the JAR file
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose the port that the application will run on (optional, if your application listens on a specific port)
EXPOSE 8080

# Set the display number for Xvfb
ENV DISPLAY=:99

# Start Xvfb and run the application
CMD Xvfb :99 -screen 0 1920x1080x24 & \
    java -jar /app/app.jar