FROM ubuntu:xenial

LABEL maintainer="unguiculus"

# Install a basic SSH server
RUN DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get install -y \
        apt-transport-https \
        apt-utils \
        ca-certificates \
        curl \
        locales \
        openssh-server \
        sudo \
        vim && \
    rm -rf /var/lib/apt/lists/*

RUN sed -i 's|session    required     pam_loginuid.so|session    optional     pam_loginuid.so|g' /etc/pam.d/sshd
RUN mkdir -p /var/run/sshd

RUN groupadd -g 1000 jenkins && \
    useradd -u 1000 -g 1000 -m -s /bin/bash jenkins

# Set password for the jenkins user
RUN echo "jenkins:jenkins" | chpasswd

# Standard SSH port
EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]

RUN DEBIAN_FRONTEND=noninteractive && \
    dpkg-reconfigure --frontend noninteractive locales && \
    echo 'en_US.UTF-8 UTF-8' > /etc/locale.gen && \
    locale-gen && \
    update-locale LANG=en_US.UTF-8

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV TERM xterm

ENV WORKSPACE /home/jenkins/workspace
RUN mkdir $WORKSPACE && \
    chown -R jenkins:jenkins $WORKSPACE

RUN DEBIAN_FRONTEND=noninteractive && \
    apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D && \
    echo 'deb https://apt.dockerproject.org/repo ubuntu-xenial main' | tee /etc/apt/sources.list.d/docker.list && \
    curl -sSL https://deb.nodesource.com/setup_6.x | bash - && \
    apt-cache policy docker-engine && \
    apt-get install -y --no-install-recommends \
        build-essential \
        docker-engine \
        git \
        jq \
        man \
        nodejs \
        python \
        python3 \
        software-properties-common \
        unzip \
        vim \
        wget \
        xvfb \
        zip && \
    rm -rf /var/lib/apt/lists/* && \
    gpasswd -a jenkins docker

# Install Docker Compose and Bash completion for Docker Compose
RUN curl -L https://github.com/docker/compose/releases/download/1.20.1/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    curl -L https://raw.githubusercontent.com/docker/compose/$(docker-compose version --short)/contrib/completion/bash/docker-compose > /etc/bash_completion.d/docker-compose

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
    apt-get update && \
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    rm -rf /var/cache/oracle-jdk8-installer && \
    rm -rf /var/lib/apt/lists/*

RUN MAVEN_VERSION=3.5.3 && \
    wget http://mirror.netcologne.de/apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    echo "51025855d5a7456fc1a67666fbef29de  apache-maven-$MAVEN_VERSION-bin.tar.gz" > maven.md5 && \
    md5sum --check maven.md5 && \
    rm -f maven.md5 && \
    tar xfz apache-maven-$MAVEN_VERSION-bin.tar.gz -C /usr/local && \
    rm -f apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    ln -s /usr/local/apache-maven-$MAVEN_VERSION/bin/mvn /usr/local/bin/mvn
