#!/usr/bin/env bash

set -e
set -u

function main() {
    local scriptdir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
    pushd $scriptdir > /dev/null

    echo 'Copying bootstrap files...'
    local target_dir=./data/bootstrap/
    mkdir -p "$target_dir"
    cp -f ./bootstrap/* "$target_dir"

    popd > /dev/null
}

main
