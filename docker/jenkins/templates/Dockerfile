FROM jenkins/jenkins:@JENKINS_VERSION@

LABEL maintainer="unguiculus"

USER root

RUN DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        apt-transport-https \
        apt-utils \
        ca-certificates \
        curl \
        jq \
        locales \
        vim && \
    rm -rf /var/lib/apt/lists/*

RUN DEBIAN_FRONTEND=noninteractive && \
    dpkg-reconfigure --frontend noninteractive locales && \
    echo 'en_US.UTF-8 UTF-8' > /etc/locale.gen && \
    locale-gen && \
    update-locale LANG=en_US.UTF-8

USER jenkins

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV TERM xterm
ENV REF_DIR /usr/share/jenkins/ref

# install plugins
COPY plugins.txt $REF_DIR/plugins.txt
RUN /usr/local/bin/install-plugins.sh < $REF_DIR/plugins.txt

# copy init script for bootstrapping
COPY ./init.groovy.d/bootstrap.groovy $REF_DIR/init.groovy.d/bootstrap.groovy

# copy init lib for bootstrapping
COPY ./init.lib /usr/local/lib/jenkins_bootstrap/init.lib/

# Disable install and plugin wizard
RUN echo @JENKINS_VERSION@ > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
RUN echo @JENKINS_VERSION@ > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion
