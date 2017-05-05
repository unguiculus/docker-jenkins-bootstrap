#!/usr/bin/env bash

scriptdir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ip_address=$("$scriptdir/tf" output -no-color ip_address)
ssh-keyscan -t rsa "$ip_address" >> ~/.ssh/known_hosts
