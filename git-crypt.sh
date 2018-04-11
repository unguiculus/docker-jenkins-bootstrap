#!/usr/bin/env bash

set -e

exec docker run -it -v "$(pwd):/repo" quay.io/lukebond/git-crypt:v1.0.0 "$@"

