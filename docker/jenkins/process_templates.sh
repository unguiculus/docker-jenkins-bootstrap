#!/usr/bin/env bash

set -e
set -u
set -o pipefail

function main() {
    local scriptdir=$(dirname "$(readlink -f "$0")")
    pushd "$scriptdir" > /dev/null

    . ./templates.env

    echo "JENKINS_MASTER_VERSION: $JENKINS_MASTER_VERSION"
    echo "JENKINS_SLAVE_VERSION: $JENKINS_SLAVE_VERSION"
    echo "JENKINS_HOST: $JENKINS_HOST"
    echo "LETSENCRYPT_EMAIL: $LETSENCRYPT_EMAIL"

    echo 'Generating docker-compose.yml from template: ./templates/docker-compose.yml'

    sed "s/@JENKINS_MASTER_VERSION@/$JENKINS_MASTER_VERSION/g" ./templates/docker-compose.yml \
        | sed "s/@JENKINS_SLAVE_VERSION@/$JENKINS_SLAVE_VERSION/g" \
        | sed "s/@JENKINS_HOST@/$JENKINS_HOST/g" \
        | sed "s/@LETSENCRYPT_EMAIL@/$LETSENCRYPT_EMAIL/g" \
        > docker-compose.yml

    echo 'Generating jenkins_master/Dockerfile from template: ./templates/Dockerfile'

    sed "s/@JENKINS_MASTER_VERSION@/$JENKINS_MASTER_VERSION/g" ./templates/Dockerfile > jenkins_master/Dockerfile

    popd > /dev/null
}

main
