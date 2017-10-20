#!/usr/bin/env bash

#
# Copies Jenkins bootstrap config files for local testing
#

set -e
set -u
set -o pipefail

scriptdir=$(dirname "$(readlink -f "$0")")
pushd "$scriptdir" > /dev/null

echo -n 'Ansible Vault password:'
read -s vault_password
echo

password_file=$(mktemp)
echo "$vault_password" > "$password_file"

target_dir="../docker/jenkins/data/bootstrap"
config_files=($(ls -d ../ansible/roles/docker_config/files/*))

for config_file in "${config_files[@]}"; do
    echo "Processing file'$config_file'..."

    if head -n 1 "$config_file" | grep '$ANSIBLE_VAULT;' > /dev/null; then
        echo "Decrypting file '$config_file'..."

        file_name=$(basename "$config_file")
        ansible-vault decrypt "$config_file" --vault-password-file "$password_file" --output "$target_dir/$file_name"
    else
        echo "Copying plain text file '$config_file'..."

        cp "$config_file" "$target_dir"
    fi
done

rm -f "$password_file"

echo 'Done.'

popd > /dev/null
