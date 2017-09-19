#!/usr/bin/env bash

set -e
set -u
set -o pipefail

main() {
    local scriptdir=$(dirname "$(readlink -f "$0")")
    pushd "$scriptdir" > /dev/null

    . ./templates.env

    echo "JENKINS_VERSION: $JENKINS_VERSION"
    echo "MASTER_IMAGE_VERSION: $MASTER_IMAGE_VERSION"
    echo "SLAVE_IMAGE_VERSION: $SLAVE_IMAGE_VERSION"
    echo "JENKINS_HOST: $JENKINS_HOST"
    echo "LETSENCRYPT_EMAIL: $LETSENCRYPT_EMAIL"

    echo 'Generating docker-compose.yml from template: ./templates/docker-compose.yml'

    sed "s/@MASTER_IMAGE_VERSION@/$MASTER_IMAGE_VERSION/g" ./templates/docker-compose.yml \
        | sed "s/@SLAVE_IMAGE_VERSION@/$SLAVE_IMAGE_VERSION/g" \
        | sed "s/@JENKINS_HOST@/$JENKINS_HOST/g" \
        | sed "s/@LETSENCRYPT_EMAIL@/$LETSENCRYPT_EMAIL/g" \
        > docker-compose.yml

    echo 'Generating jenkins_master/Dockerfile from template: ./templates/Dockerfile'

    sed "s/@JENKINS_VERSION@/$JENKINS_VERSION/g" ./templates/Dockerfile > jenkins_master/Dockerfile

    popd > /dev/null
}

main
