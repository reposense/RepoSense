FROM openjdk:8

WORKDIR ./RepoSense
COPY . .

EXPOSE 9000

# define environment params
# inspired by https://stackoverflow.com/questions/40873165/use-docker-run-command-to-pass-arguments-to-cmd-in-dockerfile
ENV NODE_VERSION=16.20.2
ENV NVM_DIR=/root/.nvm


# install procedures taken from https://stackoverflow.com/questions/36399848/install-node-in-dockerfile
RUN apt-get update -y
RUN apt-get install build-essential -y
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

# execute the command to run here
CMD ./gradlew run -Dargs="--repos https://github.com/reposense/RepoSense.git https://github.com/CATcher-org/CATcher.git https://github.com/MarkBind/markbind.git --output ./report_folder --since 31/1/2017 --formats java adoc xml --view --ignore-standalone-config --last-modified-date --timezone UTC+08 --find-previous-authors"
